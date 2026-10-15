/**
 * One dish or drink on the menu: a name and a price. Fields are final and
 * there are no setters. MenuItem does not override equals().
 *
 * Owner: Rohail Amjad
 *
 * @author Rohail Amjad
 * @version October 2026
 */
public class MenuItem
{
    private final String name;
    private final double price;

    /**
     * Creates a menu item.
     *
     * @param name the item's name; must not be blank
     * @param price the price in dollars; must be 0 or more
     * @throws IllegalArgumentException if name is null or blank, or price
     *         is negative
     */
    public MenuItem(String name, double price)
    {
        if (name == null || name.trim().isEmpty())
        {
            throw new IllegalArgumentException("Item name must not be blank");
        }
        if (price < 0)
        {
            throw new IllegalArgumentException("Price must not be negative");
        }
        this.name = name.trim();
        this.price = price;
    }

    /**
     * @return the item's name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the price in dollars
     */
    public double getPrice()
    {
        return price;
    }

    /**
     * @return the item as text, e.g. "Burger $12.00"
     */
    @Override
    public String toString()
    {
        return name + " $" + String.format("%.2f", price);
    }
}
