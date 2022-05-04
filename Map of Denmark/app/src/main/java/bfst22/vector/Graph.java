package bfst22.vector;

//Not done yet

import java.util.*;

public class Graph {
    private int vertexCount; //Number of vertices.
    private int edgeCount; //Number of edges
    float speedlimit = 0; //Speedlimit
    List<PolyPoint> nodes = new ArrayList<>();

    Map<PolyPoint, LinkedList<Edge>> adjMap = new HashMap<>();
    Map<PolyPoint, Integer> indexMap = new HashMap<>();
    Map<Integer, PolyPoint> polyMap = new HashMap<>();

    private final LinkedList<Edge> list = new LinkedList<Edge>();
    private final List<PolyPoint> graphNodes = new LinkedList<>();
    int index = -1;

    //Map<PolyPoint, TreeMap<PolyPoint,Float>> adj = new HashMap<>();

    public void add(List<PolyPoint> nodes){
        this.nodes.addAll(nodes);
    }

    public void generate(){
        vertexCount = this.nodes.size();
        for(int i = 0; i < vertexCount; i++){
            PolyPoint node = this.nodes.get(i);
            adjMap.put(node,new LinkedList<Edge>());
            indexMap.put(node, index++);
            polyMap.put(index, node);

            graphNodes.add(node);
        }
    }

    public void clearList(){
        this.nodes = null;
    }

    public void addEdge(PolyPoint from, PolyPoint to, float weight){
        Edge e = new Edge(from, to, setWeight(from, to ,speedlimit));

        if(!adjMap.containsKey(from)){
            addVertex(from);
            indexMap.put(from,++index);
            polyMap.put(index,from);

            vertexCount++;
        }
        if(!adjMap.containsKey(to)){
            addVertex(to);
            indexMap.put(from,++index);
            polyMap.put(index,from);

            vertexCount++;
        }
        adjMap.get(from).add(e);
        edgeCount++;
    }

    public void addVertex(PolyPoint node){
        adjMap.put(node,new LinkedList<Edge>());
    }

    public Iterable<Edge> edges() {
        Bag<Edge> bagList = new Bag<>();
        for(int v = 0; v < vertexCount; v++){
            for(Edge e : adjMap.get(polyMap.get(v))){
                bagList.add(e);
            }
        }
        return bagList;
    }

    public Iterable<Edge> adj(int v){
        return adjMap.get(v);
    }

    public int getIndex(){ return index; }

    // Getters & Setters method for edgeCount and vertexCount
    public void setNodecount(int nodecount){
        if(vertexCount == 0) vertexCount = nodecount;
    }

    public int getVertexCount(){
        return this.vertexCount;
    }

    public int getEdgeCount() {
        return this.edgeCount;
    }

        public Map<PolyPoint, LinkedList<Edge>> getAdjMap()
        {
            return adjMap;
        }

        public List<PolyPoint> getGraphNodes ()
        {
            return graphNodes;
        }

        public LinkedList<Edge> getList ()
        {
            return list;
        }

        public void setWaycount ( int waycount){
            if (edgeCount == 0) edgeCount = waycount;
        }

        public float setWeight (PolyPoint from, PolyPoint to,float speedlimit){
            Distance d = new Distance();
            return d.haversineFormula(from, to) / speedlimit;
        }


    }
