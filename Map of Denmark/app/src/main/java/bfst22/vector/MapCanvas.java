package bfst22.vector;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import java.util.Set;

// defines the canvas of our map; panning, zooming, painting etc.
// Whenever we add new interaction with the map, we use this class.
public class MapCanvas extends Canvas {
    Model model;
    Affine trans = new Affine();
    GraphicsContext gc = super.getGraphicsContext2D();
    double zoom_current = 1, minx = 0, miny = 0, maxx = 0, maxy = 0, originx = 0, originy = 0, mousex = 0, mousey = 0;
    boolean debugCursor = true, debugVisBox = true, debugDisableHelpText = true;

    // Runs upon startup (setting default pan, zoom for example).
    public void init(final Model model) {
        this.model = model;
        this.pan(-model.minlon, -model.minlat);

        // Default zoom level: 700
        this.zoom(700 / (model.maxlon - model.minlon), 0, 0);

        // Observer notifies the change in a particular state, being our repaint in this case.
        this.model.addObserver(this::repaint);
    }

    // Draws all of the elements of our map.
    private void repaint() {
        this.gc.setTransform(new Affine());

        // Clears the screen for the next frame
        this.gc.clearRect(0, 0, super.getWidth(), super.getHeight());

        // Performs linear mapping between Point2D points. Our trans is Affine:
        // https://docs.oracle.com/javase/8/javafx/api/javafx/scene/transform/Affine.html
        this.gc.setTransform(trans);

        double padding = 100 / zoom_current;
        Set<Drawable> range;

        this.setStylingDefault();

        if(this.debugVisBox) range = this.model.kdtree.rangeSearch(new double[]{this.miny+padding, this.minx+padding}, new double[]{this.maxy-padding, this.maxx-padding});
        else range = this.model.kdtree.rangeSearch(new double[]{this.miny, this.minx}, new double[]{this.maxy, this.maxx});

        for(Drawable obj : range) obj.draw(this.gc);

        this.strokeCursor();
        this.strokeBox(100);

        //Set<valueFeature> featureList = new HashSet<>();

        // Loops through all the key features and sets the default styling for all its objects
        /*for(keyFeature element : model.yamlObj.ways.values()){
            this.setStylingDefault();

            // Loops through all value features and sets first eventual key feature styling and then eventual any value styles set
            for (valueFeature element2 : element.valuefeatures.values()) {
                if(element2.display) {
                    // Loops through all drawable elements that shall be drawn to the screen
                    // Checks if the styling requires them to be drawn with filling and/or strokes
                    // and then proceed to draw the value feature in the way it has been told to
                    for (Drawable draw : element2.drawable) {
                        this.setStyling(element.draw);
                        this.setStyling(element2.draw);

                        if (element.draw != null && element.draw.fill && element.draw.zoom_level < this.zoom_current
                                || element2.draw != null && element2.draw.fill && element2.draw.zoom_level < this.zoom_current)
                            draw.fill(this.gc);
                        if (element.draw != null && element.draw.stroke && element.draw.zoom_level < this.zoom_current
                                || element2.draw != null && element2.draw.stroke && element2.draw.zoom_level < this.zoom_current)
                            draw.draw(this.gc);
                        if (element2.name != null && element2.nameCenter != null && element2.name.length() > 0)
                            featureList.add(element2);
                    }
                }
            }
        }*/

        //featureList.forEach(element2 -> this.drawText(element2.name, element2.nameCenter));
    }

    // Sets the current styling options for graphicscontext based on eventual keyfeature/valuefeature values provided
    private void setStyling(final featureDraw draw){
        if(draw != null) {
            if (draw.stroke_color != null)  this.gc.setStroke(Color.web(draw.stroke_color));
            if (draw.line_width != 0)       this.gc.setLineWidth(draw.line_width);
            if (draw.fill_color != null)    this.gc.setFill(Color.web(draw.fill_color));
            if (draw.dash_size != 0)        this.gc.setLineDashes(draw.dash_size);
        }
    }

    // Sets the default styling options for graphicscontext in case no values for keyfeature/valuefeature are provided
    private void setStylingDefault(){
        this.gc.setFill(Color.BLACK);
        this.gc.setLineWidth(0.00001);
        this.gc.setStroke(Color.BLACK);
        this.gc.setLineDashes(1);
    }

