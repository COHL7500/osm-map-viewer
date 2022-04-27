package bfst22.vector;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.testng.annotations.Test;
import java.util.*;
import bfst22.vector.*;

public class DijkstraSPTest {
    //ArrayList<PolyPoint> list = new ArrayList<>(); ArrayList that will hold all the PolyPoints in the Graph.

    /*PolyPoint objects*/
        /*
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
        */

        /*Method that turns an ArrayList of PolyPoints
        ArrayList<PolyPoint> returnArraylist(){

            list.add(new PolyPoint(1,12,3));
            list.add(new PolyPoint(2, 10,4));
            list.add(new PolyPoint(3,8,6));
            list.add(new PolyPoint(4,7,8));
            list.add(new PolyPoint(5, 5,10));
            list.add(new PolyPoint(6,10,2));
            list.add(new PolyPoint(7,7,2));
            list.add(new PolyPoint(8, 7,4));
            list.add(new PolyPoint(9, 5,3));
            list.add(new PolyPoint(10, 5,6));
            list.add(new PolyPoint(11,4,9));
            return list;
        }
        */

    @BeforeEach void setUp(){


    }

    @Test void DijkstraTest(){
        VehicleType vehicleType = VehicleType.MOTORCAR;
        Distance d = new Distance();
        ArrayList<PolyPoint> list = new ArrayList<>();

        list.add(new PolyPoint(0,12,3)); //A
        list.add(new PolyPoint(1, 10,4)); //B
        list.add(new PolyPoint(2,8,6)); //C
        list.add(new PolyPoint(3,7,8)); //D
        list.add(new PolyPoint(4, 5,10)); //E
        list.add(new PolyPoint(5,10,2)); //F
        list.add(new PolyPoint(6,7,2)); //G
        list.add(new PolyPoint(7, 7,4)); //H
        list.add(new PolyPoint(8, 5,3)); //I
        list.add(new PolyPoint(9, 5,6)); //J
        list.add(new PolyPoint(10,4,9)); //K

        Graph g = new Graph(list);
        g.addEdge(list.get(0),list.get(1),d.haversineFormula(list.get(0),list.get(1)));
        g.addEdge(list.get(1),list.get(2),d.haversineFormula(list.get(1),list.get(2)));
        g.addEdge(list.get(2),list.get(3),d.haversineFormula(list.get(2),list.get(3)));
        g.addEdge(list.get(3),list.get(4),d.haversineFormula(list.get(3),list.get(4)));
        g.addEdge(list.get(0),list.get(5),d.haversineFormula(list.get(0),list.get(5)));
        g.addEdge(list.get(5),list.get(6),d.haversineFormula(list.get(5),list.get(6)));
        g.addEdge(list.get(6),list.get(7),d.haversineFormula(list.get(6),list.get(7)));
        g.addEdge(list.get(0),list.get(3),d.haversineFormula(list.get(0),list.get(3)));
        g.addEdge(list.get(0),list.get(7),d.haversineFormula(list.get(0),list.get(7)));
        g.addEdge(list.get(6),list.get(8),d.haversineFormula(list.get(6),list.get(8)));
        g.addEdge(list.get(6),list.get(9),d.haversineFormula(list.get(6),list.get(9)));
        g.addEdge(list.get(9),list.get(10),d.haversineFormula(list.get(9),list.get(10)));
        g.addEdge(list.get(8),list.get(9),d.haversineFormula(list.get(8),list.get(9)));
        g.addEdge(list.get(10),list.get(4),d.haversineFormula(list.get(10),list.get(4)));
        g.addEdge(list.get(7),list.get(4),d.haversineFormula(list.get(7),list.get(4)));
        g.addEdge(list.get(7),list.get(2),d.haversineFormula(list.get(7),list.get(2)));

        DijkstraSP sp = new DijkstraSP(g,list.get(0), list.get(10), vehicleType);
        System.out.println(sp.pathTo(list.get(10)));
    }

    @AfterEach void tearDown(){


    }


}
