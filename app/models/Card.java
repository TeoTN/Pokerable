package models;
public class Card implements Comparable<Card>
{
	private char color; // H - hearts, D - diamonds, S - spades, C - clubs
	private char card;  // 2, 3, 4, 5, 6, 7, 8, 9, T, J, Q, K, A
	
	public static Character[] CardList = {'2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A'};
	
	/**
	 * Empty constructor
	 */
	public Card(){
		color = 0;
		card = 0;
	}
	
	/**
	 * Constructor taking char args.
	 * @param color Card color
	 * @param card  Card number
	 */
	public Card(char color, char card) throws Exception
	{
		color = Character.toUpperCase(color);
		card  = Character.toUpperCase(card);
		
		if(color != 'H' && color != 'D' && color != 'S' && color != 'C'){
			throw new Exception("Bad color");
		}
		this.color = color;
		
		if((card < '2' || card > '9') && card != 'T' && card != 'J' && card != 'Q' && card != 'K' && card != 'A'){
			throw new Exception("Bad card");
		}
		this.card = card;
	}
	

	
	/**
	 * @return card ID (Like 'K' for King);
	 */
	public Character getCard(){
		return card;
	}
	
	/**
	 * Returns number corresponding to the card's number
	 * @return Number of a card 0 when it's 2, 1 when 3, etc.
	 */
	public int getCardValue()
	{
		for (int i = 0 ; i < 13 ; i ++) {
			if (card == CardList[i])
				return i;
		}
		return -1;
	}
	
	/**
	 * Returns suit of the card
	 * @return Character corresponding to the card's suit
	 */
	public Character getColor() {
		return color;
	}
	
	/**
	 * Return color value according to color priority.
	 * @return color value. Highest is oldest.
	 */
	public int getColorValue()
	{
		switch(color){
		case 'S': return 3;
		case 'H': return 2;
		case 'D': return 1;
		case 'C': return 0;
		}
		return -1;
	}
	
	/**
	 * Function telling us what this class has.
	 * @return Card value
	 */
	public String toString(){
		return ""+color+card;
	}
	
	/**
	 * Comparison between cards' values. WE DON'T NEED COLOR!!
	 * @return positive value if 'this' is greater
	 */
	@Override
	public int compareTo(Card c)
	{
		/*if(c.getCardValue() == this.getCardValue()){
			return this.getColorValue() - c.getColorValue();
		}*/
		return this.getCardValue() - c.getCardValue();
	}
}
