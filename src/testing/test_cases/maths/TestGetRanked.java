package testing.test_cases.maths;

import cycling.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@SuppressWarnings({"SameReturnValue", "SpellCheckingInspection"})
public class TestGetRanked extends testing.TestCase {
    public String testIt() throws IDNotRecognisedException, InvalidNameException, IllegalNameException, InvalidLengthException, InvalidStageStateException, InvalidLocationException, InvalidStageTypeException, DuplicatedResultException, InvalidCheckpointsException, AssertError, IOException, ClassNotFoundException {
        CyclingPortal c = new CyclingPortal();
        LocalDateTime raceUFStartTime = LocalDateTime.now().minus(2, ChronoUnit.WEEKS);

        int redTeam = c.createTeam("Red", "USSR");
        int blueTeam = c.createTeam("Blue", "USA");

        int JB = c.createRider(redTeam, "Joe Blogs", 1988);
        int JS = c.createRider(redTeam, "John Smith", 1998);
        int JD = c.createRider(blueTeam, "John Doe", 2001);
        int AJ = c.createRider(blueTeam, "Andrew Johnson", 2000);

        int raceUF = c.createRace("Uber Fast", "A very fast race");

        int raceUF_StageMed = c.addStageToRace(raceUF, "Medium mountain", "Not a mountain", 40, raceUFStartTime.plus(2, ChronoUnit.HOURS), StageType.MEDIUM_MOUNTAIN);

        int raceUF_StageSprint = c.addStageToRace(raceUF, "Sprint stage", "A sprint", 20, raceUFStartTime.plus(2, ChronoUnit.DAYS), StageType.TT);
        c.concludeStagePreparation(raceUF_StageSprint);

        c.addCategorizedClimbToStage(raceUF_StageMed, 10d, SegmentType.C3, 8d, 7d);
        c.addCategorizedClimbToStage(raceUF_StageMed, 15d, SegmentType.HC, 10d, 5d);
        c.addIntermediateSprintToStage(raceUF_StageMed, 20);
        c.addIntermediateSprintToStage(raceUF_StageMed, 40);

        LocalTime start = LocalTime.from(raceUFStartTime);
        c.concludeStagePreparation(raceUF_StageMed);

        // Test with no data
        LocalTime[] res = c.getRankedAdjustedElapsedTimesInStage(raceUF_StageMed);
        assertEqual(res.length, 0);

        int[] resint = c.getRidersMountainPointsInRace(raceUF);
        assertEqual(resint.length, 0);
        resint = c.getRidersPointsInRace(raceUF);
        assertEqual(resint.length, 0);
        resint = c.getRidersRankInStage(raceUF_StageMed);
        assertEqual(resint.length, 0);
        res = c.getGeneralClassificationTimesInRace(raceUF);
        assertEqual(res.length, 0);
        resint = c.getRidersGeneralClassificationRank(raceUF);
        assertEqual(resint.length, 0);

        // Add some data
        c.registerRiderResultsInStage(
                raceUF_StageMed,
                JB,
                start,
                start.plus(300, ChronoUnit.SECONDS),
                start.plus(500, ChronoUnit.SECONDS),
                start.plus(600, ChronoUnit.SECONDS),
                start.plus(900, ChronoUnit.SECONDS),
                start.plus(900, ChronoUnit.SECONDS)
        );

        LocalTime t15m = LocalTime.MIDNIGHT.plus(900, ChronoUnit.SECONDS);
        assertEqual(c.getRiderAdjustedElapsedTimeInStage(raceUF_StageMed, JB).equals(t15m), true);

        res = c.getRankedAdjustedElapsedTimesInStage(raceUF_StageMed);
        assertEqual(res.length, 1);
        assertEqual(res[0].equals(t15m), true);
        int[] resRank = c.getRidersRankInStage(raceUF_StageMed);
        assertEqual(resRank.length, 1);
        assertEqual(resRank[0], JB);

        // Test with another rider
        c.registerRiderResultsInStage(
                raceUF_StageMed,
                JS,
                start,
                start.plus(305, ChronoUnit.SECONDS),
                start.plus(510, ChronoUnit.SECONDS),
                start.plus(620, ChronoUnit.SECONDS),
                start.plus(905, ChronoUnit.SECONDS),
                start.plus(899, ChronoUnit.SECONDS)
        );

        LocalTime t1459ms = t15m.minus(1, ChronoUnit.SECONDS);

        assertEqual(c.getRiderAdjustedElapsedTimeInStage(raceUF_StageMed, JB).equals(t15m), true);

        res = c.getRankedAdjustedElapsedTimesInStage(raceUF_StageMed);
        assertEqual(res.length, 2);
        assertEqual(res[0].equals(t1459ms), true);
        resRank = c.getRidersRankInStage(raceUF_StageMed);
        assertEqual(resRank.length, 2);
        assertEqual(resRank[0], JS); // because JS is faster
        assertEqual(resRank[1], JB); // because JS is faster

        // Points
        int[] points = c.getRidersPointsInStage(raceUF_StageMed);
        int[] riders = c.getRidersRankInStage(raceUF_StageMed);
        assertEqual(riders[0], JS);
        assertEqual(riders[1], JB);
        assertEqual(points[0], 67);
        assertEqual(points[1], 62);
        int[] pointRank = c.getRidersPointClassificationRank(raceUF);
        assertEqual(pointRank[0], JS);
        assertEqual(pointRank[1], JB);

        // Mountain points
        points = c.getRidersMountainPointsInStage(raceUF_StageMed);
        riders = c.getRidersRankInStage(raceUF_StageMed);
        assertEqual(riders[0], JS);
        assertEqual(riders[1], JB);
        assertEqual(points[0], 16);
        assertEqual(points[1], 22);
        pointRank = c.getRidersMountainPointClassificationRank(raceUF);
        assertEqual(pointRank[0], JB);
        assertEqual(pointRank[1], JS);

        // Add another racer's results: AJ
        c.registerRiderResultsInStage(
                raceUF_StageSprint,
                AJ,
                start,
                start.plus(2000, ChronoUnit.SECONDS)
        );

        // Check race functions
        int[] ridersMntRank = c.getRidersGeneralClassificationRank(raceUF);
        int[] ridersMntPoints = c.getRidersMountainPointsInRace(raceUF);
        assertEqual(ridersMntRank[0], JS);
        assertEqual(ridersMntRank[1], JB);
        assertEqual(ridersMntRank[2], AJ);
        assertEqual(ridersMntPoints[0], 16);
        assertEqual(ridersMntPoints[1], 22);
        assertEqual(ridersMntPoints[2], 0);

        ridersMntRank = c.getRidersGeneralClassificationRank(raceUF);
        ridersMntPoints = c.getRidersPointsInRace(raceUF);
        assertEqual(ridersMntRank[0], JS);
        assertEqual(ridersMntRank[1], JB);
        assertEqual(ridersMntRank[2], AJ);
        assertEqual(ridersMntPoints[0], 67);
        assertEqual(ridersMntPoints[1], 62);
        assertEqual(ridersMntPoints[2], 20);

        // Point class rank
        int[] pointClassRank = c.getRidersPointClassificationRank(raceUF);
        assertEqual(pointClassRank[0], JS);
        assertEqual(pointClassRank[1], JB);
        assertEqual(pointClassRank[2], AJ);

        int[] mntPointClassRank = c.getRidersMountainPointClassificationRank(raceUF);
        assertEqual(mntPointClassRank[0], JB);
        assertEqual(mntPointClassRank[1], JS);
        assertEqual(mntPointClassRank[2], AJ);

        // Test save load and erase in the middle
        String saveName = UUID.randomUUID().toString();
        String savePath = Paths.get(System.getProperty("java.io.tmpdir"), saveName).toString();
        c.saveCyclingPortal(savePath);
        c.eraseCyclingPortal();
        c.loadCyclingPortal(savePath);
        // DONE

        // Last
        LocalTime[] gct = c.getGeneralClassificationTimesInRace(raceUF);
        ridersMntRank = c.getRidersGeneralClassificationRank(raceUF);
        assertEqual(ridersMntRank[0], JS);
        assertEqual(ridersMntRank[1], JB);
        assertEqual(ridersMntRank[2], AJ);
        assertEqual(gct[0].equals(LocalTime.MIDNIGHT.plus(899, ChronoUnit.SECONDS)), true);
        assertEqual(gct[1].equals(LocalTime.MIDNIGHT.plus(900, ChronoUnit.SECONDS)), true);
        assertEqual(gct[2].equals(LocalTime.MIDNIGHT.plus(2000, ChronoUnit.SECONDS)), true);

        return null;
    }
}
