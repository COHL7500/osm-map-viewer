package bfst22.vector;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BIHA {
    @Test
    public void testÆØÅ(){
        var address = Address.parse("Ålekistevej 121, 2720 København");
        assertEquals("Ålekistevej", address.street,"Danish street and city names can start with ÆØÅ");
        var address2 = Address.parse("Lavendelvej 7, 3650 Ølstykke");
        assertEquals("Ølstykke", address2.city,"Danish street and city names can start with ÆØÅ");
    }

}
