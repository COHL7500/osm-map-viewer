package bfst22.vector;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import java.util.*;

// defines the canvas of our map; panning, zooming, painting etc.
// Whenever we add new interaction with the map, we use this class.
public class MapCanvas extends Canvas {
    public Model model;
    public Affine trans = new Affine();
    public Controller controller;
    public GraphicsContext gc = super.getGraphicsContext2D();
    public double zoom_current = 1, minx = 0, miny = 0, maxx = 0, maxy = 0, originx = 0, originy = 0, mousex = 0, mousey = 0;
    public boolean debugCursor = true, debugVisBox = true, debugSplits = false, debugInfo = true,
            debugDisableHelpText = true, debugBoundingBox = true, debugFreeMovement = false, debugDisplayWireframe = false;
    public long repaintTime, avgRT = 0, avgRTNum = 0;
    // https://stackoverflow.com/questions/12636613/how-to-calculate-moving-average-without-keeping-the-count-and-data-total

    // Runs upon startup (setting default pan, zoom for example).
    public void init(final Model model, final Controller controller) {
        this.model = model;
        this.controller = controller;
        this.pan(-model.minlon, -model.minlat);

        // Default zoom level: 700
        this.zoom(700 / (model.maxlon - model.minlon));
    }

    // Draws all of the elements of our map.
    private void repaint() {
        this.repaintTime = System.nanoTime();
        this.gc.setTransform(new Affine());

        // Clears the screen for the next frame
        this.gc.clearRect(0, 0, super.getWidth(), super.getHeight());

        // Performs linear mapping between Point2D points. Our trans is Affine:
        // https://docs.oracle.com/javase/8/javafx/api/javafx/scene/transform/Affine.html
        this.gc.setTransform(trans);

        double padding = this.debugVisBox ? 100 : -25;
        Set<Drawable> range = this.model.kdtree.rangeSearch(new double[]{this.miny+padding/zoom_current, this.minx+padding/zoom_current},
                                                            new double[]{this.maxy-padding/zoom_current, this.maxx-padding/zoom_current});

        // Loops through all the key features and sets the default styling for all its objects
        for(keyFeature element : model.yamlObj.ways.values()){
            this.setStylingDefault();

            // Loops through all value features and sets first eventual key feature styling and then eventual any value styles set
            for (valueFeature element2 : element.valuefeatures.values()) {
                if(element2.display) {
                    // Loops through all drawable elements that shall be drawn to the screen
                    // Checks if the styling requires them to be drawn with filling and/or strokes
                    // and then proceed to draw the value feature in the way it has been told to
                    for (Drawable draw : element2.drawable) {
                        if(range.contains(draw) || element2.draw != null && element2.draw.always_draw && this.model.isOMSloaded){
                            this.setStyling(element.draw);
                            this.setStyling(element2.draw);

                            if (element.draw != null && element.draw.fill && element.draw.zoom_level < this.zoom_current
                                    || element2.draw != null && element2.draw.fill && element2.draw.zoom_level < this.zoom_current) {
                                if(!this.debugDisplayWireframe) draw.fill(this.gc);
                                draw.draw(this.gc);
                            }
                            if (element.draw != null && element.draw.stroke && element.draw.zoom_level < this.zoom_current
                                    || element2.draw != null && element2.draw.stroke && element2.draw.zoom_level < this.zoom_current)
                                draw.draw(this.gc);
                        }
                    }
                }
            }
        }

        this.repaintTime = System.nanoTime() - this.repaintTime;
        this.calcRollingAvg();

        this.splitsTree();
        this.drawBounds();
        this.strokeCursor();
        this.strokeBox(padding);
        this.controller.updateDebugInfo();
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

    private void calcRollingAvg(){
        this.avgRTNum = this.avgRTNum < 100 ? this.avgRTNum+1 : 1;
        this.avgRT = (this.avgRT * (this.avgRTNum-1) + this.repaintTime) / this.avgRTNum;
    }

    private void strokeBox(double padding){
        if(this.debugVisBox && this.model.isOMSloaded){
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
                this.gc.fillText("relative origin (" + String.format("%.5f", originx) + "," + String.format("%.5f", originy) + ")", originx + csize, originy - csize);
                this.gc.fillText("top left (" + String.format("%.5f", minx + padding) + "," + String.format("%.5f", miny + padding) + ")", minx + padding + csize, miny + padding - csize);
                this.gc.fillText("top right (" + String.format("%.5f", maxx - padding) + "," + String.format("%.5f", miny + padding) + ")", maxx - padding + csize, miny + padding - csize);
                this.gc.fillText("bottom right (" + String.format("%.5f", maxx - padding) + "," + String.format("%.5f", maxy - padding) + ")", maxx - padding + csize, maxy - padding - csize);
                this.gc.fillText("bottom left (" + String.format("%.5f", minx + padding) + "," + String.format("%.5f", maxy - padding) + ")", minx + padding + csize, maxy - padding - csize);
            }
        }
    }

