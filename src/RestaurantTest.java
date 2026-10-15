import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests Restaurant's seating rules, waitlist positions, and table
 * lifecycle, including normal and bad-input cases from the spec.
 *
 * @author Brian Ding
 * @version October 2026
 */
public class RestaurantTest
{
    private Restaurant restaurant;

    /** Creates a fresh dining room before each test. */
    @Before
    public void setUp()
    {
        restaurant = new Restaurant();
    }

    /** The dining room has exactly the fixed tables and starts empty. */
    @Test
    public void testConstructor()
    {
        int[] seats = { 2, 2, 4, 4, 6, 6 };
        assertEquals(6, Restaurant.TABLE_COUNT);
        assertEquals(6, Restaurant.MAX_PARTY_SIZE);
        for (int i = 0; i < seats.length; i++)
        {
            Table table = restaurant.getTable(i + 1);
            assertEquals(i + 1, table.getNumber());
            assertEquals(seats[i], table.getSeats());
            assertTrue(table.isOpen());
            assertNull(table.getParty());
            assertEquals(0, table.getOrder().length);
        }
        assertEquals(0, restaurant.getWaitlist().length);
        assertTrue(restaurant.getMenu().length > 0);
    }

    /** Arrivals take the lowest-numbered open table that fits. */
    @Test
    public void testAddPartySeatsLowestNumberThatFits()
    {
        assertEquals(3, restaurant.addParty("Garcia", 4));
        assertEquals(5, restaurant.addParty("Lee", 6));
        assertEquals(1, restaurant.addParty("Solo", 1));
        assertEquals(2, restaurant.addParty("Pair", 2));
        assertEquals(4, restaurant.addParty("Trio", 3));
        assertEquals(6, restaurant.addParty("Large", 5));
        assertEquals("Garcia", restaurant.getTable(3).getParty().getName());
        assertEquals(4, restaurant.getTable(3).getParty().getSize());
        assertEquals(0, restaurant.getWaitlist().length);
    }

    /** When full, arrivals join the end of the waitlist in order. */
    @Test
    public void testAddPartyWhenFull()
    {
        fillTables();
        assertEquals(-1, restaurant.addParty("First", 2));
        assertEquals(-1, restaurant.addParty("Second", 4));
        Party[] waiting = restaurant.getWaitlist();
        assertEquals(2, waiting.length);
        assertEquals("First", waiting[0].getName());
        assertEquals("Second", waiting[1].getName());
    }

    /** A party waits even when smaller tables remain open. */
    @Test
    public void testAddPartyWhenNoOpenTableFits()
    {
        assertEquals(5, restaurant.addParty("Large A", 6));
        assertEquals(6, restaurant.addParty("Large B", 5));
        assertEquals(-1, restaurant.addParty("Waiting", 6));
        assertTrue(restaurant.getTable(1).isOpen());
        assertTrue(restaurant.getTable(4).isOpen());
        assertEquals("Waiting", restaurant.getWaitlist()[0].getName());
    }

    /** Invalid arrivals change neither the tables nor the waitlist. */
    @Test
    public void testAddPartyBadInput()
    {
        String[] names = { null, "", "   ", "Valid", "Valid", "Valid" };
        int[] sizes = { 2, 2, 2, 0, -1, 7 };
        for (int i = 0; i < names.length; i++)
        {
            try
            {
                restaurant.addParty(names[i], sizes[i]);
                fail("Expected IllegalArgumentException");
            }
            catch (IllegalArgumentException e)
            {
                assertEquals(0, restaurant.getWaitlist().length);
                for (int number = 1; number <= Restaurant.TABLE_COUNT; number++)
                {
                    assertTrue(restaurant.getTable(number).isOpen());
                }
            }
        }
    }

    /** Invalid arrivals must not enter an already populated waitlist. */
    @Test
    public void testAddPartyBadInputWhenFull()
    {
        fillTables();
        restaurant.addParty("Waiting", 2);
        Party[] before = restaurant.getWaitlist();
        try
        {
            restaurant.addParty("Too big", 7);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            assertArrayEquals(before, restaurant.getWaitlist());
        }
    }

