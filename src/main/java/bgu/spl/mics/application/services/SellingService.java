package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{
	private MoneyRegister moneyRegister;
	private int tick;
	private int processTick;
	private CountDownLatch latch;
	private static final Logger log = Logger.getLogger(SellingService.class.getName());


	public SellingService(String s, CountDownLatch latch) {
		super(s);
		this.moneyRegister=MoneyRegister.getInstance();
		this.tick=1;
		this.processTick=1;
		this.latch=latch;
	}

	@Override
	protected void initialize() {
		log.log(Level.INFO, this.getName() + " is started");
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
	 * handling {@Link BookOrderEvent}
	 * and sends {@Link CheckAvailabilityEvent}
	 */
	private void handleEvents() {
		this.processTick=tick;
		subscribeEvent(BookOrderEvent.class, ev -> {

			if(ev.getCustomer().getAvailableCreditAmount()>=ev.getPurchase().getPrice()) {
				Future<Boolean> future = sendEvent(new CheckAvailabilityEvent(ev.getSenderName(), ev.getPurchase()));
				if (future.get()) {
					this.Succeed(ev);
				} else {
					complete(ev, null);
					log.log(Level.INFO, "the order of type " + ev.getPurchase().getbookName() + " for the client "
							+ ev.getSenderName() + " is failed by " + this.getName());
				}
			}
			else{
				complete(ev, null);
				log.log(Level.INFO, "the order of type " + ev.getPurchase().getbookName() + " for the client "
						+ ev.getSenderName() + " is failed by " + this.getName());
			}
		});
	}


	/**
	 * @param ev - the event of the book ordering {@Link BookOrderEvent}
	 * if the book is available completes the processing
	 */
	private void Succeed(BookOrderEvent ev) {
		if (this.tick < ev.getPurchase().getTick())
			this.tick = ev.getPurchase().getTick();

		OrderReceipt regularResult = new OrderReceipt(ev.getOrderId(),this.getName(),ev.getId(),ev.getPurchase().getbookName(),tick,processTick, ev.getPurchase().getTick(),ev.getPurchase().getPrice());
		complete(ev, regularResult);

		moneyRegister.file(regularResult);
		moneyRegister.chargeCreditCard(ev.getCustomer(),ev.getPurchase().getPrice());
		log.log(Level.INFO, "Tick : " + tick + "\n order completed for : " + ev.getSenderName() + " bookName : "
				+ ev.getPurchase().getbookName() + " By " +this.getName());



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

