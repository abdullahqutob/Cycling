package testing.test_cases;

import cycling.CyclingPortal;
import cycling.IllegalNameException;
import cycling.InvalidNameException;

@SuppressWarnings({"SameReturnValue", "SpellCheckingInspection"})
public class TestRace extends testing.TestCase {
    final CyclingPortal c = new CyclingPortal();

    public String testCreateRaceUniqueId() throws cycling.IllegalNameException, cycling.InvalidNameException, AssertError {
        int rid = c.createRace("The test race", "blah blah");
        int rid2 = c.createRace("The second test race", null);
        assertNotEqual(rid, rid2);

        return null;
    }

    public String testCreateRaceUniqueName() {
        try {
            int rid = c.createRace("One", "a kjfds ");
            int rid2 = c.createRace("One", "dja");
            return "Identical names no error";
        } catch (IllegalNameException e) {
            return null;
        } catch (InvalidNameException e) {
            return "says it's invalid but it is not";
        }
    }

    public String testCreateRaceInvalidName() {
        try {
            int rid = c.createRace(null, null);
            return "No exception thrown";
        } catch (cycling.InvalidNameException e) {
            return null;
        } catch (cycling.IllegalNameException e) {
            return "Wrong exception for invalid name";
        }
    }
}
