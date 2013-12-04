import static org.junit.Assert.*;
import models.Card;

import org.junit.Test;


public class CardTest
{
	Card testCard1,testCard2;
	
	@Test
	public void WhenEmptyConstructor_ExpectZeros() {
		testCard1 = new Card();
		assertEquals("Card = 0",(int)0,(int)testCard1.getCard());
		assertEquals("Color = 0",(int)0,(int)testCard1.getColor());
	}
	
	@Test
	public void WhenInitializedWithGoodValues_ExpectValidCard()
	{
		try {
			testCard1 = new Card('D','5');
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("Card = 0",(char)'5',(char)testCard1.getCard());
		assertEquals("Color = 0",(char)'D',(char)testCard1.getColor());
	}
	
	@Test (expected = Exception.class)
	public void WhenInitializedWithBadCard_ExpectException() throws Exception {
		testCard1 = new Card('D','X');
	}
	
	@Test (expected = Exception.class)
	public void WhenInitializedWithBadColor_ExpectException() throws Exception {
		testCard1 = new Card('X','5');
	}
	
	@Test
	public void WhenCardIsInitialized_ExpectValidCardValue()
	{
		Character[] arr = {'2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A'};
		
		for(int i = 0 ; i < arr.length ; i ++)
		{
			try {
				testCard1 = new Card('D',arr[i]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			assertEquals("Card Value = arr[i]",i,testCard1.getCardValue());
		}
	}
	
	@Test
	public void WhenCardIsInitialized_ExpectValidColorValue()
	{
		try {
			testCard1 = new Card('D','5');
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("Color Value (Diamonds) = 1",1,testCard1.getColorValue());
	}
	
	@Test
	public void WhenCardIsInitialized_ExpectValidStringReturn()
	{
		try {
			testCard1 = new Card('D','5');
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("Card String is 'D5'","D5",testCard1.toString());
	}
	
	@Test
	public void WhenHaveTwoCards_ExpectFirstGreaterThanSecond()
	{
		try {
			testCard1 = new Card('D','5');
			testCard2 = new Card('C','2');
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertTrue("First greater than second",testCard1.compareTo(testCard2) > 0);
	}
}
