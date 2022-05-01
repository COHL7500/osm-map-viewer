package bfst22.vector;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class DijkstraShortestPath {
    private Map<PolyPoint, Boolean> markMap = new HashMap<>();
    private Map<PolyPoint,Double> distanceMap = new HashMap<>();
    private Map<PolyPoint, Edge> edgeMap = new HashMap<>();
    private MinPQ<Edge> pq;

    private class compareDistance implements Comparator<Edge> {
        public int compare(Edge e, Edge f){
            double distance1 = distanceMap.get(e.getFrom()) + e.getWeight();
            double distance2 = distanceMap.get(f.getFrom()) + e.getWeight();
            return Double.compare(distance1, distance2);
        }
    }

    public DijkstraShortestPath(Graph g, PolyPoint start, VehicleType vehicleType ) {
        pq = new MinPQ<Edge>(new compareDistance());
        for (int v = 0; v < g.getVertexCount(); v++) {
            distanceMap.put(g.indexPoly.get(v), Double.POSITIVE_INFINITY);
            distanceMap.put(start, 0.0);
            relax(g, start);
        }

        while (!pq.isEmpty()) {
            Edge e = pq.delMin();
            PolyPoint v = e.getFrom(), w = e.getTo();
            if (!markMap.get(w)) relax(g, w);
        }
    }

    /*I haven't implemented the heruristic yet*/
    float h(PolyPoint start, PolyPoint target){
        Distance d = new Distance();
        return d.haversineFormula(start,target);
    }

        private void relax(Graph g, PolyPoint v){
            markMap.put(v, true);
            for(Edge e : g.adjList.get(v)){
                PolyPoint w = e.getTo();
                if(distanceMap.get(w) > distanceMap.get(v) + e.getWeight()) {
                    distanceMap.replace(w,distanceMap.get(v) + e.getWeight());
                    edgeMap.put(w,e);
                    pq.insert(e);
                }
            }
        }

        public double distTo(PolyPoint v){
            return distanceMap.get(v);
        }

        public boolean hasPathTo(PolyPoint v){
            return markMap.get(v);
        }

        public Iterable<Edge> pathTo(PolyPoint v){
            if(!hasPathTo(v)) return null;
            Stack<Edge> path = new Stack<>();
            for(Edge e = edgeMap.get(v); e != null; e = edgeMap.get(e.getFrom())){
                path.push(e);
            }
            return path;
        }

    }



