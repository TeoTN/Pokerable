package model;

import java.util.Random;

public final class BotStrategy
{
	private static Random generator = new Random();
	
	public static String strategy1(int accountBalance, int biddingRoundNumber, int startBalance, int currentBalance, boolean amIBluffing, HandRankBot hrb)
	{
		if(biddingRoundNumber == 1)
		{
			if(accountBalance-currentBalance > 0)
			{
				if(currentBalance == 0){
					return "CHECK";
				}
				
				if((double)currentBalance/(double)startBalance <= 0.07){
					return "CALL";
				}
				
				if((double)currentBalance/(double)startBalance <= 0.22 && hrb.getBestId() < 8){
					return "CALL";
				}
				
				if((double)currentBalance/(double)startBalance <= 0.47 && hrb.getBestId() < 7){
					return "CALL";
				}
				
				if(hrb.getBestId() < 6)
				{
					int callOrRaise = generator.nextInt(2);
					
					if(callOrRaise == 0){
						return "CALL";
					}
					else{
						int raiseValue = (int)(generator.nextDouble()*0.2*(double)(accountBalance));
						return ("RAISE|" + raiseValue);
					}
					
				}
				
				return("FOLD");
			}
			else{
				return("ALLIN"); 
			}
		}
		
		if(biddingRoundNumber == 2)
		{
			if(amIBluffing)
			{
				int callOrRaise = generator.nextInt(3);
				
				if(accountBalance-currentBalance <= 0){
					return("ALLIN");
				}
				
				if(callOrRaise == 1){
					return("CALL");
				}
				else
				{
					int raiseValue = (int)(generator.nextDouble()*Math.max(generator.nextDouble(),0.3)*(double)(accountBalance));
					
					if(currentBalance == 0) {
						return("BET|" + raiseValue);
					}
					else{
						return("RAISE" + raiseValue);
					}
				}
				
			}
			
			if(hrb.getBestId() == 8)
			{
				int bluff = generator.nextInt(25);
				
				if(bluff == 13){
					amIBluffing = true;
					return strategy1(accountBalance, biddingRoundNumber, startBalance, currentBalance, false, hrb)+"F";
				}
				
				if(currentBalance == 0){
					return("CHECK");
				}
				
				return("FOLD");
				
			}
			
			if(hrb.getBestId() == 7)
			{
				int bluff = generator.nextInt(20);
				
				if(bluff == 13){
					amIBluffing = true;
					return strategy1(accountBalance, biddingRoundNumber, startBalance, currentBalance, false, hrb)+"F";
				}
				
				if(currentBalance == 0){
					return("CHECK");
				}
				
				return("FOLD");
			}
			
			if(hrb.getBestId() == 6)
			{
				int bluff = generator.nextInt(17);
				
				if(bluff == 13){
					amIBluffing = true;
					return strategy1(accountBalance, biddingRoundNumber, startBalance, currentBalance, false, hrb)+"F";
				}
				
				if(currentBalance == 0){
					return("CHECK");
				}
				
				return("FOLD");
			}
			
			if(hrb.getBestId() == 5 || hrb.getBestId() == 4)
			{
				if(hrb.bestCard[5].getCardValue() <= 8)
				{
					int bluff = generator.nextInt(15-hrb.bestCard[5].getCardValue());
					
					if(bluff == 1){
						amIBluffing = true;
						return strategy1(accountBalance, biddingRoundNumber, startBalance, currentBalance, false, hrb)+"F";
					}
					
					if(currentBalance == 0){
						return("CHECK");
					}
					
					return("FOLD");
				}
				
				int callOrRaise = generator.nextInt(3);
				
				if(callOrRaise == 0) {
					return (currentBalance == 0) ? "CHECK" : "CALL";
				}
				
				int raiseValue = (int)(Math.max(generator.nextDouble(),0.15)*(double)(accountBalance));
				
				if(currentBalance == 0){
					return ("BET|" + raiseValue);
				}
				return ("RAISE|" + raiseValue);
			}
			
			int callOrRaise = generator.nextInt(5);
			
			if(callOrRaise == 0) {
				return (currentBalance == 0) ? "CHECK" : "CALL";
			}
			
			int raiseValue = (int)(Math.min(generator.nextDouble(),0.2)*(double)(accountBalance));
			
			if(currentBalance == 0){
				return ("BET|" + raiseValue);
			}
			return ("RAISE|" + raiseValue);
		}
		
		return "";
	}
}
