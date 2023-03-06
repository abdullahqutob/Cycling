package testing.test_cases;

import cycling.CyclingPortal;
import cycling.IDNotRecognisedException;
import cycling.IllegalNameException;
import cycling.InvalidNameException;

@SuppressWarnings({"SameReturnValue", "SpellCheckingInspection"})
public class TestRidersGet extends testing.TestCase {
    final CyclingPortal c = new CyclingPortal();

        public String testGetTeamRidersIDs() throws IDNotRecognisedException, AssertError, InvalidNameException, IllegalNameException {
            int teamID = c.createTeam("Test", "jdsj");
            int[] riderIDs = c.getTeamRiders(teamID);
            assertEqual(riderIDs.length, 0);

            int riderID;
            riderID = c.createRider(teamID, "Test rider to get ID of", 2000);

            riderIDs = c.getTeamRiders(teamID);
            assertEqual(riderIDs.length, 1);
            assertEqual(riderIDs[0], riderID);

            c.createRider(teamID,"Test rider to get ID of 2nd", 2000);
            riderIDs = c.getTeamRiders(teamID);
            assertEqual(riderIDs.length, 2);

            c.removeRider(riderID); // remove first rider
            riderIDs = c.getTeamRiders(teamID);
            assertEqual(riderIDs.length, 1);

            return null;
        }
    }
