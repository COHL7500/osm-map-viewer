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