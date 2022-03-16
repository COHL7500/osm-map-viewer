package bfst22.vector;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OKRE {
    @Test void home(){
        var addr = Address.parse("Hjørringgade 31, 2100 København Ø");
        assertEquals("Hjørringgade", addr.street);
        assertEquals("31", addr.house);
        assertEquals("2100", addr.postcode);
        assertEquals("København Ø", addr.city);
    }
    @Test void homeWithFloor(){
        var addr = Address.parse("Hjørringgade 31, 5. tv., 2100 København Ø");
        assertEquals("Hjørringgade", addr.street);
        assertEquals("31", addr.house);
        assertEquals("5", addr.floor);
        assertEquals("tv", addr.side);
        assertEquals("2100", addr.postcode);
        assertEquals("København Ø", addr.city);
    }
    @Test void addressWithUnknownLetters(){
        var addr = Address.parse("Århusgade 48, 2100 København Ø");
        assertEquals("Århusgade", addr.street);
        assertEquals("48", addr.house);
        assertEquals("2100", addr.postcode);
        assertEquals("København Ø", addr.city);
    }

}