    private void strokeBox(double padding){
        if(this.debugVisBox){
            padding /= zoom_current;
            double csize = 5 / zoom_current;
            this.gc.setLineWidth(1/zoom_current);
            this.gc.setStroke(Color.BLUE);
            this.gc.setLineDashes(3/zoom_current);
            this.gc.setFont(new Font("Arial",11/zoom_current));
            this.gc.beginPath();
            this.gc.moveTo(this.minx+padding,this.miny+padding);
            this.gc.lineTo(this.minx+padding,this.miny+padding);
            this.gc.lineTo(this.minx+padding,this.maxy-padding);
            this.gc.lineTo(this.maxx-padding,this.maxy-padding);
            this.gc.lineTo(this.maxx-padding,this.miny+padding);
            this.gc.lineTo(this.minx+padding,this.miny+padding);
            this.gc.stroke();
            this.gc.closePath();
            this.gc.setFill(Color.BLACK);
            this.gc.fillOval(originx,originy,csize,csize);
            this.gc.fillOval(minx+padding-csize,miny+padding-csize,csize,csize);
            this.gc.fillOval(maxx-padding,miny+padding-csize,csize,csize);
            this.gc.fillOval(maxx-padding,maxy-padding,csize,csize);
            this.gc.fillOval(minx+padding-csize,maxy-padding,csize,csize);
            if(this.debugDisableHelpText) {
                this.gc.fillText("origin (" + String.format("%.5f", originx) + "," + String.format("%.5f", originy) + ")", originx + csize, originy - csize);
                this.gc.fillText("top left (" + String.format("%.5f", minx + padding) + "," + String.format("%.5f", miny + padding) + ")", minx + padding + csize, miny + padding - csize);
                this.gc.fillText("top right (" + String.format("%.5f", maxx - padding) + "," + String.format("%.5f", miny + padding) + ")", maxx - padding + csize, miny + padding - csize);
                this.gc.fillText("bottom right (" + String.format("%.5f", maxx - padding) + "," + String.format("%.5f", maxy - padding) + ")", maxx - padding + csize, maxy - padding - csize);
                this.gc.fillText("bottom left (" + String.format("%.5f", minx + padding) + "," + String.format("%.5f", maxy - padding) + ")", minx + padding + csize, maxy - padding - csize);
            }
        }
    }

    private void strokeCursor(){
        if(this.debugCursor){
            this.gc.setLineWidth(1);
            this.gc.setFill(Color.RED);
            this.gc.fillOval(mousex,mousey,5/zoom_current,5/zoom_current);
            this.gc.setFill(Color.BLACK);
            this.gc.setFont(new Font("Arial",11/zoom_current));
            if(this.debugDisableHelpText) this.gc.fillText("cursor (" + String.format("%.5f", mousex) + "," + String.format("%.5f", mousey) + ")",mousex+5/zoom_current,mousey-5/zoom_current);
        }
    }

    // Draws any kind of provided text to the screen
    private void drawText(final String text, final double[] coords){
        this.gc.setLineWidth(0.00001);
        this.gc.setFill(Color.BLACK);
        this.gc.setFont(new Font("Arial", 0.0001));
        this.gc.fillText(text,coords[0],coords[1]);
    }

    // Allows the user to navigate around the map by panning.
    // this is used in onMouseDragged from Controller.
    public void pan(final double dx, final double dy) {
        this.trans.prependTranslation(dx, dy);
        this.setScale(dx,dy);
        this.repaint();
    }

    // Allows the user to zoom in on the map.
    // this is used in onScroll from Controller.
    public void zoom(final double factor, final double dx, final double dy){
        this.zoom_current *= factor;
        this.trans.prependScale(factor, factor);
        this.repaint();
    }

    public void setScale(final double dx, final double dy){
        this.minx -= dx / zoom_current;
        this.miny -= dy / zoom_current;
        this.maxx = this.minx+super.getWidth() / zoom_current;
        this.maxy = (this.miny+super.getHeight() / zoom_current) - 25/zoom_current;
        this.originx = minx+(maxx-minx) / 2;
        this.originy = miny+(maxy-miny) / 2;
    }

    public void setMousePos(final Point2D point){
        this.mousex = point.getX() / zoom_current + this.minx;
        this.mousey = point.getY() / zoom_current + this.miny;
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

    public void clearScreen(){
        this.gc.clearRect(0, 0, super.getWidth(), super.getHeight());
    }
}