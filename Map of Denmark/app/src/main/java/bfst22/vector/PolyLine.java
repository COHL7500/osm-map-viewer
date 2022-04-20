package bfst22.vector;

import java.io.Serializable;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;

// Defines the lines intended to draw the polygons on the map; typically used for ways.
public class PolyLine implements Drawable, Serializable, SerialVersionIdentifiable {
    public float[] coords;

    // Constructs the line based on the given nodes for the particular polygon.
    public PolyLine(final List<PolyPoint> nodes) {
        this.coords = new float[nodes.size() * 3];
        int i = 0;
        for (PolyPoint node : nodes) {
            coords[i++] = node.id;
            coords[i++] = node.lat;
            coords[i++] = node.lon;
        }
    }

    public void mirror(){
        float[] mirror = new float[this.coords.length];
        for(int i = 0; i < this.coords.length; i+=3){
            mirror[mirror.length-i-3] = this.coords[i];
            mirror[mirror.length-i-2] = this.coords[i+1];
            mirror[mirror.length-i-1] = this.coords[i+2];
        }
        this.coords = mirror;
    }

    // traces the are needed to be drawn before drawing.
    @Override public void trace(GraphicsContext gc) {
        gc.moveTo(coords[1], coords[2]);
        for (var i = 0; i < coords.length; i += 3) gc.lineTo(coords[i + 1], coords[i + 2]);
    }
}