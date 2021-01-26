package bgu.spl.mics.application.passiveObjects;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory implements Serializable {

	private static Inventory instance = null;
	private HashMap<String, BookInventoryInfo> inventoryList;
	private HashMap<String, Integer> books;


	/**
	 * a protected constructor which realized by thread safe singleton
	 */
	protected Inventory() {
		inventoryList = new HashMap<String, BookInventoryInfo>();

		books= new HashMap<String, Integer>();
	}
	/**
     * Retrieves the single instance of this class.
     */
	public static Inventory getInstance() {
		if (instance == null) {
			instance = new Inventory();
		}
		return instance;
	}
	
	/**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load (BookInventoryInfo[ ] inventory ) {
		for (int i = 0; i < inventory.length; i++) {
			inventoryList.put(inventory[i].getBookTitle(), inventory[i]);
			books.put(inventory[i].getBookTitle(),inventory[i].getAmountInInventory());
		}

	}
	
	/**
     * Attempts to take one book from the store.
     * <p>
     * @param book 		Name of the book to take from the store
     * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * 			The first should not change the state of the inventory while the 
     * 			second should reduce by one the number of books of the desired type.
     */
	public OrderResult take (String book) {
		if (!inventoryList.containsKey(book))
			inventoryList.put(book, new BookInventoryInfo(book, 0, 0));
		synchronized (inventoryList.get(book)) {
			if (inventoryList.get(book).getAmountInInventory() > 0) {
				inventoryList.get(book).setAmountInInventory(inventoryList.get(book).getAmountInInventory() - 1);
				return OrderResult.SUCCESSFULLY_TAKEN;
			} else
				return OrderResult.NOT_IN_STOCK;
		}

	}


	
	
	
	/**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
	public synchronized int checkAvailabiltyAndGetPrice(String book) {
		if(inventoryList.containsKey(book)){
			return inventoryList.get(book).getPrice();
		}
		return -1;
	}
	
	/**
     * 
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory. 
     * This method is called by the main method in order to generate the output.
     */
	public void printInventoryToFile(String filename) throws IOException {
		for (Map.Entry<String, BookInventoryInfo> entry : inventoryList.entrySet()) {
				books.put(entry.getKey(),entry.getValue().getAmountInInventory());
		}
		FileOutputStream fout = new FileOutputStream(filename);
		ObjectOutputStream oos = new ObjectOutputStream(fout);
		oos.writeObject(books);
	}
}
