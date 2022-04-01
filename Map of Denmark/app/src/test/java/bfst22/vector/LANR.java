package bfst22.vector;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LANR {
    @Test
    public void ituApartmentTest(){
        Address address = Address.parse("Rued Langaards Vej 7A, .1 th 4000 København S");
        assertEquals("Rued Langaards Vej", address.street);
        assertEquals("7A", address.house);
        assertEquals("1.", address.floor);
        assertEquals("th", address.side);
        assertEquals("4000", address.postcode);
        assertEquals("København S", address.city);
    }
}
