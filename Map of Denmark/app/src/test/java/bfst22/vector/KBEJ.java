package bfst22.vector;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class KBEJ {
    @Test void FullAddress() {
        var addr = Address.parse("Østergade 82A-84,7.th 2100 København Ø");
        assertEquals("Østergade", addr.street);
        assertEquals("82A-84", addr.house);
        assertEquals("7", addr.floor);
        assertEquals("th", addr.side);
        assertEquals("2100", addr.postcode);
        assertEquals("København Ø", addr.city);
    }
    @Test void adressAndfloor(){
        var addr = Address.parse("Østergade 82A-84,7.th");
        assertEquals("Østergade", addr.street);
        assertEquals("82A-84", addr.house);
        assertEquals("7", addr.floor);
        assertEquals("th", addr.side);
    }
}
