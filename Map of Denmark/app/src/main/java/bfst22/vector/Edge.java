package bfst22.vector;

import java.io.Serializable;

public class Edge implements Serializable, SerialVersionIdentifiable {
    private PolyPoint from;
    private PolyPoint to;
    private float weight;

    //Fields for roads and vehicle types
    public boolean isAllowed = true;
    public boolean isOneway;
    public int speedLimit;
    public boolean leftTurn = true;
    public boolean rightTurn = true;

    public Edge(PolyPoint from, PolyPoint to, float weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public PolyPoint getFrom(){
        return this.from;
    }

    public PolyPoint getTo(){
        return this.to;
    }

    public float getWeight(){
        return this.weight;
    }

    public boolean isForwardAllowed(VehicleType v){
        return true;
    }

    public boolean isBackwardsAllowed(VehicleType v){
        return true;
    }

    public boolean getAllowed(){
        return isAllowed;
    }



}
