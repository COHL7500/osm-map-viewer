package bfst22.vector;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MIDF {
    @Test 
    void nullString() {
        assertThrows(RuntimeException.class, () -> Address.parse(null));
    }

    @Test 
    void emptyString() {
        Address address = Address.parse("");
        assertEquals(null, address.street);
        assertEquals(null, address.house);
        assertEquals(null, address.floor);
        assertEquals(null, address.side);
        assertEquals(null, address.postcode);
        assertEquals(null, address.city);
    }

    @Test 
    void onlyStreetAddress() {
        Address address = Address.parse("Vejnavn");
        assertEquals("Vejnavn", address.street);
        assertEquals(null, address.postcode);
        assertEquals(null, address.city);
        assertEquals(null, address.house);
        assertEquals(null, address.floor);
        assertEquals(null, address.side);
    }

    @Test 
    void onlyHouseAddress() {
        Address address = Address.parse("Vejnavn 127");
        assertEquals("Vejnavn", address.street);
        assertEquals("127", address.house);
        assertEquals(null, address.postcode);
        assertEquals(null, address.city);
        assertEquals(null, address.floor);
        assertEquals(null, address.side);
    }
}
