package bgu.spl.mics.application.passiveObjects;

/**
 * An object which describes a schedule of a single client-purchase at a
 * specific tick.
 */
public class orderSchedule {
    private String bookName;
    private int tick;
    private int price;

    /**
     * @param bookName
     *            - the name of the book to purchase.
     * @param tick
     *            - the tick number to send at.
     * @param price-
     *             the price of the book
     */
    public orderSchedule(String bookName, int tick,int price) {
        this.bookName = bookName;
        this.tick = tick;
        this.price=price;
    }

    public String getbookName() {
        return bookName;
    }

    public int getTick() {
        return tick;
    }

    public int getPrice() {
        return price;
    }
    public void setPrice(int price){
        this.price=price;
    }
}
