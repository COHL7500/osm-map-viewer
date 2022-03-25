package bfst22.vector;

public class Edge {
    private final int from;
    private final int to;
    private final double weight; //Skal mugligvis ændres til en float? unsure

    //Fields for roads and vehicle types
    boolean isAllowed = true;
    boolean isOneway;
    int speedLimit;
    boolean leftTurn = true;
    boolean rightTurn = true;

    public Edge(int from, int to, double weight) {
        if(from < 0 || to < 0) throw new IllegalArgumentException("error");
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public int getFrom(){
        return from;
    }

    public int getTo(){
        return to;
    }

    public double getWeight(){
        return weight;
    }

    public boolean isForwardAllowed(VehicleType v){
        return true; //Vi mangler et par metoder for at få dette til at fungere
    }

    public boolean isBackwardsAllowed(VehicleType v){
        return true;
    }

    public boolean getAllowed(){
        return isAllowed;
    }

}
