package bfst22.vector;

import java.io.Serializable;

public record Edge(PolyPoint from, PolyPoint to, float weight) implements Serializable, SerialVersionIdentifiable {

    public PolyPoint getFrom() {
        return this.from;
    }

    public PolyPoint getTo() {
        return this.to;
    }

    public float getWeight() {
        return this.weight;
    }

}
