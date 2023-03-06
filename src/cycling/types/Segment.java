package cycling.types;

import cycling.SegmentType;

import java.io.Serializable;

public class Segment implements Serializable {
    public int id;
    public SegmentType type;
    public double location;
    public double averageGradient;
}
