package cycling;

import cycling.types.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class CyclingPortal implements CyclingPortalInterface {
    // Data Store
    private ArrayList<Team>  teams = new ArrayList<>();
    private ArrayList<Rider> riders = new ArrayList<>();

    private ArrayList<Race> races = new ArrayList<>();
    private ArrayList<Stage> stages = new ArrayList<>();
    private ArrayList<Segment> segments = new ArrayList<>();

    // Private helper functions
    private Race getRaceByIDOrNull(int raceId) {
        return races.stream()
                .filter(r -> r.id == raceId)
                .findAny().orElse(null);
    }

    private Team getTeamByIDOrNull(int teamId){
        return teams.stream()
                .filter(t -> t.id == teamId)
                .findAny().orElse(null);
    }

    private Stage getStageByIDOrNull(int stageId){
        return stages.stream()
                .filter(s -> s.id == stageId)
                .findAny().orElse(null);
    }

    private Rider getRiderByIDOrNull(int riderId) {
        return riders.stream()
                .filter(r -> r.id == riderId)
                .findAny().orElse(null);
    }

    private Segment getSegmentByIDOrNull(int segmentId) {
        return segments.stream()
                .filter(s -> s.id == segmentId)
                .findAny().orElse(null);
    }

    private int stagePoints(StageType stageType, int rank) {
        if (rank > 15) return 0;
        return switch (stageType) {
            case FLAT ->                new int[]{50, 30, 20, 18, 16, 14, 12, 10, 8, 7, 6, 5, 4, 3, 2}[rank];
            case MEDIUM_MOUNTAIN ->     new int[]{30, 25, 22, 19, 17, 15, 13, 11, 9, 7, 6, 5, 4, 3, 2}[rank];
            case HIGH_MOUNTAIN, TT ->   new int[]{20, 17, 15, 13, 11, 10,  9,  8, 7, 6, 5, 4, 3, 2, 1}[rank];
        };
    }

    private int segmentPoints(SegmentType segmentType, int rank) {
        if (rank > 15) return 0;
        return switch (segmentType) {
            case SPRINT ->  new int[]{20, 17, 15, 13, 11, 10,  9,  8, 7, 6, 5, 4, 3, 2, 1}[rank];
            case HC ->      new int[]{20, 15, 12, 10,  8,  6,  4,  2, 0, 0, 0, 0, 0, 0, 0}[rank];
            case C1 ->      new int[]{10,  8,  6,  4,  2,  1,  0,  0, 0, 0, 0, 0, 0, 0, 0}[rank];
            case C2 ->      new int[]{ 5,  3,  2,  1,  0,  0,  0,  0, 0, 0, 0, 0, 0, 0, 0}[rank];
            case C3 ->      new int[]{ 2,  1,  0,  0,  0,  0,  0,  0, 0, 0, 0, 0, 0, 0, 0}[rank];
            case C4 ->      new int[]{ 1,  0,  0,  0,  0,  0,  0,  0, 0, 0, 0, 0, 0, 0, 0}[rank];
        };
    }

    /**
     * The method removes the race and all its related information, i.e., stages,
     * segments, and results.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param name The name of the race to be removed.
     * @throws NameNotRecognisedException If the name does not match to any race in
     *                                    the system.
     */
    @Override
    public void removeRaceByName(String name) throws NameNotRecognisedException {
        Race race = races.stream()
                .filter(r -> r.name.equals(name))
                .findAny()
                .orElse(null);
        if (race == null) throw new NameNotRecognisedException("Race not found in removeRaceByName");

        races.remove(race);
    }

    /**
     * Get the general classification rank of riders in a race.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param raceId The ID of the race being queried.
     * @return A ranked list of riders' IDs sorted ascending by the sum of their
     * adjusted elapsed times in all stages of the race. That is, the first
     * in this list is the winner (least time). An empty list if there is no
     * result for any stage in the race.
     * @throws IDNotRecognisedException If the ID does not match any race in the
     *                                  system.
     */
    @Override
    public int[] getRidersGeneralClassificationRank(int raceId) throws IDNotRecognisedException {
        int[] stageIds = getRaceStages(raceId);
        // Value is the adjusted and  accumulated elapsed times from all stages in the race that the rider takes part in.
        HashMap<Rider, Long> results = new HashMap<>();

        for (int stageId : stageIds) {
            Stage stage = getStageByIDOrNull(stageId);
            if (stage == null) throw new IDNotRecognisedException();

            Rider[] applicableRiders = riders.stream().filter(r -> r.results.containsKey(stage.id)).toArray(Rider[]::new);

            // Riders with their adjusted elapsed time in a stage
            for (Rider rider : applicableRiders) {
                long val = getRiderAdjustedElapsedTimeInStage(stageId, rider.id).toSecondOfDay();
                if (results.containsKey(rider)) results.put(rider, results.get(rider) + val);
                else results.put(rider, val);
            }
        }

        HashMap<Long, Rider> resultsHMFlipped = new HashMap<>();
        results.forEach((rider, val) -> resultsHMFlipped.put(val, rider));
        return hashMapValuesSortedByComparableKey(resultsHMFlipped).stream().mapToInt(r -> r.id).toArray();
    }

    /**
     * Get the general classification times of riders in a race.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param raceId The ID of the race being queried.
     * @return A list of riders' times sorted by the sum of their adjusted elapsed
     * times in all stages of the race. An empty list if there is no result
     * for any stage in the race. These times should match the riders
     * returned by {@link #getRidersGeneralClassificationRank(int)}.
     * @throws IDNotRecognisedException If the ID does not match any race in the
     *                                  system.
     */
    @Override
    public LocalTime[] getGeneralClassificationTimesInRace(int raceId) throws IDNotRecognisedException {
        // Make sure that I'm returning the correct type of times
        // I am: https://vle.exeter.ac.uk/mod/forum/discuss.php?d=227212

        int[] stageIds = getRaceStages(raceId);

        int[] riderIds = getRidersGeneralClassificationRank(raceId);
        Rider[] riders = Arrays.stream(riderIds).mapToObj(this::getRiderByIDOrNull).toArray(Rider[]::new);
        if (Arrays.stream(riders).anyMatch(Objects::isNull)) throw new IDNotRecognisedException();

        return Arrays.stream(riders).map(r -> Arrays.stream(stageIds)
                    .mapToObj(sid -> {
                        try {
                            return getRiderAdjustedElapsedTimeInStage(sid, r.id);
                        } catch (IDNotRecognisedException e) {
                            return LocalTime.MIDNIGHT;
                        }
                    })
                    .filter(time -> time != LocalTime.MIDNIGHT)
                    .reduce(LocalTime.MIDNIGHT, (subtotal, time) -> LocalTime.MIDNIGHT.plus(
                            Duration.between(LocalTime.MIDNIGHT, time).plus(Duration.between(LocalTime.MIDNIGHT, subtotal))
                    ))
        ).toArray(LocalTime[]::new);
    }

    /**
     * Get the overall points of riders in a race.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param raceId The ID of the race being queried.
     * @return A list of riders' points (i.e., the sum of their points in all stages
     * of the race), sorted by the total elapsed time. An empty list if
     * there is no result for any stage in the race. These points should
     * match the riders returned by {@link #getRidersGeneralClassificationRank(int)}.
     * @throws IDNotRecognisedException If the ID does not match any race in the
     *                                  system.
     */
    @Override
    public int[] getRidersPointsInRace(int raceId) throws IDNotRecognisedException {
        int[] rankedRiderIds = getRidersGeneralClassificationRank(raceId); // Order of rider ids to match

        Race race = getRaceByIDOrNull(raceId);
        if (race == null) throw new IDNotRecognisedException();
        int[] stageIds = getRaceStages(race.id);
        Stage[] stages = Arrays.stream(stageIds).mapToObj(this::getStageByIDOrNull).toArray(Stage[]::new);
        if (Arrays.stream(stages).anyMatch(Objects::isNull)) throw new IDNotRecognisedException();

        // Key riderId. Val Points
        HashMap<Integer, Integer> riderPoints = new HashMap<>();
        for (Stage stage : stages) {
            int[] ridersInStage = getRidersRankInStage(stage.id);
            int[] pointsInStage = getRidersPointsInStage(stage.id);

            for (int i = 0; i < ridersInStage.length; i++) {
                int rid = ridersInStage[i];
                if (!riderPoints.containsKey(rid)) riderPoints.put(rid, 0);
                riderPoints.put(rid, riderPoints.get(rid) + pointsInStage[i]);
            }
        }

        ArrayList<Integer> orderedPoints = new ArrayList<>(); // Ordered by rankedRiderIds
        for (int rid : rankedRiderIds) {
            orderedPoints.add(riderPoints.get(rid));
        }

        return orderedPoints.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Get the overall mountain points of riders in a race.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param raceId The ID of the race being queried.
     * @return A list of riders' mountain points (i.e., the sum of their mountain
     * points in all stages of the race), sorted by the total elapsed time.
     * An empty list if there is no result for any stage in the race. These
     * points should match the riders returned by
     * {@link #getRidersGeneralClassificationRank(int)}.
     * @throws IDNotRecognisedException If the ID does not match any race in the
     *                                  system.
     */
    @Override
    public int[] getRidersMountainPointsInRace(int raceId) throws IDNotRecognisedException {
        int[] rankedRiderIds = getRidersGeneralClassificationRank(raceId); // Order of rider ids to match

        Race race = getRaceByIDOrNull(raceId);
        if (race == null) throw new IDNotRecognisedException();
        int[] stageIds = getRaceStages(race.id);
        Stage[] stages = Arrays.stream(stageIds).mapToObj(this::getStageByIDOrNull).toArray(Stage[]::new);
        if (Arrays.stream(stages).anyMatch(Objects::isNull)) throw new IDNotRecognisedException();

        // Key riderId. Val Points
        HashMap<Integer, Integer> riderPoints = new HashMap<>();
        for (Stage stage : stages) {
            int[] ridersInStage = getRidersRankInStage(stage.id);
            int[] pointsInStage = getRidersMountainPointsInStage(stage.id);

            for (int i = 0; i < ridersInStage.length; i++) {
                int rid = ridersInStage[i];
                if (!riderPoints.containsKey(rid)) riderPoints.put(rid, 0);
                riderPoints.put(rid, riderPoints.get(rid) + pointsInStage[i]);
            }
        }

        ArrayList<Integer> orderedPoints = new ArrayList<>(); // Ordered by rankedRiderIds
        for (int rid : rankedRiderIds) {
            orderedPoints.add(riderPoints.get(rid));
        }

        return orderedPoints.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Get the ranked list of riders based on the point classification in a race.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param raceId The ID of the race being queried.
     * @return A ranked list of riders' IDs sorted descending by the sum of their
     * points in all stages of the race. That is, the first in this list is
     * the winner (more points). An empty list if there is no result for any
     * stage in the race.
     * @throws IDNotRecognisedException If the ID does not match any race in the
     *                                  system.
     */
    @Override
    public int[] getRidersPointClassificationRank(int raceId) throws IDNotRecognisedException {
        ArrayList<Integer> rankedRiderIds = new ArrayList<>(Arrays.stream(getRidersGeneralClassificationRank(raceId)).boxed().toList());
        ArrayList<Integer> rankedRidersPoints = new ArrayList<>(Arrays.stream(getRidersPointsInRace(raceId)).boxed().toList());
        // Same as HashMap just different interface
        rankedRiderIds.sort(Comparator.comparingInt(rankedRidersPoints::indexOf)); // Check if ide optimisations are good
        return rankedRiderIds.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Get the ranked list of riders based on the mountain classification in a race.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param raceId The ID of the race being queried.
     * @return A ranked list of riders' IDs sorted descending by the sum of their
     * mountain points in all stages of the race. That is, the first in this
     * list is the winner (more points). An empty list if there is no result
     * for any stage in the race.
     * @throws IDNotRecognisedException If the ID does not match any race in the
     *                                  system.
     */
    @Override
    public int[] getRidersMountainPointClassificationRank(int raceId) throws IDNotRecognisedException {
        int[] raceStageIds = getRaceStages(raceId);
        Stage[] raceStages = Arrays.stream(raceStageIds).mapToObj(this::getStageByIDOrNull).toArray(Stage[]::new);
        if (Arrays.stream(raceStages).anyMatch(Objects::isNull)) throw new IDNotRecognisedException();

        // Key riderId. Val Points. Points are accumulated in the loop
        HashMap<Integer, Integer> riderPointsInRace = new HashMap<>();
        for (Stage stage : raceStages) {
            int[] riderIdsRanked = getRidersRankInStage(stage.id);
            int[] riderPoints = getRidersMountainPointsInStage(stage.id);
            assert riderIdsRanked.length == riderPoints.length; // If this isn't true then something is catastrophically wrong

            for (int i = 0; i < riderIdsRanked.length; i++) {
                int rid = riderIdsRanked[i];
                if (!riderPointsInRace.containsKey(rid)) riderPointsInRace.put(rid, 0);
                riderPointsInRace.put(
                        riderIdsRanked[i],
                        riderPointsInRace.get(rid) + riderPoints[i]
                );
            }
        }

        ArrayList<Integer> riderIds = new ArrayList<>(riderPointsInRace.keySet().stream().toList());
        ArrayList<Integer> riderPoints = new ArrayList<>(riderPointsInRace.values().stream().toList());

        riderIds.sort(Comparator.comparingInt(riderPoints::indexOf));

        return riderIds.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Get the races currently created in the platform.
     *
     * @return An array of race IDs in the system or an empty array if none exists.
     */
    @Override
    public int[] getRaceIds() {
        return races.stream().mapToInt(r -> r.id).toArray();
    }

    /**
     * The method creates a staged race in the platform with the given name and
     * description.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param name        Race's name.
     * @param description Race's description (can be null).
     * @return the unique ID of the created race.
     * @throws IllegalNameException If the name already exists in the platform.
     * @throws InvalidNameException If the name is null, empty, has more than 30
     *                              characters, or has white spaces.
     */
    @Override
    public int createRace(String name, String description) throws IllegalNameException, InvalidNameException {
        if (name == null || name.equals("") || name.length() > 30) throw new InvalidNameException();

        int currentMaxRaceID = 0;
        for (Race r : races) {
            if (r.id > currentMaxRaceID) currentMaxRaceID = r.id;
            if (r.name.equals(name)) throw new IllegalNameException();
        }

        Race newRace = new Race();
        newRace.id = currentMaxRaceID +1;
        newRace.name = name;
        newRace.description = description;
        races.add(newRace);
        return newRace.id;
    }

    /**
     * Get the details from a race.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param raceId The ID of the race being queried.
     * @return Any formatted string containing the race ID, name, description, the
     * number of stages, and the total length (i.e., the sum of all stages'
     * length).
     * @throws IDNotRecognisedException If the ID does not match to any race in the
     *                                  system.
     */
    @Override
    public String viewRaceDetails(int raceId) throws IDNotRecognisedException {
        Race race = getRaceByIDOrNull(raceId);
        if (race == null) throw new IDNotRecognisedException("Race not found in viewRaceDetails");
        int raceLength = Arrays.stream(getRaceStages(raceId)).sum();
        return("Race ID: " + race.id + ", Race description: " + race.description + ", Number of stages: " + race.stages.size() + ", Length of race:" + raceLength);
    }

    /**
     * The method removes the race and all its related information, i.e., stages,
     * segments, and results.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param raceId The ID of the race to be removed.
     * @throws IDNotRecognisedException If the ID does not match to any race in the
     *                                  system.
     */
    @Override
    public void removeRaceById(int raceId) throws IDNotRecognisedException {
        Race item = getRaceByIDOrNull(raceId);
        if (item == null) throw new IDNotRecognisedException("Race not found in removeRaceByID");


        int[] raceStages = getRaceStages(raceId);
        for (int i : raceStages){
            int[] stageSegments = getStageSegments(i);
            for (int j : stageSegments){
                segments.remove(j);
            }
            Rider[] applicableRiders = riders.stream().filter(r -> r.results.containsKey(i)).toArray(Rider[]::new);
            for (Rider k : applicableRiders){
                deleteRiderResultsInStage(i, k.id);
            }
            stages.remove(i);
        }
        races.remove(item);
    }

    /**
     * The method queries the number of stages created for a race.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param raceId The ID of the race being queried.
     * @return The number of stages created for the race.
     * @throws IDNotRecognisedException If the ID does not match to any race in the
     *                                  system.
     */
    @Override
    public int getNumberOfStages(int raceId) throws IDNotRecognisedException {
        Race race = getRaceByIDOrNull(raceId);
        if (race == null) throw new IDNotRecognisedException("Race not found in removeRaceByID");

        return race.stages.size();
    }

    /**
     * Creates a new stage and adds it to the race.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param raceId      The race which the stage will be added to.
     * @param stageName   An identifier name for the stage.
     * @param description A descriptive text for the stage.
     * @param length      Stage length in kilometres.
     * @param startTime   The date and time in which the stage will be raced. It
     *                    cannot be null.
     * @param type        The type of the stage. This is used to determine the
     *                    amount of points given to the winner.
     * @return the unique ID of the stage.
     * @throws IDNotRecognisedException If the ID does not match to any race in the
     *                                  system.
     * @throws IllegalNameException     If the name already exists in the platform.
     * @throws InvalidNameException     If the new name is null, empty, has more
     *                                  than 30.
     * @throws InvalidLengthException   If the length is less than 5km.
     */
    @Override
    public int addStageToRace(int raceId, String stageName, String description, double length, LocalDateTime startTime, StageType type) throws IDNotRecognisedException, IllegalNameException, InvalidNameException, InvalidLengthException {
        Race race = getRaceByIDOrNull(raceId);

        if (race == null) throw new IDNotRecognisedException();
        if (stageName == null || stageName.equals("") || stageName.length() > 30) throw new InvalidNameException("Name is null, empty or has more than 30 characters");
        if (stages.stream().anyMatch(s -> Objects.equals(s.name, stageName))) throw new IllegalNameException();
        if (length < 5) throw new InvalidLengthException();

        Stage newStage = new Stage();
        newStage.name = stageName; newStage.description = description; newStage.length = length;  newStage.type = type; newStage.startTime = startTime;
        stages.add(new Stage());

        int currentMaxStageID = stages.stream().mapToInt(s -> s.id).max().orElse(0);
        newStage.id = currentMaxStageID + 1;

        race.stages.add(newStage.id);
        stages.add(newStage);

        return newStage.id;
    }

    /**
     * Retrieves the list of stage IDs of a race.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param raceId The ID of the race being queried.
     * @return The list of stage IDs ordered (from first to last) by their sequence in the
     * race.
     * @throws IDNotRecognisedException If the ID does not match to any race in the
     *                                  system.
     */
    @Override
    public int[] getRaceStages(int raceId) throws IDNotRecognisedException {
        Race race = getRaceByIDOrNull(raceId);
        if (race == null) throw new IDNotRecognisedException();


        return race.stages.stream().mapToInt(i->i).toArray();
    }

    /**
     * Gets the length of a stage in a race, in kilometres.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param stageId The ID of the stage being queried.
     * @return The stage's length.
     * @throws IDNotRecognisedException If the ID does not match to any stage in the
     *                                  system.
     */
    @Override
    public double getStageLength(int stageId) throws IDNotRecognisedException {
        Stage stage = getStageByIDOrNull(stageId); if (stage == null) throw new IDNotRecognisedException();

        return stage.length;
    }

    /**
     * Removes a stage and all its related data, i.e., segments and results.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param stageId The ID of the stage being removed.
     * @throws IDNotRecognisedException If the ID does not match to any stage in the
     *                                  system.
     */
    @Override
    public void removeStageById(int stageId) throws IDNotRecognisedException {
        Stage stage = getStageByIDOrNull(stageId);
        if (stage == null) throw new IDNotRecognisedException();

        Race race = races.stream().filter(r -> r.stages.contains(stage.id)).findAny().orElse(null);
        if (race == null) {System.out.println("Consistency error. Corrupted data?"); return;}

        race.stages.remove(race.id - 1);
        stages.remove(stageId -1);
        int[] stageSegments = getStageSegments(stageId);
        for (int i : stageSegments){
            segments.remove(i);
        }
        Rider[] applicableRiders = riders.stream().filter(r -> r.results.containsKey(stageId)).toArray(Rider[]::new);
        for (Rider k : applicableRiders){
            deleteRiderResultsInStage(stageId, k.id);
        }
    }

    /**
     * Adds a climb segment to a stage.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param stageId         The ID of the stage to which the climb segment is
     *                        being added.
     * @param location        The kilometre location where the climb finishes within
     *                        the stage.
     * @param type            The category of the climb - {@link SegmentType#C4},
     *                        {@link SegmentType#C3}, {@link SegmentType#C2},
     *                        {@link SegmentType#C1}, or {@link SegmentType#HC}.
     * @param averageGradient The average gradient for the climb.
     * @param length          The length of the climb in kilometre.
     * @return The ID of the segment created.
     * @throws IDNotRecognisedException   If the ID does not match to any stage in
     *                                    the system.
     * @throws InvalidLocationException   If the location is out of bounds of the
     *                                    stage length.
     * @throws InvalidStageStateException If the stage is "waiting for results".
     * @throws InvalidStageTypeException  Time-trial stages cannot contain any
     *                                    segment.
     */
    @Override
    public int addCategorizedClimbToStage(int stageId, Double location, SegmentType type, Double averageGradient, Double length) throws IDNotRecognisedException, InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
        Stage stage = getStageByIDOrNull(stageId);
        if (stage == null) throw new IDNotRecognisedException();
        if (stage.state != StageState.SETUP) throw new InvalidStageStateException();
        if (stage.type == StageType.TT) throw new InvalidStageTypeException();
        if (location > stage.length) throw new InvalidLocationException();

        Segment newSegment = new Segment();
        newSegment.id = segments.stream().mapToInt(s -> s.id).max().orElse(0) + 1;
        newSegment.location = location;
        newSegment.type = type;
        newSegment.averageGradient = averageGradient;

        segments.add(newSegment);
        stage.segments.add(newSegment.id);

        return newSegment.id;
    }

    /**
     * Adds an intermediate sprint to a stage.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param stageId  The ID of the stage to which the intermediate sprint segment
     *                 is being added.
     * @param location The kilometre location where the intermediate sprint finishes
     *                 within the stage.
     * @return The ID of the segment created.
     * @throws IDNotRecognisedException   If the ID does not match to any stage in
     *                                    the system.
     * @throws InvalidLocationException   If the location is out of bounds of the
     *                                    stage length.
     * @throws InvalidStageStateException If the stage is "waiting for results".
     * @throws InvalidStageTypeException  Time-trial stages cannot contain any
     *                                    segment.
     */
    @Override
    public int addIntermediateSprintToStage(int stageId, double location) throws IDNotRecognisedException, InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
        Stage stage = getStageByIDOrNull(stageId);
        if (stage == null) throw new IDNotRecognisedException();
        if (stage.state != StageState.SETUP) throw new InvalidStageStateException();
        if (stage.type == StageType.TT) throw new InvalidStageTypeException();
        if (location > stage.length) throw new InvalidLocationException();

        Segment newSegment = new Segment();
        newSegment.id = segments.stream().mapToInt(s -> s.id).max().orElse(0) + 1;
        newSegment.location = location;
        newSegment.type = SegmentType.SPRINT;

        segments.add(newSegment);
        stage.segments.add(newSegment.id);

        return newSegment.id;
    }

    /**
     * Removes a segment from a stage.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param segmentId The ID of the segment to be removed.
     * @throws IDNotRecognisedException   If the ID does not match to any segment in
     *                                    the system.
     * @throws InvalidStageStateException If the stage is "waiting for results".
     */
    @Override
    public void removeSegment(int segmentId) throws IDNotRecognisedException, InvalidStageStateException {
        Segment segment = getSegmentByIDOrNull(segmentId);
        if (segment == null) throw new IDNotRecognisedException();
        Stage stage = stages.stream().filter(s -> s.segments.contains(segment.id)).findAny().orElse(null);
        if (stage == null) {System.out.println("Consistency error. Corrupted data?"); return;}
        if (stage.state == StageState.WAITING_FOR_RESULTS) throw new InvalidStageStateException();

        segments.remove(segment);
        stage.segments.remove(stage.segments.stream().filter(sid -> sid == segment.id).findAny().orElse(0)); // Or else will never happen bc we check for consistency earlier
    }

    /**
     * Concludes the preparation of a stage. After conclusion, the stage's state
     * should be "waiting for results".
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param stageId The ID of the stage to be concluded.
     * @throws IDNotRecognisedException   If the ID does not match to any stage in
     *                                    the system.
     * @throws InvalidStageStateException If the stage is "waiting for results".
     */
    @Override
    public void concludeStagePreparation(int stageId) throws IDNotRecognisedException, InvalidStageStateException {
        Stage stage = getStageByIDOrNull(stageId);
        if (stage == null) throw new IDNotRecognisedException();
        if (stage.state == StageState.WAITING_FOR_RESULTS) throw new InvalidStageStateException();

        stage.state = StageState.WAITING_FOR_RESULTS;
    }

    /**
     * Retrieves the list of segment (mountains and sprints) IDs of a stage.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param stageId The ID of the stage being queried.
     * @return The list of segment IDs ordered (from first to last) by their location in the
     * stage.
     * @throws IDNotRecognisedException If the ID does not match to any stage in the
     *                                  system.
     */
    @Override
    public int[] getStageSegments(int stageId) throws IDNotRecognisedException {
        Stage stage = getStageByIDOrNull(stageId);
        if (stage == null) throw new IDNotRecognisedException();

        return stage.segments.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Creates a team with name and description.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param name        The identifier name of the team.
     * @param description A description of the team.
     * @return The ID of the created team.
     * @throws IllegalNameException If the name already exists in the platform.
     * @throws InvalidNameException If the new name is null, empty, has more than
     *                              30 characters.
     */
    @Override
    public int createTeam(String name, String description) throws IllegalNameException, InvalidNameException {
        // Check data
        if (name == null || name.equals("") || name.length() > 30) throw new InvalidNameException("Name is null, empty or has more than 30 characters");

        // Find duplicates and next valid ID
        int currentMaxTeamID = 0;
        for (Team t : teams) {
            if (t.id > currentMaxTeamID) currentMaxTeamID = t.id;
            if (t.name.equals(name)) throw new IllegalNameException("A Team with the same name already exists");
        }

        // Create the team
        Team newTeam = new Team();
        newTeam.id = currentMaxTeamID + 1;
        newTeam.name = name;
        newTeam.description = description;

        teams.add(newTeam);
        return newTeam.id;
    }

    /**
     * Removes a team from the system.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param teamId The ID of the team to be removed.
     * @throws IDNotRecognisedException If the ID does not match to any team in the
     *                                  system.
     */
    @Override
    public void removeTeam(int teamId) throws IDNotRecognisedException {
        Team team = getTeamByIDOrNull(teamId);
        if (team == null) throw new IDNotRecognisedException("ID not found in removeTeam");

        teams.remove(teamId - 1);

    }

    /**
     * Get the list of teams' IDs in the system.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @return The list of IDs from the teams in the system. An empty list if there
     * are no teams in the system.
     */
    @Override
    public int[] getTeams() {
        return teams.stream()
                .mapToInt(t -> t.id)
                .toArray();
    }


    /**
     * Get the riders of a team.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param teamId The ID of the team being queried.
     * @return A list with riders' ID.
     * @throws IDNotRecognisedException If the ID does not match to any team in the
     *                                  system.
     */
    @Override
    public int[] getTeamRiders(int teamId) throws IDNotRecognisedException {
        Team team = getTeamByIDOrNull(teamId);
        if (team == null) throw new IDNotRecognisedException("Team ID not found in getTeamRiders");

        return team.riders.stream().mapToInt(r -> r).toArray();
    }

    /**
     * Creates a rider.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param teamID      The ID rider's team.
     * @param name        The name of the rider.
     * @param yearOfBirth The year of birth of the rider.
     * @return The ID of the rider in the system.
     * @throws IDNotRecognisedException If the ID does not match to any team in the
     *                                  system.
     * @throws IllegalArgumentException If the name of the rider is null or the year
     *                                  of birth is less than 1900.
     */
    @Override
    public int createRider(int teamID, String name, int yearOfBirth) throws IDNotRecognisedException, IllegalArgumentException {
        Team team = getTeamByIDOrNull(teamID); if (team == null) throw new IDNotRecognisedException();
        if (name == null || yearOfBirth < 1900) throw new IllegalArgumentException();

        Rider newRider = new Rider();
        newRider.name = name; newRider.yearOfBirth = yearOfBirth;

        int maxRiderID = riders.stream().mapToInt(r -> r.id).max().orElse(0);
        newRider.id = maxRiderID + 1;
        newRider.results = new HashMap<>();

        team.riders.add(newRider.id);
        riders.add(newRider);
        return newRider.id;
    }

    /**
     * Removes a rider from the system. When a rider is removed from the platform,
     * all of its results should be also removed. Race results must be updated.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param riderId The ID of the rider to be removed.
     * @throws IDNotRecognisedException If the ID does not match to any rider in the
     *                                  system.
     */
    @Override
    public void removeRider(int riderId) throws IDNotRecognisedException {
        Rider rider = getRiderByIDOrNull(riderId); if (rider == null) throw new IDNotRecognisedException();

        Team team = teams.stream().filter(t -> t.riders.contains(rider.id)).findAny().orElse(null);
        if (team == null) {System.out.println("Consistency error. Corrupted data?"); return;}

        riders.remove(rider);
        team.riders.remove(rider.id - 1);
    }

    /**
     * Record the times of a rider in a stage.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param stageId     The ID of the stage the result refers to.
     * @param riderId     The ID of the rider.
     * @param checkpoints An array of times at which the rider reached each of the
     *                    segments of the stage, including the start time and the
     *                    finish line.
     * @throws IDNotRecognisedException    If the ID does not match to any rider or
     *                                     stage in the system.
     * @throws DuplicatedResultException   Thrown if the rider has already a result
     *                                     for the stage. Each rider can have only
     *                                     one result per stage.
     * @throws InvalidCheckpointsException Thrown if the length of checkpoints is
     *                                     not equal to n+2, where n is the number
     *                                     of segments in the stage; +2 represents
     *                                     the start time and the finish time of the
     *                                     stage.
     * @throws InvalidStageStateException  Thrown if the stage is not "waiting for
     *                                     results". Results can only be added to a
     *                                     stage while it is "waiting for results".
     */
    @Override
    public void registerRiderResultsInStage(int stageId, int riderId, LocalTime... checkpoints) throws IDNotRecognisedException, DuplicatedResultException, InvalidCheckpointsException, InvalidStageStateException {
        Rider rider = getRiderByIDOrNull(riderId);
        Stage stage = getStageByIDOrNull(stageId);
        if (rider == null) throw new IDNotRecognisedException("rider ID not recognized");
        if (stage == null) throw new IDNotRecognisedException("stage not recognized");
        if (rider.results.containsKey(stageId)) throw new DuplicatedResultException("results already exist");
        if (stage.state != StageState.WAITING_FOR_RESULTS) throw new InvalidStageStateException("invalid stage state");
        if (checkpoints.length != stage.segments.size() + 2) throw new InvalidCheckpointsException();

        rider.results.put(stageId, checkpoints);
    }

    /**
     * Get the times of a rider in a stage.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param stageId The ID of the stage the result refers to.
     * @param riderId The ID of the rider.
     * @return The array of times at which the rider reached each of the segments of
     * the stage and the total elapsed time. The elapsed time is the
     * difference between the finish time and the start time. Return an
     * empty array if there is no result registered for the rider in the
     * stage.
     * @throws IDNotRecognisedException If the ID does not match to any rider or
     *                                  stage in the system.
     */
    @Override
    public LocalTime[] getRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
        Rider rider = getRiderByIDOrNull(riderId);
        if (rider == null) throw new IDNotRecognisedException("Rider ID not found");
        return rider.results.get(stageId);

    }

    /**
     * For the general classification, the aggregated time is based on the adjusted
     * elapsed time, not the real elapsed time. Adjustments are made to take into
     * account groups of riders finishing very close together, e.g., the peloton. If
     * a rider has a finishing time less than one second slower than the
     * previous rider, then their adjusted elapsed time is the smallest of both. For
     * instance, a stage with 200 riders finishing "together" (i.e., less than 1
     * second between consecutive riders), the adjusted elapsed time of all riders
     * should be the same as the first of these riders, even if the real gap
     * between the 200th and the 1st rider is much bigger than 1 second. There is no
     * adjustments on elapsed time on time-trials.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param stageId The ID of the stage the result refers to.
     * @param riderId The ID of the rider.
     * @return The adjusted elapsed time for the rider in the stage. Return an empty
     * array if there is no result registered for the rider in the stage.
     * @throws IDNotRecognisedException If the ID does not match to any rider or
     *                                  stage in the system.
     */
    @Override
    public LocalTime getRiderAdjustedElapsedTimeInStage(int stageId, int riderId) throws IDNotRecognisedException {
        Stage stage = getStageByIDOrNull(stageId);
        if (stage == null) throw new IDNotRecognisedException();
        Rider rider = getRiderByIDOrNull(riderId);
        if (rider == null) throw new IDNotRecognisedException();

        if (!rider.results.containsKey(stageId)) return LocalTime.MIDNIGHT; // WARNING: docstring says to return empty array. The function does not return an array, so I used localtime.MIDNIGHT
        LocalTime thisRiderFinishTime = rider.results.get(stageId)[rider.results.get(stageId).length - 1];
        LocalTime thisRiderStartTime = rider.results.get(stageId)[0];
        if (stage.type == StageType.TT) return LocalTime.MIDNIGHT.plus(Duration.between(thisRiderStartTime, thisRiderFinishTime));

        // If this rider finished this stage less than a second after another rider then return the other rider's time.
        // This is the adjusted time
        Rider[] ridersThatTookPartInTheStage = riders.stream().filter(r -> r.results.containsKey(stageId)).toArray(Rider[]::new);
        // If the rider's time was adjusted then it may now be less than a second after another rider's time.
        // If so then run the adjustment again until no rider is in range
        boolean anAdjustmentWasMadeLastIteration = true;
        while (anAdjustmentWasMadeLastIteration) {
            for (Rider otherRider : ridersThatTookPartInTheStage) {
                LocalTime otherRiderFinishTime = otherRider.results.get(stageId)[otherRider.results.get(stageId).length - 1];
                long differenceBetweenFinishTimes = ChronoUnit.MILLIS.between(otherRiderFinishTime, thisRiderFinishTime);

                if (differenceBetweenFinishTimes < 1000 && differenceBetweenFinishTimes > 0) { // If I finish less than one second after another rider
                    thisRiderFinishTime = otherRiderFinishTime;
                    anAdjustmentWasMadeLastIteration = true;
                } else {
                    anAdjustmentWasMadeLastIteration = false;
                }
            }
        }

        return LocalTime.MIDNIGHT.plus(Duration.between(thisRiderStartTime, thisRiderFinishTime));
    }

    /**
     * Removes the stage results from the rider.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param stageId The ID of the stage the result refers to.
     * @param riderId The ID of the rider.
     * @throws IDNotRecognisedException If the ID does not match to any rider or
     *                                  stage in the system.
     */
    @Override
    public void deleteRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
        Rider rider = getRiderByIDOrNull(riderId);
        if (rider == null) throw new IDNotRecognisedException("Rider ID not found");

        rider.results.remove(stageId);
    }

    /**
     * Get the riders finished position in a stage.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param stageId The ID of the stage being queried.
     * @return A list of riders ID sorted by their elapsed time. An empty list if
     * there is no result for the stage.
     * @throws IDNotRecognisedException If the ID does not match any stage in the
     *                                  system.
     */
    @Override
    public int[] getRidersRankInStage(int stageId) throws IDNotRecognisedException {
        Stage stage = getStageByIDOrNull(stageId);
        if (stage == null) throw new IDNotRecognisedException();

        Rider[] applicableRiders = riders.stream().filter(r -> r.results.containsKey(stage.id)).toArray(Rider[]::new);

        HashMap<Long, Rider> results = new HashMap<>();
        Arrays.stream(applicableRiders).forEach(r -> {
            LocalTime[] times = r.results.get(stageId);
            Long time = ChronoUnit.MICROS.between(times[0], times[times.length - 1]);

            // If someone else has the exact same time then increase the time by one microsecond
            // I shouldn't have to do this, but I don't know any other decent way
            // I use this form because results.containsKey is always false according to the IDE
            while (results.containsKey(time)) time++;

            results.put(time, r);
        });

        ArrayList<Rider> ridersInOrder = hashMapValuesSortedByComparableKey(results);
        return ridersInOrder.stream().mapToInt(r -> r.id).toArray();
    }

    private <Key extends Comparable<? super Key>, O> ArrayList<O> hashMapValuesSortedByComparableKey(HashMap<Key, O> hm) {
        ArrayList<O> objectsInOrder =  new ArrayList<>();
        ArrayList<Key> keys = new ArrayList<>(hm.keySet());

        while(keys.size() > 0) {
            // Find the smallest key
            Key smallestKey = keys.stream().min(Key::compareTo).orElse(null);
            keys.remove(smallestKey);
            objectsInOrder.add(hm.get(smallestKey));
        }

        return objectsInOrder;
    }





    /**
     * Get the adjusted elapsed times of riders in a stage.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param stageId The ID of the stage being queried.
     * @return The ranked list of adjusted elapsed times sorted by their finish
     * time. An empty list if there is no result for the stage. These times
     * should match the riders returned by
     * {@link #getRidersRankInStage(int)}.
     * @throws IDNotRecognisedException If the ID does not match any stage in the
     *                                  system.
     */
    @Override
    public LocalTime[] getRankedAdjustedElapsedTimesInStage(int stageId) throws IDNotRecognisedException {
        int[] rankedRiderIds = getRidersRankInStage(stageId);
        // Key: RiderId. Val: Adjusted elapsed times.
        HashMap<Integer, LocalTime> adjustedTimes = new HashMap<>();
        for (int rid : rankedRiderIds) {
            adjustedTimes.put(rid, getRiderAdjustedElapsedTimeInStage(stageId, rid));
        }

        ArrayList<LocalTime> orderedResults = new ArrayList<>();
        for (int rid : rankedRiderIds) {
            orderedResults.add(adjustedTimes.get(rid));
        }

        return orderedResults.toArray(LocalTime[]::new);
    }

    /**
     * Get the number of points obtained by each rider in a stage.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param stageId The ID of the stage being queried.
     * @return The ranked list of points each rider received in the stage, sorted
     * by their elapsed time. An empty list if there is no result for the
     * stage. These points should match the riders returned by
     * {@link #getRidersRankInStage(int)}.
     * @throws IDNotRecognisedException If the ID does not match any stage in the
     *                                  system.
     */
    @Override
    public int[] getRidersPointsInStage(int stageId) throws IDNotRecognisedException {
        // Should this include intermediate sprints? YES
        Stage stage = getStageByIDOrNull(stageId);
        if (stage == null) throw new IDNotRecognisedException();

        int[] rankedRiderIds = getRidersRankInStage(stageId);

        ArrayList<Integer> riderPoints = new ArrayList<>();
        for (int riderIdIndex = 0; riderIdIndex < rankedRiderIds.length; riderIdIndex++) {
            riderPoints.add(stagePoints(stage.type, riderIdIndex) + getRidersSegmentPointsInStage(stageId, false)[riderIdIndex]);
        }
        return riderPoints.stream().mapToInt(i -> i).toArray();
    }



    /**
     * Get the number of mountain points obtained by each rider in a stage.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param stageId The ID of the stage being queried.
     * @return The ranked list of mountain points each rider received in the stage,
     * sorted by their finish time. An empty list if there is no result for
     * the stage. These points should match the riders returned by
     * {@link #getRidersRankInStage(int)}.
     * @throws IDNotRecognisedException If the ID does not match any stage in the
     *                                  system.
     */
    @Override
    public int[] getRidersMountainPointsInStage(int stageId) throws IDNotRecognisedException {
        return getRidersSegmentPointsInStage(stageId, true);
    }

    private int[] getRidersSegmentPointsInStage(int stageId, boolean mountainTrueSprintFalse) throws IDNotRecognisedException {
        // Should I include the intermediate sprint segment points? NO
        Stage stage = getStageByIDOrNull(stageId);
        if (stage == null) throw new IDNotRecognisedException();

        Segment[] segments = stage.segments.stream().map(this::getSegmentByIDOrNull).toArray(Segment[]::new);
        if (Arrays.stream(segments).anyMatch(Objects::isNull)) throw new IDNotRecognisedException();

        int[] rankedRiderIds = getRidersRankInStage(stageId);
        Rider[] rankedRiders = Arrays.stream(rankedRiderIds).mapToObj(this::getRiderByIDOrNull).toArray(Rider[]::new);
        if (Arrays.stream(rankedRiders).anyMatch(Objects::isNull)) throw new IDNotRecognisedException();

        // Foreach segment work out the rank
        HashMap<Rider, Integer> riderPointsInStage = new HashMap<>();
        for (int segmentIndex = 0; segmentIndex < segments.length; segmentIndex++) {
            if (mountainTrueSprintFalse && segments[segmentIndex].type == SegmentType.SPRINT) continue; // TODO Check if it works okay? // Excludes all intermediate sprints
            if (!mountainTrueSprintFalse && segments[segmentIndex].type != SegmentType.SPRINT) continue;

            HashMap<Duration, Rider> ridersDurationsInASegment = new HashMap<>();
            for (Rider rider : rankedRiders) {
                LocalTime[] ridersResults = rider.results.get(stageId);
                ridersDurationsInASegment.put(
                        Duration.between(ridersResults[segmentIndex], ridersResults[segmentIndex + 1]),
                        rider
                );
            }
            ArrayList<Rider> ridersRankInASegment = hashMapValuesSortedByComparableKey(ridersDurationsInASegment);

            // Convert a rider's rank into points
            // Key rider. Value points
            HashMap<Rider, Integer> riderSegPoints = new HashMap<>();
            for (int riderRnkSegIndex = 0; riderRnkSegIndex < ridersRankInASegment.size(); riderRnkSegIndex++) {
                riderSegPoints.put(
                        ridersRankInASegment.get(riderRnkSegIndex),
                        segmentPoints(segments[segmentIndex].type, riderRnkSegIndex)
                );
            }

            // riderSegPoints is now representative of each rider's total points in this segment
            for (Rider rider : riderSegPoints.keySet()) {
                if (!riderPointsInStage.containsKey(rider)) riderPointsInStage.put(rider, 0);
                riderPointsInStage.put(rider, riderPointsInStage.get(rider) + riderSegPoints.get(rider));
            }
        }

        // Now we have riderPointsInStage filled out
        ArrayList<Integer> rankedRiderPoints = new ArrayList<>();
        for (int riderId : rankedRiderIds) {
            Rider thisRiderObj = getRiderByIDOrNull(riderId); // Will not be null
            rankedRiderPoints.add(riderPointsInStage.get(thisRiderObj));
        }

        // Filter out riders with no associated points
        rankedRiderPoints = new ArrayList<>(rankedRiderPoints.stream().map(p -> p == null ? 0 : p).toList());

        return rankedRiderPoints.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Method empties this MiniCyclingPortalInterface of its contents and resets all
     * internal counters.
     */
    @Override
    public void eraseCyclingPortal() {
        teams = new ArrayList<>();
        riders = new ArrayList<>();
        races = new ArrayList<>();
        stages = new ArrayList<>();
        segments = new ArrayList<>();
    }

    /**
     * Method saves this MiniCyclingPortalInterface contents into a serialised file,
     * with the filename given in the argument.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param filename Location of the file to be saved.
     * @throws IOException If there is a problem experienced when trying to save the
     *                     store contents to the file.
     */
    @Override
    public void saveCyclingPortal(String filename) throws IOException {
        FileOutputStream file = new FileOutputStream(filename);
        ObjectOutputStream objOut = new ObjectOutputStream(file);
        objOut.writeObject(this);
        objOut.flush();
        objOut.close();
    }

    /**
     * Method should load and replace this MiniCyclingPortalInterface contents with the
     * serialised contents stored in the file given in the argument.
     * <p>
     * The state of this MiniCyclingPortalInterface must be unchanged if any
     * exceptions are thrown.
     *
     * @param filename Location of the file to be loaded.
     * @throws IOException            If there is a problem experienced when trying
     *                                to load the store contents from the file.
     * @throws ClassNotFoundException If required class files cannot be found when
     *                                loading.
     */
    @Override
    public void loadCyclingPortal(String filename) throws IOException, ClassNotFoundException {
        FileInputStream file = new FileInputStream(filename);
        ObjectInputStream objIn = new ObjectInputStream(file);
        CyclingPortal c = (CyclingPortal) objIn.readObject();

        teams = c.teams;
        riders = c.riders;
        races = c.races;
        stages = c.stages;
        segments = c.segments;

        objIn.close();
        file.close();
    }
}