    /** Reservations insert at the front, middle, and end, using 1-based positions. */
    @Test
    public void testAddReservation()
    {
        fillTables();
        restaurant.addReservation("B", 2, 1);
        restaurant.addReservation("A", 1, 1);
        restaurant.addReservation("D", 4, 3);
        restaurant.addReservation("C", 3, 3);
        Party[] waiting = restaurant.getWaitlist();
        assertEquals(4, waiting.length);
        for (int i = 0; i < waiting.length; i++)
        {
            assertEquals(String.valueOf((char)('A' + i)), waiting[i].getName());
            assertEquals(i + 1, waiting[i].getSize());
        }
    }

    /** Reservation insertion follows its contract even when tables are open. */
    @Test
    public void testAddReservationWithOpenTables()
    {
        restaurant.addReservation("Reserved", 2, 1);
        assertEquals("Reserved", restaurant.getWaitlist()[0].getName());
        assertTrue(restaurant.getTable(1).isOpen());
    }

    /** Invalid insertion positions leave the waitlist unchanged. */
    @Test
    public void testAddReservationBadPosition()
    {
        assertReservationPositionRejected(0);
        assertReservationPositionRejected(-1);
        assertReservationPositionRejected(2);
        assertReservationPositionRejected(Integer.MIN_VALUE);
        restaurant.addReservation("Existing", 2, 1);
        assertReservationPositionRejected(3);
    }

    /** Reservation names and sizes have the same validation as arrivals. */
    @Test
    public void testAddReservationBadInput()
    {
        restaurant.addReservation("Existing", 2, 1);
        Party[] before = restaurant.getWaitlist();
        String[] names = { null, "", "   ", "Valid", "Valid", "Valid" };
        int[] sizes = { 2, 2, 2, 0, -1, 7 };
        for (int i = 0; i < names.length; i++)
        {
            try
            {
                restaurant.addReservation(names[i], sizes[i], 1);
                fail("Expected IllegalArgumentException");
            }
            catch (IllegalArgumentException e)
            {
                assertArrayEquals(before, restaurant.getWaitlist());
            }
        }
    }

    /** Cancellation returns the exact party and preserves the others' order. */
    @Test
    public void testCancelParty()
    {
        restaurant.addReservation("A", 2, 1);
        restaurant.addReservation("B", 2, 2);
        restaurant.addReservation("C", 2, 3);
        Party[] before = restaurant.getWaitlist();
        assertSame(before[1], restaurant.cancelParty(2));
        assertArrayEquals(new Party[] { before[0], before[2] },
            restaurant.getWaitlist());
        assertSame(before[2], restaurant.cancelParty(2));
        assertSame(before[0], restaurant.cancelParty(1));
        assertEquals(0, restaurant.getWaitlist().length);
        restaurant.addReservation("New", 2, 1);
        assertEquals("New", restaurant.getWaitlist()[0].getName());
    }

    /** Identical-looking parties are cancelled by position, not name. */
    @Test
    public void testCancelDuplicateParties()
    {
        restaurant.addReservation("Smith", 2, 1);
        restaurant.addReservation("Smith", 2, 2);
        Party[] before = restaurant.getWaitlist();
        assertNotSame(before[0], before[1]);
        assertSame(before[1], restaurant.cancelParty(2));
        assertSame(before[0], restaurant.getWaitlist()[0]);
    }

    /** Empty waitlists and invalid cancellation positions are rejected. */
    @Test
    public void testCancelPartyBadPosition()
    {
        assertCancellationRejected(1);
        restaurant.addReservation("Existing", 2, 1);
        assertCancellationRejected(0);
        assertCancellationRejected(-1);
        assertCancellationRejected(2);
        assertCancellationRejected(Integer.MIN_VALUE);
    }

    /** The waitlist snapshot cannot be used to replace stored parties. */
    @Test
    public void testGetWaitlistSnapshot()
    {
        assertEquals(0, restaurant.getWaitlist().length);
        restaurant.addReservation("First", 2, 1);
        Party[] snapshot = restaurant.getWaitlist();
        Party first = snapshot[0];
        snapshot[0] = null;
        assertSame(first, restaurant.getWaitlist()[0]);
        restaurant.addReservation("Second", 4, 2);
        assertEquals(1, snapshot.length);
        assertEquals(2, restaurant.getWaitlist().length);
    }

