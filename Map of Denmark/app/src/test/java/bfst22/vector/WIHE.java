package bfst22.vector;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WIHE {
    @Test
    public void testMyAddress(){
        assertEquals("Øresundsvej 24, null null\n2300 København S", Address.parse("Øresundsvej 24, 2300 København S").toString());
    }
}
