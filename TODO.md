# TODO

Check all remove that it removes in all places
Correct invalid name except in correct places
Is a averageGradient a fraction or a percent
Recursive delete for deletion functions

## Remember/New things
- Test functions should have a `throws` statement that passes all errors that they are not explicitly testing for
- Test functions should now throw `AssertError` and use the functions `assertEqual` and `assertNotEqual` instead of the `assert` keyword
- Test Classes should be named in descending order of significance e.g. `testRidersDelete`, `testRidersGetIDs` etc. Test methods can be named however you'd like
- When you throw an error in a `CyclingPortal` function it should contain a message explaining why it happened e.g. `throw new InvalidNameException("Name was null, empty or over 30 characters")`
- Test return for empty arrays. Not null.

## race -> stage -> segment
Testing: Abdullah.
Coding: Rillian.

- [x] createRace
- [x] getRaceIds
- [x] removeRaceById
- [x] removeRaceByName
- ---
- [x] addStageToRace
- [x] getRaceStages
- [x] getNumberOfStages
- [x] getStageLength
- [x] removeStageById
- ---
- ---
- [x] getStageSegments
- [x] addCategorizedClimbToStage
- [x] addIntermediateSprintToStage
- [x] concludeStagePreparation
- [x] removeSegment

## team -> races -> riders
Testing: Rillian.
Coding: Abdullah.

- [x] createTeam
- [x] removeTeam
- [x] getTeams
- ---
- [x] createRider
- [x] removeRider
- [x] getTeamRiders
- ---
- ---
- [x] registerRiderResultsInStage
- [x] getRiderResultsInStage
- [x] deleteRiderResultsInStage

## Maths
### Testing: Rillian. Code: Abdullah
- [ ] getRidersGeneralClassificationRank
- [ ] getGeneralClassificationTimesInRace
- [ ] getRidersPointClassificationRank
- [ ] getRidersMountainPointClassificationRank
- ---
- [x] getRidersRankInStage
- [ ] viewRaceDetails
### Testing: Abdullah. Code: Rillian
- [ ] getRidersPointsInRace
- [ ] getRidersMountainPointsInRace
- [ ] getRidersPointsInStage
- [ ] getRidersMountainPointsInStage
- ---
- [ ] getRankedAdjustedElapsedTimesInStage
- [ ] getRiderAdjustedElapsedTimeInStage

## Management
- [ ] eraseCyclingPortal
- [ ] saveCyclingPortal
- [ ] loadCyclingPortal

## TODO After complete coding
- Make all exception messages uniform
- Executable .jar?

## Personal notes
### Other of order method
```text
getRidersRankInStage =>

getRiderAdjustedElapsedTimeInStage => done
getGeneralClassificationTimesInRace done
getRidersGeneralClassificationRank done


======

getRidersPointsInStage done ->
getRidersPointClassificationRank done

getRidersMountainPointsInStage done ->
getRidersMountainPointClassificationRank done

==> getRidersMountainPointsInRace done
==> getRidersPointsInRace done


also: getRankedAdjustedElapsedTimesInStage
```

### Testing of other methods
```text
getRankedAdjustedElapsedTimesInStage done
getRidersRankInStage done
getRiderAdjustedElapsedTimeInStage done

======

getRidersPointsInStage done
getRidersPointClassificationRank done

getRidersMountainPointsInStage done
getRidersMountainPointClassificationRank done

=======

getGeneralClassificationTimesInRace done
getRidersMountainPointsInRace done
getRidersPointsInRace done
getRidersGeneralClassificationRank done
```