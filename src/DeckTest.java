import static org.junit.Assert.*;

import org.junit.Test;


/**
 * @author Micha� Kie�bowicz
 * @author Piotr Stani�w
 */
public class DeckTest
{
	Deck testing;
	
	@Test
	public void test()
	{
		testing = new Deck();
		
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
		testing = new Deck();
		testing.pullCard();
		testing.pushCard("D5");
	}
}