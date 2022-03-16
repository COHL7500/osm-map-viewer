package bfst22.vector;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OLJH {
    @Test void floorAndSideFullAddress() {
        var addr = Address.parse("Eventyrvej 2, 3. th., 4100 Korsør");
        assertEquals("Eventyrvej", addr.street);
        assertEquals("2", addr.house);
        assertEquals("3.", addr.floor);
        assertEquals("th.", addr.side);
        assertEquals("4100", addr.postcode);
        assertEquals("Korsør", addr.city);
    }

    @Test void specialCharacter() {
        var addr = Address.parse("Slots Allé 56");
        assertEquals("Slots Allé", addr.street);
        assertEquals("56", addr.house);
    }
}
