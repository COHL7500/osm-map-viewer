package bfst22.vector;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

// Interface defining the core requirements for a drawable entity.
public interface Drawable {
    // The default keyword allow methods in an interface to have a body.
    // draws the current element.
    default void draw(GraphicsContext gc) {
        gc.beginPath();
        trace(gc);
        gc.stroke();
        gc.closePath(); // Just as a safety measure; uncertain if it does anything different.
    }

    // fills an object.
    default void fill(GraphicsContext gc) {
        gc.beginPath();
        trace(gc);
        gc.fill();
        gc.closePath();
    }

    // traces the element's area for where it has to be drawn.
    void trace(GraphicsContext gc);
}

class Node implements Comparable<Node> {
    public float[] coords;
    public Drawable obj;
    private int compareAxis;

    public Node(float lat, float lon, Drawable objRef){
        this.coords = new float[]{lat,lon};
        this.obj = objRef;
        this.compareAxis = 0;
    }

    public void setCompareAxis(int i){
        this.compareAxis = i;
    }

    @Override public int compareTo(Node node){
        return Float.compare(this.coords[this.compareAxis], node.coords[this.compareAxis]);
    }
}