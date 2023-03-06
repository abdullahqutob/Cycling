package testing.data;

import cycling.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class ComprehensiveTestData {
    public int JB, JS, JD, AJ;
    public int raceUF_StageMed;
    public void add(CyclingPortal c) throws InvalidNameException, IllegalNameException, IDNotRecognisedException, InvalidLengthException, InvalidStageStateException, InvalidLocationException, InvalidStageTypeException, DuplicatedResultException, InvalidCheckpointsException {
        /*
        Teams: Red, Blue
        Riders: Joe Blogs, John Smith, John Doe, Andrew Johnson

        Races: Uber Fast, The Sprint
        * */
        int redTeam = c.createTeam("Red", "USSR");
        int blueTeam = c.createTeam("Blue", "USA");

        JB = c.createRider(redTeam, "Joe Blogs", 1988);
        JS = c.createRider(redTeam, "John Smith", 1998);
        JD = c.createRider(blueTeam, "John Doe", 2001);
        AJ = c.createRider(blueTeam, "Andrew Johnson", 2000);

        int raceUF = c.createRace("Uber Fast", "A very fast race");
        LocalDateTime raceUFStartTime = LocalDateTime.now().minus(2, ChronoUnit.WEEKS);
        int raceTS = c.createRace("The Sprint", "A second very fast race");

        // ### Uber Fast
        int raceUF_StageTT = c.addStageToRace(raceUF, "Time trial", "A timed introduction", 10, raceUFStartTime, StageType.TT);
        int raceUF_StageFlat = c.addStageToRace(raceUF, "Flat race", "flat........", 50, raceUFStartTime.plus(1, ChronoUnit.DAYS), StageType.FLAT);
        raceUF_StageMed = c.addStageToRace(raceUF, "Medium mountain", "Not a mountain", 20, raceUFStartTime.plus(2, ChronoUnit.DAYS), StageType.MEDIUM_MOUNTAIN);

        c.addIntermediateSprintToStage(raceUF_StageFlat, 20);
        c.addIntermediateSprintToStage(raceUF_StageFlat, 40);

        c.addCategorizedClimbToStage(raceUF_StageMed, 10d, SegmentType.C3, 8d, 7d);
        c.addCategorizedClimbToStage(raceUF_StageMed, 15d, SegmentType.HC, 10d, 5d);
        c.addIntermediateSprintToStage(raceUF_StageMed, 20);

        // Results
        LocalTime start = LocalTime.from(raceUFStartTime);

        c.registerRiderResultsInStage(
                raceUF_StageTT,
                JB,
                start,
                start.plus(10, ChronoUnit.MINUTES)
        );
        c.registerRiderResultsInStage(
                raceUF_StageTT,
                JS,
                start,
                start.plus(11, ChronoUnit.MINUTES)
        );
        c.registerRiderResultsInStage(
                raceUF_StageTT,
                JD,
                start,
                start.plus(15, ChronoUnit.MINUTES)
        );
        c.registerRiderResultsInStage(
                raceUF_StageTT,
                AJ,
                start,
                start.plus(7, ChronoUnit.MINUTES)
        );

        // Possibly add offset to localtime else they all start at the same time each day

        c.registerRiderResultsInStage(
                raceUF_StageTT,
                JB,
                start,
                start.plus(10, ChronoUnit.MINUTES),
                start.plus(13, ChronoUnit.MINUTES),
                start.plus(14, ChronoUnit.MINUTES),
                start.plus(18, ChronoUnit.MINUTES)
        );
        c.registerRiderResultsInStage(
                raceUF_StageTT,
                JS,
                start,
                start.plus(11, ChronoUnit.MINUTES),
                start.plus(15, ChronoUnit.MINUTES),
                start.plus(17, ChronoUnit.MINUTES),
                start.plus(20, ChronoUnit.MINUTES)
        );
        c.registerRiderResultsInStage(
                raceUF_StageTT,
                JD,
                start,
                start.plus(15, ChronoUnit.MINUTES),
                start.plus(18, ChronoUnit.MINUTES),
                start.plus(20, ChronoUnit.MINUTES),
                start.plus(23, ChronoUnit.MINUTES)
        );
        c.registerRiderResultsInStage(
                raceUF_StageTT,
                AJ,
                start,
                start.plus(7, ChronoUnit.MINUTES),
                start.plus(8, ChronoUnit.MINUTES),
                start.plus(10, ChronoUnit.MINUTES),
                start.plus(12, ChronoUnit.MINUTES)
        );

        // Possibly add offset to localtime else they all start at the same time each day

        c.registerRiderResultsInStage(
                raceUF_StageTT,
                JB,
                start,
                start.plus(10, ChronoUnit.MINUTES),
                start.plus(13, ChronoUnit.MINUTES),
                start.plus(14, ChronoUnit.MINUTES),
                start.plus(18, ChronoUnit.MINUTES),
                start.plus(20, ChronoUnit.MINUTES)
        );
        c.registerRiderResultsInStage(
                raceUF_StageTT,
                JS,
                start,
                start.plus(11, ChronoUnit.MINUTES),
                start.plus(15, ChronoUnit.MINUTES),
                start.plus(17, ChronoUnit.MINUTES),
                start.plus(20, ChronoUnit.MINUTES),
                start.plus(23, ChronoUnit.MINUTES)
        );
        c.registerRiderResultsInStage(
                raceUF_StageTT,
                JD,
                start,
                start.plus(15, ChronoUnit.MINUTES),
                start.plus(18, ChronoUnit.MINUTES),
                start.plus(20, ChronoUnit.MINUTES),
                start.plus(23, ChronoUnit.MINUTES),
                start.plus(27, ChronoUnit.MINUTES)
        );
        c.registerRiderResultsInStage(
                raceUF_StageTT,
                AJ,
                start,
                start.plus(7, ChronoUnit.MINUTES),
                start.plus(8, ChronoUnit.MINUTES),
                start.plus(10, ChronoUnit.MINUTES),
                start.plus(12, ChronoUnit.MINUTES),
                start.plus(15, ChronoUnit.MINUTES)
        );
    }
}
