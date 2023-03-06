package cycling.types;

import cycling.StageType;

import java.io.Serializable;
import java.util.ArrayList;

public class Race implements Serializable {
    public int id;
    public String name;
    public String description;
    public StageType type;
    public final ArrayList<Integer> stages = new ArrayList<>();
}
