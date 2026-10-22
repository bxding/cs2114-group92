import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

/**
 * JUnit tests for Table, from the test plan in our spec.
 *
 * @author Rohail Amjad
 * @version October 2026
 */
public class TableTest
{
    private static final double DELTA = 0.001;
    private Table table;
    private Party garcia;
    private MenuItem burger;
    private MenuItem soda;

    /**
     * Sets up shared objects before each test.
     */
    @Before
    public void setUp()
    {
        table = new Table(1, 4);
        garcia = new Party("Garcia", 4);
        burger = new MenuItem("Burger", 12.00);
        soda = new MenuItem("Soda", 2.50);
    }

    /**
     * new Table(1, 4) -> isOpen() is true, getSeats() is 4.
     */
    @Test
    public void testConstructor()
    {
        assertTrue(table.isOpen());
        assertEquals(4, table.getSeats());
        assertEquals(1, table.getNumber());
        assertNull(table.getParty());
    }

    /**
     * 0 or negative seats -> throws IllegalArgumentException.
     */
    @Test
    public void testConstructorBadInput()
    {
        try
        {
            new Table(1, 0);
            fail("Expected IllegalArgumentException for 0 seats");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
        try
        {
            new Table(1, -2);
            fail("Expected IllegalArgumentException for negative seats");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
        try
        {
            new Table(0, 4);
            fail("Expected IllegalArgumentException for table number 0");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }

    /**
     * New table -> true.
     */
    @Test
    public void testIsOpen()
    {
        assertTrue(table.isOpen());
    }

    /**
     * After seat(...) -> false.
     */
    @Test
    public void testIsOpenAfterSeat()
    {
        table.seat(garcia);
        assertFalse(table.isOpen());
    }

    /**
     * Open table -> party is seated, order starts empty.
     */
    @Test
    public void testSeat()
    {
        table.seat(garcia);
        assertSame(garcia, table.getParty());
        assertEquals(0, table.getOrder().length);
    }

    /**
     * Table already has a party -> throws IllegalStateException.
     */
    @Test
    public void testSeatOccupied()
    {
        table.seat(garcia);
        try
        {
            table.seat(new Party("Lee", 2));
            fail("Expected IllegalStateException");
        }
        catch (IllegalStateException e)
        {
            assertSame(garcia, table.getParty());
        }
    }

    /**
     * Party too big for the table -> throws IllegalArgumentException.
     */
    @Test
    public void testSeatTooBig()
    {
        Table small = new Table(2, 2);
        try
        {
            small.seat(garcia);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(small.isOpen());
        }
    }

    /**
     * fits() compares party size to seats.
     */
    @Test
    public void testFits()
    {
        assertTrue(table.fits(garcia));
        assertTrue(table.fits(new Party("Solo", 1)));
        assertFalse(table.fits(new Party("Big", 5)));
    }

    /**
     * Seated table, add Burger -> order has 1 item.
     */
    @Test
    public void testAddItem()
    {
        table.seat(garcia);
        table.addItem(burger);
        assertEquals(1, table.getOrder().length);
        assertSame(burger, table.getOrder()[0]);
    }

    /**
     * Open table -> throws IllegalStateException.
     */
    @Test
    public void testAddItemOpenTable()
    {
        try
        {
            table.addItem(burger);
            fail("Expected IllegalStateException");
        }
        catch (IllegalStateException e)
        {
            assertEquals(0, table.getOrder().length);
        }
    }

    /**
     * Order [Burger, Soda], removeItem(1) -> returns Burger.
     */
    @Test
    public void testRemoveItem()
    {
        table.seat(garcia);
        table.addItem(burger);
        table.addItem(soda);
        assertSame(burger, table.removeItem(1));
        assertEquals(1, table.getOrder().length);
        assertSame(soda, table.getOrder()[0]);
    }

    /**
     * Position past the end, or empty order -> throws
     * IndexOutOfBoundsException; order unchanged.
     */
    @Test
    public void testRemoveItemNotThere()
    {
        table.seat(garcia);
        try
        {
            table.removeItem(1);
            fail("Expected IndexOutOfBoundsException on empty order");
        }
        catch (IndexOutOfBoundsException e)
        {
            // expected
        }

        
        table.addItem(burger);
        try
        {
            table.removeItem(2);
            fail("Expected IndexOutOfBoundsException past the end");
        }
        catch (IndexOutOfBoundsException e)
        {
            assertEquals(1, table.getOrder().length);
        }
        try
        {
            table.removeItem(0);
            fail("Expected IndexOutOfBoundsException for position 0");
        }
        catch (IndexOutOfBoundsException e)
        {
            assertEquals(1, table.getOrder().length);
        }
    }
    /**
     * removeItem on an open table -> throws IllegalStateException.
     */
    @Test
    public void testRemoveItemOpenTable()
    {
        try
        {
            table.removeItem(1);
            fail("Expected IllegalStateException");
        }
        catch (IllegalStateException e)
        {
            assertEquals(0, table.getOrder().length);
        }
    }

    /**
     * Items come back in the order added.
     */
    @Test
    public void testGetOrder()
    {
        table.seat(garcia);
        table.addItem(burger);
        table.addItem(soda);
        table.addItem(burger);
        MenuItem[] items = table.getOrder();
        assertEquals(3, items.length);
        assertSame(burger, items[0]);
        assertSame(soda, items[1]);
        assertSame(burger, items[2]);
    }

    /**
     * Nothing ordered -> empty array.
     */
    @Test
    public void testGetOrderEmpty()
    {
        assertEquals(0, table.getOrder().length);
        table.seat(garcia);
        assertEquals(0, table.getOrder().length);
    }

    /**
     * Burger 12.00 + Soda 2.50 -> 14.50.
     */
    @Test
    public void testGetTotal()
    {
        table.seat(garcia);
        table.addItem(burger);
        table.addItem(soda);
        assertEquals(14.50, table.getTotal(), DELTA);
    }

    /**
     * Empty order -> 0.0.
     */
    @Test
    public void testGetTotalEmpty()
    {
        assertEquals(0.0, table.getTotal(), DELTA);
        table.seat(garcia);
        assertEquals(0.0, table.getTotal(), DELTA);
    }

    /**
     * Occupied table -> returns bill, table open, order empty.
     */
    @Test
    public void testClose()
    {
        table.seat(garcia);
        table.addItem(burger);
        table.addItem(soda);
        String bill = table.close();
        assertTrue(bill.contains("Garcia (4)"));
        assertTrue(bill.contains("Burger $12.00"));
        assertTrue(bill.contains("Soda $2.50"));
        assertTrue(bill.contains("Total: $14.50"));
        assertTrue(table.isOpen());
        assertNull(table.getParty());
        assertEquals(0, table.getOrder().length);
    }

    /**
     * Seated party that ordered nothing -> $0.00 bill with a note.
     */
    @Test
    public void testCloseEmptyOrder()
    {
        table.seat(garcia);
        String bill = table.close();
        assertTrue(bill.contains("No items were ordered"));
        assertTrue(bill.contains("Total: $0.00"));
        assertTrue(table.isOpen());
    }

    /**
     * Already open -> throws IllegalStateException.
     */
    @Test
    public void testCloseOpenTable()
    {
        try
        {
            table.close();
            fail("Expected IllegalStateException");
        }
        catch (IllegalStateException e)
        {
            assertTrue(table.isOpen());
        }
    }
}
