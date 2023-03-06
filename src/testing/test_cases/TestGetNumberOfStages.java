package testing.test_cases;

import cycling.*;

import java.time.LocalDateTime;

@SuppressWarnings("SameReturnValue")
public class TestGetNumberOfStages extends testing.TestCase{
    final CyclingPortal c = new CyclingPortal();

    public String testStageSize() throws InvalidNameException, IllegalNameException, IDNotRecognisedException, InvalidLengthException, AssertError {
        int rid = c.createRace("test race", null);
        int sid1 = c.addStageToRace(rid, "stage test 1", null, 10, LocalDateTime.now(), StageType.FLAT);
        int sid2 = c.addStageToRace(rid, "stage test 2", null, 15, LocalDateTime.now(), StageType.HIGH_MOUNTAIN);
        int sid3 = c.addStageToRace(rid, "stage test 3", null, 20, LocalDateTime.now(), StageType.TT);
        int stages = c.getNumberOfStages(rid);
        assertEqual(stages, 3);
        return null;
    }
}
