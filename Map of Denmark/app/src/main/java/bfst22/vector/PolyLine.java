package bfst22.vector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;

// Defines the lines intended to draw the polygons on the map; typically used for ways.
public class PolyLine implements Drawable, Serializable, SerialVersionIdentifiable {
    private final float[] coords;

    // Constructs the line based on the given nodes for the particular polygon.
    public PolyLine(final List<OSMNode> nodes) {
        this.coords = new float[nodes.size() * 2];
        int i = 0;
        for (OSMNode node : nodes) {
            coords[i++] = node.lat;
            coords[i++] = node.lon;
        }
    }

    public List<Node> getNodes() {
        List<Node> nodes = new ArrayList<>();
        for(int i = 0; i < this.coords.length; i+=2)
            nodes.add(new Node(this.coords[i],this.coords[i+1],this));
        return nodes;
    }

    // traces the are needed to be drawn before drawing.
    @Override public void trace(GraphicsContext gc) {
        gc.moveTo(coords[0], coords[1]);
        for (var i = 2 ; i < coords.length ; i += 2) {
            gc.lineTo(coords[i], coords[i+1]);
        }
    }
}