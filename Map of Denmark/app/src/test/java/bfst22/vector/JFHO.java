package bfst22.vector;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JFHO {
    @Test void kunVej() {
        var addr = Address.parse("Jernbanegade 41B");
        assertEquals("Jernbanegade", addr.street);
        assertEquals("41B", addr.house);
        assertNull(addr.postcode);
        assertNull(addr.city);
    }
    @Test void masserAfKomma() {
        var addr = Address.parse("Jernbanegade       41B,,,,,, 3600 Frederikssund");
        assertEquals("Jernbanegade", addr.street);
        assertEquals("41B", addr.house);
        assertEquals("3600", addr.postcode);
        assertEquals("Frederikssund", addr.city);
    }
    @Test void helAddresse() {
        var addr = Address.parse("Jernbanegade 41B, 3. th. 3600 Frederikssund");
        assertEquals("Jernbanegade", addr.street);
        assertEquals("41B", addr.house);
        assertEquals("3", addr.floor);
        assertEquals("th", addr.side);
        assertEquals("3600", addr.postcode);
        assertEquals("Frederikssund", addr.city);
    }
}
