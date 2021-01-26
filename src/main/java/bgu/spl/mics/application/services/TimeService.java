package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.MessageTerminate;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private int speed;
	private int duration;
	private Timer time;
	private int tick;
	private CountDownLatch latch;
	private MoneyRegister mr;
	private static final Logger log = Logger.getLogger(TimeService.class.getName());

	/**
	 * constructor
	 *
	 * @param
	 *            speed : time period for each tick.
	 * @param
	 *            duration : time period for the system to work. it should
	 *            terminate immediately after this duration
	 * @param
	 *            latch : execution of {@link CountDownLatch} , the TimeService
	 *            must start lastly.
	 */
	public TimeService(int speed, int duration, CountDownLatch latch) {
		super("timer");
		this.speed = speed;
		this.duration = duration;
		time = new Timer();
		this.latch = latch;
		this.tick=1;
		mr=MoneyRegister.getInstance();
	}

	@Override
	protected void initialize() {

		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		log.log(Level.INFO, this.getName() + " is started");
		this.timeSchedule();
		this.BroadcastTermination();
	}

	/**
	 * starting the time.
	 */
	private void timeSchedule() {
		time.schedule(new TimerTask() {
		//	private int tick = 1;

			@Override
			public void run() {

				if (tick <= duration)
					sendBroadcast(new TickBroadcast(tick));
				if (tick == duration) {
					sendBroadcast(new MessageTerminate(new CyclicBarrier(MessageBus_.numOfMicroServices())));
					time.cancel();
				}
				tick++;
			}
		}, 0, speed);
	}

	/**
	 * see {@link MessageTerminate}
	 */
	private void BroadcastTermination() {
		subscribeBroadcast(MessageTerminate.class, terminate -> {
			try {
				terminate.getC().await();
			} catch (Exception e) {
				e.printStackTrace();
			}
			terminate();
		});
	}

}
