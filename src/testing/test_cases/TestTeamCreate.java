package testing.test_cases;
import cycling.CyclingPortal;
import cycling.IllegalNameException;
import cycling.InvalidNameException;

@SuppressWarnings("SameReturnValue")
public class TestTeamCreate extends testing.TestCase{
    final CyclingPortal c = new CyclingPortal();


    public String testCreateTeamUniqueID() throws cycling.IllegalNameException, cycling.InvalidNameException, AssertError {
        int tid1 = c.createTeam("Unique ID test", "blah blah");
        int tid2 = c.createTeam("Unique ID test 2", null);

        assertNotEqual(tid1, tid2);
        assertNotEqual(tid1,0);
        assertNotEqual(tid2, 0);
        return null;
    }

    public String testTeamName(){
        try{
            int tid1 = c.createTeam("The test team", "blah blah");
            int tid2 = c.createTeam("The test team", null);
            return "error, no exceptions";
        } catch (IllegalNameException e) {
            return null;
        } catch (InvalidNameException e) {
            return "says it's invalid but it is not";
        }
    }

    public String testCreateTeamInvalidName() {
        try {
            int tid = c.createTeam(null, null);
            return "No exception thrown";
        } catch (cycling.InvalidNameException e) {
            return null;
        } catch (cycling.IllegalNameException e) {
            return "Error, Duplicate";
        }
    }



}