    /** The menu has valid entries in stable order, in an independent array. */
    @Test
    public void testGetMenu()
    {
        MenuItem[] menu = restaurant.getMenu();
        MenuItem[] next = restaurant.getMenu();
        assertTrue(menu.length > 0);
        assertNotSame(menu, next);
        assertArrayEquals(menu, next);
        for (MenuItem item : menu)
        {
            assertNotNull(item);
            assertFalse(item.getName().trim().isEmpty());
            assertTrue(item.getPrice() >= 0);
        }
        menu[0] = null;
        assertSame(next[0], restaurant.getMenu()[0]);
    }

    /** Table access returns the live table for taking orders. */
    @Test
    public void testGetTable()
    {
        assertSame(restaurant.getTable(1), restaurant.getTable(1));
        restaurant.addParty("Pair", 2);
        Table table = restaurant.getTable(1);
        MenuItem item = restaurant.getMenu()[0];
        table.addItem(item);
        assertSame(item, restaurant.getTable(1).getOrder()[0]);
        assertEquals(item.getPrice(), restaurant.getTable(1).getTotal(), 0.001);
    }

    /** Table numbers outside 1 through 6 are rejected. */
    @Test
    public void testGetTableBadNumber()
    {
        int[] numbers = { -1, 0, 7, Integer.MIN_VALUE, Integer.MAX_VALUE };
        for (int number : numbers)
        {
            try
            {
                restaurant.getTable(number);
                fail("Expected IndexOutOfBoundsException");
            }
            catch (IndexOutOfBoundsException e)
            {
                assertTrue(restaurant.getTable(1).isOpen());
            }
        }
    }

    /** Closing returns the old party's bill and gives the next party a clean order. */
    @Test
    public void testCloseTableSeatsFirstWaitingParty()
    {
        fillTables();
        Table table = restaurant.getTable(1);
        table.addItem(new MenuItem("Burger", 12.00));
        table.addItem(new MenuItem("Soda", 2.50));
        restaurant.addParty("First", 2);
        restaurant.addParty("Second", 1);
        Party[] before = restaurant.getWaitlist();
        String bill = restaurant.closeTable(1);
        assertTrue(bill.contains("Seated 1"));
        assertTrue(bill.contains("Burger $12.00"));
        assertTrue(bill.contains("Soda $2.50"));
        assertTrue(bill.contains("Total: $14.50"));
        assertFalse(bill.contains("First"));
        assertSame(before[0], table.getParty());
        assertEquals(0, table.getOrder().length);
        assertEquals(0.0, table.getTotal(), 0.001);
        assertArrayEquals(new Party[] { before[1] }, restaurant.getWaitlist());
    }

    /** A smaller table skips large parties without reordering the waitlist. */
    @Test
    public void testCloseTableSeatsFirstPartyThatFits()
    {
        fillTables();
        restaurant.addParty("Too large", 6);
        restaurant.addParty("First fit", 2);
        restaurant.addParty("Later fit", 1);
        Party[] before = restaurant.getWaitlist();
        restaurant.closeTable(1);
        assertSame(before[1], restaurant.getTable(1).getParty());
        assertArrayEquals(new Party[] { before[0], before[2] },
            restaurant.getWaitlist());
        restaurant.closeTable(5);
        assertSame(before[0], restaurant.getTable(5).getParty());
        assertArrayEquals(new Party[] { before[2] }, restaurant.getWaitlist());
    }

    /** A matching party at the tail is removed correctly before a later arrival. */
    @Test
    public void testCloseTableSeatsLastWaitingParty()
    {
        fillTables();
        restaurant.addParty("Large", 6);
        restaurant.addParty("Pair", 2);
        Party[] before = restaurant.getWaitlist();
        restaurant.closeTable(1);
        assertSame(before[1], restaurant.getTable(1).getParty());
        assertEquals(-1, restaurant.addParty("New", 3));
        Party[] after = restaurant.getWaitlist();
        assertEquals(2, after.length);
        assertSame(before[0], after[0]);
        assertEquals("New", after[1].getName());
    }

