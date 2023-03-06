package cycling.types;

import cycling.StageType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Stage implements Serializable {
    public int id;
    public String name;
    public String description;
    public double length;
    public StageType type;
    public LocalDateTime startTime;
    public final List<Integer> segments = new ArrayList<>();
    public StageState state = StageState.SETUP;
}