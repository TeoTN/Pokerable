package model;
public class Human extends Player {
	Human(String host, int port) {
		super(host, port);
	}
	public void run() {
		connect();
	}
	
	/**
	 * Documentation and usage available in Player class
	 * @see Player
	 */
	public void promptChange()
	{
		int a;
    	System.out.println("How many cards do you wish to change? (0-4)");
    	String str = input.next();
    	try {
    		a = Integer.parseInt(str);
    	}
    	catch (NumberFormatException ex) {
    		System.err.println("This was not amount of cards to change.");
    		promptChange();
    		return;
    	}
    	if (a >= 0 && a < 5) {
    		if (a!=0) {
		    	System.out.println("This is your hand: "+getHandToString());
		    	System.out.println("Get desc of cards you'd like to change (e.g. S4 D4)");
    		}
	    	String toChange=getHandToString()+"|"; //CHANGE|...|D4|D5
	    	for (int i=0; i<a; i++) {
	    		String c = input.next();
	    		if (c.length()!=2) {
	    			System.out.println(c+" is not correct format of card.");
	    			promptChange();
	    			return;
	    		}
	    		if (!handIncludes(c)) {
	    			System.out.println("You don't have such card. Are you trying to cheat?");
	    			promptChange();
	    			return;
	    		}
	    		toChange+=c;
	    		if (i!=a-1) toChange+="|";
	    	}
	    	msgr.broadcast("CHANGE|"+toChange); 
	    	System.out.println("Wait for other players to change their hands.");
    	}
    	else {
    		System.out.println("Please, notice that you may change only up to 4 cards.");
    		promptChange();
    		return;
    	}
	}
	
	public void setMoneyAtBeginning() {}
	
	/**
	 * Documentation and usage available in Player class
	 * @see Player
	 */
	@Override
	public void promptBet(String data) {
		System.out.println("RAW BET PROMPT: ");
		String params = input.nextLine();
		msgr.broadcast("SETBET|"+params);
	}
	@Override
	public String promptName() {
		System.out.println("Enter your name:");
		String name = "Player";
		name = input.nextLine();
		return name;
	}
}
