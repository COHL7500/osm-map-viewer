package bfst22.vector;

public class Edge {
    private final int from;
    private final int to;
    private final double weight; //Skal mugligvis ændres til en float? unsure

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



}
