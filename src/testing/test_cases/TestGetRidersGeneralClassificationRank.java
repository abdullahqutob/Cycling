package testing.test_cases;

import cycling.*;
import testing.data.ComprehensiveTestData;

import java.util.Arrays;

@SuppressWarnings("SameReturnValue")
public class TestGetRidersGeneralClassificationRank extends testing.TestCase {
    final CyclingPortal c = new CyclingPortal();

    public String testTest() throws InvalidStageStateException, InvalidNameException, InvalidLocationException, IDNotRecognisedException, InvalidLengthException, IllegalNameException, InvalidStageTypeException, DuplicatedResultException, InvalidCheckpointsException, AssertError {
        ComprehensiveTestData d = new ComprehensiveTestData();
        d.add(c);

        // JB: 48
        // JS: 54
        // JD: 65
        // AJ: 34
        int[] expected = new int[]{d.AJ, d.JB, d.JS, d.JD};
        int[] res = c.getRidersGeneralClassificationRank(c.getRaceIds()[0]); // UF race
        assertEqual(true, Arrays.equals(res, expected));
        return null;
    }
}
