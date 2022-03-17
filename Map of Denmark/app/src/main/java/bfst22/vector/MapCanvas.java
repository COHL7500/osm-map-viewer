package bfst22.vector;

import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

// defines the canvas of our map; panning, zooming, painting etc.
// Whenever we add new interaction with the map, we use this class.
public class MapCanvas extends Canvas {
    Model model;
    Affine trans = new Affine();
    double zoom_current = 1;

    // Runs upon startup (setting default pan, zoom for example).
    void init(Model model) {
        this.model = model;
        this.pan(-model.minlon, -model.minlat);

        // Default zoom level: 700
        this.zoom(700 / (model.maxlon - model.minlon), 0, 0);

        // Observer notifies the change in a particular state, being our repaint in this case.
        model.addObserver(this::repaint);

        // Instantly paints upon initialization
        this.repaint();
    }

    // Draws all of the elements of our map.
    void repaint() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setTransform(new Affine());

        // Clears the screen for the next frame
        gc.clearRect(0, 0, getWidth(), getHeight());

        // Performs linear mapping between Point2D points. Our trans is Affine:
        // https://docs.oracle.com/javase/8/javafx/api/javafx/scene/transform/Affine.html
        gc.setTransform(trans);

        for(keyFeature element : model.yamlObj.ways.values()){
            this.setStylingDefault(gc);

            for (valueFeature element2 : element.valuefeatures.values()) {
                this.setStyling(gc, element.draw);
                this.setStyling(gc, element2.draw);

                for(Drawable draw : element2.drawable){
                    if (element.draw != null && element.draw.fill && element.draw.zoom_level < this.zoom_current
                            || element2.draw != null && element2.draw.fill && element2.draw.zoom_level < this.zoom_current) draw.fill(gc);
                    if (element.draw != null && element.draw.stroke && element.draw.zoom_level < this.zoom_current
                            || element2.draw != null && element2.draw.stroke && element2.draw.zoom_level < this.zoom_current) draw.draw(gc);
                    //if(element2.name != null && element2.name.length() > 0) this.drawText(gc,element2.name);
                }
            }
        }
    }

    public void setStyling(GraphicsContext gc, featureDraw draw){
        if(draw != null) {
            if (draw.stroke_color != null) gc.setStroke(Color.web(draw.stroke_color));
            if (draw.line_width != 0) gc.setLineWidth(draw.line_width);
            if (draw.fill_color != null) gc.setFill(Color.web(draw.fill_color));
            if (draw.dash_size != 0) gc.setLineDashes(draw.dash_size);
        }
    }

    public void setStylingDefault(GraphicsContext gc){
        gc.setFill(Color.BLACK);
        gc.setLineWidth(0.00001);
        gc.setStroke(Color.BLACK);
        gc.setLineDashes(1);
    }

    public void drawText(GraphicsContext gc, String text){
        gc.setFill(Color.BLACK);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(text, 0, getHeight()/2);
    }

    // Allows the user to navigate around the map by panning.
    // this is used in onMouseDragged from Controller.
    public void pan(final double dx, final double dy) {
        this.trans.prependTranslation(dx, dy);
        this.repaint();
    }

    // Allows the user to zoom in on the map.
    // this is used in onScroll from Controller.
    public void zoom(final double factor, final double x, final double y) {
        this.zoom_current *= factor;
        this.trans.prependTranslation(-x, -y);
        this.trans.prependScale(factor, factor);
        this.trans.prependTranslation(x, y);
        this.repaint();
    }

    // Uncertain what the utility of this method is.
    public Point2D mouseToModel(final Point2D point) {
        try {
            return this.trans.inverseTransform(point);
        } catch (NonInvertibleTransformException e) {
            throw new RuntimeException(e);
        }
    }
}