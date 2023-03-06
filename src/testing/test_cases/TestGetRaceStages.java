package testing.test_cases;

import cycling.*;
import java.time.LocalDateTime;

@SuppressWarnings("SameReturnValue")
public class TestGetRaceStages extends testing.TestCase {
    final CyclingPortal c = new CyclingPortal();

    public String testGetStage() throws IllegalNameException, InvalidNameException, IDNotRecognisedException, InvalidLengthException, AssertError {
        int raceID = c.createRace("test name", null);
        int[] stageIDs = c.getRaceStages(raceID);
        assertEqual(stageIDs.length, 0);

        int stage1;
        stage1 = c.addStageToRace(raceID, "test stage name", null, 10, LocalDateTime.now(), StageType.FLAT);

        stageIDs = c.getRaceStages(raceID);
        assertEqual(stageIDs.length, 1);
        assertEqual(stageIDs[0], stage1);

        c.addStageToRace(raceID,"test stage name 2", null, 10, LocalDateTime.now(), StageType.FLAT);
        stageIDs = c.getRaceStages(raceID);
        assertEqual(stageIDs.length, 2);

        c.removeStageById(stage1);  //remove first stage
        stageIDs = c.getRaceStages(raceID);
        assertEqual(stageIDs.length, 1);
        return null;
        }
    }
