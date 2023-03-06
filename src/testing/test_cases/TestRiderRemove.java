package testing.test_cases;

import cycling.CyclingPortal;
import cycling.IDNotRecognisedException;
import cycling.IllegalNameException;
import cycling.InvalidNameException;

@SuppressWarnings("SpellCheckingInspection")
public class TestRiderRemove extends testing.TestCase {
    final CyclingPortal c = new CyclingPortal();

    public String testRemoveRider() throws InvalidNameException, IllegalNameException, IDNotRecognisedException {
        // Setup
        int teamID = c.createTeam("Test team to remove rider from", null);
        int riderID = c.createRider(teamID, "jdsadh", 2004);

        // Test
        try {
            c.removeRider(riderID);
        } catch (IDNotRecognisedException e) {
            return "ID not recognised when I just created the rider";
        }
        try {
            c.removeRider(riderID);
            return "removeRider threw no exception when run twice on the same ID";
        } catch (IDNotRecognisedException e) {
            // Success can't delete twice
            return null;
        }
    }
}
