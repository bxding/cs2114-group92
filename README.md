# README

## Attribution

| File | Team member(s) | Contribution (how) |
| --- | --- | --- |
| `LinkedChain.java` | Rahul Rajendran | Revised the given LinkedChain class with AI assistance. The most notable change was adding a `lastNode` reference as a tail.Specifically, I asked AI to draft code after explaining the functionality we wanted (e.g., “We want to easily add items at the end of the chain; how should we adjust the given methods as a result of adding the last node?”). |
| `LinkedChainTest.java` | Rahul Rajendran | Drafted using GenAI based on the initial tests from our spec’s test plan, then hand-checked to ensure complete code coverage and that additional edge cases were handled. Manually updated the exception tests.Used AI by prompting for the specific cases I wanted after pasing through the spec doc. |
| `Party.java`, `MenuItem.java` | Rohail Amjad | Wrote both classes from the spec’s signature tables with AI assistance (validation, getters, `toString`), then reviewed each method against the spec. |
| `Table.java` | Rohail Amjad | Wrote with AI assistance from the spec and class design: seating, order handling, and the itemized bill in `close()`. Checked each method’s behavior and Big-O against the spec by hand. |
| `PartyTest.java`, `MenuItemTest.java`, `TableTest.java` | Rohail Amjad | Drafted with AI assistance from the spec’s test plan, then added extra edge cases (too-big party, `fits`, empty-order bill). |
| `Restaurant.java` | Brian Ding | Implemented with AI assistance from the spec: fixed tables and menu, size-aware seating, waitlist reservations and cancellations, and closing tables with automatic seating of the first waiting party that fits. |
| `RestaurantTest.java` | Brian Ding | Drafted with AI assistance from the spec’s test plan, including invalid inputs, duplicate party names, reservation positions, skipped parties, clean orders after seating, and independent array snapshots. |
| `RestaurantApp.java` | Alexander Chai | Written with AI assistance to implement the spec, came up with the various menu options for the user and how it would be communicated properly to the user.   |
| `RestaurantAppTest.java` | Alexander Chai | Drafted with AI assistance to test the various inputs that the user could type into the console such as invalid names and numbers, blank input, or non-existent tables or parties. |

## Reflection

### What changed from the specification?

- `LinkedChain`’s `add`, `addToEnd`, and `add(int, T)` return `boolean` (always `true`) to match the provided code; the spec said `void`.
- `LinkedChainTest` uses `student.TestCase` to match our lab setup, and its `contains` tests use `String` entries. `String` overrides `equals()`, so the spec’s “two identical parties don’t match” case moved to `PartyTest`.
- Coverage showed untested branches, so we added tests the spec didn’t list: `remove(0)` on a non-empty chain, `remove(T)` with `null`, and removing a middle entry by value.
- `Party` and `MenuItem` match the spec’s signature tables, with one addition: a `toString()` on each (`"Garcia (4)"`, `"Burger $12.00"`) so the app can print them. Both trim the name before storing it.
- `Table` gained `getNumber()`, `getSeats()`, `getParty()`, and `fits(Party)`, which the spec didn’t list. `Restaurant` needs them to find the lowest-numbered open table that fits a party, report who is seated, and seat the first waiting party that fits when a table closes.
- The `Table` constructor also rejects a table number below 1, not just a seat count below 1. `seat()` throws `IllegalArgumentException` for a null party or one larger than the table, so a table can never hold a party bigger than its seats. It still throws `IllegalStateException` if the table is occupied.
- `Restaurant` stores its menu as a `LinkedChain<MenuItem>`, following the spec’s Data & State section and generic contract. `getMenu()` returns a new `MenuItem[]` so callers cannot replace stored entries. The spec does not choose dishes, so the initial menu is Burger ($12.00), Salad ($8.00), Pasta ($15.00), Soda ($2.50), and Water ($0.00). Copying this fixed-size menu is O(1) relative to the waitlist and orders (O(m) in menu length).
- `Restaurant.getTable()` throws `IndexOutOfBoundsException` for numbers outside 1 through 6, following the spec’s validation table and its other position-based operations. The test plan’s `IllegalArgumentException` entry conflicts with that validation table.
- `addReservation()` inserts at the requested waitlist position even when a table is open, following its explicit method contract. This is an exception to the spec’s broader statement that no open table can fit a waiting party. Arrivals use `addParty()` for immediate seating; reservations remain on the waitlist until cancellation or seating when a table closes.
- `closeTable()` is O(o + w), where o is the order length and w is the waitlist length. The spec’s complexity table omitted scanning the waitlist. A single array snapshot and at most one positional removal keep that work linear.
- `RestaurantApp` takes a `Scanner` in its constructor and is used through user input into the console. We tested this class by using Junit tests instead of hand testing it like we originally planned because it is easier to keep track of and show our testing this way instead of typing all the cases into the console or using a script as well as adding an additional method for displaying all tables.

