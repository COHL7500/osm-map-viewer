package bfst22.vector;
import java.util.PriorityQueue;

public class DijkstraSP {
    private double[] distTo;
    private Edge[] edgeTo;
    private PriorityQueue<Double> pq;

    public DijkstraSP(Graph G, int s){
        distTo = new double[G.getV()];
        edgeTo = new Edge[G.getV()];
        for(int i = 0; i < G.getV(); i++){
            distTo[i] = Double.POSITIVE_INFINITY;
            distTo[s] = 0.0;

            pq = new PriorityQueue<>(G.getV());
            pq.add(distTo[s]);
            while(!pq.isEmpty()) {
            }
        }
    }

    private void relax(Edge e){
        int v = e.getFrom();
        int w = e.getTo();
        if(distTo[w] > distTo[v] + e.getWeight()){
            distTo[w] = distTo[v] + e.getWeight();
            edgeTo[w] = e;

        }
    }

    public double getDistTo(int v){
        return distTo[v];
    }



}
