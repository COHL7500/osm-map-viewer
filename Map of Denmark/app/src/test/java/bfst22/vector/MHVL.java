package bfst22.vector;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MHVL {
    @Test
    public void emptyAddress() {
        var address = Address.parse("");
        assertNull(address.street);
        assertNull(address.house);
        assertNull(address.floor);
        assertNull(address.side);
        assertNull(address.postcode);
        assertNull(address.city);
    }

    @Test
    public void fullAddress() {
        var address = Address.parse("StopStalkingAddresserDinCreepVej 69, 21. tv, 4200 Assens");
        assertEquals("StopStalkingAddresserDinCreepVej", address.street);
        assertEquals("69", address.house);
        assertEquals("21", address.floor);
        assertEquals("tv", address.side);
        assertEquals("4200", address.postcode);
        assertEquals("Assens", address.city);
    }
}