### What changed in the LinkedChain?

- Added a `lastNode` tail reference so `addToEnd` is O(1). Every new party joins the end of the waitlist and every new item joins the end of an order, so this is our most-used add.
- Added `add(int, T)` (O(n)) for reservations inserted at a chosen waitlist position.
- Added `remove(int)` (O(n)) for cancelling a party, removing an order line, and seating the first waiting party that fits a table.
- Updated `add`, `remove()`, `remove(T)`, and `clear` to keep `lastNode` correct, and added private helpers `nodeAt` and `checkIndex`.
- A failing test (`testRemoveEntryLastThenAddToEnd`) showed that the provided `remove(T)` left `lastNode` pointing at a removed node, so the next `addToEnd` was lost. We fixed it.

### What happened to our unknowns?

- **Tail reference:** Figured out. We added it; the hard part was keeping it correct in every method that removes, which testing confirmed.
- **Cancelling the right party when two look identical:** Figured out. We cancel by waitlist position, because `Party` doesn’t override `equals()`.
- **Open table doesn’t fit the next party:** Figured out. `closeTable()` seats the first waiting party that fits and preserves the order of everyone else; if none fits, the table stays open. Both cases have passing tests in `RestaurantTest`.

### What doesn’t work yet?

- `LinkedChain` and `LinkedChainTest` are complete, and all tests pass.
- **Rohail (`Party`, `MenuItem`, `Table`):**
    1. MVP features not finished: none. The stretch goal (special requests in `Table`) is not built yet.
    2. Bad-input cases that still crash: none. Blank names, bad sizes, negative prices, and invalid seat counts throw the documented exceptions.
    3. Tests failing or marked TODO: none. `PartyTest`, `MenuItemTest`, and `TableTest` are fully written and pass.
- **Brian (`Restaurant`):** Complete. All assigned MVP methods and `RestaurantTest` are implemented. Invalid names, sizes, waitlist positions, table numbers, and attempts to close open tables throw the documented exceptions for the app to handle. All 26 `RestaurantTest` tests pass, along with all existing tests in `LinkedChainTest`, `PartyTest`, `MenuItemTest`, and `TableTest` (90 tests total).
- **Alex(`RestaurantApp`):** All MVP methods were finished successfully and work as expected. All bad input is handled and is proven to be handled through our testing.`RestaurantAppTest` passes all the tests and are fully written to test all cases.

## Next Steps

### What is left to finish the MVP?

> **TODO (whole group):** After filling in “What doesn’t work yet,” copy each unfinished item here in the order you’ll do it. Classes the others depend on go first (`Table`, then `Restaurant`, then `RestaurantApp`), then an end-to-end run of the MVP path, then a final check of every bad-input case.
> 

### Which stretch goals will we attempt?

- **Committed:** Special requests, stored as a `LinkedChain<String>` in each `Table`. This adds a third element type to the same chain class, with no changes to `LinkedChain`.
    - Rohail: Add special requests to `Table` as a `LinkedChain<String>`, with `addRequest(String)` and `getRequests()`, then write the tests listed under “What new tests will we need?”
- **Dropped:** Estimated wait times, bill splitting, end-of-shift report, kitchen simulation, allergy flags, and save/load menu. Each needs new logic we can’t finish and test in one week.
- **TODO (whole group):** Confirm or change these choices together before submitting.

### What new tests will we need?

- `Table`: adding a request to a seated table; a blank request is rejected; a request at an open table throws; requests are cleared when the table closes.
- `RestaurantApp`: the new “add request” menu option, including a blank entry and an open table.
- **TODO (whole group):** Add tests for anything listed under “What is left to finish the MVP.”

### Who does what?

- **Rahul:** Integration, plus reviewing everyone’s tests against the spec’s test plan.
- **Rohail:** Special requests in `Table`, plus its tests.
- **Brian:** `Restaurant` and its tests are complete; support integration with `RestaurantApp`.
- **Alex:** The “add request” menu option in `RestaurantApp`, plus its tests.
- **TODO (whole group):** Confirm the owners. Every MVP item and stretch goal above needs a name.
