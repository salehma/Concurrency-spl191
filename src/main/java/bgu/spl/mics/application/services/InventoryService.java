package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CheckAvailabilityEvent;
import bgu.spl.mics.application.messages.MessageTerminate;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{
	private int tick;
	Inventory inventory_;
	private CountDownLatch latch;
	private static final Logger log = Logger.getLogger(InventoryService.class.getName());

	/**
	 * @param s - the name of the service
	 * @param latch-
	 *             execution of {@link CountDownLatch}
	 */
	public InventoryService( String s, CountDownLatch latch) {
		super(s);
		tick = 1;
		this.inventory_ = Inventory.getInstance();
		this.latch = latch;


	}

	@Override
	protected void initialize() {
		log.log(Level.INFO, this.getName() + " is started");
		this.BroadcastTick();
		this.checkEvent();
		this.BroadcastTermination();
		latch.countDown();

	}



	/**
	 * a {@link TickBroadcast} message which indicates the time.
	 */
	private void BroadcastTick(){
		subscribeBroadcast(TickBroadcast.class, tick -> {
			this.tick = tick.getTick();

		});
	}


	/**
	 * handling {@Link CheckAvailabilityEvent}
	 */
	private void checkEvent() {
		subscribeEvent(CheckAvailabilityEvent.class, ev -> {
			OrderResult result = inventory_.take(ev.getPurchase().getbookName());
			if (result.name().compareTo("NOT_IN_STOCK") == 0){

				complete(ev,false);
			}
			else {

				complete(ev, true);
			}
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
