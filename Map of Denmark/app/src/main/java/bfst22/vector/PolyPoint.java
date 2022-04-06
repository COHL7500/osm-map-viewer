
package bfst22.vector;

// Defines what a node is in the OSM file and its properties.
public class PolyPoint {
    public long id;
    public float lat, lon;

    public PolyPoint(final long id, final float lat, final float lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }
}
