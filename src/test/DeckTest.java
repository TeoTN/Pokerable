package test;
import model.*;
import static org.junit.Assert.*;

import org.junit.Test;


/**
 * @author Micha³ Kie³bowicz
 * @author Piotr Staniów
 */
public class DeckTest
{
	Deck testing;
	
	@Test
	public void test()
	{
		testing = Deck.getInstance();
		
		int cnt = 0;
		while(!testing.empty())
		{
			cnt ++;
			try{
				System.out.print(testing.pullCard().toString()+" ");
			}
			catch(Exception ex){
				fail(ex.getMessage());
			}
		}
		System.out.println("");
		assertTrue("Good amount of cards",cnt == 52);
	}
	
	@Test
	public void pushpull() throws Exception
	{
		testing = Deck.getInstance();
		testing.pullCard();
		testing.pushCard("D5");
	}
}
