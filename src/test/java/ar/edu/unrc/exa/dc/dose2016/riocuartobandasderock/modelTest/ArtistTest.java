package ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.modelTest;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ar.edu.unrc.exa.dc.dose2016.riocuartobandasderock.model.Artist;


public class ArtistTest {
	
	@Rule
	public ExpectedException expected = ExpectedException.none();
	
	@Test
	public void constructorTest(){
		String a_name = "Carlos Alberto";
		String a_surname = "Solari";
		String a_nick = "Indio";
		Artist artist = new Artist(a_name, a_surname, a_nick);
		assertTrue(artist.getName().equals(a_name));
		assertTrue(artist.getSurname().equals(a_surname));
		assertTrue(artist.getNickname().equals(a_nick));
	}	
	
	@Test
	public void constructorTestWithoutNickname(){
		String a_name = "Gustavo";
		String a_surname = "Napoli";
		Artist artist = new Artist(a_name, a_surname);
		assertTrue(artist.getName().equals(a_name));
		assertTrue(artist.getSurname().equals(a_surname));
	}
	
	@Test
	public void setNameTest(){
	    String a_name = "Pablo";
	    String a_surname = "Beilinson";
	    Artist artist = new Artist(a_name, a_surname);
	    String new_name = "Eduardo";
	    artist.setName(new_name);
	    assertEquals(artist.getName(),new_name);  
	}
	
	@Test
	public void setNullNameTest(){
		String a_name = "Pablo";
	    String a_surname = "Beilinson";
	    Artist artist = new Artist(a_name, a_surname);
		expected.expect(IllegalArgumentException.class);
		artist.setName(null);
	}
	
	@Test
	public void setSurnameTest(){
	    String a_name = "Gustavo";
	    String a_surname = "González";
	    Artist artist = new Artist(a_name, a_surname);
	    String new_surname = "Cerati";
	    artist.setSurname(new_surname);
	    assertEquals(artist.getSurname(),new_surname);  
	}
	
	@Test
	public void setNullSurnameTest(){
		String a_name = "Gustavo";
	    String a_surname = "Cerati";
	    Artist artist = new Artist(a_name, a_surname);
		expected.expect(IllegalArgumentException.class);
		artist.setSurname(null);
	}
	
	@Test
	public void setNicknameTest(){
	    String a_name = "Juan Sebastián";
	    String a_surname = "Gutiérrez";
	    Artist artist = new Artist(a_name, a_surname);
	    String nickname = "Juanse";
	    artist.setNickname(nickname);
	    assertEquals(artist.getNickname(),nickname);  
	}
	
	@Test
	public void setNullNicknameTest(){
      String a_name = "Juan Sebastián";
      String a_surname = "Gutiérrez";
      String a_nickname = "Juanse";
      Artist artist = new Artist(a_name, a_surname, a_nickname);
      expected.expect(IllegalArgumentException.class);
      artist.setNickname(null);
	}
	
	@Test
	public void equalsTrueTest(){
		String a_name = "Carlos Alberto";
		String a_surname = "Solari";
		String a_nick = "Indio";
		Artist artist1 = new Artist(a_name, a_surname, a_nick);
		Artist artist2 = new Artist(a_name, a_surname, a_nick);
		assertTrue(artist1.equals(artist2));
	}
	
	@Test
	public void equalsFalse1Test(){
		String a_name1 = "Carlos Alberto";
		String a_surname1 = "Solari";
		String a_nick1 = "Indio";
		Artist artist1 = new Artist(a_name1, a_surname1, a_nick1);
		String a_name2 = "Juan Sebastián";
	    String a_surname2 = "Gutiérrez";
	    String a_nick2 = "Juanse";
		Artist artist2 = new Artist(a_name2, a_surname2, a_nick2);
		assertFalse(artist1.equals(artist2));
	}
	
	@Test
	public void equalsFalse2Test(){
		String a_name1 = "Carlos Alberto";
		String a_surname1 = "Solari";
		String a_nick1 = "Indio";
		Artist artist1 = new Artist(a_name1, a_surname1, a_nick1);
		String a_name2 = "Carlos Alberto";
	    String a_surname2 = "Solari";
	    String a_nick2 = "Juanse";
		Artist artist2 = new Artist(a_name2, a_surname2, a_nick2);
		assertFalse(artist1.equals(artist2));
	}
	
	@Test
	public void equalsFalse3Test(){
		String a_name1 = "Carlos Alberto";
		String a_surname1 = "Solari";
		String a_nick1 = "Indio";
		Artist artist1 = new Artist(a_name1, a_surname1, a_nick1);
		String a_name2 = "Carlos Alberto";
	    String a_surname2 = "Gutiérrez";
	    String a_nick2 = "Indio";
		Artist artist2 = new Artist(a_name2, a_surname2, a_nick2);
		assertFalse(artist1.equals(artist2));
	}
	
	@Test
	public void equalsFalse4Test(){
		String a_name1 = "Carlos Alberto";
		String a_surname1 = "Solari";
		String a_nick1 = "Indio";
		Artist artist1 = new Artist(a_name1, a_surname1, a_nick1);
		String a_name2 = "Juan Sebastián";
	    String a_surname2 = "Solari";
	    String a_nick2 = "Juanse";
		Artist artist2 = new Artist(a_name2, a_surname2, a_nick2);
		assertFalse(artist1.equals(artist2));
	}
	
	@Test
	public void equalsNullTest(){
		String a_name = "Carlos Alberto";
		String a_surname = "Solari";
		String a_nick = "Indio";
		Artist artist1 = new Artist(a_name, a_surname, a_nick);
		expected.expect(IllegalArgumentException.class);
		assertTrue(artist1.equals(null));
	}
}

