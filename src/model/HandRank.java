package model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class HandRank implements Comparable<HandRank>
{
	protected Hand assessedHand  = null;
	public int[]  missingCards   = new int[9];  // Missing cards for fixed Poker Hand (ex. bestPokerHand[7] = 1 means that we miss 1 card to have One Pair).
	public Card[] bestCard       = new Card[9]; // Best card for fixed Poker Hand (ex. bestCard[8] = 'CA' means that best card for High Card we have is Club Ace)
	public Card[] secondBestCard = new Card[2]; // Two Pairs ([0]) and Full House ([1]) needs extra information about second highest card. 
	
	/**
	 * Indexes: 
	 * 	8 - high card
	 *  7 - one pair
	 *  6 - two pairs
	 *  5 - three of a kind
	 *  4 - straight
	 *  3 - flush
	 *  2 - full house
	 *  1 - four of a kind
	 *  0 - straight flush
	 * @param cards
	 */
	public HandRank(List<Card> cards)
	{
		Collections.sort(cards); // Lowest to highest
		
		for(int i = 0 ; i < 9 ; i ++){
			missingCards[i]         = 0;
			try {
				bestCard[i]         = new Card('C','2');
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				secondBestCard[i%2] = new Card('C','2');
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		checkHighCard(cards);
		checkOnePair(cards);
		checkTwoPair(cards);
		checkThree(cards);
		checkStraight(cards);
		checkFlush(cards);
		checkFull(cards);
		checkFour(cards);
		checkStraightFlush(cards);
	}
	
	public HandRank(Hand h) {
		this(h.getHand());
		assessedHand = h;
	}
	
	public int compareTo(HandRank x) // 1 <-> this wins; 0 <-> tie; -1 <-> x wins
	{
		for(int i = 0 ; i < 9 ; i ++)
		{
			if(this.missingCards[i] == 0 && x.missingCards[i] != 0){
				return 1;
			}
			if(this.missingCards[i] != 0 && x.missingCards[i] == 0){
				return -1;
			}
			if(this.missingCards[i] == 0 && x.missingCards[i] == 0)
			{
				if(this.bestCard[i].getCardValue() > x.bestCard[i].getCardValue()){
					return 1;
				}
				
				if(this.bestCard[i].getCardValue() < x.bestCard[i].getCardValue()){
					return -1;
				}
				
				if(i == 6){
					if(this.secondBestCard[0].getCardValue() > x.secondBestCard[0].getCardValue()){
						return 1;
					}
					
					if(this.secondBestCard[0].getCardValue() < x.secondBestCard[0].getCardValue()){
						return -1;
					}
				}
				
				if(i == 2){
					if(this.secondBestCard[1].getCardValue() > x.secondBestCard[1].getCardValue()){
						return 1;
					}
					
					if(this.secondBestCard[1].getCardValue() < x.secondBestCard[1].getCardValue()){
						return -1;
					}
				}
				return 0;
			}
		}
		
		return 0;
	}


	protected void checkStraightFlush(List<Card> cards)
	{
		int[] anyCard = new int[14];
		for(int i = 0 ; i < 14 ; i ++){
			anyCard[i] = -1;
		}
		
		for(int i = 0 ; i < cards.size() ; i ++){
			int it        = cards.get(i).getCardValue();
			anyCard[it+1] = i;
		}
		anyCard[0] = anyCard[13];
		
		int cnt = 0;
		for(int i = 0 ; i < 14-5 ; i ++)
		{
			int tmp_spades   = 0; int lastCardSpades   = 0;
			int tmp_hearts   = 0; int lastCardHearts   = 0;
			int tmp_diamonds = 0; int lastCardDiamonds = 0;
			int tmp_clubs    = 0; int lastCardClubs    = 0;
			
			for(int j = i ; j < i+5 ; j ++)
			{
				if(anyCard[j] != -1)
				{
					for(int k = 0 ; k < cards.size(); k ++)
					{
						if(cards.get(anyCard[j]).getCardValue() == cards.get(k).getCardValue())
						{
							switch(cards.get(k).getColor()){
							case 'S': tmp_spades   ++; lastCardSpades   = j; break;
							case 'H': tmp_hearts   ++; lastCardHearts   = j; break;
							case 'D': tmp_diamonds ++; lastCardDiamonds = j; break;
							case 'C': tmp_clubs    ++; lastCardClubs    = j; break;
							}
						}
					}
				}
			}
			
			if(Math.max(Math.max(Math.max(tmp_spades, tmp_hearts), tmp_diamonds), tmp_clubs) == tmp_spades   && tmp_spades > cnt){
				missingCards[0] = 5-tmp_spades;
				bestCard[0]     = cards.get(anyCard[lastCardSpades]);
				cnt             = tmp_spades;
			}
			if(Math.max(Math.max(Math.max(tmp_spades, tmp_hearts), tmp_diamonds), tmp_clubs) == tmp_hearts   && tmp_hearts > cnt){
				missingCards[0] = 5-tmp_hearts;
				bestCard[0]     = cards.get(anyCard[lastCardHearts]);
				cnt             = tmp_hearts;
			}
			if(Math.max(Math.max(Math.max(tmp_spades, tmp_hearts), tmp_diamonds), tmp_clubs) == tmp_diamonds && tmp_diamonds > cnt){
				missingCards[0] = 5-tmp_diamonds;
				bestCard[0]     = cards.get(anyCard[lastCardDiamonds]);
				cnt             = tmp_diamonds;
			}
			if(Math.max(Math.max(Math.max(tmp_spades, tmp_hearts), tmp_diamonds), tmp_clubs) == tmp_clubs    && tmp_clubs > cnt){
				missingCards[0] = 5-tmp_clubs;
				bestCard[0]     = cards.get(anyCard[lastCardClubs]);
				cnt             = tmp_clubs;
			}
		}
		
		if(cnt == 0){
			missingCards[0] = 5;
			bestCard[0]     = cards.get(cards.size()-1);
		}
	}

	protected void checkFour(List<Card> cards)
	{
		for(int i = cards.size()-1 ; i >= 0  ; i --)
		{
			for(int j = i-1 ; j >= 0 ; j --)
			{
				for(int k = j-1 ; k >= 0 ; k --)
				{
					for(int l = k-1 ; l >= 0 ; l --)
					{
						if(cards.get(i).compareTo(cards.get(j)) == 0 && cards.get(j).compareTo(cards.get(k)) == 0 && cards.get(k).compareTo(cards.get(l)) == 0){
							missingCards[1] = 0;
							bestCard[1]     = cards.get(i);
							return;
						}
					}
				}
			}
		}
		
		missingCards[1] = missingCards[5]+1;
		bestCard[1]     = bestCard[5];
	}

	protected void checkFull(List<Card> cards)
	{
		if(missingCards[5] == 0 && missingCards[6] == 0){
			missingCards[2]   = 0;
			bestCard[2]       = bestCard[5];
			secondBestCard[1] = (secondBestCard[0] == bestCard[5]) ? bestCard[6] : secondBestCard[0];
			return;
		}
		
		if(missingCards[5] == 0 && missingCards[6] == 1){
			missingCards[2]   = 1;
			bestCard[2]       = bestCard[5];
			secondBestCard[1] = (secondBestCard[0] == bestCard[5]) ? bestCard[6] : secondBestCard[0];
			return;
		}
		
		if(missingCards[5] == 1 && missingCards[6] == 0){
			missingCards[2]   = 1;
			bestCard[2]       = bestCard[6];
			secondBestCard[1] = secondBestCard[0];
			return;
		}
		
		if(missingCards[5] == 1 && missingCards[6] == 1){
			missingCards[2]   = 2;
			bestCard[2]       = bestCard[6];
			secondBestCard[1] = secondBestCard[0];
			return;
		}
		
		missingCards[2]   = 3;
		bestCard[2]       = bestCard[6];
		secondBestCard[1] = secondBestCard[0];
	}

	protected void checkFlush(List<Card> cards)
	{
		List<Integer> Spades   = new ArrayList<Integer>();
		List<Integer> Hearts   = new ArrayList<Integer>();
		List<Integer> Diamonds = new ArrayList<Integer>();
		List<Integer> Clubs    = new ArrayList<Integer>();
		
		for(int i = 0 ; i < cards.size() ; i ++)
		{
			char tmp = cards.get(i).getColor();
			switch(tmp){
				case 'S': Spades.add(i); break;
				case 'H': Hearts.add(i); break;
				case 'D': Diamonds.add(i); break;
				case 'C': Clubs.add(i); break;
			}
		}
		
		int highest = 0;
		if(Spades.size() > highest){
			missingCards[3] = 5-Spades.size();
			bestCard[3]     = cards.get(Spades.get(Spades.size()-1));
			highest         = Spades.size();
		}
		if(Hearts.size() > highest){
			missingCards[3] = 5-Hearts.size();
			bestCard[3]     = cards.get(Hearts.get(Hearts.size()-1));
			highest         = Hearts.size();
		}
		if(Diamonds.size() > highest){
			missingCards[3] = 5-Diamonds.size();
			bestCard[3]     = cards.get(Diamonds.get(Diamonds.size()-1));
			highest         = Diamonds.size();
		}
		if(Clubs.size() > highest){
			missingCards[3] = 5-Clubs.size();
			bestCard[3]     = cards.get(Clubs.get(Clubs.size()-1));
			highest         = Clubs.size();
			
		}
		
		if(highest == 0){
			missingCards[3] = 5;
			bestCard[3]     = cards.get(cards.size()-1);
		}
	}

	protected void checkStraight(List<Card> cards)
	{
		int[] anyCard = new int[14];
		
		for(int i = 0 ; i < 14 ; i ++){
			anyCard[i] = -1;
		}
		
		for(int i = 0 ; i < cards.size() ; i ++){
			int it        = cards.get(i).getCardValue();
			anyCard[it+1] = i;
		}
		anyCard[0] = anyCard[13];
		
		int cnt = 0;
		for(int i = 0 ; i < 14-5 ; i ++)
		{
			int tmp_cnt = 0; int last_card = 0;
			for(int j = i ; j < i+5 ; j ++)
			{
				if(anyCard[j] != -1){
					tmp_cnt ++;
					last_card = j;
				}
			}
			
			if(tmp_cnt >= cnt && tmp_cnt != 0){
				cnt = tmp_cnt;
				missingCards[4] = 5-tmp_cnt;
				bestCard[4]     = cards.get(anyCard[last_card]);
			}
		}
		
		if(cnt == 0){
			missingCards[4] = 5;
			bestCard[4]     = cards.get(cards.size()-1);
		}
	}
	
	protected void checkThree(List<Card> cards)
	{
		for(int i = cards.size()-1 ; i >= 0  ; i --)
		{
			for(int j = i-1 ; j >= 0 ; j --)
			{
				for(int k = j-1 ; k >= 0 ; k --)
				{
					if(cards.get(i).compareTo(cards.get(j)) == 0 && cards.get(j).compareTo(cards.get(k)) == 0){
						missingCards[5] = 0;
						bestCard[5]     = cards.get(i);
						return;
					}
				}
			}
		}
		
		missingCards[5] = missingCards[7]+1;
		bestCard[5] = bestCard[7];
	}

	protected void checkTwoPair(List<Card> cards)
	{
		boolean foundOnePair = false;
		
		for(int i = cards.size()-1 ; i >= 0  ; i --)
		{
			for(int j = i-1 ; j >= 0 ; j --)
			{
				if(cards.get(i).compareTo(cards.get(j)) == 0)
				{
					if(!foundOnePair){
						foundOnePair    = true;
						missingCards[6] = 1;
						bestCard[6]     = cards.get(i);
						break;
					}
					else if(bestCard[6].compareTo(cards.get(j)) != 0){
						missingCards[6]   = 0;
						secondBestCard[0] = cards.get(j);
						return;
					}
				}
			}
		}
		
		if(foundOnePair){
			secondBestCard[0] = cards.get(cards.size()-1);
		}
		else{
			missingCards[6]   = 2;
			bestCard[6]       = cards.get(cards.size()-1);
			secondBestCard[0] = cards.get(cards.size()-2);
		}
	}

	protected void checkOnePair(List<Card> cards)
	{
		for(int i = cards.size()-1 ; i >= 0  ; i --)
		{
			for(int j = i-1 ; j >= 0 ; j --)
			{
				if(cards.get(i).compareTo(cards.get(j)) == 0){
					missingCards[7] = 0;
					bestCard[7]     = cards.get(i);
					return;
				}
			}
		}
		
		missingCards[7]  = 1;
		bestCard[7]      = cards.get(cards.size()-1);
	}

	protected void checkHighCard(List<Card> cards) {
		missingCards[8]  = 0;
		bestCard[8]      = cards.get(cards.size()-1);
	}
	
	public Hand getAssessedHand() {
		assessedHand.sort();
		return assessedHand;
	}
}
