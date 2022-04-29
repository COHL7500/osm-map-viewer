package bfst22.vector;
import java.util.*;
/*
public class DijkstraSP {

    Map<PolyPoint, Double> distanceMap = new HashMap<>();
    Map<PolyPoint, Edge> edgeMap = new HashMap<>();
    private Edge[] edgeTo;
    private IndexMinPQ<Double> pq;
    Graph G;


    public DijkstraSP(Graph graph, PolyPoint start, PolyPoint target, VehicleType vehicle) {
        G = graph;
        edgeTo = new Edge[graph.getVertexIndex()];
        for (int i = 0; i < graph.getVertexIndex(); i++) {
            distanceMap.put(graph.indexPoly.get(i), Double.POSITIVE_INFINITY);
            distanceMap.put(start, 0.0);
            System.out.println(1);
        }

        pq = new MinPQ<Double>(graph.vertexCount);
        System.out.println("pq size is: " + pq.size());

        pq.insert(graph.indexes.get(start), distanceMap.get(start));
        while (!pq.isEmpty()) {
            int v = pq.delMin();

            for (Edge e : G.getList()) {
                relax(e, target);
            }
        }
    }

    float h(PolyPoint start, PolyPoint target) { //Start of A*
        Distance d = new Distance();
        return d.haversineFormula(start, target);

    }

    public void relax(Edge e, PolyPoint target) {
        int v = G.indexes.get(e.getFrom());
        int w = G.indexes.get(e.getTo());
        if (distanceMap.get(e.getTo()) > distanceMap.get(e.getFrom()) + e.getWeight()) {
            distanceMap.replace(e.getTo(), distanceMap.get(e.getFrom()) + e.getWeight());
            edgeTo[w] = e;
            double priority = distanceMap.get(e.getTo()) + h(e.getTo(), e.getFrom());
            if (pq.contains(w)) {
                pq.decreaseKey(w, priority);
            } else {
                pq.insert(w, priority);
            }
        }
    }

    public double distTo(PolyPoint v) {
        return distanceMap.get(v);
    }

    public Iterable<Edge> pathTo(PolyPoint v){
        if (!hasPathTo(v)) return null;
        Stack<Edge> path = new Stack<>();
        for(Edge e = edgeTo[G.indexes.get(v)]; e != null; e = edgeTo[G.indexes.get(v)]) {
            path.push(e);
        }
        return path;
    }

    public boolean hasPathTo(PolyPoint v) {
        return distanceMap.get(v) < Double.POSITIVE_INFINITY;
    }
}

    /* Integer implementation
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

    /*
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

    public Iterable<Edge> pathTo(Edge e) {
        if (!hasPathTo(edgeMap.get(e.getFrom()))) return null;
        Stack<Edge> path = new Stack<>();
        for (Edge i = edgeTo[G.indexes.get(e.getFrom())]; i != null; i = edgeTo[G.indexes.get(e.getFrom())]) {
            path.push(e);
        }
        return path;
    }
    */


