/**
 * One table in the dining room: a fixed number and seat count, the party
 * sitting there (null when open), and that party's order. The table adds
 * up its own bill, because the generic LinkedChain cannot call getPrice().
 *
 * Positions passed in are 1-based (what the manager sees). This class
 * subtracts 1 before calling the chain.
 *
 * Invariants: party == null exactly when the table is open; an open table
 * always has an empty order; a seated party's size is <= seats.
 *
 * Owner: Rohail Amjad
 *
 * @author Rohail Amjad
 * @version October 2026
 */
public class Table
{
    private final int number;
    private final int seats;
    private Party party;                    // null when open
    private final LinkedChain<MenuItem> order;

    /**
     * Creates an open table with an empty order.
     *
     * @param number the table number, from 1 to 6
     * @param seats the seat count (2, 4, or 6)
     * @throws IllegalArgumentException if number or seats is less than 1
     */
    public Table(int number, int seats)
    {
        if (number < 1)
        {
            throw new IllegalArgumentException("Table number must be at least 1");
        }
        if (seats < 1)
        {
            throw new IllegalArgumentException("Seat count must be at least 1");
        }
        this.number = number;
        this.seats = seats;
        this.party = null;
        this.order = new LinkedChain<MenuItem>();
    }

    /**
     * @return the table number
     */
    public int getNumber()
    {
        return number;
    }

    /**
     * @return the number of seats
     */
    public int getSeats()
    {
        return seats;
    }

    /**
     * @return the seated party, or null if the table is open
     */
    public Party getParty()
    {
        return party;
    }

    /**
     * @param p the party to check
     * @return true if p's size is no more than this table's seats
     */
    public boolean fits(Party p)
    {
        return p != null && p.getSize() <= seats;
    }

    /**
     * @return true if no party is seated
     */
    public boolean isOpen()
    {
        return party == null;
    }

    /**
     * Seats a party at this open table. O(1).
     *
     * @param newParty the party to seat
     * @throws IllegalStateException if a party is already seated
     * @throws IllegalArgumentException if newParty is null or too big
     *         for this table
     */
    public void seat(Party newParty)
    {
        if (party != null)
        {
            throw new IllegalStateException(
                "Table " + number + " is already occupied");
        }
        if (!fits(newParty))
        {
            throw new IllegalArgumentException(
                "Party does not fit at table " + number);
        }
        party = newParty;
    }

    /**
     * Adds an item to the end of the order with addToEnd. O(1).
     *
     * @param item the menu item ordered
     * @throws IllegalStateException if the table is open
     * @throws IllegalArgumentException if item is null
     */
    public void addItem(MenuItem item)
    {
        if (party == null)
        {
            throw new IllegalStateException("Table " + number + " is empty");
        }
        order.addToEnd(item); // chain rejects null
    }

    /**
     * Removes and returns the item on a given order line. O(n).
     *
     * @param position the 1-based line number in the order
     * @return the removed item
     * @throws IndexOutOfBoundsException if position is not a line in the
     *         order (including when the order is empty)
     * @throws IllegalStateException if the table is open
     */
    public MenuItem removeItem(int position)
    {
        if (party == null) {
            throw new IllegalStateException("Table " + number + " is empty");
        }
            
        return order.remove(position - 1);
    }

    /**
     * @return the order's items in the order they were added, as a new
     *         array (empty if nothing was ordered). O(n).
     */
    public MenuItem[] getOrder()
    {
        return order.toArray(new MenuItem[order.size()]);
    }

    /**
     * @return the sum of every item's price, or 0.0 for an empty order.
     *         O(n), using one toArray pass.
     */
    public double getTotal()
    {
        double total = 0.0;
        for (MenuItem item : getOrder())
        {
            total += item.getPrice();
        }
        return total;
    }

    /**
     * Builds the itemized bill, then clears the party and the order
     * (order.clear()). An empty order gives a $0.00 bill with the note
     * "No items were ordered". O(n).
     *
     * @return the bill text
     * @throws IllegalStateException if the table is already open
     */
    public String close()
    {
        if (party == null)
        {
            throw new IllegalStateException(
                "Table " + number + " is already open");
        }
        MenuItem[] items = getOrder();
        StringBuilder bill = new StringBuilder();
        bill.append("Table ").append(number).append(" - ")
            .append(party).append("\n");
        if (items.length == 0)
        {
            bill.append("No items were ordered\n");
        }
        else
        {
            for (int i = 0; i < items.length; i++)
            {
                bill.append(i + 1).append(". ").append(items[i]).append("\n");
            }
        }
        bill.append("Total: $").append(String.format("%.2f", getTotal()));

        party = null;
        order.clear();
        return bill.toString();
    }
}
