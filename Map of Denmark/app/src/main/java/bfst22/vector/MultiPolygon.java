package bfst22.vector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;

// Is intended to draw relations
public class MultiPolygon implements Drawable, Serializable, SerialVersionIdentifiable {
    List<Drawable> parts = new ArrayList<>(); // List of what constitutes the relation.
    private double[] center;

    public MultiPolygon(final ArrayList<OSMWay> rel) {
        for (OSMWay way : rel) this.parts.add(new PolyLine(way.nodes));
        this.findCenter();
    }

    // Traces the area that has to be drawn before drawing.
    public void trace(final GraphicsContext gc) {
        for (Drawable part : this.parts) part.trace(gc);
    }

    private void findCenter(){
        double[] sum = new double[2];

        for(Drawable poly : this.parts){
            PolyLine polydraw = (PolyLine) poly;
            double[] polysum = polydraw.getCenter();

            sum[0] += polysum[0];
            sum[1] += polysum[1];
        }

        sum[0] /= this.parts.size();
        sum[1] /= this.parts.size();
        this.center = sum;
    }

    public double[] getCenter(){
        return this.center;
    }
}