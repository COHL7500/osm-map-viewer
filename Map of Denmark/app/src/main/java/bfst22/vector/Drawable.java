package bfst22.vector;

import javafx.scene.canvas.GraphicsContext;

// Interface defining the core requirements for a drawable entity.

public interface Drawable {

    // The default keyword allow methods in an interface to have a body.

    // draws the current element.
    default void draw(GraphicsContext gc) {
        gc.beginPath();
        trace(gc);
        gc.stroke();
    }

    // fills an object.
    default void fill(GraphicsContext gc) {
        gc.beginPath();
        trace(gc);
        gc.fill();
    }

    // traces the element's area for where it has to be drawn.
    void trace(GraphicsContext gc);
}
