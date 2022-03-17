package bfst22.vector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;

// Is intended to draw relations
public class MultiPolygon implements Drawable, Serializable {
    public static final long serialVersionUID = 1325234;

    // List of what constitutes the relation.
    List<Drawable> parts = new ArrayList<>();

    public MultiPolygon(final ArrayList<OSMWay> rel) {
        for (OSMWay way : rel) this.parts.add(new PolyLine(way.nodes));
    }

    // Traces the area that has to be drawn before drawing.
    public void trace(final GraphicsContext gc) {
        for (Drawable part : this.parts) part.trace(gc);
    }
}
