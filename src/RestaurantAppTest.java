import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit tests for RestaurantApp. Each test scripts the manager's input, runs
 * the app, and checks what it printed. Every script ends with 10 (Quit); if a
 * script runs out early, readInt returns its maximum, which is Quit at the main
 * menu, so no test can hang. Covers both constructors, main, and run(). The
 * private handlers are tested through run(), one bad-input case from the scope
 * at a time.
 *
 * @author Alex Chai
 * @version October 2026
 */
public class RestaurantAppTest
{
    private PrintStream originalOut;
    private InputStream originalIn;
    private ByteArrayOutputStream output;

    /**
     * Captures everything the app prints.
     */
    @Before
    public void setUp()
    {
        originalOut = System.out;
        originalIn = System.in;
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }


    /**
     * Restores the real console after each test.
     */
    @After
    public void tearDown()
    {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }


    /**
     * Runs the app on a script of input lines and returns what it printed.
     *
     * @param lines
     *            the manager's input, one line per entry
     * @return everything the app printed
     */
    private String run(String... lines)
    {
        String script = String.join("\n", lines) + "\n";
        new RestaurantApp(new Scanner(script)).run();
        return output.toString();
    }


    /**
     * Counts how many times a piece of text appears in the output.
     *
     * @param text
     *            the full output
     * @param piece
     *            the text to count
     * @return the number of occurrences
     */
    private int count(String text, String piece)
    {
        return text.split(java.util.regex.Pattern.quote(piece), -1).length - 1;
    }

    // ----- constructors and main --------------------------------------


    /**
     * The Scanner constructor reads from the given script.
     */
    @Test
    public void testScannerConstructor()
    {
        String out = run("10");
        assertTrue(out.contains("=== Restaurant Manager ==="));
        assertTrue(out.contains("Goodbye!"));
    }


    /**
     * Empty input ends the session cleanly instead of hanging or crashing.
     */
    @Test
    public void testScannerConstructorNoInput()
    {
        new RestaurantApp(new Scanner("")).run();
        assertTrue(output.toString().contains("Goodbye!"));
    }


    /**
     * The no-argument constructor reads from System.in.
     */
    @Test
    public void testDefaultConstructor()
    {
        System.setIn(new ByteArrayInputStream("1\nGarcia\n4\n10\n".getBytes()));
        new RestaurantApp().run();
        assertTrue(output.toString().contains("Garcia (4) seated at table 3."));
    }


    /**
     * main starts the app and quits normally.
     */
    @Test
    public void testMain()
    {
        System.setIn(new ByteArrayInputStream("10\n".getBytes()));
        RestaurantApp.main(new String[0]);
        assertTrue(output.toString().contains("Goodbye!"));
    }


    /**
     * main survives bad menu input before quitting.
     */
    @Test
    public void testMainBadInput()
    {
        System.setIn(new ByteArrayInputStream("abc\n10\n".getBytes()));
        RestaurantApp.main(new String[0]);
        String out = output.toString();
        assertTrue(out.contains("Please enter a number from 1 to 10."));
        assertTrue(out.contains("Goodbye!"));
    }

    // ----- run(): main menu -------------------------------------------


    /**
     * Letters, 0, and 99 at the main menu each reprompt.
     */
    @Test
    public void testRunBadMenuChoice()
    {
        String out = run("abc", "0", "99", "10");
        assertEquals(3, count(out, "Please enter a number from 1 to 10."));
        assertTrue(out.contains("Goodbye!"));
    }

    // ----- run(): adding parties --------------------------------------


    /**
     * A party is seated at the lowest-numbered table that fits.
     */
    @Test
    public void testRunAddParty()
    {
        String out = run("1", "Garcia", "4", "10");
        assertTrue(out.contains("Garcia (4) seated at table 3."));
    }


    /**
     * A bad party size (negative, too big, letters) reprompts.
     */
    @Test
    public void testRunAddPartyBadSize()
    {
        String out = run("1", "Garcia", "-3", "9", "four", "4", "10");
        assertEquals(3, count(out, "Please enter a number from 1 to 6."));
        assertTrue(out.contains("Garcia (4) seated at table 3."));
    }


    /**
     * A blank or all-spaces name reprompts.
     */
    @Test
    public void testRunAddPartyBlankName()
    {
        String out = run("1", "", "   ", "Lee", "2", "10");
        assertEquals(2, count(out, "Name cannot be blank."));
        assertTrue(out.contains("Lee (2) seated at table 1."));
    }


    /**
     * When every table is full, the next party joins the waitlist.
     */
    @Test
    public void testRunAddPartyWhenFull()
    {
        String out = run(
            "1",
            "A",
            "2",
            "1",
            "B",
            "2",
            "1",
            "C",
            "2",
            "1",
            "D",
            "2",
            "1",
            "E",
            "2",
            "1",
            "F",
            "2",
            "1",
            "G",
            "2",
            "10");
        assertTrue(out.contains("Added to the waitlist at position 1."));
    }

    // ----- run(): reservations and cancelling -------------------------


