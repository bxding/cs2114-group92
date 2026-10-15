/**
 * Manages six tables, the waitlist, and the restaurant's fixed menu.
 * Arrivals use the lowest-numbered open table that fits. When a table
 * closes, the first waiting party that fits is seated there.
 *
 * Table numbers and waitlist positions are 1-based. Only this class
 * converts them to array or chain indices.
 *
 * Owner: Brian Ding
 *
 * @author Brian Ding
 * @version October 2026
 */
public class Restaurant
{
    /** The number of tables in the dining room. */
    public static final int TABLE_COUNT = 6;

    /** The largest party that any table can seat. */
    public static final int MAX_PARTY_SIZE = 6;

    private final Table[] tables;
    private final LinkedChain<Party> waitlist;
    private final LinkedChain<MenuItem> menu;

    /**
     * Creates open tables with 2, 2, 4, 4, 6, and 6 seats, an empty
     * waitlist, and a menu. The spec leaves the dishes unspecified.
     */
    public Restaurant()
    {
        int[] seats = { 2, 2, 4, 4, 6, 6 };
        tables = new Table[TABLE_COUNT];
        for (int i = 0; i < tables.length; i++)
        {
            tables[i] = new Table(i + 1, seats[i]);
        }
        waitlist = new LinkedChain<Party>();
        menu = new LinkedChain<MenuItem>();
        menu.addToEnd(new MenuItem("Burger", 12.00));
        menu.addToEnd(new MenuItem("Salad", 8.00));
        menu.addToEnd(new MenuItem("Pasta", 15.00));
        menu.addToEnd(new MenuItem("Soda", 2.50));
        menu.addToEnd(new MenuItem("Water", 0.00));
    }

    /**
     * Seats an arriving party at the lowest-numbered open table that
     * fits, or appends the party to the waitlist. O(1), since there are
     * always six tables and addToEnd uses the chain's tail reference.
     *
     * @param name the party's nonblank name
     * @param size the number of guests, from 1 to MAX_PARTY_SIZE
     * @return the table number, or -1 if the party must wait
     * @throws IllegalArgumentException if the name or size is invalid
     */
    public int addParty(String name, int size)
    {
        Party party = new Party(name, size);
        for (Table table : tables)
        {
            if (table.isOpen() && table.fits(party))
            {
                table.seat(party);
                return table.getNumber();
            }
        }
        waitlist.addToEnd(party);
        return -1;
    }

    /**
     * Inserts a reservation at the requested waitlist position. As in
     * the spec's method contract, a reservation joins the waitlist even
     * if a table is open; addParty handles immediate seating. O(n).
     *
     * @param name the party's nonblank name
     * @param size the number of guests, from 1 to MAX_PARTY_SIZE
     * @param position the 1-based position, from 1 to waitlist size + 1
     * @throws IllegalArgumentException if the name or size is invalid
     * @throws IndexOutOfBoundsException if position is outside the range
     */
    public void addReservation(String name, int size, int position)
    {
        Party party = new Party(name, size);
        waitlist.add(position - 1, party);
    }

    /**
     * Cancels the party at a waitlist position, so parties with identical
     * names and sizes can still be distinguished. O(n).
     *
     * @param position the 1-based waitlist position
     * @return the removed party
     * @throws IndexOutOfBoundsException if position is not on the waitlist
     */
    public Party cancelParty(int position)
    {
        return waitlist.remove(position - 1);
    }

    /**
     * @return a new array of waiting parties in order, empty if no one
     *         is waiting. O(n).
     */
    public Party[] getWaitlist()
    {
        return waitlist.toArray(new Party[waitlist.size()]);
    }

    /**
     * @return a new array of menu items in display order. The fixed-size
     *         menu makes this O(1) with respect to the waitlist and orders.
     */
    public MenuItem[] getMenu()
    {
        return menu.toArray(new MenuItem[menu.size()]);
    }

    /**
     * @param tableNumber the 1-based table number, from 1 to TABLE_COUNT
     * @return the table, for taking and viewing orders. O(1).
     * @throws IndexOutOfBoundsException if the table number is invalid
     */
    public Table getTable(int tableNumber)
    {
        if (tableNumber < 1 || tableNumber > TABLE_COUNT)
        {
            throw new IndexOutOfBoundsException(
                "Table number must be from 1 to " + TABLE_COUNT);
        }
        return tables[tableNumber - 1];
    }

    /**
     * Returns the departing party's bill and seats the first waiting
     * party that fits this table. If none fits, the table stays open and
     * the waitlist is unchanged. A snapshot allows one O(w) scan rather
     * than repeated O(n) chain lookups. Total time is O(o + w), where o
     * is the order length and w is the waitlist length.
     *
     * @param tableNumber the 1-based table number
     * @return the departing party's itemized bill
     * @throws IndexOutOfBoundsException if the table number is invalid
     * @throws IllegalStateException if the table is already open
     */
    public String closeTable(int tableNumber)
    {
        Table table = getTable(tableNumber);
        String bill = table.close();
        Party[] waiting = getWaitlist();
        for (int i = 0; i < waiting.length; i++)
        {
            if (table.fits(waiting[i]))
            {
                table.seat(cancelParty(i + 1));
                break;
            }
        }
        return bill;
    }
}
