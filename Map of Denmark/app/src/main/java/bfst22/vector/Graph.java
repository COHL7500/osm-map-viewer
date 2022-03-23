package bfst22.vector;
//Not done yet
public class Graph {
    private final int V;
    private int E;
    private Bag<Edge>[] adj; //Maybe should implement bag, unsure at the moment

    private int[] indegree;

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
        this(V); //Unsure what this does atm
        if(E < 0) throw new IllegalArgumentException("Non-negative edge");
            for(int i = 0; i < E; i++){
                //Finde en måde at indlæse v, w og weight
                int from = 0;
                int to = 0;
                double weight = 0.0;
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