    /**
     * A reservation goes to the chosen waitlist position.
     */
    @Test
    public void testRunReservation()
    {
        String out = run("2", "Kim", "2", "1", "4", "10");
        assertTrue(
            out.contains("Kim (2) added to the waitlist at position 1."));
        assertTrue(out.contains("1. Kim (2)"));
    }


    /**
     * A reservation position outside 1 to (waitlist size + 1) reprompts.
     */
    @Test
    public void testRunReservationBadPosition()
    {
        String out = run("2", "Kim", "2", "0", "5", "1", "10");
        assertEquals(2, count(out, "Please enter a number from 1 to 1."));
        assertTrue(out.contains("added to the waitlist at position 1."));
    }


    /**
     * Cancelling by position removes that party.
     */
    @Test
    public void testRunCancel()
    {
        String out = run("2", "Kim", "2", "1", "3", "1", "4", "10");
        assertTrue(out.contains("Kim (2) was removed from the waitlist."));
        assertTrue(out.contains("The waitlist is empty."));
    }


    /**
     * Cancelling with nobody waiting prints "Party not found".
     */
    @Test
    public void testRunCancelEmptyWaitlist()
    {
        String out = run("3", "10");
        assertTrue(out.contains("Party not found"));
    }


    /**
     * An out-of-range cancel position reprompts (it does not print "Party not
     * found"; see the README reflection).
     */
    @Test
    public void testRunCancelBadPosition()
    {
        String out = run("2", "Kim", "2", "1", "3", "5", "1", "10");
        assertTrue(out.contains("Please enter a number from 1 to 1."));
        assertTrue(out.contains("Kim (2) was removed from the waitlist."));
    }

    // ----- run(): orders ----------------------------------------------


    /**
     * Taking an order adds the chosen menu item; viewing shows the total.
     */
    @Test
    public void testRunTakeAndViewOrder()
    {
        String out = run("1", "Garcia", "4", "5", "3", "1", "6", "3", "10");
        assertTrue(out.contains("Added Burger $12.00 to table 3."));
        assertTrue(out.contains("Total: $12.00"));
    }


    /**
     * A menu item number that isn't on the menu reprompts.
     */
    @Test
    public void testRunTakeOrderBadItem()
    {
        String out = run("1", "Garcia", "4", "5", "3", "0", "99", "1", "10");
        assertEquals(2, count(out, "Please enter a number from 1 to 5."));
        assertTrue(out.contains("Added Burger $12.00 to table 3."));
    }


    /**
     * Taking, viewing, or removing from an open table prints a message.
     */
    @Test
    public void testRunOrderOpenTable()
    {
        String out = run("5", "1", "6", "1", "7", "1", "10");
        assertEquals(3, count(out, "Table 1 is empty"));
        assertFalse(out.contains("Error:"));
    }


    /**
     * Removing an order line removes that item.
     */
    @Test
    public void testRunRemoveItem()
    {
        String out = run("1", "Lee", "2", "5", "1", "1", "7", "1", "1", "10");
        assertTrue(out.contains("Removed Burger $12.00 from table 1."));
    }


    /**
     * A line past the end, or an empty order, prints "Item not found".
     */
    @Test
    public void testRunRemoveItemNotThere()
    {
        String out =
            run("1", "Lee", "2", "7", "1", "5", "1", "1", "7", "1", "5", "10");
        assertEquals(2, count(out, "Item not found in this order"));
        assertFalse(out.contains("Removed"));
    }

    // ----- run(): closing tables and the floor ------------------------


    /**
     * Closing a table prints the bill and leaves it open.
     */
    @Test
    public void testRunCloseTable()
    {
        String out = run("1", "Lee", "2", "5", "1", "1", "8", "1", "10");
        assertTrue(out.contains("Total: $12.00"));
        assertTrue(out.contains("Table 1 is now open."));
    }


    /**
     * Closing a table seats the first waiting party that fits.
     */
    @Test
    public void testRunCloseTableSeatsNext()
    {
        String out = run(
            "1",
            "A",
            "2",
            "1",
            "B",
            "2",
            "1",
            "C",
            "2",
            "1",
            "D",
            "2",
            "1",
            "E",
            "2",
            "1",
            "F",
            "2",
            "1",
            "G",
            "2",
            "8",
            "1",
            "10");
        assertTrue(out.contains("G (2) was seated next at table 1."));
    }


    /**
     * Closing an open table prints a message instead of crashing.
     */
    @Test
    public void testRunCloseOpenTable()
    {
        String out = run("8", "1", "10");
        assertTrue(out.contains("Table 1 is empty"));
        assertFalse(out.contains("Error:"));
    }


    /**
     * The floor view lists every table and who is seated.
     */
    @Test
    public void testRunFloorStatus()
    {
        String out = run("1", "Lee", "2", "9", "10");
        assertTrue(out.contains("Table 1 (2 seats): Lee (2)"));
        assertTrue(out.contains("Table 6 (6 seats): open"));
    }


    /**
     * Input that runs out in the middle of an action is caught and the session
     * still ends cleanly.
     */
    @Test
    public void testRunInputRunsOut()
    {
        String out = run("1");
        assertTrue(out.contains("Error:"));
        assertTrue(out.contains("Goodbye!"));
    }
}