    private void strokeCursor(){
        if(this.debugCursor && this.model.isOMSloaded){
            this.gc.setLineWidth(1);
            this.gc.setFill(Color.BLUE);
            this.gc.fillOval(mousex,mousey,5/zoom_current,5/zoom_current);
            this.gc.setFont(new Font("Arial",11/zoom_current));
            if(this.debugDisableHelpText) this.gc.fillText("cursor (" + String.format("%.5f", mousex) + "," + String.format("%.5f", mousey) + ")",mousex+5/zoom_current,mousey-5/zoom_current);
            this.gc.setFill(Color.BLACK);
        }
    }

    private void splitsTree(){
        if(this.debugSplits && this.model.isOMSloaded){
            List<float[]> lines = this.model.kdtree.getSplit();
            this.gc.setLineWidth(2.5/zoom_current);
            this.gc.setStroke(Color.GREEN);
            this.gc.setLineDashes(0);

            for(int i = 0; i < lines.size(); i+=2){
                this.gc.beginPath();
                this.gc.moveTo(lines.get(i)[0],lines.get(i)[1]);
                this.gc.lineTo(lines.get(i+1)[0],lines.get(i+1)[1]);
                this.gc.stroke();
                this.gc.closePath();
            }
        }
    }

    private void drawBounds(){
        if(this.debugBoundingBox && this.model.isOMSloaded){
            this.gc.setLineWidth(1/zoom_current);
            this.gc.setLineDashes(0);
            this.gc.setStroke(Color.RED);
            this.gc.beginPath();
            this.gc.moveTo(this.model.minlon,this.model.minlat);
            this.gc.lineTo(this.model.maxlon,this.model.minlat);
            this.gc.lineTo(this.model.maxlon,this.model.maxlat);
            this.gc.lineTo(this.model.minlon,this.model.maxlat);
            this.gc.lineTo(this.model.minlon,this.model.minlat);
            this.gc.stroke();
            this.gc.closePath();
            this.gc.setFill(Color.RED);

            double bbx = (this.model.maxlon+this.model.minlon)/2, bby = (this.model.maxlat+this.model.minlat)/2;
            double csize = 5/zoom_current;

            this.gc.fillOval(bbx,bby, csize, csize);
            this.gc.fillText("boundary origin (" + String.format("%.5f", bbx) + "," + String.format("%.5f", bby) + ")", bbx + csize, bby - csize);
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
    public void pan(double dx, double dy) {
        this.setScale(dx,dy);

        if(!this.debugFreeMovement && (this.originx < this.model.minlon || this.originx > this.model.maxlon
                || this.originy < this.model.minlat || this.originy > this.model.maxlat)){
            this.setScale(-dx,-dy);
        } else {
            this.trans.prependTranslation(dx,dy);
        }

        this.repaint();
    }

    // Allows the user to zoom in on the map.
    // this is used in onScroll from Controller.
    public void zoom(final double factor){
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

    public void update(){
        this.setScale(0,0);
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
        this.gc.setFill(Color.WHITE);
        this.repaint();
    }

    public void goToPosAbsolute(final double x, final double y){
        double dx = (x - this.originx) * this.zoom_current;
        double dy = (y - this.originy) * this.zoom_current;
        this.pan(-dx,-dy);
    }

    public void goToPosRelative(final double x, final double y){
        this.pan(x*this.zoom_current, y*this.zoom_current);
    }

    public void centerPos(){
        double dx = (this.model.maxlon + this.model.minlon)/2;
        double dy = (this.model.maxlat + this.model.minlat)/2;
        this.goToPosAbsolute(dx,dy);
    }
}