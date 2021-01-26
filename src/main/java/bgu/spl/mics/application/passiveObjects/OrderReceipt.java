package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

/**
 * Passive data-object representing a receipt that should 
 * be sent to a customer after the completion of a BookOrderEvent.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class OrderReceipt implements Serializable {
	private String seller;
	private int customerId;
	private String bookTitle;
	private int issuedTick;
	private int orderTick;
	private int processTick;
	private int Price;
	private int orderId;
	/**
	 * @param seller
	 *            - the name of the service which issued the receipt.
	 * @param customerId
	 *            - the name of the service this receipt issued to.
	 * @param bookTitle
	 *            - the book type ordered.
	 * @param issuedTick
	 *            - tick in which the receipt was issued
	 * @param orderTick
	 *            - tick in which the customer ordered to buy the book.
	 * @param processTick
	 *             -
	 * @param Price
	 *            - the price of the book.
	 */
	public OrderReceipt(int orderId,String seller, int customerId, String bookTitle, int issuedTick,int processTick, int orderTick,
				   int Price) {
		super();
		this.orderId=orderId;
		this.seller = seller;
		this.customerId = customerId;
		this.bookTitle = bookTitle;
		this.issuedTick = issuedTick;
		this.orderTick = orderTick;
		this.Price = Price;
		this.processTick=processTick;
	}
	/**
     * Retrieves the orderId of this receipt.
     */
	public int getOrderId() {

		return orderId;
	}
	
	/**
     * Retrieves the name of the selling service which handled the order.
     */
	public String getSeller() {

		return seller;
	}
	
	/**
     * Retrieves the ID of the customer to which this receipt is issued to.
     * <p>
     * @return the ID of the customer
     */
	public int getCustomerId() {
		return customerId;
	}
	
	/**
     * Retrieves the name of the book which was bought.
     */
	public String getBookTitle() {
		return bookTitle;
	}
	
	/**
     * Retrieves the price the customer paid for the book.
     */
	public int getPrice() {
		return Price;
	}
	
	/**
     * Retrieves the tick in which this receipt was issued.
     */
	public int getIssuedTick() {
		return issuedTick;
	}
	
	/**
     * Retrieves the tick in which the customer sent the purchase event.
     */
	public int getOrderTick() {

		return orderTick;
	}
	
	/**
     * Retrieves the tick in which the treating selling service started 
     * processing the order.
     */
	public int getProcessTick() {

		return processTick;
	}
}
