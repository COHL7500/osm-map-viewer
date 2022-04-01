package bfst22.vector;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EMNO {
	@Test
	void ituLetter(){
		var addr = Address.parse("Rued Langgaards Vej 7a, 2300 København S");
		assertEquals("7a", addr.house);
	}
	
	@Test
	void ituLetterCapital(){
		var addr = Address.parse("Rued Langgaards Vej 7A, 2300 København S");
		assertEquals("7A", addr.house);
	}
	
	@Test
	void ituFloor(){
		var addr = Address.parse("Rued Langgaards Vej 7 2, 2000 København");
		assertEquals("2", addr.floor);
	}
	
	@Test
	void ituFloorOrdinal(){
		var addr = Address.parse("Rued Langgaards Vej 7 2., 2000 København");
		assertEquals("2", addr.floor);
	}	
	
	@Test
	void ituFloorSide(){
		var addr = Address.parse("Rued Langgaards Vej 7 2grøn, 2000 København");
		assertEquals("2", addr.floor);
		assertEquals("grøn", addr.side);
	}
	
	@Test
	void ituNoHouseNumber(){
		var addr = Address.parse("Rued Langgaards Vej, 2000 København");
		assertEquals("Rued Langgaards Vej", addr.street);
		assertEquals("2000", addr.postcode);
	}
	
	@Test
	void ituNoPostcode(){
		var addr = Address.parse("Rued Langgaards Vej 7, København");
		assertEquals("København", addr.city);
	}
	
	@Test
	void ituNoCity(){
		var addr = Address.parse("Rued Langgaards Vej 7, 2000");
		assertEquals("2000", addr.postcode);
	}	
	
	@Test
	void alley(){
		var addr = Address.parse("Allé 3, 2720 Vanløse");
		assertEquals("Allé", addr.street);
	}
	
	@Test
	void intervalHouse(){
		var addr = Address.parse("gudhjemvej 85-87, 4878 gudhjem");
		assertEquals("85-87", addr.house);
	}
	
	@Test
	void longPostcode(){
		var addr = Address.parse("gudhjemvej 1, 99999 gudhjem");
		assertEquals("99999", addr.postcode);
		addr = Address.parse("gudhjemvej 1, 888888 gudhjem");
		assertEquals("888888", addr.postcode);		
	}
	
}
