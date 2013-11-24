package model;
import java.util.ArrayList;
import java.util.List;


public final class HandRankBot extends HandRank
{
	public HandRankBot(String x) throws Exception {
		super(new Hand(x));
	}
	
	public HandRankBot(List<Card> cards){
		super(cards);
	}
	
	public HandRankBot(Hand x){
		super(x);
	}
	
	public List<Card> getChangeList(List<Card> cards)
	{
		for(int i = 0 ; i < 9 ; i ++)
		{
			if(missingCards[i] == 1 || missingCards[i] == 0)
			{
				switch(i){
				case 0: return getStraightFlush(cards);
				case 1: return getFour(cards);
				case 2: return getFullHouse(cards);
				case 3: return getFlush(cards);
				case 4: return getStraight(cards);
				case 5: return getThree(cards);
				case 6: return getTwoPair(cards);
				case 7: return getOnePair(cards);
				case 8: return getHighCard(cards);
				}
			}
		}
		
		return null;
	}
	
	public List<Card> getChangeList(Hand x){
		return getChangeList(x.getHand());
	}
	
	public List<Card> getChangeList(String x){
		List<Card> ret = null;
		try {
			ret = getChangeList(new Hand(x).getHand());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	private List<Card> getHighCard(List<Card> cards)
	{
		List<Card> ret = new ArrayList<Card>();
		
		for(int i = 0 ; i < cards.size() ; i ++)
		{
			if(cards.get(i).getCardValue() != bestCard[8].getCardValue()){
				ret.add(cards.get(i));
			}
		}
		
		return ret;
	}

	private List<Card> getOnePair(List<Card> cards)
	{
		List<Card> ret = new ArrayList<Card>();
		
		for(int i = 0 ; i < cards.size() ; i ++)
		{
			if(cards.get(i).getCardValue() != bestCard[7].getCardValue()){
				ret.add(cards.get(i));
			}
		}
		
		return ret;
	}

	private List<Card> getTwoPair(List<Card> cards)
	{
		List<Card> ret = new ArrayList<Card>();
		
		for(int i = 0 ; i < cards.size() ; i ++)
		{
			if(cards.get(i).getCardValue() != bestCard[6].getCardValue() && cards.get(i).getCardValue() != secondBestCard[0].getCardValue()){
				ret.add(cards.get(i));
			}
		}
		
		return ret;
	}

	private List<Card> getThree(List<Card> cards)
	{
		List<Card> ret = new ArrayList<Card>();
		
		for(int i = 0 ; i < cards.size() ; i ++)
		{
			if(cards.get(i).getCardValue() != bestCard[5].getCardValue()){
				ret.add(cards.get(i));
			}
		}
		
		return ret;
	}

	private List<Card> getStraight(List<Card> cards)
	{
		List<Card> ret = new ArrayList<Card>();
		
		int minValue = 13;
		for(int i = 0 ; i < cards.size() ; i ++)
		{
			if(cards.get(i).getCardValue() > bestCard[4].getCardValue()-5 && cards.get(i).getCardValue() <= bestCard[4].getCardValue()){
				minValue = Math.min(minValue, cards.get(i).getCardValue());
			}
			if(bestCard[4].getCardValue() < 4 && cards.get(i).getCardValue() == 12){
				minValue = -1;
			}
		}
		
		for(int i = 0 ; i < cards.size(); i ++)
		{
			if(!(cards.get(i).getCardValue() >= minValue && cards.get(i).getCardValue() < minValue+5)){
				ret.add(cards.get(i));
			}
		}
		
		return ret;
	}

	private List<Card> getFlush(List<Card> cards)
	{
		List<Card> ret = new ArrayList<Card>();
		
		for(int i = 0 ; i < cards.size() ; i ++)
		{
			if(cards.get(i).getColorValue() != bestCard[3].getColorValue()){
				ret.add(cards.get(i));
			}
		}
		
		return ret;
	}

	private List<Card> getFullHouse(List<Card> cards)
	{
		List<Card> ret = new ArrayList<Card>();
		
		for(int i = 0 ; i < cards.size() ; i ++)
		{
			if(cards.get(i).getCardValue() != bestCard[2].getCardValue() && cards.get(i).getCardValue() != secondBestCard[1].getCardValue()){
				ret.add(cards.get(i));
			}
		}
		
		return ret;
	}

	private List<Card> getFour(List<Card> cards)
	{
		List<Card> ret = new ArrayList<Card>();
		
		for(int i = 0 ; i < cards.size() ; i ++)
		{
			if(cards.get(i).getCardValue() != bestCard[1].getCardValue()){
				ret.add(cards.get(i));
			}
		}
		
		return ret;
	}

	private List<Card> getStraightFlush(List<Card> cards)
	{
		List<Card> ret = new ArrayList<Card>();
		
		int minValue = 13;
		for(int i = 0 ; i < cards.size() ; i ++)
		{
			if(cards.get(i).getCardValue() > bestCard[0].getCardValue()-5 && cards.get(i).getCardValue() <= bestCard[0].getCardValue() && cards.get(i).getColorValue() == bestCard[0].getColorValue()){
				minValue = Math.min(minValue, cards.get(i).getCardValue());
			}
			if(bestCard[0].getCardValue() < 4 && cards.get(i).getCardValue() == 12 && bestCard[0].getColorValue() == cards.get(i).getColorValue()){
				minValue = -1;
			}
		}
		
		for(int i = 0 ; i < cards.size(); i ++)
		{
			if(!(cards.get(i).getCardValue() >= minValue && cards.get(i).getCardValue() < minValue+5 && cards.get(i).getColorValue() == bestCard[0].getColorValue())){
				ret.add(cards.get(i));
			}
		}
		
		return ret;
	}
	
}
