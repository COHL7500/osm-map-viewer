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
    DijkstraSP sp;
    int speedLimit = 75;
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
        g = new Graph();

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

        g.add(nodes);
        g.generate();

        System.out.println("Vertex Count: " + g.getVertexCount());
        System.out.println("Edge Count: " + g.getEdgeCount());

        /*
        g.addEdge(g.nodes.get(0), g.nodes.get(1),g.setWeight((float)2.24));
        g.addEdge(g.nodes.get(1), g.nodes.get(2),g.setWeight((float)2.83));
        g.addEdge(g.nodes.get(2), g.nodes.get(3),(float)2.24);
        g.addEdge(g.nodes.get(3), g.nodes.get(4),(float)2.83);
        g.addEdge(g.nodes.get(0), g.nodes.get(5),(float)2.24);
        g.addEdge(g.nodes.get(5), g.nodes.get(6),(float)3);
        g.addEdge(g.nodes.get(6), g.nodes.get(7),(float)2);
        g.addEdge(g.nodes.get(0), g.nodes.get(3),(float)7.07);
        g.addEdge(g.nodes.get(0), g.nodes.get(7),(float)5.1);
        g.addEdge(g.nodes.get(6), g.nodes.get(8),(float)2.24);
        g.addEdge(g.nodes.get(6), g.nodes.get(9),(float)4.47);
        g.addEdge(g.nodes.get(9), g.nodes.get(10),(float)3.16);
        g.addEdge(g.nodes.get(8), g.nodes.get(9),(float)3);
        g.addEdge(g.nodes.get(10), g.nodes.get(4),(float)1.41);
        g.addEdge(g.nodes.get(7), g.nodes.get(4),(float)6.32);
        g.addEdge(g.nodes.get(7), g.nodes.get(2),(float)2.24);
        */

        g.addEdge(g.nodes.get(0), g.nodes.get(1),g.setWeightDistance(g.nodes.get(0), g.nodes.get(1),speedLimit));
        g.addEdge(g.nodes.get(1), g.nodes.get(2),g.setWeightDistance(g.nodes.get(1), g.nodes.get(2),speedLimit));
        g.addEdge(g.nodes.get(2), g.nodes.get(3),g.setWeightDistance(g.nodes.get(2), g.nodes.get(3),speedLimit));
        g.addEdge(g.nodes.get(3), g.nodes.get(4),g.setWeightDistance(g.nodes.get(3), g.nodes.get(4),speedLimit));
        g.addEdge(g.nodes.get(0), g.nodes.get(5),g.setWeightDistance(g.nodes.get(0), g.nodes.get(5),speedLimit));
        g.addEdge(g.nodes.get(5), g.nodes.get(6),g.setWeightDistance(g.nodes.get(5), g.nodes.get(6),speedLimit));
        g.addEdge(g.nodes.get(6), g.nodes.get(7),g.setWeightDistance(g.nodes.get(6), g.nodes.get(7),speedLimit));
        g.addEdge(g.nodes.get(0), g.nodes.get(3),g.setWeightDistance(g.nodes.get(0), g.nodes.get(3),speedLimit));
        g.addEdge(g.nodes.get(0), g.nodes.get(7),g.setWeightDistance(g.nodes.get(0), g.nodes.get(7),speedLimit));
        g.addEdge(g.nodes.get(6), g.nodes.get(8),g.setWeightDistance(g.nodes.get(6), g.nodes.get(8),speedLimit));
        g.addEdge(g.nodes.get(6), g.nodes.get(9),g.setWeightDistance(g.nodes.get(6), g.nodes.get(9),speedLimit));
        g.addEdge(g.nodes.get(9), g.nodes.get(10),g.setWeightDistance(g.nodes.get(9), g.nodes.get(10),speedLimit));
        g.addEdge(g.nodes.get(8), g.nodes.get(9),g.setWeightDistance(g.nodes.get(8), g.nodes.get(9),speedLimit));
        g.addEdge(g.nodes.get(10), g.nodes.get(4),g.setWeightDistance(g.nodes.get(10), g.nodes.get(4),speedLimit));
        g.addEdge(g.nodes.get(7), g.nodes.get(4),g.setWeightDistance(g.nodes.get(7), g.nodes.get(4),speedLimit));
        g.addEdge(g.nodes.get(7), g.nodes.get(2),g.setWeightDistance(g.nodes.get(7), g.nodes.get(2),speedLimit));


        System.out.println("Vertex Count: " + g.getVertexCount());
        System.out.println("Edge Count: " + g.getEdgeCount());


    }
    @Test void dijkstraTest0to4(){
        sp = new DijkstraSP(g, g.nodes.get(0),g.nodes.get(4));
        System.out.println(sp.pathToString(sp.pathTo(g.nodes.get(4))));
        assertEquals("[4->5  4.181843, 1->4  10.410108]","" + sp.pathToString(sp.pathTo(g.nodes.get(4))));
    }

    @Test void dijkstraTest0to10(){
        sp = new DijkstraSP(g,g.nodes.get(0),g.nodes.get(10));
        assertEquals("[10->11  4.6753273, 7->10  6.6010547, 6->7  4.447797, 1->6  3.3030636]","" + sp.pathToString(sp.pathTo(g.nodes.get(10))));
    }


    @AfterEach void tearDown(){
        g.clearList();
        nodes.clear();
        sp = null;

    }


}
