package testing.test_cases;

import cycling.*;

import java.time.LocalDateTime;

@SuppressWarnings({"SameReturnValue", "SpellCheckingInspection"})
public class TestGetStageSegments extends testing.TestCase{
    final CyclingPortal c = new CyclingPortal();

    public String testGetStageSegment() throws InvalidNameException, IllegalNameException, IDNotRecognisedException, InvalidLengthException, InvalidStageStateException, InvalidLocationException, InvalidStageTypeException, AssertError {
        int rid = c.createRace("test race", null);
        int sid = c.addStageToRace(rid, "test stage", null, 20, LocalDateTime.now(), StageType.FLAT);
        int[] segids = c.getStageSegments(sid);
        assertEqual(segids.length, 0);

        int segid1 =  c.addCategorizedClimbToStage(sid, 10.0, SegmentType.C1, 5.0, 10.0);
        segids = c.getStageSegments(sid);
        assertEqual(segids.length, 1);

        int segid2 = c.addCategorizedClimbToStage(sid, 20.0, SegmentType.C2, 3.0, 10.0);
        segids = c.getStageSegments(sid);
        assertEqual(segids.length, 2);
        assertNotEqual(segid1, segid2);

        c.removeSegment(segid1);
        segids = c.getStageSegments(sid);
        assertEqual(segids.length, 1);
        return null;
    }


}
