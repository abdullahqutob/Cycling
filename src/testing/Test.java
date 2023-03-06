package testing;

import testing.test_cases.*;
import testing.test_cases.maths.TestGetRanked;

public class Test {
    public static void main(String[] args) {
        // Each inherited of test case make instance and run runner
        int exit = 0;
        exit = exit | new TestRace().runner();
        exit = exit | new TestTest().runner();
        exit = exit | new TestTeamCreate().runner();
        exit = exit | new TestTeamRemove().runner();
        exit = exit | new TestTeamGet().runner();
        exit = exit | new TestAddStageToRace().runner();
        exit = exit | new TestGetRaceStages().runner();
        exit = exit | new TestRidersGet().runner();
        exit = exit | new TestRiderCreate().runner();
        exit = exit | new TestRiderRemove().runner();

        exit = exit | new TestAddStageToRace().runner();

        exit = exit | new TestAddCategorizedClimbToStage().runner();
        exit = exit | new TestAddIntermediateSprintToStage().runner();
        exit = exit | new TestGetStageSegments().runner();

        exit = exit | new TestGetRanked().runner();

        System.exit(exit);
    }
}
