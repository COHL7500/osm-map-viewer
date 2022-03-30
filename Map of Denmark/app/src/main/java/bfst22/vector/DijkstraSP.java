package bfst22.vector;
import java.util.Iterator;
import java.util.Stack;

public class DijkstraSP {
    private double[] distTo;
    private Edge[] edgeTo;
    private IndexMinPQ<Double> pq;

    public DijkstraSP(Graph G, int s){
        distTo = new double[G.getV()];
        edgeTo = new Edge[G.getV()];
        for(int i = 0; i < G.getV(); i++){
            distTo[i] = Double.POSITIVE_INFINITY;
            distTo[s] = 0.0;

            pq = new IndexMinPQ<>(G.getV());
            pq.insert(s, distTo[s]);
            while(!pq.isEmpty()) {
                int v = pq.delMin();
                for(Edge e : G.adj(v)) {
                    relax(e);
                }
            }
        }
    }

    double h(int i, int t){ //Start of A*
        return 0.0; //Will return 0.0 for now
    }

    private void relax(Edge e){
        int v = e.getFrom();
        int w = e.getTo();
        if(distTo[w] > distTo[v] + e.getWeight()){
            distTo[w] = distTo[v] + e.getWeight();
            edgeTo[w] = e;
            double priority = distTo[w] + h(w,0);
            if(pq.contains(w)){
                    pq.decreaseKey(w,priority);
            } else{
                pq.insert(w,priority);
            }

        }
    }

    public double getDistTo(int v){
        return distTo[v];
    }

    public boolean hasPathTo(int v){
        return distTo[v] < Double.POSITIVE_INFINITY;
    }

    public Iterable<Edge> pathTo(int v){
        if (!hasPathTo(v)) return null;
        Stack<Edge> path = new Stack<>();
        for (Edge e = edgeTo[v]; e != null; e = edgeTo[e.getFrom()]) {
            path.push(e);
        }
        return path;
    }
}


