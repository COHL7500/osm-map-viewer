package bfst22.vector;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DistanceTest {
    PolyPoint A;
    PolyPoint B;
    PolyPoint C;
    Distance d = new Distance();

    @BeforeEach void setUp(){
        A = new PolyPoint(1,12,3);
        B = new PolyPoint(2, 10,4);
        C = new PolyPoint(3,8,6);
    }

    @Test void testDistances(){
        assertEquals(2.24,d.haversineFormula(A,B));

    }

    @AfterEach void tearDown(){

    }

}