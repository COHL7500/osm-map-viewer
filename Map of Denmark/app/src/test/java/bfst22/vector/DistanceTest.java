package bfst22.vector;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DistanceTest {
    PolyPoint A;
    PolyPoint B;
    PolyPoint C;

    PolyPoint point1;
    PolyPoint point2;

    Distance d = new Distance();

    @BeforeEach void setUp(){
        A = new PolyPoint(1,12,3);
        B = new PolyPoint(2, 10,4);
        C = new PolyPoint(3,8,6);

        point1 = new PolyPoint(1,(float)38.8976,(float)-77.0366);
        point2 = new PolyPoint(1,(float)39.9496,(float)-75.1503);
    }

    @Test void DistanceTest1(){
        assertEquals(2.24,d.haversineFormula(A,B));

    }

    @Test void DistanceTest2(){
        assertEquals(200,d.haversineFormula(point1,point2));

    }

    @AfterEach void tearDown(){

    }

}