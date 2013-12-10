package models;

public class PlayerData implements Comparable<PlayerData> {
	private Hand hand;
	private String name;
	private int balance,  wins, previousBet;
	private boolean sentWins;
	private boolean inGame, allIn;
	private boolean isBetting;
	
	public PlayerData() {
		hand = null;
		name = null;
		previousBet = balance = wins = 0;
		sentWins = false;
		inGame = true;
		allIn = false;
		isBetting = true;
	}
	
	public void reset() {
		hand = null;
		previousBet = 0;
		sentWins = false;
		allIn = false;
		isBetting = true;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getBalance() {
		return balance;
	}
	
	public void setIsBetting() {
		isBetting = true;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}
	
	public void addBalance(int balance) {
		this.balance += balance;
	}

	public Hand getHand() {
		return hand;
	}

	public String getHandToString() {
		return hand.toString();
	}
	public void setHand(Hand hand) {
		this.hand = hand;
	}

	public Integer getWins() {
		return wins;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}
	
	public void setSentWins() {
		sentWins = true;
	}

	public int compareTo(PlayerData d) {
		if (getWins() > d.getWins())
			return 1;
		else if (getWins() == d.getWins())
			return 0;
		else return -1;
	}

	public boolean isSentWins() {
		return sentWins;
	}

	public boolean isInGame() {
		return inGame;
	}

	public void setInGame(boolean inGame) {
		this.inGame = inGame;
	}

	public int getPreviousBet() {
		return previousBet;
	}
	
	public boolean getIsBetting() {
		return isBetting;
	}

	public void setPreviousBet(int previousBet) {
		this.previousBet = previousBet;
	}

	public boolean isAllIn() {
		return allIn;
	}

	public void setAllIn(boolean wasAllIn) {
		this.allIn = wasAllIn;
	}
	
	public void setIsBetting(boolean isBetting) {
		this.isBetting = isBetting;
	}
}
