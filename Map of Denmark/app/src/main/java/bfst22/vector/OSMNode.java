
package bfst22.vector;

import java.io.Serializable;
import java.util.ArrayList;

// Defines what a node is in the OSM file and its properties.
public class OSMNode implements Serializable, SerialVersionIdentifiable {
    long id;
    float lat, lon;

    public OSMNode(final long id, final float lat, final float lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }
}