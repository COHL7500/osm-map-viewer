package bfst22.vector;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LULN {
  
  @Test
  void testParser() {
      var address = Address.parse("Rued Langgaards Vej 7, 2300 København S Danmark");
      assertEquals("Rued Langgaards Vej", address.street);
      assertEquals("7", address.house);
      assertEquals("2300", address.postcode);
      assertEquals("København S Danmark", address.city);
    
    }
}
