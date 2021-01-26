package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InventoryTest {

    Inventory inv;
    @Before
    public void setUp() throws Exception {
        inv = Inventory.getInstance();
        BookInventoryInfo[] sti = new BookInventoryInfo[7];
        for (int i = 0; i < 5; i++) {
            sti[i] = new BookInventoryInfo("book " + i, i + 5, i);
        }
        sti[5] = new BookInventoryInfo("book " + 5, 0, 0);
        sti[6] = new BookInventoryInfo("book " + 6, 0, 0);
        inv.load(sti);
    }

    @Test
    public void getInstance() {
        assertTrue(inv!=null);
    }

    @Test
    public void load() {
        try {
            assertEquals(inv.take("book 2"), OrderResult.SUCCESSFULLY_TAKEN);
            assertEquals(inv.take("book 5"), OrderResult.NOT_IN_STOCK);

        } catch (Exception e) {
            fail("Not yet implemented");
        }
    }

    @Test
    public void take() {
        try {
            assertEquals(inv.take("book 3"), OrderResult.SUCCESSFULLY_TAKEN);
            assertEquals(inv.take("book 6"), OrderResult.NOT_IN_STOCK);
        } catch (Exception e) {
            fail("Not yet implemented");
        }
    }

    @Test
    public void checkAvailabiltyAndGetPrice() {
        try {
            assertEquals(inv.take("book 2"), OrderResult.SUCCESSFULLY_TAKEN);
            assertEquals(inv.checkAvailabiltyAndGetPrice("book 5"),-1);

        } catch (Exception e) {
            fail("Not yet implemented");
        }
    }


}