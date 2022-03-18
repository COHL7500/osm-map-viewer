package bfst22.vector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// Defines what a way is in the OSM file and its properties.
public class OSMWay implements Serializable, SerialVersionIdentifiable {
    List<OSMNode> nodes;

    public OSMWay(final List<OSMNode> nodes) {
        this.nodes = new ArrayList<>(nodes);
    }
}
