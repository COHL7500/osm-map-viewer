package bfst22.vector;

//Not done yet

import java.util.*;

public class Graph {
    private int V;
    private int E;
    float speedlimit = 0;
    Map<PolyPoint, LinkedList<Edge>> adjList = new HashMap<>();
    Map<PolyPoint, Integer> indexes = new HashMap<>();
    LinkedList<Edge> list = new LinkedList<>();
    int index = -1;

    //Map<PolyPoint, TreeMap<PolyPoint,Float>> adj = new HashMap<>();

    public Graph(List<PolyPoint> nodes){
        for(int i = 0; i < nodes.size(); i++){
            PolyPoint node = nodes.get(i);
            adjList.put(node,list);
            indexes.put(node,++index);
        }
    }

    public void addEdge(PolyPoint from, PolyPoint to, float weight){
        LinkedList<Edge> list = new LinkedList<>();
        list.add(new Edge(from, to, setWeight(from,to, speedlimit)));

    }


    /*
    public void addEdge(PolyPoint from, PolyPoint to, float weight){

    }

    */
    public void setNodecount(int nodecount){
        if(V == 0) V = nodecount;
    }

    public int getVertexIndex(){
        return V;
    }

    public int getEdgeIndex(){
        return E;
    }

    public void setWaycount(int waycount){
        if(E == 0) E = waycount;
    }

    public float setWeight(PolyPoint from, PolyPoint to, float speedlimit){
        Distance d = new Distance();
        return d.haversineFormula(from,to)/speedlimit;
    }

    /*
    //private Bag<PolyPoint>[] adj;
    private int[] indegree;
    PolyLine way;
    int speedLimit;
    private PolyPoint from;
    private PolyPoint to;
    private float weight;

    public Graph(int V){
        if (V < 0) throw new IllegalArgumentException("error");
        this.V = V;
        this.E = 0; //The graph will start with 0 edges
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
