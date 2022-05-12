package bfst22.vector;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class DirectionsTest {
    Distance distance = new Distance();
    Directions directions = new Directions();
    float km2meter;
    Graph g = new Graph();
    DijkstraSP sp1;
    DijkstraSP sp2;
    List<PolyPoint> path1 = new ArrayList<>();
    List<PolyPoint> path2 = new ArrayList<>();
    VehicleType vehicleType;


    PolyPoint A = new PolyPoint(1,(float)12.6039900,(float)55.6390580);
    PolyPoint B = new PolyPoint(2,(float)12.6041791,(float)55.6385458);
    PolyPoint C = new PolyPoint(3,(float)12.5999974,(float)55.6380447);
    PolyPoint D = new PolyPoint(4, (float)12.5993906,(float)55.6397972);
    PolyPoint E = new PolyPoint(5,(float)12.6015500,(float)55.6400283);

    @BeforeEach void setUp(){
        path1.add(A);
        path1.add(B);
        path1.add(C);
        path1.add(D);
        path1.add(E);

        path2.add(E);
        path2.add(D);
        path2.add(C);
        path2.add(B);
        path2.add(A);

        g.add(path1);
        g.add(path2);
        g.generate();


        g.addEdge(A,B, (float)distance.haversineFormula(A,B));
        g.addEdge(B,C, (float)distance.haversineFormula(B,C));
        g.addEdge(C,D, (float)distance.haversineFormula(C,D));
        g.addEdge(D,E, (float)distance.haversineFormula(D,E));

        g.addEdge(E,D, (float)distance.haversineFormula(E,D));
        g.addEdge(D,C, (float)distance.haversineFormula(D,C));
        g.addEdge(C,B, (float)distance.haversineFormula(C,B));
        g.addEdge(B,A, (float)distance.haversineFormula(B,A));

    }

    @Test void path1Test(){
        sp1 = new DijkstraSP(g,A,E,vehicleType.MOTORCAR);
        Stack<String> path = new Stack<>();
        Iterator<Edge> edgeIterator = sp1.pathTo(E).iterator();

        /*
        while(edgeIterator.hasNext()){
            path.push(directions.turn(e.getFrom(),e.getTo()));
        }
        */

        for(Edge e : sp1.pathTo(E)){

            path.push(directions.turn(e.getFrom(),sp1.pathTo(E).iterator().next().getTo()));

        }
        assertEquals("idk",path.toString());
    }

    @Test void path2Test(){
        sp1 = new DijkstraSP(g,E,A,vehicleType.MOTORCAR);
        Stack<Edge> path = new Stack<>();
        for(Edge e : sp1.pathTo(A)){

            path.push(e);
        }
        assertEquals("idk",sp1.pathToString(path));

    }

    @AfterEach void tearDown (){

    }

}