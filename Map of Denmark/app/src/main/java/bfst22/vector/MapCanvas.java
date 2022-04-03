package bfst22.vector;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

// defines the canvas of our map; panning, zooming, painting etc.
// Whenever we add new interaction with the map, we use this class.
public class MapCanvas extends Canvas {
    public Model model;
    public Affine trans = new Affine();
    public GraphicsContext gc = super.getGraphicsContext2D();
    public double zoom_current = 1, minx = 0, miny = 0, maxx = 0, maxy = 0, originx = 0, originy = 0, mousex = 0, mousey = 0;
    public boolean debugCursor = true, debugVisBox = true, debugSplits = true, debugInfo = true, debugDisableHelpText = true, debugBoundingBox = true, debugFreeMovement = false;
    public long repaintTime;

    // Runs upon startup (setting default pan, zoom for example).
    public void init(final Model model) {
        this.model = model;
        this.pan(-model.minlon, -model.minlat);

        // Default zoom level: 700
        this.zoom(700 / (model.maxlon - model.minlon));

        // Observer notifies the change in a particular state, being our repaint in this case.
        this.model.addObserver(this::repaint);
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

        double padding = this.debugVisBox ? (100 / zoom_current) : 0;
        Set<Drawable> range = this.model.kdtree.rangeSearch(new double[]{this.miny+padding, this.minx+padding}, new double[]{this.maxy-padding, this.maxx-padding});

        //Set<valueFeature> featureList = new HashSet<>();

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
                        if(range.contains(draw)){
                            this.setStyling(element.draw);
                            this.setStyling(element2.draw);

                            if (element.draw != null && element.draw.fill && element.draw.zoom_level < this.zoom_current
                                    || element2.draw != null && element2.draw.fill && element2.draw.zoom_level < this.zoom_current) {
                                draw.fill(this.gc);
                                draw.draw(this.gc);
                            }
                            if (element.draw != null && element.draw.stroke && element.draw.zoom_level < this.zoom_current
                                    || element2.draw != null && element2.draw.stroke && element2.draw.zoom_level < this.zoom_current)
                                draw.draw(this.gc);
                        /*if (element2.name != null && element2.nameCenter != null && element2.name.length() > 0)
                            featureList.add(element2);*/
                        }
                    }
                }
            }
        }

        this.repaintTime = System.nanoTime() - this.repaintTime;

        //this.splitsTree();
        this.drawBounds();
        this.strokeCursor();
        this.strokeBox(100);
        this.debugInfo();

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
                this.gc.fillText("origin (" + String.format("%.5f", originx) + "," + String.format("%.5f", originy) + ")", originx + csize, originy - csize);
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
            this.gc.setFill(Color.RED);
            this.gc.fillOval(mousex,mousey,5/zoom_current,5/zoom_current);
            this.gc.setFill(Color.BLACK);
            this.gc.setFont(new Font("Arial",11/zoom_current));
            if(this.debugDisableHelpText) this.gc.fillText("cursor (" + String.format("%.5f", mousex) + "," + String.format("%.5f", mousey) + ")",mousex+5/zoom_current,mousey-5/zoom_current);
        }
    }

    private void splitsTree(){
        if(this.debugSplits && this.model.isOMSloaded){
            List<float[][]> lines = this.model.kdtree.getSplit();
            this.gc.setLineWidth(2.5/zoom_current);
            this.gc.setStroke(Color.GREEN);

            for(float[][] coord : lines){
                this.gc.beginPath();
                this.gc.moveTo(coord[0][0],coord[0][1]);
                this.gc.lineTo(coord[1][0],coord[1][1]);
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
        }
    }

    private void debugInfo(){
        if(this.debugInfo && this.model.isOMSloaded) {
            this.gc.setFill(Color.BLACK);
            this.gc.setGlobalAlpha(0.5);
            this.gc.fillRect(minx,miny,160 / zoom_current,85 / zoom_current);
            this.gc.fillRect(maxx-165 / zoom_current,miny,165 / zoom_current,85 / zoom_current);
            this.gc.fillRect(minx,maxy-50 / zoom_current,this.model.currFileName.length() * 5.25 / zoom_current,35 / zoom_current);
            this.gc.fillRect(minx,maxy-105 / zoom_current,150 / zoom_current,52 / zoom_current);
            this.gc.setGlobalAlpha(1);
            this.gc.setFill(Color.WHITE);
            this.gc.setFont(new Font("Arial", 10 / zoom_current));

            double min_x = minx + 5 / zoom_current, max_x = maxx - 155 / zoom_current, min_y = miny + 15 / zoom_current, max_y = maxy - 35 / zoom_current;
            this.gc.fillText(String.format("%-11s%s", "min:", String.format("%.5f", minx) + ", " + String.format("%.5f", miny)), min_x, min_y);
            this.gc.fillText(String.format("%-10s%s", "max:", String.format("%.5f", maxx) + ", " + String.format("%.5f", maxy)), min_x, min_y + 15 / zoom_current);
            this.gc.fillText(String.format("%-11s%s", "origin:", String.format("%.5f", originx) + ", " + String.format("%.5f", originy)), min_x, min_y + 30 / zoom_current);
            this.gc.fillText(String.format("%-8s%s", "mouse:", String.format("%.5f", mousex) + ", " + String.format("%.5f", mousey)), min_x, min_y + 45 / zoom_current);
            this.gc.fillText(String.format("%-9s%s", "zoom:", String.format("%.5f", zoom_current)), min_x, min_y + 60 / zoom_current);
            this.gc.fillText(String.format("%-14s%s", "bounds min:", String.format("%.5f", this.model.minlon) + ", " + String.format("%.5f", this.model.minlat)), max_x, min_y);
            this.gc.fillText(String.format("%-13s%s", "bounds max:", String.format("%.5f", this.model.maxlon) + ", " + String.format("%.5f", this.model.maxlat)), max_x, min_y + 15 / zoom_current);
            this.gc.fillText(String.format("%-18s%s", "nodes:", this.model.nodecount), max_x, min_y + 30 / zoom_current);
            this.gc.fillText(String.format("%-19s%s", "ways:", this.model.waycount), max_x, min_y + 45 / zoom_current);
            this.gc.fillText(String.format("%-18s%s", "relations:", this.model.relcount), max_x, min_y + 60 / zoom_current);
            this.gc.fillText(String.format("%5s", this.model.currFileName), min_x, max_y);
            this.gc.fillText(String.format("%-18s%d bytes", "file size:", this.model.filesize), min_x, max_y - 25 / zoom_current);
            this.gc.fillText(String.format("%-16s%d ms", "load time:", this.model.loadTime/1000000), min_x, max_y - 40 / zoom_current);
            this.gc.fillText(String.format("%-15s%d ms", "repaint time:", this.repaintTime/1000000), min_x, max_y - 55 / zoom_current);
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
}