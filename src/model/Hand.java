package model;
import java.util.*;

public class Hand {
	private List<Card> cards;
	
	/**
	 * Constructs Hand from string
	 * @param s String representing Hand to be created, e.g. "C9|D9|S9|H9|HA"
	 * @throws Exception When string includes incorrect parameter
	 */
	public Hand(String s) throws Exception
	{
		cards = new ArrayList<Card>(5);
		String[] arr = s.split("\\|");
		Card addedCard = null;
		
		for (String cardString: arr)
		{
			try {
				addedCard = new Card(cardString.charAt(0), cardString.charAt(1));
			}
			catch(Exception ex) {
				throw new Exception("Failed to create card from string: "+cardString);
			}
			cards.add(addedCard);
		}
	}
	
	/**
	 * Function giving poker hand.
	 * @return Sorted list of cards in our hand.
	 */
	public List<Card> getHand(){
		sort();
		return cards;
	}
	
	/**
	 * Converts cards on hand to suitable String equivalent.
	 * @return String equivalent of Hand, e.g. "D5|SJ|D9|C9|H3" stands for: Diamond 5, Spade J, Diamond 9, Club 9, Heart 3
	 */
	public String toString() {
		String out="";
		for (Card c: cards) {
			out+=c.getColor()+""+c.getCard()+"|";
		}
		return out.substring(0, out.length()-1);
	}
	
	public boolean includes(String s)
	{
		Card c = null;
		try {
			c = new Card(s.charAt(0), s.charAt(1));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		for (Card v: cards)
			if (v.toString().equals(c.toString()))
				return true;
		return false;
	}
	
	/**
	 * Sorts Hand by its value
	 */
	public void sort() {
		Collections.sort(cards);
	}
}
