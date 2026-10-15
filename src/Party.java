/**
 * One group of guests: a name and a size. Fields are final and there are
 * no setters, so a Party cannot change while it is stored in a chain.
 * Party does not override equals(), so two separate "Smith (2)" parties
 * are different entries.
 *
 * Owner: Rohail Amjad
 *
 * @author Rohail Amjad
 * @version October 2026
 */
public class Party
{
    private final String name;
    private final int size;

    /**
     * Creates a party.
     *
     * @param name the party's name; must not be blank
     * @param size the number of guests, from 1 to 6
     * @throws IllegalArgumentException if name is null or blank, or size
     *         is outside 1 to 6
     */
    public Party(String name, int size)
    {
        if (name == null || name.trim().isEmpty())
        {
            throw new IllegalArgumentException("Party name must not be blank");
        }
        if (size < 1 || size > Restaurant.MAX_PARTY_SIZE)
        {
            throw new IllegalArgumentException(
                "Party size must be from 1 to " + Restaurant.MAX_PARTY_SIZE);
        }
        this.name = name.trim();
        this.size = size;
    }

    /**
     * @return the party's name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the number of guests
     */
    public int getSize()
    {
        return size;
    }

    /**
     * @return the party as text, e.g. "Garcia (4)"
     */
    @Override
    public String toString()
    {
        return name + " (" + size + ")";
    }
}
