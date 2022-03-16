package bfst22.vector;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ASLY {
    @Test
    public void testStreetNameWithCapitalÅ() {
        var adr = bfst22.vector.Address.parse("Ådalsparkvej 37, st th 2970 Hørsholm");
        assertEquals("Ådalsparkvej", adr.street);
    }

    @Test
    public void test_ST_and_TH_AreValidFloors() {
        var adr = bfst22.vector.Address.parse("Ådalsparkvej 37, st th 2970 Hørsholm");
        assertEquals("st", adr.floor);
        assertEquals("th", adr.side);

    }

    @Test
    public void testØinCityName() {
        var adr = bfst22.vector.Address.parse("Ådalsparkvej 37, st th 2970 Hørsholm");
        assertEquals("Hørsholm", adr.city);
    }

    @Test
    public void testUmlautInStreetName() {
        var adr = bfst22.vector.Address.parse("Paludan Müllers Vej 1 1815 Frederiksberg C");
        assertEquals("Paludan Müllers Vej", adr.street);
        assertEquals("1",adr.house);
        assertEquals("1815", adr.postcode);
        assertEquals("Frederiksberg C", adr.city);
    }

    @Test
    public void testWithSpecialLetterŸ() {
        var adr = bfst22.vector.Address.parse("Konsul Beÿers Allé 2, 4300 Holbæk");
        assertEquals("Konsul Beÿers Allé", adr.street);
        assertEquals("4300", adr.postcode);
        assertEquals("Holbæk", adr.city);
    }

    @Test
    public void testWithSpecialLetterÈ() {
        var adr = bfst22.vector.Address.parse("Stenbro Allè 6650 Brørup");
        assertEquals("Stenbro Allè", adr.street);
    }
}
