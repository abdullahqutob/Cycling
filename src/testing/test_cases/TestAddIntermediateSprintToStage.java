package testing.test_cases;

import cycling.*;

import java.time.LocalDateTime;

@SuppressWarnings("SameReturnValue")
public class TestAddIntermediateSprintToStage extends testing.TestCase{
    final CyclingPortal c = new CyclingPortal();

    public String testAddIntermediate() throws InvalidNameException, IllegalNameException, IDNotRecognisedException, InvalidLengthException, InvalidStageStateException, InvalidLocationException, InvalidStageTypeException, AssertError {
        int rid = c.createRace("test race 1", null);
        int sid = c.addStageToRace(rid, "test stage 1", null, 20, LocalDateTime.now(), StageType.FLAT);
        int seg1 = c.addIntermediateSprintToStage(sid, 10.0);
        int seg2 = c.addIntermediateSprintToStage(sid, 20.0);

        assertNotEqual(seg1, seg2);
        assertNotEqual(seg1, 0);
        assertNotEqual(seg2, 0);
        return null;
    }

    public String testInvalidLocation() throws InvalidNameException, IllegalNameException, IDNotRecognisedException, InvalidLengthException, InvalidStageStateException {
        int rid = c.createRace("test race 2", null);
        int sid = c.addStageToRace(rid, "test stage 2", null, 10, LocalDateTime.now(), StageType.FLAT);
        try{
            int seg1 = c.addIntermediateSprintToStage(sid, 20.0);
            return "No exception thrown";
        } catch (InvalidLocationException e){
            return null;
        } catch (InvalidStageTypeException e){
            return "Says it's invalid but its not";
        }
    }

    public String testInvalidStageType() throws InvalidNameException, IllegalNameException, InvalidLengthException, IDNotRecognisedException, InvalidStageStateException {
        int rid = c.createRace("test race 3", null);
        int sid = c.addStageToRace(rid, "test stage 3", null, 10, LocalDateTime.now(), StageType.TT);

        try {
            int seg1 = c.addCategorizedClimbToStage(sid, 10.0, SegmentType.C3, 5.0, 10.0);
            return "No exceptions thrown";
        } catch (InvalidStageTypeException e) {
            return null;
        } catch (InvalidLocationException e){
            return "Says It's invalid but its not";
        }
    }



}


