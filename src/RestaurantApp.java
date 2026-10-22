import java.util.Scanner;

/**
 * The only class that reads input or prints. Shows the main menu, reads and
 * validates input (reprompting on bad input), calls one Restaurant method per
 * action, and prints the result or a caught error message.
 *
 * @author Alex Chai
 * @version October 2026
 */
public class RestaurantApp
{
    private final Restaurant restaurant;
    private final Scanner in;
    /** The main menu number for Quit. */
    private static final int QUIT = 11;

    /**
     * Creates an app that reads from the keyboard.
     */
    public RestaurantApp()
    {
        this(new Scanner(System.in));
    }


    /**
     * Creates an app that reads from the given Scanner (used by tests).
     *
     * @param input
     *            where to read the manager's input from
     */
    public RestaurantApp(Scanner input)
    {
        restaurant = new Restaurant();
        in = input;
    }


    /**
     * Starts the program.
     *
     * @param args
     *            not used
     */
    public static void main(String[] args)
    {
        new RestaurantApp().run();
    }


    /**
     * Shows the main menu, reads a choice with readInt, and calls the matching
     * handler until the manager picks Quit.
     */
    public void run()
    {
        int choice = 0;
        while (choice != QUIT)
        {
            printMainMenu();
            choice = readInt("Choice", 1, QUIT);
            if (choice != QUIT)
            {
                try
                {
                    handleChoice(choice);
                }
                catch (IllegalArgumentException | IllegalStateException
                    | IndexOutOfBoundsException e)
                {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
        System.out.println("Goodbye!");
    }


    /**
     * Calls the handler that matches a main menu choice.
     *
     * @param choice
     *            a number from 1 to 10 (11 is Quit and is handled by run)
     */
    private void handleChoice(int choice)
    {
        switch (choice)
        {
            case 1:
                handleAddParty();
                break;
            case 2:
                handleReservation();
                break;
            case 3:
                handleCancel();
                break;
            case 4:
                handleViewWaitlist();
                break;
            case 5:
                handleTakeOrder();
                break;
            case 6:
                handleViewOrder();
                break;
            case 7:
                handleRemoveItem();
                break;
            case 8:
                handleCloseTable();
                break;
            case 9:
                handleFloorStatus();
                break;
            case 10:
                handleAddRequest();
            default:
                break;
        }
    }


    /** Prints the main menu. */
    private void printMainMenu()
    {
        System.out.println();
        System.out.println("=== Restaurant Manager ===");
        System.out.println("1. Add a party");
        System.out.println("2. Add a reservation to the waitlist");
        System.out.println("3. Cancel a waiting party");
        System.out.println("4. View the waitlist");
        System.out.println("5. Take an order");
        System.out.println("6. View a table's order");
        System.out.println("7. Remove an item from an order");
        System.out.println("8. Close a table");
        System.out.println("10. Add a special request");
        System.out.println("11. Quit");
    }


    /**
     * Reads a whole line and reprompts until it is a whole number from min to
     * max. Reading whole lines avoids the nextInt() leftover-newline bug.
     *
     * @param prompt
     *            the text to show
     * @param min
     *            the smallest allowed value
     * @param max
     *            the largest allowed value
     * @return the number the manager typed
     */
    private int readInt(String prompt, int min, int max)
    {
        while (true)
        {
            System.out.print(prompt + ": ");
            if (!in.hasNextLine())
            {
                return max;
            }
            String line = in.nextLine().trim();
            try
            {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max)
                {
                    return value;
                }
            }
            catch (NumberFormatException e)
            {

            }
            if (max == Integer.MAX_VALUE)
            {
                System.out
                    .println("Please enter a number of " + min + " or more.");
            }
            else
            {
                System.out.println(
                    "Please enter a number from " + min + " to " + max + ".");
            }
        }
    }


    /**
     * Reprompts until the line is not blank.
     *
     * @param prompt
     *            the text to show
     * @return the trimmed line
     */
    private String readNonBlank(String prompt)
    {
        while (true)
        {
            System.out.print(prompt + ": ");
            if (!in.hasNextLine())
            {
                return "";
            }
            String line = in.nextLine().trim();
            if (!line.isEmpty())
            {
                return line;
            }
            System.out.println(prompt + " cannot be blank.");
        }
    }


    /** Reads name and size, calls addParty, prints the result. */
    private void handleAddParty()
    {
        String name = readNonBlank("Party name");
        int size = readInt("Party size (1-6)", 1, 6);
        int table = restaurant.addParty(name, size);
        if (table == -1)
        {
            System.out.println(
                "No open table fits " + name + " (" + size
                    + "). Added to the waitlist at position "
                    + restaurant.getWaitlist().length + ".");
        }
        else
        {
            System.out.println(
                name + " (" + size + ") seated at table " + table + ".");
        }
    }


    /** Reads name, size, and position, then calls addReservation. */
    private void handleReservation()
    {
        String name = readNonBlank("Party name");
        int size = readInt("Party size (1-6)", 1, 6);
        int last = restaurant.getWaitlist().length + 1;
        int position = readInt("Waitlist position (1-" + last + ")", 1, last);
        restaurant.addReservation(name, size, position);
        System.out.println(
            name + " (" + size + ") added to the waitlist at position "
                + position + ".");
    }


    /**
     * Shows the numbered waitlist, reads a position, calls cancelParty; prints
     * "Party not found" if the waitlist is empty.
     */
    private void handleCancel()
    {
        Party[] waiting = restaurant.getWaitlist();
        if (waiting.length == 0)
        {
            System.out.println("Party not found");
            return;
        }
        printWaitlist(waiting);
        int position = readInt("Position to cancel", 1, waiting.length);
        Party removed = restaurant.cancelParty(position);
        System.out.println(
            removed.getName() + " (" + removed.getSize()
                + ") was removed from the waitlist.");
    }


    /** Prints each waiting party with its position, starting at 1. */
    private void handleViewWaitlist()
    {
        Party[] waiting = restaurant.getWaitlist();
        if (waiting.length == 0)
        {
            System.out.println("The waitlist is empty.");
            return;
        }
        printWaitlist(waiting);
    }


    /**
     * Reads a table number, prints the menu, reads an item number, and adds
     * that item. Prints "Table n is empty" if no party is seated.
     */
    private void handleTakeOrder()
    {
        int number = readInt("Table (1-6)", 1, 6);
        Table table = restaurant.getTable(number);
        if (table.isOpen())
        {
            System.out.println("Table " + number + " is empty");
            return;
        }
        MenuItem[] menu = restaurant.getMenu();
        for (int i = 0; i < menu.length; i++)
        {
            System.out.println((i + 1) + ". " + menu[i]);
        }
        int choice = readInt("Item (1-" + menu.length + ")", 1, menu.length);
        MenuItem item = menu[choice - 1];
        table.addItem(item);
        System.out.println("Added " + item + " to table " + number + ".");
    }


    /** Prints a table's numbered order lines and running total. */
    private void handleViewOrder()
    {
        int number = readInt("Table (1-6)", 1, 6);
        Table table = restaurant.getTable(number);
        if (table.isOpen())
        {
            System.out.println("Table " + number + " is empty");
            return;
        }
        System.out.println("Table " + number + " - " + table.getParty());
        printOrder(table);
        System.out
            .println("Total: $" + String.format("%.2f", table.getTotal()));
    }


    /**
     * Shows the table's numbered order, reads a line number, removes it; prints
     * "Item not found in this order" if out of range.
     */
    private void handleRemoveItem()
    {
        int number = readInt("Table (1-6)", 1, 6);
        Table table = restaurant.getTable(number);
        if (table.isOpen())
        {
            System.out.println("Table " + number + " is empty");
            return;
        }
        if (table.getOrder().length == 0)
        {
            System.out.println("Item not found in this order");
            return;
        }
        printOrder(table);
        int line = readInt("Line number to remove", 1, Integer.MAX_VALUE);
        if (line > table.getOrder().length)
        {
            System.out.println("Item not found in this order");
            return;
        }
        MenuItem removed = table.removeItem(line);
        System.out
            .println("Removed " + removed + " from table " + number + ".");
    }


    /** Reads a table number, calls closeTable, prints the bill. */
    private void handleCloseTable()
    {
        int number = readInt("Table (1-6)", 1, 6);
        Table table = restaurant.getTable(number);
        if (table.isOpen())
        {
            System.out.println("Table " + number + " is empty");
            return;
        }
        String bill = restaurant.closeTable(number);
        System.out.println(bill);
        Party next = table.getParty();
        if (next == null)
        {
            System.out.println("Table " + number + " is now open.");
        }
        else
        {
            System.out
                .println(next + " was seated next at table " + number + ".");
        }
    }


    /** Prints every table, whether it is open, and who is seated. */
    private void handleFloorStatus()
    {
        for (int number = 1; number <= 6; number++)
        {
            Table table = restaurant.getTable(number);
            String status =
                table.isOpen() ? "open" : table.getParty().toString();
            System.out.println(
                "Table " + number + " (" + table.getSeats() + " seats): "
                    + status);
        }
    }
    
    /**
     * Reads a table number and a special request (e.g. "no onions"), then
     * adds it to that table. Prints "Table n is empty" if no party is seated.
     */
    private void handleAddRequest()
    {
        int number = readInt("Table (1-6)", 1, 6);
        Table table = restaurant.getTable(number);
        if (table.isOpen())
        {
            System.out.println("Table " + number + " is empty");
            return;
        }
        String request = readNonBlank("Request");
        table.addRequest(request);
        System.out.println(
            "Added request \"" + request + "\" to table " + number + ".");
    }


    /**
     * Prints a numbered waitlist, starting at 1.
     *
     * @param waiting
     *            the waiting parties, in order
     */
    private void printWaitlist(Party[] waiting)
    {
        for (int i = 0; i < waiting.length; i++)
        {
            System.out.println((i + 1) + ". " + waiting[i]);
        }
    }


    /**
     * Prints a table's order as numbered lines, starting at 1.
     *
     * @param table
     *            the table whose order to print
     */
    private void printOrder(Table table)
    {
        MenuItem[] order = table.getOrder();
        if (order.length == 0)
        {
            System.out.println("No items were ordered");
            return;
        }
        for (int i = 0; i < order.length; i++)
        {
            System.out.println((i + 1) + ". " + order[i]);
        }
    }
}
