package testing.test_cases;

import cycling.CyclingPortal;
import cycling.IDNotRecognisedException;
import cycling.IllegalNameException;
import cycling.InvalidNameException;
import testing.TestCase;

@SuppressWarnings("SameReturnValue")
public class TestTeamGet extends TestCase {
    final CyclingPortal c = new CyclingPortal();

    public String testGetTeamIDs() throws IDNotRecognisedException, InvalidNameException, IllegalNameException, AssertError {
        int[] teamIDs = c.getTeams();
        assertEqual(teamIDs.length, 0);

        int teamID;
        teamID = c.createTeam("Test team to get ID of", null);

        teamIDs = c.getTeams();
        assertEqual(teamIDs.length, 1);
        assertEqual(teamIDs[0], teamID);

        c.createTeam("Test team to get ID of 2nd", null);
        teamIDs = c.getTeams();
        assertEqual(teamIDs.length, 2);

        c.removeTeam(teamID); // remove first team
        teamIDs = c.getTeams();
        assertEqual(teamIDs.length, 1);

        return null;
    }
}
