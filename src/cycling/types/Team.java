package cycling.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Team implements Serializable {
    public int id;
    public String name;
    public String description;
    public final List<Integer> riders = new ArrayList<>();
}
