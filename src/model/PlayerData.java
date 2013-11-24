package model;

public class PlayerData implements Comparable<PlayerData> {
	private Hand hand;
	private String name;
	private int balance,  wins;
	private boolean sentWins;
	
	public PlayerData() {
		hand = null;
		name = null;
		balance = wins = 0;
		sentWins = false;
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

	public void setBalance(int balance) {
		this.balance = balance;
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
}
