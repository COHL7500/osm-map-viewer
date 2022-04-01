package bfst22.vector;

//Not done yet

import java.util.List;

public class Graph {
    private final int V;
    private int E;
    private Bag<Edge>[] adj; //Maybe should implement bag, unsure at the moment
    private List<OSMNode> list;
    private int[] indegree;

    //Other fields
    int from;
    int to;
    double weight;

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
        if(E < 0) throw new IllegalArgumentException("Non-negative edge");
            for(int i = 0; i < E; i++){
                //Finde en måde at indlæse v, w og weight
                from = 0;
                to = 0;
                weight = 0.0;
                Edge e = new Edge(from, to, weight);
            }

    }
    //Trying to implement Graph with OSMNodes
    public Graph(List<OSMNode> nodes){
        if(nodes.size() < 0) throw new IllegalArgumentException("Empty list with Nodes"); //Can be integrated later with an Exception class
        this.V = nodes.size(); //Might not be needed
        this.E = 0;
        adj = new Bag[nodes.size()];
            for(int i = 0; i < nodes.size(); i++){
                adj[i] = new Bag<Edge>();
            }
        this.indegree = new int[nodes.size()];
    }

    public Graph(List<OSMNode> nodes,int E){
        this(nodes);
        if(E < 0) throw new IllegalArgumentException("Non-Negative Edge not allowed!!!"); //Can be integrated later with an Exception class
            for(int i = 0; i < E; i++){
                //Ikke korrekt implementation
                from = 0;
                to = 0;
                weight = 0.0;
                Edge e = new Edge(from, to, weight);
            }
    }

    public Iterable<Edge> adj(int v){
        return adj[v];
    }

    public Iterable<Edge> edges() {
        Bag<Edge> list = new Bag<Edge>();
        for(int i = 0; i < V; i++){
            for(Edge e : adj(i)){
                list.add(e);
            }
        }
        return list;
    }

    public int getV(){
        return V;
    }

    public int getE(){
        return E;
    }

    public void incrementE(){
        E++;
    }

    public void addEdge(Edge e){
        int from = e.getFrom();
        int to = e.getTo();
        adj[from].add(e);
        indegree[to]++;
        E++;
    }

    public int getIndegree(int v){
        return indegree[v];
    }

}
