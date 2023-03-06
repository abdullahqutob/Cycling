package testing.test_cases;

import cycling.*;


public class TestRaceRemove extends testing.TestCase {
    final CyclingPortal r1 = new CyclingPortal();
    final CyclingPortal r2 = new CyclingPortal();

    public String testRemoveRaceById() {
        // setup for upcoming tests
        int raceID;
        try {
            raceID = r1.createRace("test race to remove", null);
        } catch (InvalidNameException | IllegalNameException e) {
            return "createRace had an exception with valid inputs";
        }

        // Tests
        try {
            r1.removeRaceById(raceID);
        } catch (IDNotRecognisedException e) {
            return "ID not recognised";
        }
        //confirming previous test works
        try {
            r1.removeRaceById(raceID);
            return "remove race threw no exception when run twice on the same ID";
        } catch (IDNotRecognisedException e) {
            //previous test was a success, can't delete same race twice
            return null;
        }

    }

    public String testRemoveRaceByName() {
        // creating the race for further tests
        try {
            r2.createRace("testName", null);
        } catch (InvalidNameException | IllegalNameException e) {
            return "createRace had an exception with valid inputs";
        }

        //removing race by name
        try {
            r2.removeRaceByName("testName");
        } catch (NameNotRecognisedException e) {
            return "valid name not recognized";
        }
        //confirming previous test works
        try {
            r2.removeRaceByName("testName");
            return "no exception thrown when run on the same name";
        } catch (NameNotRecognisedException e) {
            //twas a success, can't delete twice
            return null;
        }
    }
}
