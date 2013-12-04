import static org.junit.Assert.*;
import models.Deck;

import org.junit.Before;
import org.junit.Test;


public class DeckTest
{
	Deck testDeck1, testDeck2;
	
	@Before
	public void SetDecksToNull(){
		testDeck1 = null;
		testDeck2 = null;
	}
	
	@Test
	public void WhenDeckIsNull_ExpectNewInstance() {
		assertSame("Deck = null",testDeck1,null);
		testDeck1 = Deck.getInstance();
		assertNotSame("Deck != null",testDeck1,null);
	}
	
	@Test
	public void WhenTwoDecks_ExpectSameReference() {
		assertSame("Deck = null",testDeck1,null);
		assertSame("Deck = null",testDeck2,null);
		testDeck1 = Deck.getInstance();
		testDeck2 = Deck.getInstance();
		assertSame("Deck1 = Deck2",testDeck1,testDeck2);
	}
	
	@Test (expected = Exception.class)
	public void WhenDeckInitialized_ExpectValidCardAmount() throws Exception
	{
		testDeck1 = Deck.getInstance();
		
		for(int i = 0 ; i < 52 ; i ++) {
			testDeck1.pullCard();
		}
		
		assertTrue("Deck is empty",testDeck1.empty());
		
		testDeck1.pullCard();
	}
}
