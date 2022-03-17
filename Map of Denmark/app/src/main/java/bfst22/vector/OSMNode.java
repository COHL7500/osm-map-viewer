package bfst22.vector;

import java.io.Serializable;

// Defines what a node is in the OSM file and its properties.
public class OSMNode implements Serializable {
    public static final long serialVersionUID = 9082413;
    long id;
    float lat, lon;

    public OSMNode(long id, float lat, float lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }
}