package bfst22.vector;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import java.util.*;

// defines the canvas of our map; panning, zooming, painting etc.
// Whenever we add new interaction with the map, we use this class.
public class MapCanvas extends Canvas {
    private Model model;
    private Affine trans;
    private GraphicsContext gc;
    public Point2D minPos, maxPos, originPos, mousePos, rtMousePos;
    public double zoom_current;
    public final int minZoom = 1, maxZoom = 100000;
    public boolean zoomMagnifyingGlass = false;
    public long repaintTime, avgRT, avgRTNum;
    public Painter painter;
    public ZoomBox zoombox;
    public PinPoints pinpoints;
    public DebugProperties deprop;
    public boolean drags;

    /* ----------------------------------------------------------------------------------------------------------------- *
     * ------------------------------------------------ General Methods ------------------------------------------------ *
     * ----------------------------------------------------------------------------------------------------------------- */
    // Runs upon startup (setting default pan, zoom for example).
    public void init(final Model model) {
        this.model = model;
        this.deprop = new DebugProperties();
        this.reset();
        this.zoom(42000);
        this.addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
            if(this.zoomMagnifyingGlass) event.consume();
        });
    }

    public void reset(){
        this.minPos = new Point2D(0,0);
        this.maxPos = new Point2D(0,0);
        this.originPos = new Point2D(0,0);
        this.mousePos = new Point2D(0,0);
        this.rtMousePos = new Point2D(0,0);
        this.painter = new Painter();
        this.zoombox = new ZoomBox();
        this.pinpoints = new PinPoints();
        this.repaintTime = this.avgRT = this.avgRTNum = 0;
        this.trans = new Affine();
        this.gc = super.getGraphicsContext2D();
        this.gc.setFillRule(FillRule.NON_ZERO);
        this.zoom_current = 1;
        this.drags = false;
    }

    // https://stackoverflow.com/questions/12636613/how-to-calculate-moving-average-without-keeping-the-count-and-data-total
    private void calcRollingAvg(){
        this.avgRTNum = this.avgRTNum < 100 ? this.avgRTNum+1 : 1;
        this.avgRT = (this.avgRT * (this.avgRTNum-1) + this.repaintTime) / this.avgRTNum;
    }

    private void magnifyingGlass(MouseEvent e){
        if(this.zoomMagnifyingGlass){
            if(e.getButton() == MouseButton.PRIMARY) this.zoomTo(2);
            else if(e.getButton() == MouseButton.SECONDARY) this.zoomTo(0.5);
            this.goToPosAbsolute(this.mousePos);
        }
    }

    private void doDrag(boolean state){
        this.drags = this.pinpoints.drag(this.mousePos,this.zoom_current,state);
    }

    /* ----------------------------------------------------------------------------------------------------------------- *
     * ----------------------------------------------- Painting Methods ------------------------------------------------ *
     * ----------------------------------------------------------------------------------------------------------------- */
    // Draws all of the elements of our map.
    private void repaint() {
        this.gc.setTransform(new Affine());

        // Background color
        this.gc.setFill(Color.web("#b5d2de"));
        this.gc.fillRect(0, 0, super.getWidth(), super.getHeight());

        // Performs linear mapping between Point2D points. Our trans is Affine:
        // https://docs.oracle.com/javase/8/javafx/api/javafx/scene/transform/Affine.html
        this.gc.setTransform(this.trans);

        if(this.model.isLoaded()) {
            this.repaintTime = System.nanoTime();

            double padding = this.deprop.get("debugVisBox") ? 100 : -25;
            Set<Drawable> range = (Set<Drawable>)(Set<?>) this.model.kdtree.rangeSearch(new double[]{this.minPos.getY() + this.z(padding), this.minPos.getX() + this.z(padding)},
                    new double[]{this.maxPos.getY() - this.z(padding), this.maxPos.getX() - this.z(padding)});

            // Only display if set to do so, else display nothing at all
            if(this.model.yamlObj.draw.display) {
                // Loops through all the key features and sets the default styling for all its objects
                for (keyFeature element : this.model.yamlObj.keyfeatures.values()) {
                    if (element.draw.display) {
                        this.setStylingDefault();

                        // Loops through all value features and sets first eventual key feature styling and then eventual any value styles set
                        for (valueFeature element2 : element.valuefeatures.values()) {
                            if (element2.draw.display) {
                                // Loops through all drawable elements that shall be drawn to the screen
                                // Checks if the styling requires them to be drawn with filling and/or strokes
                                // and then proceed to draw the value feature in the way it has been told to
                                for (Drawable draw : element2.drawable) {
                                    if (range.contains(draw) || element2.draw.always_draw) {
                                        this.setStyling(element.draw);
                                        this.setStyling(element2.draw);

                                        if ((element.draw != null && element.draw.fill && element.draw.zoom_level < this.zoom_current
                                                || element2.draw != null && element2.draw.fill && element2.draw.zoom_level < this.zoom_current)
                                                && !this.deprop.get("debugDisplayWireframe")) {
                                            draw.fill(this.gc);
                                        }
                                        if (element.draw != null && element.draw.stroke && element.draw.zoom_level < this.zoom_current
                                                || element2.draw != null && element2.draw.stroke && element2.draw.zoom_level < this.zoom_current)
                                            draw.stroke(this.gc);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            this.repaintTime = System.nanoTime() - this.repaintTime;
            this.calcRollingAvg();
            this.painter.stroke(this.gc, this.mousePos, this.zoom_current);
            this.setStylingDefault();

            this.pinpoints.draw(this.gc,this.zoom_current,this.mousePos);
            this.splitsTree();
            this.strokeNN();
            this.drawBounds();
            this.strokeCursor();
            this.strokeBox(padding);
        }
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
        this.gc.setFont(new Font("Arial",this.z(11)));
        this.gc.setFill(Color.BLACK);
        this.gc.setLineWidth(0.00001);
        this.gc.setStroke(Color.BLACK);
        this.gc.setFillRule(FillRule.NON_ZERO);
        this.gc.setLineDashes(1);
        this.gc.setGlobalAlpha(1);
    }

    /* ----------------------------------------------------------------------------------------------------------------- *
     * ------------------------------------------------- Event Methods ------------------------------------------------- *
     * ----------------------------------------------------------------------------------------------------------------- */
    public void scrolled(final double dy){
        this.zoomTo(Math.pow(1.003, dy));
    }

    public void dragged(final MouseEvent e, final Point2D p){
        this.doDrag(true);
        this.panTo(p);
        this.setMousePos(p);
        this.zoombox.drag(this.gc,this.mousePos,this.zoom_current);
        this.painter.drag(this.mousePos);
    }

    public void pressed(final MouseEvent e){
        this.magnifyingGlass(e);
        this.zoombox.press(this.mousePos);
        this.painter.press(this.mousePos);
    }

    public void released(final MouseEvent e){
        this.doDrag(false);
        this.zoombox.release(this,this.mousePos);
        this.painter.release();
    }

    public void moved(Point2D p){
        this.setMousePos(p);
    }

    /* ----------------------------------------------------------------------------------------------------------------- *
     * -------------------------------------------- Canvas Drawing Methods --------------------------------------------- *
     * ----------------------------------------------------------------------------------------------------------------- */
    private void strokeBox(double padding){
        if(this.deprop.get("debugVisBox") && this.model.isLoaded()){
            padding = this.z(padding);
            double csize = this.z(5);

            this.gc.setLineWidth(this.z(1));
            this.gc.setStroke(Color.BLUE);
            this.gc.setLineDashes(this.z(3));
            this.gc.beginPath();
            this.gc.moveTo(this.minPos.getX()+padding,this.minPos.getY()+padding);
            this.gc.lineTo(this.minPos.getX()+padding,this.maxPos.getY()-padding);
            this.gc.lineTo(this.maxPos.getX()-padding,this.maxPos.getY()-padding);
            this.gc.lineTo(this.maxPos.getX()-padding,this.minPos.getY()+padding);
            this.gc.lineTo(this.minPos.getX()+padding,this.minPos.getY()+padding);
            this.gc.stroke();
            this.gc.closePath();
            this.gc.setFill(Color.BLACK);
            this.gc.fillOval(this.originPos.getX(),this.originPos.getY(),csize,csize);
            this.gc.fillOval(this.minPos.getX()+padding-csize,this.minPos.getY()+padding-csize,csize,csize);
            this.gc.fillOval(this.maxPos.getX()-padding,this.minPos.getY()+padding-csize,csize,csize);
            this.gc.fillOval(this.maxPos.getX()-padding,this.maxPos.getY()-padding,csize,csize);
            this.gc.fillOval(this.minPos.getX()+padding-csize,this.maxPos.getY()-padding,csize,csize);

            if(this.deprop.get("debugDisableHelpText")) {
                this.gc.fillText("relative origin (" + String.format("%.5f", this.originPos.getX()) + "," + String.format("%.5f", this.originPos.getY()) + ")", this.originPos.getX() + csize, this.originPos.getY() - csize);
                this.gc.fillText("top left (" + String.format("%.5f", this.minPos.getX() + padding) + "," + String.format("%.5f", this.minPos.getY() + padding) + ")", this.minPos.getX() + padding + csize, this.minPos.getY() + padding - csize);
                this.gc.fillText("top right (" + String.format("%.5f", this.maxPos.getX() - padding) + "," + String.format("%.5f", this.minPos.getY() + padding) + ")", this.maxPos.getX() - padding + csize, this.minPos.getY() + padding - csize);
                this.gc.fillText("bottom right (" + String.format("%.5f", this.maxPos.getX() - padding) + "," + String.format("%.5f", this.maxPos.getY() - padding) + ")", this.maxPos.getX() - padding + csize, this.maxPos.getY() - padding - csize);
                this.gc.fillText("bottom left (" + String.format("%.5f", this.minPos.getX() + padding) + "," + String.format("%.5f", this.maxPos.getY() - padding) + ")", this.minPos.getX() + padding + csize, this.maxPos.getY() - padding - csize);
            }
        }
    }

    private void strokeNN(){
        if(this.deprop.get("debugNeighbor") && this.model.isLoaded()){
            float[] mouse = new float[]{(float) this.mousePos.getX(),(float) this.mousePos.getY()};
            float[] node = this.model.kdtree.findNN(mouse);

            this.gc.setLineWidth(this.z(2.5));
            this.gc.setStroke(Color.RED);
            this.gc.setLineDashes(0);

            this.gc.fillOval(node[0],node[1],this.z(5),this.z(5));
            this.gc.beginPath();
            this.gc.moveTo(node[0],node[1]);
            this.gc.lineTo(this.mousePos.getX(),this.mousePos.getY());
            this.gc.stroke();
            this.gc.closePath();
        }
    }

    private void strokeCursor(){
        if(this.deprop.get("debugCursor") && this.model.isLoaded()){
            this.gc.setLineWidth(1);
            this.gc.setFill(Color.BLUE);
            this.gc.fillOval(this.mousePos.getX(),this.mousePos.getY(),this.z(5),this.z(5));
            if(this.deprop.get("debugDisableHelpText")) this.gc.fillText("cursor (" + String.format("%.5f", this.mousePos.getX()) + "," + String.format("%.5f", this.mousePos.getY()) + ")",this.mousePos.getX()+this.z(5),this.mousePos.getY()-this.z(5));
            this.gc.setFill(Color.BLACK);
        }
    }

    private void splitsTree(){
        if(this.deprop.get("debugSplits") && this.model.isLoaded()){
            List<float[]> lines = this.model.kdtree.getSplits();
            this.gc.setLineWidth(this.z(2.5));
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
        if(this.deprop.get("debugBoundingBox") && this.model.isLoaded()){
            this.gc.setLineWidth(this.z(1));
            this.gc.setLineDashes(0);
            this.gc.setStroke(Color.RED);
            this.gc.beginPath();
            this.gc.moveTo(this.model.minBoundsPos.getY(),this.model.minBoundsPos.getX());
            this.gc.lineTo(this.model.maxBoundsPos.getY(), this.model.minBoundsPos.getX());
            this.gc.lineTo(this.model.maxBoundsPos.getY(),this.model.maxBoundsPos.getX());
            this.gc.lineTo(this.model.minBoundsPos.getY(),this.model.maxBoundsPos.getX());
            this.gc.lineTo(this.model.minBoundsPos.getY(),this.model.minBoundsPos.getX());
            this.gc.stroke();
            this.gc.closePath();
            this.gc.setFill(Color.RED);

            double csize = this.z(5);
            this.gc.fillOval(this.model.originBoundsPos.getX(),this.model.originBoundsPos.getY(), csize, csize);
            this.gc.fillText("boundary origin (" + String.format("%.5f", this.model.originBoundsPos.getX())
                    + "," + String.format("%.5f", this.model.originBoundsPos.getY()) + ")", this.model.originBoundsPos.getX() + csize, this.model.originBoundsPos.getY() - csize);
        }
    }

    /* ----------------------------------------------------------------------------------------------------------------- *
     * ------------------------------------------- Canvas Interaction Methods ------------------------------------------ *
     * ----------------------------------------------------------------------------------------------------------------- */
    // Allows the user to navigate around the map by panning.
    // this is used in onMouseDragged from Controller.
    public void panTo(Point2D pos){
        double dx = pos.getX() - this.rtMousePos.getX();
        double dy = pos.getY() - this.rtMousePos.getY();
        Point2D diff = new Point2D(dx,dy);

        if(!this.zoombox.isZooming() && !this.painter.isDrawing() && !this.drags) this.pan(diff);
        if(!this.isInBounds() && !this.deprop.get("debugFreeMovement")) this.pan(diff.multiply(-1));
    }

    private void pan(Point2D pos) {
        this.setScale(new Point2D(pos.getX(), pos.getY()));
        this.trans.prependTranslation(pos.getX(),pos.getY());
        this.repaint();
    }

    public void zoomTo(final double factor){
        if((this.zoom_current * factor) > this.minZoom && (this.zoom_current * factor) < this.maxZoom) this.zoom(factor);
    }

    // Allows the user to zoom in on the map.
    // this is used in onScroll from Controller.
    private void zoom(final double factor){
        Point2D cen = new Point2D(this.originPos.getX(),this.originPos.getY());
        this.zoom_current *= factor;
        this.trans.prependScale(factor, factor);
        this.setScale(new Point2D(0,0));
        this.goToPosAbsolute(cen);
    }

    private void setScale(final Point2D pos){
        double minx = this.minPos.getX() - this.z(pos.getX());
        double miny = this.minPos.getY() - this.z(pos.getY());
        this.minPos = new Point2D(minx,miny);

        double maxx = minx + this.z(super.getWidth());
        double maxy = (miny + this.z(super.getHeight())) - this.z(25);
        this.maxPos = new Point2D(maxx,maxy);

        double originx = minx+(maxx-minx) / 2;
        double originy = miny+(maxy-miny) / 2;
        this.originPos = new Point2D(originx,originy);
    }

    public void setMousePos(final Point2D point){
        double dx = this.z(point.getX()) + this.minPos.getX();
        double dy = this.z(point.getY()) + this.minPos.getY();
        this.mousePos = new Point2D(dx,dy);
        this.rtMousePos = point;
        this.repaint();
    }

    public void update(){
        this.setScale(new Point2D(0,0));
        this.repaint();
    }

    public void checkInBounds(){
        if(!this.isInBounds() && !this.deprop.get("debugFreeMovement")) this.placeInBounds();
    }

    private boolean isInBounds(){
        return (this.originPos.getX() >= this.model.minBoundsPos.getY() && this.originPos.getX() <= this.model.maxBoundsPos.getY()
                && this.originPos.getY() >= this.model.minBoundsPos.getX() && this.originPos.getY() <= this.model.maxBoundsPos.getX());
    }

    // https://math.stackexchange.com/questions/127613/closest-point-on-circle-edge-from-point-outside-inside-the-circle
    private void placeInBounds(){
        double r = (this.model.maxBoundsPos.getY()-this.model.originBoundsPos.getX());
        double d = Math.sqrt(Math.pow(this.originPos.getX()-this.model.originBoundsPos.getX(),2)+Math.pow(this.originPos.getY()-this.model.originBoundsPos.getY(),2));
        double x = this.model.originBoundsPos.getX() + r * (this.originPos.getX()-this.model.originBoundsPos.getX())/d;
        double y = this.model.originBoundsPos.getY() + r * (this.originPos.getY()-this.model.originBoundsPos.getY())/d;
        this.goToPosAbsolute(new Point2D(x,y));
    }

    public void clearScreen(){
        this.gc.setFill(Color.WHITE);
        this.repaint();
    }

    public void goToPosAbsolute(final Point2D pos){
        double dx = this.rz(pos.getX() - this.originPos.getX());
        double dy = this.rz(pos.getY() - this.originPos.getY());
        this.pan(new Point2D(-dx,-dy));
    }

    public void goToPosRelative(final Point2D pos){
        this.pan(pos.multiply(this.zoom_current));
    }

    public void centerPos(){
        double dx = (this.model.maxBoundsPos.getY() + this.model.minBoundsPos.getY())/2;
        double dy = (this.model.maxBoundsPos.getX() + this.model.minBoundsPos.getX())/2;
        this.goToPosAbsolute(new Point2D(dx,dy));
    }

    public void centerMap(){
        this.centerPos();
        this.pan(new Point2D(0,-50));
        this.update();
    }

    /* ----------------------------------------------------------------------------------------------------------------- *
     * ------------------------------------------------- Misc Methods -------------------------------------------------- *
     * ----------------------------------------------------------------------------------------------------------------- */
    public double z(double num){
        return (num / this.zoom_current);
    }

    public double rz(double num){
        return (num * this.zoom_current);
    }
}