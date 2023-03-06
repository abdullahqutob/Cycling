package testing.test_cases;

import cycling.*;

import java.time.LocalDateTime;

@SuppressWarnings("SameReturnValue")
public class TestAddStageToRace extends testing.TestCase{
    final CyclingPortal c = new CyclingPortal();

    public String testCreateStageUniqueID() throws IDNotRecognisedException, IllegalNameException, InvalidNameException, InvalidLengthException, AssertError {

        //creating the stage
        int rid = c.createRace("test race", null);
        int sid1 = c.addStageToRace(rid, "test stage name", null, 10, LocalDateTime.now(), StageType.FLAT);
        int sid2 = c.addStageToRace(rid, "test stage name 2", null, 10, LocalDateTime.now(), StageType.FLAT);

        assertNotEqual(sid1, sid2);
        assertNotEqual(sid1, 0);
        assertNotEqual(sid2, 0);
        return null;

    }

    public String testStageInvalidID() throws IllegalNameException, InvalidNameException {
        int rid = c.createRace("test type", null);

        try{
            int sid1 = c.addStageToRace(1203912, "test stage id", null, 10, LocalDateTime.now(), StageType.FLAT);
            return "no exceptions";
        } catch (IDNotRecognisedException e){
            return null;
        } catch (InvalidLengthException e){
            return "says invalid, but it's not";
        }


    }


    public String testStageInvalidLength() throws IllegalNameException, InvalidNameException{
        int rid = c.createRace("test race length ", null);

        try{
            int sid1 = c.addStageToRace(rid, "test stage length", null, 4, LocalDateTime.now(), StageType.FLAT);
            return "no exceptions";
        } catch (IDNotRecognisedException e){
            return "says it's invalid, but it's not";
        } catch (InvalidLengthException e){
            return null;
        }
    }


}
















