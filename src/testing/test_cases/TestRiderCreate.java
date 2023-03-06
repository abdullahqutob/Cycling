package testing.test_cases;

import cycling.CyclingPortal;
import cycling.IDNotRecognisedException;
import cycling.IllegalNameException;
import cycling.InvalidNameException;

@SuppressWarnings("SameReturnValue")
public class TestRiderCreate extends testing.TestCase {
    final CyclingPortal c = new CyclingPortal();

    public String testCreateTeamUniqueID() throws cycling.IllegalNameException, cycling.InvalidNameException, AssertError, cycling.IDNotRecognisedException {
        int tid = c.createTeam("Test", "To test riders");
        int rid = c.createRider(tid, "Jake", 2000);
        int rid2 = c.createRider(tid, "Jake2", 2000);

        assertNotEqual(rid, rid2);
        assertNotEqual(rid,0);
        assertNotEqual(rid2, 0);
        return null;
    }

    public String testRiderInvalidYOB() throws InvalidNameException, IllegalNameException {
        int tid = c.createTeam("Test YOB", "To test riders");
        try{
            c.createRider(tid, "The test rider", 0);
            return "error, no exceptions";
        } catch (IllegalArgumentException e) {
            return null;
        } catch (IDNotRecognisedException e) {
            return "says it's invalid but it is not";
        }
    }

    public String testRiderNameNull() throws InvalidNameException, IllegalNameException {
        int tid = c.createTeam("Test Name", "To test riders");
        try{
            c.createRider(tid, null, 2000);
            return "error, no exceptions";
        } catch (IllegalArgumentException e) {
            return null;
        } catch (IDNotRecognisedException e) {
            return "says it's invalid but it is not";
        }
    }

    public String testCreateRiderInvalidID() {
        try {
            c.createRider(500000000, "Test team id rider", 2000);
            return "No exception thrown";
        } catch (IllegalArgumentException e) {
            return "Wrong exception";
        } catch (IDNotRecognisedException e) {
            return null;
        }
    }
}
