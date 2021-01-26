package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a information about a certain book in the inventory.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class BookInventoryInfo {
	private String BookTitle;
	private int AmountInInventory;
	private int Price;
	/**
     * Retrieves the title of this book.
     * <p>
     * @return The title of this book.   
     */
	public BookInventoryInfo(String BookTitle, int AmountInInventory, int Price) {
		this.BookTitle=BookTitle;
		this.AmountInInventory=AmountInInventory;
		this.Price=Price;
	}

	public String getBookTitle() {
		return BookTitle;
	}

	/**
     * Retrieves the amount of books of this type in the inventory.
     * <p>
     * @return amount of available books.      
     */
	public int getAmountInInventory() {
		return AmountInInventory;
	}

	public void setAmountInInventory(int amount){
		this.AmountInInventory=amount;
	}

	/**
     * Retrieves the price for  book.
     * <p>
     * @return the price of the book.
     */
	public int getPrice() {
		return Price;
	}
	
	

	
}
