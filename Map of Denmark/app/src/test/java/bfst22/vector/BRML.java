package bfst22.vector;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BRML {
    @Test void normalAdresse(){
        var adresse = Address.parse("Svend Gønges Vej 13, 2680 Solrød Strand");
        assertEquals("Svend Gønges Vej", adresse.street);
        assertEquals("13", adresse.house);
        assertEquals("2680", adresse.postcode);
        assertEquals("Solrød Strand", adresse.city);
    }

    @Test void adresseFloorOgSide(){
        var adresse = Address.parse("Rued Langgaards Vej 7, 3 th 2300 København S");
        assertEquals("Rued Langgaards Vej", adresse.street);
        assertEquals("7", adresse.house);
        assertEquals("3", adresse.floor);
        assertEquals("th", adresse.side);
        assertEquals("2300", adresse.postcode);
        assertEquals("København S", adresse.city);
    }

    @Test void adresseMedBogstavIHusnummer(){
        var adresse = Address.parse("Rued Langgaards Vej 7A, 2300 København S");
        assertEquals("Rued Langgaards Vej", adresse.street);
        assertEquals("7A", adresse.house);
        assertEquals("2300", adresse.postcode);
        assertEquals("København S", adresse.city);
    }
}
