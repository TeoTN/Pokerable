package model;
import java.util.*;

/**
 * Class holding deck of cards.
 * Following implementation was made with usage of Singleton Pattern.
 * @author Piotr Staniów
 */
public class Deck
{
	/**
	 * Array of cards in the deck
	 */
	private static Deck instance;
	private List<Card> cards;
	private Character[] CardList = {'2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A'};
	
	
	/**
	 * Constructor of class Deck. Loads ArrayList cards.
	 */
	private Deck()
	{
		cards = new ArrayList<Card>();
		
		for(int i = 0 ; i < 4 ; i ++)
		{
			char currentColor = 0;
			
			switch(i){
				case 0: currentColor = 'H'; break;
				case 1: currentColor = 'D'; break;
				case 2: currentColor = 'S'; break;
				case 3: currentColor = 'C'; break;
			}
			
			for(int j = 0 ; j < 13 ; j ++)
			{
				try{
					Card tmp = new Card(currentColor,CardList[j]);
					cards.add(tmp);
				}
				catch(Exception ex){
					System.out.println(ex);
				}
				
				Collections.shuffle(cards);
			}
			
			Collections.shuffle(cards);
		}
		
		for(int i = 0 ; i < 10 ; i ++){
			Collections.shuffle(cards);
		}
	}
	
	/**
	 * Getter of instance of Deck class.
	 * Instance of Deck is being created unless it exists,
	 * in other case reference to existing instance is returned.  
	 */
	public static synchronized Deck getInstance() {
		if (instance == null)
			instance = new Deck();
		return instance;
	}
	
	/**
	 * Pulls a card from the top of deck
	 * @return top card
	 */
	public Card pullCard() throws Exception
	{
		Card c = cards.remove(0);
		
		if (c == null){
			throw new Exception("Deck is empty");
		}
		
		return c;
	}
	
	public void pushCard(String s) throws Exception
	{
		Card c = null;
		try {
			c = new Card(s.charAt(0), s.charAt(1));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(cards.size() < 52){
			cards.add(c);
		}
		else{
			throw new Exception("Too many cards");
		}
	}
	
	public String pullCardToString() {
		String ss = "";
		try {
			ss = pullCard().toString();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return ss;
	}
	
	/**
	 * Checks if deck is empty.
	 */
	public boolean empty(){
		return cards.isEmpty();
	}
	
	/**
	 * Method shuffles cards in deck with equal probability of each permutation occurrence (as specified in documentation).
	 * @see http://docs.oracle.com/javase/7/docs/api/java/util/Collections.html#shuffle(java.util.List)
	 */
	public void shuffle() {
		Collections.shuffle(cards);
	}
}
