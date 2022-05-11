
package bfst22.vector;

import javafx.scene.canvas.GraphicsContext;

// Defines what a node is in the OSM file and its properties.
public class PolyPoint implements Drawable {
    public long id;
    public float lat, lon;

    //Fields for roads and vehicle types
    public boolean foot = false;
    public boolean bicycle = false;
    public boolean motorVehicle = true;
    public int speedLimit = 50; //Speed limit in Denmark within towns
    public boolean isOneway = false;

    public PolyPoint(final long id, final float lat, final float lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }

    @Override public void trace(GraphicsContext gc) {
        gc.moveTo(this.lat,this.lon);
    }
}
