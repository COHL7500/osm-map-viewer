package bfst22.vector;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;

// Is intended to draw relations
public class MultiPolygon implements Drawable, Serializable, SerialVersionIdentifiable {
    List<PolyLine> parts = new ArrayList<>(); // List of what constitutes the relation.

    public MultiPolygon(final List<PolyLine> rel) {
        this.parts.addAll(rel);
        this.removeLoops();
        this.mirrorReversed();
    }

    private void removeLoops(){
        this.parts.removeIf(poly -> {
            float[] a = poly.coords;
            return ((int) a[0]) == ((int) a[a.length - 3]);
        });
    }

    private void mirrorReversed(){
        for(int i = 1; i < this.parts.size(); i++){
            float[] a1 = this.parts.get(i-1).coords, a2 = this.parts.get(i).coords;
            if(a1[a1.length-1] == a2[a2.length-1]) this.parts.get(i).mirror();
        }
    }

    // Traces the area that has to be drawn before drawing.
    @Override public void trace(final GraphicsContext gc) {
        gc.moveTo(this.parts.get(0).coords[1],this.parts.get(0).coords[2]);
        this.parts.forEach(poly -> {
            for(int i = 0; i < poly.coords.length; i+=3){
                gc.lineTo(poly.coords[i+1],poly.coords[i+2]);
            }
        });
    }
}