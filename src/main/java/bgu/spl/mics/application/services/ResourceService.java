package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
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
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{
	private static final Logger log = Logger.getLogger(LogisticsService.class.getName());
	private ResourcesHolder rh;
	private int tick;
	private CountDownLatch latch;


	/**
	 * @param s -
	 *          the name of the service
	 * @param latch-
	 *             execution of {@link CountDownLatch}
	 */
	public ResourceService(String s, CountDownLatch latch) {
		super(s);
		this.rh=ResourcesHolder.getInstance();
		this.tick=1;
		this.latch=latch;
	}

	@Override
	protected void initialize() {
		log.log(Level.INFO, "Acquiring:  " + this.getName() + " is started");
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
	 * handling {@Link acquireDeliveryEvent}
	 */
	private void handleEvents(){
		subscribeEvent(acquireDeliveryEvent.class,ev -> {
			Future<DeliveryVehicle> futureVehicle= null;
			try {
				futureVehicle = rh.acquireVehicle();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			log.log(Level.INFO, "Acquiring:  " + this.getName() + " is Done by" + this.getName());
			complete(ev,futureVehicle.get());
			rh.releaseVehicle(futureVehicle.get());

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
