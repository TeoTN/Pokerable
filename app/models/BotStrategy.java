package models;

import java.util.Random;

public final class BotStrategy
{
	private static Random generator = new Random();
	
	// typeOfReturn = 0 <-> check/call, 1 <-> bet/raise, 2 <-> fold, 3 <-> allin
	private static String returnBet(int typeOfReturn, int money, int currentBalance, int accountBalance)
	{
		if(typeOfReturn == 0)
		{
			if(currentBalance == 0) {
				return "CHECK";
			}
			
			if(currentBalance < accountBalance) {
				return "CALL";
			}
			
			return "ALLIN";
		}
		
		if(typeOfReturn == 1)
		{
			if(currentBalance == 0) {
				return "BET|"+money;
			}
			
			if(currentBalance+money < accountBalance) {
				return "RAISE|"+money;
			}
			
			return "ALLIN";
		}
		
		if(typeOfReturn == 3) {
			return "FOLD";
		}
		
		if(typeOfReturn == 4) {
			return "ALLIN";
		}
		
		return null;
	}
	
	public static String strategy1(int accountBalance, int biddingRoundNumber, int startBalance, int currentBalance, boolean amIBluffing, HandRankBot hrb)
	{
		int best0 = hrb.getBestId();
		int best1 = hrb.getSecondBestId();
		
		int randomVarI;
		double randomVarD;
		
		if(amIBluffing)
		{
			randomVarI = generator.nextInt(2);
			randomVarD = generator.nextDouble();
			
			if(randomVarI == 1){
				return returnBet(0,(int)(randomVarD*0.05*(double)accountBalance),currentBalance,accountBalance);
			}
			return returnBet(0,0,currentBalance,accountBalance);
		}
		
		if(biddingRoundNumber == 1)
		{
			if(best0 <= 7) // Jeżeli para lub lepiej
			{
				randomVarI = generator.nextInt(3);
				
				if(randomVarI == 1) { // Call/Check
					return returnBet(0,0,currentBalance,accountBalance);
				}
				
				randomVarD = generator.nextDouble();
				
				if(currentBalance == 0) {
					return returnBet(1,(int)(randomVarD*0.15*(double)accountBalance),currentBalance,accountBalance);
				}
				
				if(currentBalance > 0.33*(double)accountBalance && (best0 <= 5 || best1 <= 4))
				{
					randomVarI = generator.nextInt(6);
					
					if(randomVarI != 1) {
						return returnBet(1,(int)(randomVarD*0.15*(double)accountBalance),currentBalance,accountBalance);
					}
					
					return returnBet(0,0,currentBalance,accountBalance);
				}
				
				if(currentBalance > 0.33*(double)accountBalance)
				{
					randomVarI = generator.nextInt(10);
					
					if(randomVarI == 4) 
					{
						randomVarI = generator.nextInt(6);
						
						if(randomVarI != 1) {
							return returnBet(1,(int)(randomVarD*0.15*(double)accountBalance),currentBalance,accountBalance)+"F";
						}
						
						return returnBet(0,0,currentBalance,accountBalance)+"F";
					}
					
					return returnBet(2,0,currentBalance,accountBalance);
				}
				
				return returnBet(0,0,currentBalance,accountBalance);
			}
			
			randomVarI = generator.nextInt(2);
			randomVarD = generator.nextDouble();
			
			if(randomVarI == 1){
				return returnBet(1,(int)(randomVarD*0.05*(double)accountBalance),currentBalance,accountBalance);
			}
			return returnBet(0,0,currentBalance,accountBalance);
		}
		
		if(biddingRoundNumber == 2)
		{
			if(best0 <= 3)
			{
				randomVarI = generator.nextInt(2);
				randomVarD = generator.nextDouble();
				
				if(randomVarI == 1){
					return returnBet(1,(int)(randomVarD*0.25*(double)accountBalance),currentBalance,accountBalance);
				}
				return returnBet(0,0,currentBalance,accountBalance);	
			}
			
			double balanceProp = (double)currentBalance/(double)startBalance;
			
			if(best0 <= 5)
			{
				if(balanceProp <= 0.6)
				{
					randomVarI = generator.nextInt(2);
					randomVarD = generator.nextDouble();
					
					if(randomVarI == 1){
						return returnBet(1,(int)(randomVarD*0.25*(double)accountBalance),currentBalance,accountBalance);
					}
					return returnBet(0,0,currentBalance,accountBalance);
				}
				
				randomVarI = generator.nextInt(3);
				
				if(randomVarI == 1)
				{
					if(randomVarI == 1) {
						randomVarD = generator.nextDouble();
						return returnBet(1,(int)(randomVarD*0.25*(double)accountBalance),currentBalance,accountBalance)+"F";
					}
					return returnBet(0,0,currentBalance,accountBalance)+"F";
				}
				
				return returnBet(3,0,currentBalance,accountBalance);
			}
			
			if(best0 == 6)
			{
				if(balanceProp <= 0.3)
				{
					randomVarI = generator.nextInt(2);
					randomVarD = generator.nextDouble();
					
					if(randomVarI == 1){
						return returnBet(1,(int)(randomVarD*0.25*(double)accountBalance),currentBalance,accountBalance);
					}
					return returnBet(0,0,currentBalance,accountBalance);
				}
				
				randomVarI = generator.nextInt(4);
				
				if(randomVarI == 1)
				{
					if(randomVarI == 1) {
						randomVarD = generator.nextDouble();
						return returnBet(1,(int)(randomVarD*0.25*(double)accountBalance),currentBalance,accountBalance)+"F";
					}
					return returnBet(0,0,currentBalance,accountBalance)+"F";
				}
				
				return returnBet(3,0,currentBalance,accountBalance);
			}
			
			if(best0 == 7)
			{
				if(balanceProp <= 0.15)
				{
					randomVarI = generator.nextInt(2);
					randomVarD = generator.nextDouble();
					
					if(randomVarI == 1){
						return returnBet(1,(int)(randomVarD*0.25*(double)accountBalance),currentBalance,accountBalance);
					}
					return returnBet(0,0,currentBalance,accountBalance);
				}
				
				randomVarI = generator.nextInt(5);
				
				if(randomVarI == 1)
				{
					if(randomVarI == 1) {
						randomVarD = generator.nextDouble();
						return returnBet(1,(int)(randomVarD*0.20*(double)accountBalance),currentBalance,accountBalance)+"F";
					}
					return returnBet(0,0,currentBalance,accountBalance)+"F";
				}
				
				return returnBet(3,0,currentBalance,accountBalance);
			}
			
			randomVarI = generator.nextInt(6);
			
			if(randomVarI == 1)
			{
				if(randomVarI == 1) {
					randomVarD = generator.nextDouble();
					return returnBet(1,(int)(randomVarD*0.15*(double)accountBalance),currentBalance,accountBalance)+"F";
				}
				return returnBet(0,0,currentBalance,accountBalance)+"F";
			}
			
			return returnBet(3,0,currentBalance,accountBalance);
		}
		
		
		return null;
	}
	
	public static String strategyBAAD(int accountBalance, int biddingRoundNumber, int startBalance, int currentBalance, boolean amIBluffing, HandRankBot hrb)
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
		
		return "ERROR";
	}
}
