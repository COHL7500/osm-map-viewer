package bfst22.vector;

import java.io.Serializable;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;

// Defines the lines intended to draw the polygons on the map; typically used for ways.
public class PolyLine implements Drawable, Serializable, SerialVersionIdentifiable {
    private final float[] coords;
    private double[] center;

    // Constructs the line based on the given nodes for the particular polygon.
    public PolyLine(final List<OSMNode> nodes) {
        this.coords = new float[nodes.size() * 2];
        int i = 0;
        for (var node : nodes) {
            coords[i++] = node.lat;
            coords[i++] = node.lon;
        }
        this.findCenter(nodes);
    }

    // traces the are needed to be drawn before drawing.
    @Override public void trace(GraphicsContext gc) {
        gc.moveTo(coords[0], coords[1]);
        for (var i = 2 ; i < coords.length ; i += 2) {
            gc.lineTo(coords[i], coords[i+1]);
        }
    }

    private void findCenter(final List<OSMNode> nodes){
        double[] sum = new double[2];

        for(OSMNode node : nodes){
            sum[0] += node.lat;
            sum[1] += node.lon;
        }

        sum[0] /= nodes.size();
        sum[1] /= nodes.size();
        this.center = sum;
    }

    public double[] getCenter(){
        return this.center;
    }
}