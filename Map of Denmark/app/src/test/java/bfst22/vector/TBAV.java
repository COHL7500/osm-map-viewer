package bfst22.vector;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TBAV {
    @Test void streetWithNumber(){
        Address address= Address.parse("Rued Langgaards Vej 7");
        assertEquals("Rued Langgaards Vej",address.street);
        assertEquals("7", address.house);
    }

    @Test void streetWithNumberAndFloor(){
        Address address= Address.parse("Rued Langgaards Vej 7, 2.");
        assertEquals("Rued Langgaards Vej",address.street);
        assertEquals("7", address.house);
        assertEquals("2",address.floor);
    }

    @Test void streetWithNumberAndFloorAndSide(){
        Address address= Address.parse("Rued Langgaards Vej 7, 2. th.");
        assertEquals("Rued Langgaards Vej",address.street);
        assertEquals("7", address.house);
        assertEquals("2",address.floor);
        assertEquals("th",address.side);
    }
}
