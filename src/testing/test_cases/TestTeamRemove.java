package testing.test_cases;

import cycling.CyclingPortal;
import cycling.IDNotRecognisedException;
import cycling.IllegalNameException;
import cycling.InvalidNameException;

public class TestTeamRemove extends testing.TestCase {
    final CyclingPortal c = new CyclingPortal();

    public String testRemoveTeam() {
        // Setup
        int teamID;
        try {
            teamID = c.createTeam("Test team to remove", null);
        } catch (InvalidNameException | IllegalNameException e) {
            return "createTeam had exception with valid input in the setup to remove team test";
        }

        // Test
        try {
            c.removeTeam(teamID);
        } catch (IDNotRecognisedException e) {
            return "ID not recognised when I just created the team";
        }
        try {
            c.removeTeam(teamID);
            return "removeTeam threw no exception when run twice on the same ID";
        } catch (IDNotRecognisedException e) {
            // Success can't delete twice
            return null;
        }
    }
}
