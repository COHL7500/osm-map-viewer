package bfst22.vector;

public record Edge(PolyPoint from, PolyPoint to, float weight) {

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
