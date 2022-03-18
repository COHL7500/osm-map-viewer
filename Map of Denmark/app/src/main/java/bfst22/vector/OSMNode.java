package bfst22.vector;

import javafx.geometry.Point2D;
import java.io.Serializable;
import java.util.List; //Can potentially be used

// Defines what a node is in the OSM file and its properties.
public class OSMNode implements Serializable, SerialVersionIdentifiable {
    long id;
    float lat, lon;

    public OSMNode(final long id, final float lat, final float lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }

    public float getLat(){
        return lat;
    }

    public float getLon(){
        return lon;
    }
    public long getId(){
        return id;
    }

    public Point2D getPoint(){
        return new Point2D(this.lon,this.lat);
    }
}
