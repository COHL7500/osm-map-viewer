package bfst22.vector;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/*Skal lige ordnes, så det ikke bare er en kopi*/

class MBIA {
    @Test void itu() {
        var addr = Address.parse("Pærevangen 22, 2765 Smørum");
        assertEquals("Pærevangen", addr.street);
        assertEquals("22", addr.house);
        assertEquals("2765", addr.postcode);
        assertEquals("Smørum", addr.city);
    }
    @Test void ituComma() {
        var addr = Address.parse("Pærevangen    22 ,  , 2765     Smørum");
        assertEquals("Pærevangen", addr.street);
        assertEquals("22", addr.house);
        assertEquals("2765", addr.postcode);
        assertEquals("Smørum", addr.city);
    }
    @Test void ituSimple() {
        var addr = Address.parse("Pærevangen 22");
        assertEquals("Pærevangen", addr.street);
        assertEquals("22", addr.house);
    }
}
