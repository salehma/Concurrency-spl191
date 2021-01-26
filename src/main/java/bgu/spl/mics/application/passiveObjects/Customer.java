package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable {

	private String name;
	private int id;
	private String address;
	private int ditance;
	private int AvailableCreditAmount;
	private int CreditNumber;
	private List<OrderReceipt> orderReceiptsList;

	public Customer(String name, int id, String address, int distance, int AvailableCreditAmount, int CreditNumber){
		this.name=name;
		this.id=id;
		this.address=address;
		this.ditance=distance;this.AvailableCreditAmount=AvailableCreditAmount;
		this.CreditNumber=CreditNumber;
		this.orderReceiptsList = new ArrayList<OrderReceipt>();
	}


	/**
     * Retrieves the name of the customer.
     */
	public String getName() {
		return name;
	}

	public void addReceipt(OrderReceipt or){
		this.orderReceiptsList.add(or);
	}
	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {

		return id;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {

		return address;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {
		return ditance;
	}

	
	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {
		return orderReceiptsList;
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		return AvailableCreditAmount;
	}
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		return CreditNumber;
	}

	public void setAvailableCreditAmount(int availableCreditAmount){
		this.AvailableCreditAmount=availableCreditAmount;
	}
	
}
