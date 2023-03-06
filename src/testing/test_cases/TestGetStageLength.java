package testing.test_cases;

import cycling.*;

import java.time.LocalDateTime;

@SuppressWarnings("SameReturnValue")
public class TestGetStageLength extends testing.TestCase{
    final CyclingPortal c = new CyclingPortal();

    public String testGetStageLength() throws AssertError, IDNotRecognisedException, InvalidNameException, IllegalNameException, InvalidLengthException {
        int rid = c.createRace("test race", null);
        int sid = c.addStageToRace(rid, "test stage", null, 10, LocalDateTime.now(), StageType.FLAT);
        int length = (int) c.getStageLength(sid);
        assertEqual(length, 10);
        return null;
    }

}
