package bfst22.vector;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TUKA {
    @Test
    void testEverything() {
        var addr = Address.parse("Kierulffsvej 72, 4200 Slagelse");
        assertEquals("Kierulffsvej", addr.street);
        assertEquals("72", addr.house);
        assertEquals("4200", addr.postcode);
        assertEquals("Slagelse", addr.city);
    }

    @Test
    void addressPostCode() {
        var addr = Address.parse("Kierulffsvej 72, 4200 Slagelse");
        assertEquals("4200", addr.postcode);
    }

    @Test
    void addressCity() {
        var addr = Address.parse("Kierulffsvej 72, 4200 Slagelse");
        assertEquals("Slagelse", addr.city);
    }

    @Test
    void addressFloor() {
        var addr = Address.parse("Kierulffsvej 72");
        assertEquals("72", addr.house);
    }
}