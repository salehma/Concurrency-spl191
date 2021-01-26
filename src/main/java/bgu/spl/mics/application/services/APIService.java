package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.MessageTerminate;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService {
	private ConcurrentHashMap<Integer,LinkedList<orderSchedule>> OrderSchedule;
	private int tick;
	private CountDownLatch latch;
	private static final Logger log = Logger.getLogger(APIService.class.getName());
    private int id;
    private int j;
    private int orderID;
    private Customer c;

	/**
	 * @param c -
	 *          the customer that the service simulates
	 * @param id -
	 *           the id of the customer
	 * @param name -
	 *             the name of the service
	 *
	 * @param listOfPurchases -
	 *                        the orders that the the customer wants
	 * @param latch -
	 *              execution of {@link CountDownLatch}
	 */

	public APIService(Customer c, int id, String name, ArrayList<orderSchedule> listOfPurchases, CountDownLatch latch) {
		super(name);
		this.id=id;
		OrderSchedule= new ConcurrentHashMap<>();
		for (int i=0;i<listOfPurchases.size();i++){
			if(OrderSchedule.containsKey(listOfPurchases.get(i).getTick())){
				OrderSchedule.get(listOfPurchases.get(i).getTick()).addLast(listOfPurchases.get(i));
			}
			else {
				OrderSchedule.put(listOfPurchases.get(i).getTick(), new LinkedList<orderSchedule>());
				OrderSchedule.get(listOfPurchases.get(i).getTick()).addLast(listOfPurchases.get(i));
			}

		}
		tick = 1;
		this.latch = latch;
		this.orderID=id*j;
		this.c=c;
	}

	@Override
	protected void initialize() {
		log.log(Level.INFO, "Client : " + this.getName() + " is started");
		this.BroadcastTick();
		this.BroadcastTermination();
		latch.countDown();

	}


	private void BroadcastTick() {
		subscribeBroadcast(TickBroadcast.class, tick -> {

			int t=0;
			this.tick = tick.getTick();
			if(OrderSchedule.containsKey(tick.getTick())){
				LinkedList<orderSchedule> orderSchedule = OrderSchedule.get(tick.getTick());
				t++;
				for (int i = 0; i < orderSchedule.size(); i++) {
					orderSchedule os=orderSchedule.get(i);
					Future<OrderReceipt> future = sendEvent(new BookOrderEvent(this.c, this.orderID, this.id, this.getName(), os));
					OrderReceipt or= future.get();
					if (or != null) {
						this.c.addReceipt(or);
						j++;
						sendEvent(new DeliveryEvent(c.getAddress(), c.getDistance()));
					}
					else
					    j++;
					if (orderSchedule.size() == j) {
						if(OrderSchedule.size()==t) {
							log.log(Level.INFO, "Tick : " + this.tick + " - the client " + this.getName()
									+ " has done purchasing and terminated.");
							terminate();
						}
					}
				}
			}
		});


	}


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
