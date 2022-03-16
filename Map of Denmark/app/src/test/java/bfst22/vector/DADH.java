package bfst22.vector;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DADH {

    @Test
    void testParser() {
        var addr = bfst22.vector.Address.parse("Rued Langgaards Vej 7, 2300 København S Danmark");

        assertEquals("Rued Langgaards Vej", addr.street);
        assertEquals("7", addr.house);
        assertEquals("2300", addr.postcode);
        assertEquals("København S Danmark", addr.city);
    }

    @Test
    void testParserWithMissingInput() {
        var addr = Address.parse("");

        assertNull(addr.street);
        assertNull(addr.house);
        assertNull(addr.postcode);
        assertNull(addr.city);
    }

    

}
