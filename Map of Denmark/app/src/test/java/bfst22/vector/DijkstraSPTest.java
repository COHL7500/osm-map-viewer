package bfst22.vector;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import java.util.*;

public class DijkstraSPTest {
    //ArrayList<PolyPoint> list = new ArrayList<>(); ArrayList that will hold all the PolyPoints in the Graph.
    List<PolyPoint> nodes = new ArrayList<>();
    Graph g;
    Distance d;
    DijkstraShortestPath sp;
    VehicleType vehicleType = VehicleType.MOTORCAR;

    /*PolyPoint objects*/
        PolyPoint A = new PolyPoint(1,12,3);
        PolyPoint B = new PolyPoint(2, 10,4);
        PolyPoint C = new PolyPoint(3,8,6);
        PolyPoint D = new PolyPoint(4,7,8);
        PolyPoint E = new PolyPoint(5, 5,10);
        PolyPoint F = new PolyPoint(6,10,2);
        PolyPoint G = new PolyPoint(7,7,2);
        PolyPoint H = new PolyPoint(8, 7,4);
        PolyPoint I = new PolyPoint(9, 5,3);
        PolyPoint J = new PolyPoint(10, 5,6);
        PolyPoint K = new PolyPoint(11, 4,9);

        /*Method that turns an ArrayList of PolyPoints
        nodes.add(new PolyPoint(0,12,3)); //A
        nodes.add(new PolyPoint(1, 10,4)); //B
        nodes.add(new PolyPoint(2,8,6)); //C
        nodes.add(new PolyPoint(3,7,8)); //D
        nodes.add(new PolyPoint(4, 5,10)); //E
        nodes.add(new PolyPoint(5,10,2)); //F
        nodes.add(new PolyPoint(6,7,2)); //G
        nodes.add(new PolyPoint(7, 7,4)); //H
        nodes.add(new PolyPoint(8, 5,3)); //I
        nodes.add(new PolyPoint(9, 5,6)); //J
        nodes.add(new PolyPoint(10,4,9)); //K
        */

    @BeforeEach void setUp(){
        System.out.println(nodes.isEmpty());
        d = new Distance();

        nodes.add(A);
        nodes.add(B);
        nodes.add(C);
        nodes.add(D);
        nodes.add(E);
        nodes.add(F);
        nodes.add(G);
        nodes.add(H);
        nodes.add(I);
        nodes.add(J);
        nodes.add(K);

        g = new Graph();
        g.add(nodes);
        g.generate();

        System.out.println("Vertex Count: " + g.getVertexCount());
        System.out.println("Edge Count: " + g.getEdgeCount());

        g.addEdge(g.nodes.get(0), g.nodes.get(1),d.haversineFormula(nodes.get(0), nodes.get(1)));

        System.out.println(g.getIndex());

        g.addEdge(g.nodes.get(1), g.nodes.get(2),d.haversineFormula(g.nodes.get(1), g.nodes.get(2)));

        System.out.println(g.getIndex());

        g.addEdge(g.nodes.get(2), g.nodes.get(3),d.haversineFormula(g.nodes.get(2), g.nodes.get(3)));

        System.out.println(g.getIndex());

        g.addEdge(g.nodes.get(3), g.nodes.get(4),d.haversineFormula(g.nodes.get(3), g.nodes.get(4)));
        g.addEdge(g.nodes.get(0), g.nodes.get(5),d.haversineFormula(g.nodes.get(0), g.nodes.get(5)));
        g.addEdge(g.nodes.get(5), g.nodes.get(6),d.haversineFormula(g.nodes.get(5), g.nodes.get(6)));
        g.addEdge(g.nodes.get(6), g.nodes.get(7),d.haversineFormula(g.nodes.get(6), g.nodes.get(7)));
        g.addEdge(g.nodes.get(0), g.nodes.get(3),d.haversineFormula(g.nodes.get(0), g.nodes.get(3)));
        g.addEdge(g.nodes.get(0), g.nodes.get(7),d.haversineFormula(g.nodes.get(0), g.nodes.get(7)));
        g.addEdge(g.nodes.get(6), g.nodes.get(8),d.haversineFormula(g.nodes.get(6), g.nodes.get(8)));
        g.addEdge(g.nodes.get(6), g.nodes.get(9),d.haversineFormula(g.nodes.get(6), g.nodes.get(9)));
        g.addEdge(g.nodes.get(9), g.nodes.get(10),d.haversineFormula(g.nodes.get(9), g.nodes.get(10)));
        g.addEdge(g.nodes.get(8), g.nodes.get(9),d.haversineFormula(g.nodes.get(8), g.nodes.get(9)));
        g.addEdge(g.nodes.get(10), g.nodes.get(4),d.haversineFormula(g.nodes.get(10), g.nodes.get(4)));
        g.addEdge(g.nodes.get(7), g.nodes.get(4),d.haversineFormula(g.nodes.get(7), g.nodes.get(4)));
        g.addEdge(g.nodes.get(7), g.nodes.get(2),d.haversineFormula(g.nodes.get(7), g.nodes.get(2)));

        System.out.println("Vertex Count: " + g.getVertexCount());
        System.out.println("Edge Count: " + g.getEdgeCount());

        for(int v = 0; v < g.getVertexCount(); v++){
            System.out.println(g.polyMap.get(v));
            System.out.println(g.adjMap.get(g.polyMap.get(v)));

        }

        System.out.println(g.edges());

        sp = new DijkstraShortestPath(g, g.nodes.get(0), vehicleType);
        System.out.println(g.nodes.isEmpty());
        System.out.println(3);
        System.out.println(sp.pathTo(g.nodes.get(10)));
        System.out.println(3);
    }
    @Test void dijkstraTest(){

        assertEquals("0->7  5,10 7->4  6,32 4->10 1.44 ",sp.pathTo(g.nodes.get(10)).toString());
    }


    @AfterEach void tearDown(){


    }


}
