package bfst22.vector;

//Not done yet

import java.util.*;

public class Graph {
    public int vertexCount; //Amount of vertices.
    public int edgeCount; //Amount of edges
    float speedlimit = 0; //Speedlimit

    Map<PolyPoint, LinkedList<Edge>> adjList = new HashMap<>();
    Map<PolyPoint, Integer> indexes = new HashMap<>();
    Map<Integer, PolyPoint> indexPoly = new HashMap<>();

    private final LinkedList<Edge> list = new LinkedList<>();
    private final List<PolyPoint> graphNodes = new LinkedList<>();
    int index = -1;

    //Map<PolyPoint, TreeMap<PolyPoint,Float>> adj = new HashMap<>();

    public Graph(List<PolyPoint> nodes){
        this.vertexCount = nodes.size();
        for(int i = 0; i < nodes.size(); i++){
            PolyPoint node = nodes.get(i);
            adjList.put(node,list);
            indexes.put(node, i);
            indexPoly.put(i, node);

            graphNodes.add(node);
        }
    }

    public void addEdge(PolyPoint from, PolyPoint to, float weight){
        Edge e = new Edge(from, to, setWeight(from, to ,speedlimit));

        if(!adjList.containsKey(from)){
            addVertex(from);
            indexes.put(from,++index);
            indexPoly.put(index,from);
            vertexCount++;
        }
        else {
            adjList.get(from).add(e);
            edgeCount++;
        }


    }

    public void addVertex(PolyPoint node){
        adjList.put(node,new LinkedList<Edge>());
    }

    public Iterable<Edge> edges() {
        Bag<Edge> bagList = new Bag<>();
        for(int v = 0; v < vertexCount; v++){
            for(Edge e : adjList.get(v)){
                bagList.add(e);
            }
        }
        return bagList;
    }

    public Iterable<Edge> adj(int v){
        return adjList.get(v);
    }

    /*
    public void addEdge(PolyPoint from, PolyPoint to, float weight){

    }

    */

    /*Getters & Setters method for edgeCount and vertexCount */
    public void setNodecount(int nodecount){
        if(vertexCount == 0) vertexCount = nodecount;
    }

    public int getVertexCount(){
        return vertexCount;
    }

    public int getEdgeCount() {
        return edgeCount;
    }

        public Map<PolyPoint, LinkedList<Edge>> getAdjList ()
        {
            return adjList;
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
