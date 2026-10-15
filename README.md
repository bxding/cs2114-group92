## Attribution

| File | Team member(s) | Contribution (how) |
|---|---|---|
| `LinkedChain.java` | Rahul Rajendran | Revised the given LinkedChain class with AI assistance. The most notable change was adding a `lastNode` reference as a tail.<br>Specifically, I asked AI to draft code after explaining the functionality we wanted (e.g., "We want to easily add items at the end of the chain; how should we adjust the given methods as a result of adding the last node?"). |
| `LinkedChainTest.java` | Rahul Rajendran | Drafted using GenAI based on the initial tests from our spec's test plan, then hand-checked to ensure complete code coverage and that additional edge cases were handled. Manually updated the exception tests.Used AI by prompting for the specific cases I wanted after pasing through the spec doc. |

## Reflection

### What changed from the specification?
- `LinkedChain`'s `add`, `addToEnd`, and `add(int, T)` return `boolean` (always `true`) to match the provided code; the spec said `void`.
- `LinkedChainTest` uses `student.TestCase` to match our lab setup, and its `contains` tests use `String` entries. `String` overrides `equals()`, so the spec's "two identical parties don't match" case moved to `PartyTest`.
- Coverage showed untested branches, so we added tests the spec didn't list: `remove(0)` on a non-empty chain, `remove(T)` with `null`, and removing a middle entry by value.
- > **TODO (Rohail):** List any `Party`, `MenuItem`, or `Table` method you added, removed, or renamed compared to the spec's signature tables, and why. (For example: did `Table` need `getNumber`, `getSeats`, `getParty`, or `fits`?)
- > **TODO (Brian):** State what you built for the two spec contradictions: is the menu a `LinkedChain<MenuItem>` or a `MenuItem[]`, and does `getTable` throw `IllegalArgumentException` or `IndexOutOfBoundsException`? Say why.
- > **TODO (Alex):** Say whether `RestaurantApp` takes a `Scanner` in its constructor, and whether it is tested with JUnit instead of the manual script the spec planned. Say why.

### What changed in the LinkedChain?
- Added a `lastNode` tail reference so `addToEnd` is O(1). Every new party joins the end of the waitlist and every new item joins the end of an order, so this is our most-used add.
- Added `add(int, T)` (O(n)) for reservations inserted at a chosen waitlist position.
- Added `remove(int)` (O(n)) for cancelling a party, removing an order line, and seating the first waiting party that fits a table.
- Updated `add`, `remove()`, `remove(T)`, and `clear` to keep `lastNode` correct, and added private helpers `nodeAt` and `checkIndex`.
- A failing test (`testRemoveEntryLastThenAddToEnd`) showed that the provided `remove(T)` left `lastNode` pointing at a removed node, so the next `addToEnd` was lost. We fixed it.

### What happened to our unknowns?
- **Tail reference:** Figured out. We added it; the hard part was keeping it correct in every method that removes, which testing confirmed.
- **Cancelling the right party when two look identical:** Figured out. We cancel by waitlist position, because `Party` doesn't override `equals()`.
- **Open table doesn't fit the next party:** Designed (seat the first waiting party that fits; otherwise leave the table open).
  > **TODO (Brian):** Replace "Designed" with "Figured out" or "Still open," depending on whether `closeTable` implements this and has a passing test.

### What doesn't work yet?
- `LinkedChain` and `LinkedChainTest` are complete, and all tests pass.
- > **TODO (Rohail, Brian, Alex):** Each list, for your own classes: (1) MVP features not finished, (2) bad-input cases from the scope that still crash, (3) tests still failing or still marked TODO. Name the specific method or test. If nothing is missing, write "Complete."

## Next Steps

### What is left to finish the MVP?
> **TODO (whole group):** After filling in "What doesn't work yet," copy each unfinished item here in the order you'll do it. Classes the others depend on go first (`Table`, then `Restaurant`, then `RestaurantApp`), then an end-to-end run of the MVP path, then a final check of every bad-input case.

### Which stretch goals will we attempt?
- **Committed:** Special requests, stored as a `LinkedChain<String>` in each `Table`. This adds a third element type to the same chain class, with no changes to `LinkedChain`.
- **Dropped:** Estimated wait times, bill splitting, end-of-shift report, kitchen simulation, allergy flags, and save/load menu. Each needs new logic we can't finish and test in one week.
- > **TODO (whole group):** Confirm or change these choices together before submitting.

### What new tests will we need?
- `Table`: adding a request to a seated table; a blank request is rejected; a request at an open table throws; requests are cleared when the table closes.
- `RestaurantApp`: the new "add request" menu option, including a blank entry and an open table.
- > **TODO (whole group):** Add tests for anything listed under "What is left to finish the MVP."

### Who does what?
- **Rahul:** Integration, plus reviewing everyone's tests against the spec's test plan.
- **Rohail:** Special requests in `Table`, plus its tests.
- **Brian:** Remaining `Restaurant` work, plus its tests.
- **Alex:** The "add request" menu option in `RestaurantApp`, plus its tests.
- > **TODO (whole group):** Confirm the owners. Every MVP item and stretch goal above needs a name.