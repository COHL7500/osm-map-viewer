package bfst22.vector;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JSPI {
    @Test void itu() {
        var addr = Address.parse("Hobrogade 11, 7.tv 2100 København Ø");
        assertEquals("Hobrogade", addr.street);
        assertEquals("5", addr.house);
        assertEquals("2100", addr.postcode);
        assertEquals("København Ø", addr.city);
        assertEquals("1", addr.floor);
        assertEquals("tv", addr.side);
    }
    @Test void hjem(){
        var addr = Address.parse("Hobrogade 89, 2100 København Ø");
        assertEquals("Hobrogade", addr.street);
        assertEquals("5", addr.house);
        assertEquals("2100", addr.postcode);
        assertEquals("København Ø", addr.city);
    }
}
