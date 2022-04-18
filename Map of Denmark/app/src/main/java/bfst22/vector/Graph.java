package bfst22.vector;

//Not done yet

import java.util.*;

public class Graph {
    private int vertex;
    private int edge;
    Map<PolyPoint, List<Edge>> adj = new HashMap<>();
    Map<PolyPoint, Integer> indexes = new HashMap<>();
    int i = -1;

    public Graph(List<PolyPoint> nodes){
        for(int i = 0; i < nodes.size() ; i++){
            PolyPoint node = nodes.get(i);
            LinkedList<Edge> list = new LinkedList<>();
            adj.put(node,list);
            indexes.put(node, i++);
        }
    }

    public void addEdge(PolyPoint from, PolyPoint to, float weight){
        adj.get(from).add(new Edge(from, to, weight));
    }

    public void addVertex(PolyPoint node){
        adj.put(node, new LinkedList<>());
    }


    public void setNodecount(int nodecount){
        if(vertex == 0) vertex = nodecount;
    }

    public int getVertex(){
        return vertex;
    }

    public int getEdge(){
        return edge;
    }

    public void setWaycount(int waycount){
        if(edge == 0) edge = waycount;
    }

    public float setWeight(PolyPoint from, PolyPoint to, int speedlimit){
        Distance d = new Distance();
        return d.haversineFormula(from,to)/speedlimit;
    }

    /*
    private final int vertex;
    private int edge;
    //private Bag<PolyPoint>[] adj;
    private int[] indegree;
    PolyLine way;
    int speedLimit;
    private PolyPoint from;
    private PolyPoint to;
    private float weight;

    public Graph(int V){
        if (V < 0) throw new IllegalArgumentException("error");
        this.vertex = V;
        this.edge = 0; //The graph will start with 0 edges
        adj = new Bag[V];
            for (int i = 0; i < V; i++){
                adj[i] = new Bag<Edge>();
            }
        this.indegree = new int[V];
    }

    public Graph(int V, int E){
        this(V); //Calls previous Graph constructor
            for(int i = 0; i < E; i++){

                Edge e = new Edge(from, to, weight);
            }

    }

 */
    /*
    public Graph(List<PolyLine> nodes, int V){
        this.vertex = vertex;
        this.edge = 0;
        indegree = new int[vertex]
        adj = new Bag[vertex];
            for(int i = 0; i < vertex; i++){
                adj[i] = new Bag<Edge>();
            }
    }

    public Graph(List<PolyLine> ways,int V,int E){
        this(ways,V);
            int i = 0;
            for(PolyLine w : ways){
                from.lat = way.coords[i++];
                from.lon = way.coords[i++];
                to.lat = way.coords[i++];
                to.lon = way.coords[i++];
                weight = setWeight(from, to, speedLimit);
                Edge e = new Edge(from, to , weight);
            }
    }

    public Iterable<Edge> adj(int v){
        return adj[v];
    }

    public Iterable<Edge> edges() {
        Bag<Edge> list = new Bag<Edge>();
        for(int i = 0; i < vertex; i++){
            for(Edge e : adj(i)){
                list.add(e);
            }
        }
        return list;
    }

    public int getV(){
        return vertex;
    }
    public int getE() { return edge; }

    public void setNodecount(int nodecount){
        if(vertex == 0) vertex = nodecount;
    }

    public void setWaycount(int waycount){
        if(edge == 0) edge = waycount;
    }

    public float setWeight(PolyPoint from, PolyPoint to, int speedlimit){
        Distance d = new Distance();
        return d.haversineFormula(from,to)/speedlimit;
    }

    public void addEdge(Edge e){
        PolyPoint from = e.getFrom();
        PolyPoint to = e.getTo();
        adj[from].add(e);
        indegree[to]++;
    }

    public int getIndegree(int v){
        return indegree[v];
    }

    public void setSpeedLimit(int limit){
        speedLimit = limit;
    }
    */
}
