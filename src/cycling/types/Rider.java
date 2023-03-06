package cycling.types;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.HashMap;

public class Rider implements Serializable {
    public int id;
    public String name;
    public int yearOfBirth;
    // Integer key is the stage id, the array of times is the rider's times for the respective segments
    public HashMap<Integer, LocalTime[]> results;
}
