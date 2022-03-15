package bfst22.vector;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

// defines the canvas of our map; panning, zooming, painting etc.
// Whenever we add new interaction with the map, we use this class.

public class MapCanvas extends Canvas {
    Model model;
    Affine trans = new Affine();

    // Runs upon startup (setting default pan, zoom for example).

    void init(Model model) {
        this.model = model;
        pan(-model.minlon, -model.minlat);

        // Default zoom level: 700
        zoom(700 / (model.maxlon - model.minlon), 0, 0);

        // Observer notifies the change in a particular state, being our repaint in this case.
        model.addObserver(this::repaint);

        // Instantly paints upon initialization
        repaint();
    }


    // Draws all of the elements of our map.
    void repaint() {
        var gc = getGraphicsContext2D();
        gc.setTransform(new Affine());

        // Background color
        gc.setFill(Color.WHITESMOKE);

        // Uncertain what this does exactly, but yeah.. Try and see what happens if you comment it  out.
        gc.fillRect(0, 0, getWidth(), getHeight());

        // Performs linear mapping between Point2D points. Our trans is Affine:
        // https://docs.oracle.com/javase/8/javafx/api/javafx/scene/transform/Affine.html
        gc.setTransform(trans);

        gc.setLineWidth(0.000035);

        // specifies line properties of our entities with the type HIGHWAY in our lines list.
        for(var line : model.iterable((WayType.PRIMARY)))
        {
            gc.setStroke(Color.BLACK);
            line.draw(gc);
            gc.setStroke(Color.web("#fdd7a1"));
            line.draw(gc);
        }

        gc.setLineWidth(0.00002);

        for(var line : model.iterable((WayType.SECONDARY)))
        {
            gc.setStroke(Color.BLACK);
            line.draw(gc);
            gc.setStroke(Color.web("#f6fabb"));
            line.draw(gc);
        }

        for(var line : model.iterable((WayType.TERTIARY)))
        {
            gc.setStroke(Color.BLACK);
            line.draw(gc);
            gc.setStroke(Color.WHITE);
            line.draw(gc);
        }

        // specifies fill properties all entities with the type WATER in our lines list.
        for (var line : model.iterable(WayType.WATER)) {
            gc.setFill(Color.LIGHTBLUE);
            line.fill(gc);
        }

        for(var line : model.iterable((WayType.BUILDING)))
        {
            gc.setFill(Color.web("#d8d0c9"));
            line.fill(gc);
        }

        gc.setLineWidth(1/Math.sqrt(trans.determinant()));

        // draws all entities with no particular type (their tag has not yet been accounted for).
        // In the final product, this should be removed.


        for (var line : model.iterable(WayType.UNKNOWN)) {
            gc.setStroke(Color.BLACK);
            line.draw(gc);
        }

        for(var line : model.iterable((WayType.ROUTE)))
        {
            gc.setFill(Color.LIGHTBLUE);
            line.fill(gc);
        }
    }

    // Allows the user to navigate around the map by panning.
    // this is used in onMouseDragged from Controller.
    void pan(double dx, double dy) {
        trans.prependTranslation(dx, dy);
        repaint();
    }

    // Allows the user to zoom in on the map.
    // this is used in onScroll from Controller.
    void zoom(double factor, double x, double y) {
        trans.prependTranslation(-x, -y);
        trans.prependScale(factor, factor);
        trans.prependTranslation(x, y);
        repaint();
    }

    // Uncertain what the utility of this method is.

    public Point2D mouseToModel(Point2D point) {
        try {
            return trans.inverseTransform(point);
        } catch (NonInvertibleTransformException e) {
            throw new RuntimeException(e);
        }
    }

}