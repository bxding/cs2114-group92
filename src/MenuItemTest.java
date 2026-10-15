import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

/**
 * JUnit tests for MenuItem, from the test plan in our spec.
 *
 * @author Rohail Amjad
 * @version October 2026
 */
public class MenuItemTest
{
    private static final double DELTA = 0.001;
    private MenuItem burger;

    /**
     * Sets up shared objects before each test.
     */
    @Before
    public void setUp()
    {
        burger = new MenuItem("Burger", 12.00);
    }

    /**
     * MenuItem("Soda", 2.50) -> getPrice() is 2.5.
     */
    @Test
    public void testConstructor()
    {
        MenuItem soda = new MenuItem("Soda", 2.50);
        assertEquals("Soda", soda.getName());
        assertEquals(2.5, soda.getPrice(), DELTA);
    }

    /**
     * Blank name or negative price -> throws IllegalArgumentException.
     */
    @Test
    public void testConstructorBadInput()
    {
        assertThrowsIAE(null, 1.0);
        assertThrowsIAE("", 1.0);
        assertThrowsIAE("  ", 1.0);
        assertThrowsIAE("Soda", -0.01);
    }

    /**
     * MenuItem("Burger", 12.00) -> returns "Burger" and 12.0.
     */
    @Test
    public void testGetters()
    {
        assertEquals("Burger", burger.getName());
        assertEquals(12.0, burger.getPrice(), DELTA);
    }

    /**
     * Item priced 0.00 -> getPrice() returns 0.0.
     */
    @Test
    public void testGettersFreeItem()
    {
        MenuItem water = new MenuItem("Water", 0.00);
        assertEquals(0.0, water.getPrice(), DELTA);
    }

    /**
     * MenuItem("Burger", 12.00) -> "Burger $12.00".
     */
    @Test
    public void testToString()
    {
        assertEquals("Burger $12.00", burger.toString());
        assertEquals("Soda $2.50", new MenuItem("Soda", 2.5).toString());
    }

    private void assertThrowsIAE(String name, double price)
    {
        try
        {
            new MenuItem(name, price);
            fail("Expected IllegalArgumentException for " + name + ", " + price);
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }
}
