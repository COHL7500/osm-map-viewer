package bfst22.vector;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SIAR {
    @Test void testFlorAndSide() {
        var addr = Address.parse("Rued Langgaards Vej 7, 2. th., 2300 København S");
        assertEquals("Rued Langgaards Vej", addr.street);
        assertEquals("7", addr.house);
        assertEquals("2", addr.floor);
        assertEquals("th.", addr.side);
        assertEquals("2300", addr.postcode);
        assertEquals("København S", addr.city);
    }
    @Test void testFloorAndSideWithSpaces() {
        var addr = Address.parse("Rued Langgaards Vej    7 , 2.      th. , 2300     København S");
        assertEquals("Rued Langgaards Vej", addr.street);
        assertEquals("7", addr.house);
        assertEquals("2", addr.floor);
        assertEquals("th.", addr.side);
        assertEquals("2300", addr.postcode);
        assertEquals("København S", addr.city);
    }
}
