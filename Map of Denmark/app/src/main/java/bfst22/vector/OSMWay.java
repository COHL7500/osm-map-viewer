package bfst22.vector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;

// Defines what a way is in the OSM file and its properties.

public class OSMWay implements Serializable {
    public static final long serialVersionUID = 42;
    List<OSMNode> nodes;
    public OSMWay(List<OSMNode> nodes) {
        this.nodes = new ArrayList<>(nodes);
    }
}
