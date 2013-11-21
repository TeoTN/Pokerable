package test;
import model.*;
import static org.junit.Assert.*;
import org.junit.Test;


public class CardTest
{
	Card x;
	
	@Test
	public void testEmpty(){
		x = new Card();
		assertNotNull("Empty constructor for Card", x);
	}
	
	@Test
	public void testChar()
	{
		try{
			x = new Card('S','K');
			assertNotNull("Constructor for Card", x);
			assertTrue("Good init",x.toString().equals("SK"));
		}
		catch(Exception ex){
			fail(ex.getMessage());
		}
	}
	
	@Test
	public void testGetCard()
	{
		try{
			x = new Card('S','K');
			assertNotNull("Constructor for Card", x);
			assertTrue("getCard()",x.getCard() == 'K');
		}
		catch(Exception ex){
			fail(ex.getMessage());
		}
	}
	
	@Test
	public void testGetColor()
	{
		try{
			x = new Card('S','K');
			assertNotNull("Constructor for Card", x);
			assertTrue("getColor()",x.getColor() == 'S');
		}
		catch(Exception ex){
			fail(ex.getMessage());
		}
	}
	
	@Test
	public void testGetCardValue()
	{
		try{
			x = new Card('S','K');
			assertNotNull("Constructor for Card", x);
			assertTrue("getCardValue()",x.getCardValue() == 11);
		}
		catch(Exception ex){
			fail(ex.getMessage());
		}
	}
	
	@Test
	public void testGetColorValue()
	{
		try{
			x = new Card('S','K');
			assertNotNull("Constructor for Card", x);
			assertTrue("getColorValue()",x.getColorValue() == 3);
		}
		catch(Exception ex){
			fail(ex.getMessage());
		}
	}
	
	@Test
	public void testCompareTo()
	{
		try{
			x = new Card('S','K');
			Card tmp = new Card('S','3');
			assertNotNull("Constructor for Card", x);
			assertNotNull("Constructor for Card", tmp);
			assertTrue("Compare",x.compareTo(tmp) > 0);
		}
		catch(Exception ex){
			fail(ex.getMessage());
		}
	}
	
}
