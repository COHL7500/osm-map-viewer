package bfst22.vector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

public class DijkstraSP {
    Map<PolyPoint, Double> distanceMap;
    Map<PolyPoint, PolyPoint> edgeMap;


    private double[] distTo;
    private Edge[] edgeTo;
    private IndexMinPQ<Double> pq;

    public DijkstraSP(Graph G, PolyPoint s, PolyPoint t){
        distanceMap = new HashMap<>();
        edgeMap = new HashMap<>();
        for(int i = 0; i < G.getVertex(); i++){
            distanceMap.put(,Double.POSITIVE_INFINITY);
            edgeMap.put(,0.0);
        }
    }

    public void relax(Edge e, PolyPoint target){
        if(distanceMap.get)
    }

    /*
    public DijkstraSP(Graph G, int s, int t){
        distTo = new double[G.getVertex()];
        edgeTo = new Edge[G.getEdge()];
        for(int i = 0; i < G.getVertex(); i++){
            distTo[i] = Double.POSITIVE_INFINITY;
            distTo[s] = 0.0;

            pq = new IndexMinPQ<>(G.getVertex());
            pq.insert(s, distTo[s]);
            while(!pq.isEmpty()) {
                int v = pq.delMin();
                for(Edge e : G.adj(v)) {
                    relax(e, t);
                }
            }
        }
    }
    */


    float h(int start, int target){ //Start of A*
         Distance d = new Distance();
         return d.haversineFormula(start, target);

    }

    private void relax(Edge e, int target){
        int v = e.getFrom();
        int w = e.getTo();
        if(distTo[w] > distTo[v] + e.getWeight()){
            distTo[w] = distTo[v] + e.getWeight();
            edgeTo[w] = e;
            double priority = distTo[w] + h(w,target);
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


