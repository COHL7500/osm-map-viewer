package bfst22.vector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;

// Is intended to draw relations
public class MultiPolygon implements Drawable, Serializable, SerialVersionIdentifiable {
    List<Drawable> parts = new ArrayList<>(); // List of what constitutes the relation.

    public MultiPolygon(final List<OSMWay> rel) {
        for (OSMWay way : rel) this.parts.add(new PolyLine(way.nodes));
    }

    // Traces the area that has to be drawn before drawing.
    @Override public void trace(final GraphicsContext gc) {
        for (Drawable part : this.parts) part.trace(gc);
    }
}