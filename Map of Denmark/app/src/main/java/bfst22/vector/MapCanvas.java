package bfst22.vector;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

import java.util.ArrayList;
import java.util.List;

// defines the canvas of our map; panning, zooming, painting etc.
// Whenever we add new interaction with the map, we use this class.
public class MapCanvas extends Canvas {
    Model model;
    Affine trans = new Affine();
    GraphicsContext gc = super.getGraphicsContext2D();
    double zoom_current = 1, minx = 0, miny = 0, maxx = 0, maxy = 0, originx = 0, originy = 0, mousex = 0, mousey = 0;

    // Runs upon startup (setting default pan, zoom for example).
    public void init(final Model model) {
        this.model = model;
        //this.pan(-model.minlon, -model.minlat);

        // Default zoom level: 700
        //this.zoom(700 / (model.maxlon - model.minlon), 0, 0);

        // Observer notifies the change in a particular state, being our repaint in this case.
        //this.model.addObserver(this::repaint);
    }

    // Draws all of the elements of our map.
    private void repaint() {
        List<Drawable> elements = new ArrayList<>();

        this.gc.setTransform(new Affine());

        // Clears the screen for the next frame
        this.gc.clearRect(0, 0, super.getWidth(), super.getHeight());

        // Performs linear mapping between Point2D points. Our trans is Affine:
        // https://docs.oracle.com/javase/8/javafx/api/javafx/scene/transform/Affine.html
        this.gc.setTransform(trans);

        //float[] min = new float[]{(float) (x-getWidth()*zoom_current/2),(float) (y-getHeight()*zoom_current/2)};
        //float[] max = new float[]{(float) (x+getWidth()*zoom_current/2),(float) (y+getHeight()*zoom_current/2)};
        List<Node> range = this.model.kdtree.rangeSearch(new float[]{-1000,-1000},new float[]{1000,1000});

        //System.out.println("range: " + range.size());
        this.strokeCursor();
        this.strokeBox(100);
        //this.setStylingDefault();

        /*this.setStylingDefault();

        for(Node node : range){
            if(!elements.contains(node.obj)){
                elements.add(node.obj);
                node.obj.draw(this.gc);
            }
        }*/

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

    private void strokeBox(int padding){
        this.gc.setLineWidth(1);
        this.gc.setStroke(Color.BLUE);
        this.gc.setLineDashes(3);
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
        this.gc.fillOval(originx,originy,5,5);
        this.gc.fillOval(minx+padding-5,miny+padding-5,5,5);
        this.gc.fillOval(maxx-padding,miny+padding-5,5,5);
        this.gc.fillOval(maxx-padding,maxy-padding,5,5);
        this.gc.fillOval(minx+padding-5,maxy-padding,5,5);
        this.gc.fillText("origin (" + Math.round(originx) + "," + Math.round(originy) + ")",originx+5,originy-5);
        this.gc.fillText("min x, min y (" + Math.round(minx+padding) + "," + Math.round(miny+padding) + ")",minx+padding+5,miny+padding-5);
        this.gc.fillText("max x, min y (" + Math.round(maxx-padding) + "," + Math.round(miny+padding) + ")",maxx-padding+5,miny+padding-5);
        this.gc.fillText("max x, max y (" + Math.round(maxx-padding) + "," + Math.round(maxy-padding) + ")",maxx-padding+5,maxy-padding-5);
        this.gc.fillText("min x, max y (" + Math.round(minx+padding) + "," + Math.round(maxy-padding) + ")",minx+padding+5,maxy-padding-5);
    }

    private void strokeCursor(){
        this.gc.setLineWidth(1);
        this.gc.setFill(Color.RED);
        this.gc.fillOval(mousex,mousey,5,5);
        this.gc.setFill(Color.BLACK);
        this.gc.fillText("cursor (" + Math.round(mousex) + "," + Math.round(mousey) + ")",mousex+5,mousey-5);
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
        //System.out.println(dx + ", " + dy);
        this.trans.prependTranslation(dx, dy);
        this.setScale(dx,dy);
        this.repaint();
    }

    // Allows the user to zoom in on the map.
    // this is used in onScroll from Controller.
    public void zoom(final double factor, final double dx, final double dy){
        this.zoom_current *= factor;
        this.trans.prependTranslation(-dx, -dy);
        this.trans.prependScale(factor, factor);
        this.trans.prependTranslation(dx, dy);
        System.out.println(zoom_current + ", " + dx + ", " + dy);
        this.repaint();
    }

    public void setScale(final double dx, final double dy){
        this.minx -= dx;
        this.miny -= dy;
        this.maxx = this.minx+super.getWidth();
        this.maxy = this.miny+super.getHeight()-25;
        this.originx = minx+(maxx-minx)/2;
        this.originy = miny+(maxy-miny)/2;
    }

    public void setMousePos(final Point2D point){
        this.mousex = point.getX() + this.minx;
        this.mousey = point.getY() + this.miny;
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