    /** An unfittable waitlist stays intact and the table is left open. */
    @Test
    public void testCloseTableWhenNoPartyFits()
    {
        fillTables();
        restaurant.addParty("Large A", 5);
        restaurant.addParty("Large B", 6);
        Party[] before = restaurant.getWaitlist();
        restaurant.closeTable(1);
        assertTrue(restaurant.getTable(1).isOpen());
        assertEquals(0, restaurant.getTable(1).getOrder().length);
        assertArrayEquals(before, restaurant.getWaitlist());
        assertEquals(1, restaurant.addParty("Small arrival", 2));
        assertArrayEquals(before, restaurant.getWaitlist());
    }

    /** With no waitlist, closing clears the table and permits reuse. */
    @Test
    public void testCloseTableWithoutWaitlist()
    {
        restaurant.addParty("Departing", 2);
        restaurant.getTable(1).addItem(restaurant.getMenu()[0]);
        String bill = restaurant.closeTable(1);
        assertTrue(bill.contains("Departing"));
        assertTrue(restaurant.getTable(1).isOpen());
        assertNull(restaurant.getTable(1).getParty());
        assertEquals(0, restaurant.getTable(1).getOrder().length);
        assertEquals(0, restaurant.getWaitlist().length);
        assertEquals(1, restaurant.addParty("Next", 1));
    }

    /** A party with no order receives a zero bill and the table still closes. */
    @Test
    public void testCloseTableEmptyOrder()
    {
        restaurant.addParty("No order", 2);
        String bill = restaurant.closeTable(1);
        assertTrue(bill.contains("No items were ordered"));
        assertTrue(bill.contains("Total: $0.00"));
        assertTrue(restaurant.getTable(1).isOpen());
    }

    /** Closing an open table fails before moving a waiting reservation. */
    @Test
    public void testCloseOpenTable()
    {
        restaurant.addReservation("Reserved", 2, 1);
        Party[] before = restaurant.getWaitlist();
        try
        {
            restaurant.closeTable(1);
            fail("Expected IllegalStateException");
        }
        catch (IllegalStateException e)
        {
            assertTrue(restaurant.getTable(1).isOpen());
            assertArrayEquals(before, restaurant.getWaitlist());
        }
    }

    /** Invalid table numbers cannot close a valid table or change the waitlist. */
    @Test
    public void testCloseTableBadNumber()
    {
        fillTables();
        restaurant.addParty("Waiting", 2);
        Party[] before = restaurant.getWaitlist();
        int[] numbers = { 0, -1, 7 };
        for (int number : numbers)
        {
            try
            {
                restaurant.closeTable(number);
                fail("Expected IndexOutOfBoundsException");
            }
            catch (IndexOutOfBoundsException e)
            {
                assertArrayEquals(before, restaurant.getWaitlist());
                for (int i = 1; i <= Restaurant.TABLE_COUNT; i++)
                {
                    assertFalse(restaurant.getTable(i).isOpen());
                }
            }
        }
    }

    /** Seating the only waiting party leaves a reusable empty chain. */
    @Test
    public void testCloseTableEmptiesWaitlist()
    {
        fillTables();
        restaurant.addParty("Only waiting", 2);
        Party waiting = restaurant.getWaitlist()[0];
        restaurant.closeTable(1);
        assertSame(waiting, restaurant.getTable(1).getParty());
        assertEquals(0, restaurant.getWaitlist().length);
        assertEquals(-1, restaurant.addParty("New waiting", 2));
        assertEquals("New waiting", restaurant.getWaitlist()[0].getName());
    }

    /** Fills tables through the public arrival API. */
    private void fillTables()
    {
        for (int i = 1; i <= Restaurant.TABLE_COUNT; i++)
        {
            assertEquals(i, restaurant.addParty("Seated " + i, 2));
        }
    }

    /** Checks that an invalid reservation position preserves existing entries. */
    private void assertReservationPositionRejected(int position)
    {
        Party[] before = restaurant.getWaitlist();
        try
        {
            restaurant.addReservation("Invalid position", 2, position);
            fail("Expected IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e)
        {
            assertArrayEquals(before, restaurant.getWaitlist());
        }
    }

    /** Checks that an invalid cancellation preserves existing entries. */
    private void assertCancellationRejected(int position)
    {
        Party[] before = restaurant.getWaitlist();
        try
        {
            restaurant.cancelParty(position);
            fail("Expected IndexOutOfBoundsException");
        }
        catch (IndexOutOfBoundsException e)
        {
            assertArrayEquals(before, restaurant.getWaitlist());
        }
    }
}
