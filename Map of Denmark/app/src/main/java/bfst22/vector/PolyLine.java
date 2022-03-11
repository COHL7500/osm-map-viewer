package bfst22.vector;

import java.io.Serializable;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;

// Defines the lines intended to draw the polygons on the map; typically used for ways.

public class PolyLine implements Drawable, Serializable {
    public static final long serialVersionUID = 134123;
    float[] coords;

    // Constructs the line based on the given nodes for the particular polygon.
    public PolyLine(List<OSMNode> nodes) {
        coords = new float[nodes.size() * 2];
        int i = 0;
        for (var node : nodes) {
            coords[i++] = node.lat;
            coords[i++] = node.lon;
        }
    }

    // traces the are needed to be drawn before drawing.
    @Override
    public void trace(GraphicsContext gc) {
        gc.moveTo(coords[0], coords[1]);
        for (var i = 2 ; i < coords.length ; i += 2) {
            gc.lineTo(coords[i], coords[i+1]);
        }
    }
}
