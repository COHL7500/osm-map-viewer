package bfst22.vector;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class KMSA {
    @Test void adress() {
        var addr = Address.parse("Skansørevej 19, 3000 Helsingør");
        assertEquals("Skansørevej", addr.street);
        assertEquals("19", addr.house);
        assertEquals("3000", addr.postcode);
        assertEquals("Helsingør", addr.city);
    }
    @Test void ituComma() {
        var addr = Address.parse("Rued Langgaards Vej    7 ,  , 2300     København S");
        assertEquals("Rued Langgaards Vej", addr.street);
        assertEquals("7", addr.house);
        assertEquals("2300", addr.postcode);
        assertEquals("København S", addr.city);
    }
    @Test void ituApartment() {
        var addr = Address.parse("Johan Kellersvej 38, 1tv");
        assertEquals("Johan Kellersvej", addr.street);
        assertEquals("38", addr.house);
        assertEquals("1", addr.floor);
        assertEquals("tv", addr.side);
    }
    @Test void specialCharacters(){
        var addr = Address.parse("Johan Kellersvej 38, 1tv 2300 Ålborg");
        assertEquals("Ålborg", addr.city);
    }
    @Test void standard(){
        var addr = Address.parse("Rued Langgaards Vej 7, 2300 København S");
        assertEquals("Rued Langgaards Vej", addr.street);
        assertEquals("7", addr.house);
        assertEquals("2300", addr.postcode);
        assertEquals("København S", addr.city);
    }
}
