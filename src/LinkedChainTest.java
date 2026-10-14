import static org.junit.Assert.*;
import student.TestCase;
import org.junit.Test;

public class LinkedChainTest extends TestCase
{

    private LinkedChain<String> chain;

    /**
     * Creates a fresh, empty chain before each test.
     */
    public void setUp()
    {
        chain = new LinkedChain<String>();
    }
    /**
     * Helper: fills the chain with the given entries, in order.
     *
     * @param entries the entries to add to the end
     */
    private void fill(String... entries)
    {
        for (String entry : entries)
        {
            chain.addToEnd(entry);
        }
    }


    /**
     * add puts each new entry at the front.
     */
    @Test
    public void testAddPutsEntriesAtFront()
    {
        chain.add("A");
        chain.add("B");
        chain.add("C");
        assertEquals("[C, B, A]", chain.toString());
        assertEquals(3, chain.size());
    }
    
    /**
     * Adding null throws and leaves the chain unchanged.
     */
    @Test
    public void testAddNullThrows()
    {
        chain.add("A");
        Exception exception = null;
        try
        {
            chain.add(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }
        assertEquals(1, chain.size());
        assertNotNull(exception);
    }
    
    /**
     * add on an empty chain also sets the tail, so addToEnd works after.
     */
    @Test
    public void testAddToEmptyThenAddToEnd()
    {
        chain.add("A");
        chain.addToEnd("B");
        assertEquals("[A, B]", chain.toString());
    }
    
    /**
     * addToEnd keeps insertion order.
     */
    @Test
    public void testAddToEndKeepsOrder()
    {
        fill("A", "B", "C");
        assertEquals("[A, B, C]", chain.toString());
        assertEquals(3, chain.size());
    }
    
    /**
     * Adding null to the end throws and leaves the chain unchanged.
     */
    @Test
    public void testAddToEndNullThrows()
    {
        Exception exception = null;
        try
        {
            chain.addToEnd(null);
            fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }
        assertTrue(chain.isEmpty());
        assertNotNull(exception);
    }
    
    /**
     * Inserting in the middle links the new entry between its neighbors.
     */
    @Test
    public void testAddAtPositionMiddle()
    {
        fill("A", "C");
        chain.add(1, "B");
        assertEquals("[A, B, C]", chain.toString());
        assertEquals(3, chain.size());
    }

    /**
     * Inserting at the front and at the end both work, and the end
     * insert updates the tail.
     */
    @Test
    public void testAddAtPositionFrontAndEnd()
    {
        chain.add(0, "B");          // empty chain, position 0
        chain.add(0, "A");          // front
        chain.add(2, "C");          // position == size: the end
        chain.addToEnd("D");        // tail must point at C
        assertEquals("[A, B, C, D]", chain.toString());
    }
    
    /**
     * A position past size() throws, and so does a negative one.
     */
    @Test
    public void testAddAtPositionOutOfBounds()
    {
        fill("A", "B");
        Exception eOne = null;
        Exception eTwo = null;
        try
        {
            chain.add(3, "X");
            
        }
        catch (IndexOutOfBoundsException e)
        {
            eOne = e;
        }
        try
        {
            chain.add(-1, "X");
            
        }
        catch (IndexOutOfBoundsException e)
        {
            eTwo = e;
        }
        assertNotNull(eTwo);
        assertNotNull(eOne);
    }
    
    /**
     * Inserting null at a valid position still throws.
     */
    @Test
    public void testAddAtPositionNullThrows()
    {
        Exception exception = null;
        try
        {
            chain.add(0, null);
            
        }
        catch (IllegalArgumentException e)
        {
            exception = e;
        }
        assertTrue(chain.isEmpty());
        assertNotNull(exception);
    }

    /**
     * remove() returns the first entry and shifts the front.
     */
    @Test
    public void testRemoveFirst()
    {
        fill("A", "B");
        assertEquals("A", chain.remove());
        assertEquals("[B]", chain.toString());
    }
    
    /**
     * remove() on an empty chain returns null.
     */
    @Test
    public void testRemoveFirstEmpty()
    {
        assertNull(chain.remove());
    }
    
    /**
     * Removing the only entry resets the tail.
     */
    @Test
    public void testRemoveOnlyEntryThenAddToEnd()
    {
        fill("A");
        chain.remove();
        chain.addToEnd("B");
        assertEquals("[B]", chain.toString());
        assertEquals(1, chain.size());
    }
    /**
     * Removing position 0 removes the first entry.
     */
    @Test
    public void testRemoveAtPositionFirst()
    {
        fill("A", "B", "C");
        assertEquals("A", chain.remove(0));
        assertEquals("[B, C]", chain.toString());
        assertEquals(2, chain.size());
    }
    /**
     * Removing from the middle returns that entry and relinks the chain.
     */
    @Test
    public void testRemoveAtPositionMiddle()
    {
        fill("A", "B", "C");
        assertEquals("B", chain.remove(1));
        assertEquals("[A, C]", chain.toString());
    }
    
    /**
     * Removing the last entry moves the tail back one node.
     */
    @Test
    public void testRemoveAtPositionLastThenAddToEnd()
    {
        fill("A", "B", "C");
        assertEquals("C", chain.remove(2));
        chain.addToEnd("D");
        assertEquals("[A, B, D]", chain.toString());
    }
    /**
     * A position equal to size() throws; nothing is removed.
     */
    @Test
    public void testRemoveAtPositionOutOfBounds()
    {
        fill("A", "B", "C");
        Exception exception = null;
        try
        {
            chain.remove(3);
            
        }
        catch (IndexOutOfBoundsException e)
        {
            exception = e;
        }
        assertEquals(3, chain.size());
        assertNotNull(exception);
    }
    
    /**
     * Removing by position from an empty chain throws.
     */
    @Test
    public void testRemoveAtPositionEmpty()
    {
        Exception exception = null;
        try
        {
            chain.remove(0);
        }
        catch (IndexOutOfBoundsException e)
        {
            exception = e;
        }
        assertTrue(chain.isEmpty());
        assertNotNull(exception);
    }
    
    /**
     * Removing the last entry by value updates the tail.
     */
    @Test
    public void testRemoveEntryLastThenAddToEnd()
    {
        fill("A", "B", "C");
        assertTrue(chain.remove("C"));
        assertEquals("[A, B]", chain.toString());
        chain.addToEnd("D");
        assertEquals("[A, B, D]", chain.toString());
    }
    /**
     * Removing null returns false and changes nothing.
     */
    @Test
    public void testRemoveEntryNull()
    {
        fill("A", "B");
        assertFalse(chain.remove((String) null));
        assertEquals("[A, B]", chain.toString());
    }
    /**
     * Removing a middle entry by value keeps the tail where it was.
     */
    @Test
    public void testRemoveEntryMiddle()
    {
        fill("A", "B", "C");
        assertTrue(chain.remove("B"));
        assertEquals("[A, C]", chain.toString());
        chain.addToEnd("D");
        assertEquals("[A, C, D]", chain.toString());
    }
    /**
     * Removing something that isn't there returns false.
     */
    @Test
    public void testRemoveEntryNotFound()
    {
        fill("A", "B");
        assertFalse(chain.remove("D"));
        assertEquals("[A, B]", chain.toString());
        chain.clear();
        assertFalse(chain.remove("A"));
    }

    /**
     * get returns the entry at a position.
     */
    @Test
    public void testGet()
    {
        fill("A", "B", "C");
        assertEquals("C", chain.get(2));
    }
    
    /**
     * get with a negative position throws.
     */
    @Test
    public void testGetOutOfBounds()
    {
        Exception exception = null;
        fill("A");
        try
        {
            chain.get(-1);
            
        }
        catch (IndexOutOfBoundsException e)
        {
            exception = e;
        }
        assertNotNull(exception);
    }
    
    /**
     * contains is true for an entry that was added.
     */
    @Test
    public void testContains()
    {
        fill("A", "B", "C");
        assertTrue(chain.contains("B"));
    }

    /**
     * contains is false for an entry that isn't there, for null,
     * and on an empty chain.
     */
    @Test
    public void testContainsNotFound()
    {
        assertFalse(chain.contains("A"));   // empty chain
        fill("A", "B");
        assertFalse(chain.contains("Z"));
        assertFalse(chain.contains(null));
    }
    /**
     * count finds every copy of the same entry.
     */
    @Test
    public void testCount()
    {
        fill("A", "B", "A");
        assertEquals(2, chain.count("A"));
    }

    /**
     * count is 0 for a missing entry, an empty chain, or null.
     */
    @Test
    public void testCountMissing()
    {
        assertEquals(0, chain.count("A"));
        fill("A");
        assertEquals(0, chain.count("Z"));
        assertEquals(0, chain.count(null));
    }
    
    /**
     * A new chain is empty; one add makes size 1.
     */
    @Test
    public void testSizeAndIsEmpty()
    {
        assertEquals(0, chain.size());
        assertTrue(chain.isEmpty());
        chain.addToEnd("A");
        assertEquals(1, chain.size());
        assertFalse(chain.isEmpty());
    }

    /**
     * Adding then removing the same entry returns to empty.
     */
    @Test
    public void testSizeAfterAddAndRemove()
    {
        chain.addToEnd("A");
        chain.remove("A");
        assertEquals(0, chain.size());
        assertTrue(chain.isEmpty());
    }
    
    /**
     * toArray copies entries in order into the caller's array.
     */
    @Test
    public void testToArray()
    {
        fill("A", "B");
        String[] result = chain.toArray(new String[chain.size()]);
        assertEquals(2, result.length);
        assertEquals("A", result[0]);
        assertEquals("B", result[1]);
    }
    
    /**
     * toArray rejects an array that is too small or null.
     */
    @Test
    public void testToArrayBadArray()
    {
        Exception eOne = null;
        Exception eTwo = null;
        fill("A", "B");
        try
        {
            chain.toArray(new String[1]);
        }
        catch (IllegalArgumentException e)
        {
            eOne = e;
        }
        try
        {
            chain.toArray(null);
            
        }
        catch (IllegalArgumentException e)
        {
            eTwo = e;
        }
        assertNotNull(eOne);
        assertNotNull(eTwo);
    }
    /**
     * clear empties the chain.
     */
    @Test
    public void testClear()
    {
        fill("A", "B");
        chain.clear();
        assertEquals(0, chain.size());
        assertTrue(chain.isEmpty());
        assertEquals("[]", chain.toString());
    }
    
    /**
     * After clear, addToEnd starts a new chain (lastNode was reset), and
     * clearing an empty chain is harmless.
     */
    @Test
    public void testClearThenAddToEnd()
    {
        fill("A", "B");
        chain.clear();
        chain.addToEnd("C");
        assertEquals("[C]", chain.toString());
        assertEquals(1, chain.size());
        chain.clear();
        chain.clear();
        assertTrue(chain.isEmpty());
    }
    
    /**
     * toString lists entries first to last.
     */
    @Test
    public void testToString()
    {
        fill("A", "B", "C");
        assertEquals("[A, B, C]", chain.toString());
    }

    /**
     * toString of an empty chain is "[]".
     */
    @Test
    public void testToStringEmpty()
    {
        assertEquals("[]", chain.toString());
    }























}
