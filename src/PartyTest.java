import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit tests for Party, from the test plan in our spec.
 *
 * @author Rohail Amjad
 * @version October 2026
 */
public class PartyTest
{
    private Party garcia;

    /**
     * Sets up shared objects before each test.
     */
    @Before
    public void setUp()
    {
        garcia = new Party("Garcia", 4);
    }

    /**
     * Party("Garcia", 4) -> getName() is "Garcia", getSize() is 4.
     */
    @Test
    public void testConstructor()
    {
        assertEquals("Garcia", garcia.getName());
        assertEquals(4, garcia.getSize());
    }

    /**
     * Blank name, size 0, or size 7 -> throws IllegalArgumentException.
     */
    @Test
    public void testConstructorBadInput()
    {
        assertThrowsIAE(null, 2);
        assertThrowsIAE("", 2);
        assertThrowsIAE("   ", 2);
        assertThrowsIAE("Lee", 0);
        assertThrowsIAE("Lee", 7);
        assertThrowsIAE("Lee", -1);
    }

    /**
     * Party("Lee", 6) -> returns "Lee" and 6.
     */
    @Test
    public void testGetters()
    {
        Party lee = new Party("Lee", 6);
        assertEquals("Lee", lee.getName());
        assertEquals(6, lee.getSize());
    }

    /**
     * Party("Van Buren", 1) -> returns "Van Buren" and 1 unchanged.
     */
    @Test
    public void testGettersEdge()
    {
        Party p = new Party("Van Buren", 1);
        assertEquals("Van Buren", p.getName());
        assertEquals(1, p.getSize());
    }

    /**
     * Party("Garcia", 4) -> "Garcia (4)".
     */
    @Test
    public void testToString()
    {
        assertEquals("Garcia (4)", garcia.toString());
    }
    /**
     * Two separate "Smith (2)" parties don't match, because Party doesn't
     * override equals(). This is why the app cancels by waitlist position
     * instead of by name.
     */
    @Test
    public void testIdenticalPartiesAreDifferent()
    {
        LinkedChain<Party> waitlist = new LinkedChain<Party>();
        waitlist.addToEnd(new Party("Smith", 2));
        assertFalse(waitlist.contains(new Party("Smith", 2)));
    }

    private void assertThrowsIAE(String name, int size)
    {
        try
        {
            new Party(name, size);
            org.junit.Assert.fail("Expected IllegalArgumentException for "
                + name + ", " + size);
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }
}
