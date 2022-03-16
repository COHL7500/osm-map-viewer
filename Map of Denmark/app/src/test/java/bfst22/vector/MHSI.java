package bfst22.vector;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MHSI {
    @Test void itu() {
        var addr = Address.parse("Rued Langgaards Vej 7, 2300 København S");
        assertEquals("Rued Langgaards Vej", addr.street);
        assertEquals("7", addr.house);
        assertEquals("2300", addr.postcode);
        assertEquals("København S", addr.city);
    }
    @Test void ituComma() {
        var addr = Address.parse("Rued Langgaards Vej    7 ,  , 2300     København S");
        assertEquals("Rued Langgaards Vej", addr.street);
        assertEquals("7", addr.house);
        assertEquals("2300", addr.postcode);
        assertEquals("København S", addr.city);
    }
    @Test void ituSimple() {
        var addr = Address.parse("Rued Langgaards Vej 7");
        assertEquals("Rued Langgaards Vej", addr.street);
        assertEquals("7", addr.house);
    }
    @Test void ituCommaNoCity() {
        var addr = Address.parse("Rued Langgaards Vej 7, ");
        assertEquals("Rued Langgaards Vej", addr.street);
        assertEquals("7", addr.house);
    }
    @Test void ituOnlyPostcode() {
        var addr = Address.parse("Rued Langgaards Vej 7, 2300");
        assertEquals("Rued Langgaards Vej", addr.street);
        assertEquals("7", addr.house);
        assertEquals("2300", addr.postcode);
        //assertEquals("København S", addr.city, "addr.city should equal \"København S\", but was \"" + addr.city + "\"");
    }
    @Test void ituPostcodeCommaCity() {
        var addr = Address.parse("Rued Langgaards Vej 7, 2300, København S");
        assertEquals("Rued Langgaards Vej", addr.street);
        assertEquals("7", addr.house);
        assertEquals("2300", addr.postcode);
        assertEquals("København S", addr.city);
    }
    @Test void ituOnlyCity() {
        var addr = Address.parse("Rued Langgaards Vej 7, København S");
        assertEquals("Rued Langgaards Vej", addr.street);
        assertEquals("7", addr.house);
        assertEquals("København S", addr.city);
    }
}
