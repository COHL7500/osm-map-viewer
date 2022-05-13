
package bfst22.vector;

import javafx.scene.canvas.GraphicsContext;

import java.io.Serializable;

// Defines what a node is in the OSM file and its properties.
public class PolyPoint implements Drawable, Serializable, SerialVersionIdentifiable {
    public long id;
    public float lat, lon;

    //Fields for roads and vehicle types
    public boolean foot = false;
    public boolean bicycle = false;
    public boolean motorVehicle = true;
    public int speedLimit = 50; //Speed limit in Denmark within towns
    public boolean isOneway = false;
    public String address;

    public PolyPoint(final long id, final float lat, final float lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }

    @Override public void trace(GraphicsContext gc) {
        gc.moveTo(this.lat, this.lon);
    }

    @Override public Drawable clone(){
        try {
            super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return new PolyPoint(this.id,this.lat,this.lon);
    }
}
