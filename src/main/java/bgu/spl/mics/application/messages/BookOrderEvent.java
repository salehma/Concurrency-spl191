package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.orderSchedule;
/**
* an {@link Event} sent went the customer want to buy a specifec book
 * Its
 *  * response type expected to be a {@link OrderReceipt}.
 * */
public class BookOrderEvent implements Event<OrderReceipt> {
    private String senderName;
    private orderSchedule orderSchedule;
    private int amountSold;
    private int id;
    private int orderId;
    private Customer customer;

    /**
     * @param customer -
     *                 the customer tha wants to buy the book
     * @param orderID-
     *               the id of the current order
     * @param id-
     *          the id of the customer
     * @param senderName-
     *                  the name of the service that sent the event
     * @param orderSchedule-
     *                     the order that should be proccess
     */
    public BookOrderEvent(Customer customer,int orderID,int id,String senderName, orderSchedule orderSchedule) {
        this.customer=customer;
        this.senderName = senderName;
        this.orderSchedule = orderSchedule;
        this.id=id;
        this.orderId=id;
    }


    public String getSenderName() {
        return senderName;
    }

    public orderSchedule getPurchase() {
        return orderSchedule;
    }


    public int getId() {
        return id;
    }

    public int getOrderId() {
        return orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

}
