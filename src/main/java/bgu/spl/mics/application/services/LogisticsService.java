package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.MessageTerminate;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.acquireDeliveryEvent;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {
	private static final Logger log = Logger.getLogger(LogisticsService.class.getName());
	private int tick;
	private CountDownLatch latch;


	/**
	 * @param s -
	 *          the name of the service
	 * @param latch-
	 *             execution of {@link CountDownLatch}
	 */
	public LogisticsService(String s, CountDownLatch latch) {
		super(s);
		tick=0;
		this.latch=latch;
	}

	@Override
	protected void initialize() {
		log.log(Level.INFO, "Delivery : " + this.getName() + " is started");
		this.BroadcastTick();
		this.handleEvents();
		this.BroadcastTermination();
		latch.countDown();
	}


	/**
	 * a {@link TickBroadcast} message which indicates the time.
	 */
	private void BroadcastTick() {
		subscribeBroadcast(TickBroadcast.class, tick -> {
			this.tick = tick.getTick();
		});
	}


	/**
	 * handles the {@Link DeliveryEvent}
	 * and send new {@Link acquireDeliveryEvent}
	 */
	private void handleEvents() {
		subscribeEvent(DeliveryEvent.class,ev ->{
			Future<DeliveryVehicle > dv= sendEvent(new acquireDeliveryEvent());
			DeliveryVehicle vehicle =dv.get();
			vehicle.deliver(ev.getAddress(),ev.getDistance());
			log.log(Level.INFO, "Tick : " + tick + "\n order Delivered to : " + ev.getAddress() + " by " + this.getName() );

		});

	}

	/**
	 * see {@link MessageTerminate}
	 */
	private void BroadcastTermination() {
		subscribeBroadcast(MessageTerminate.class, terminate -> {
			try {
				terminate.getC().await();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			terminate();
		});
	}
}
