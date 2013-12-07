package models;
public class Human extends Player
{
	public Human(String host, int port) {
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
		String[] toChange;
    	Printer.print("This is your hand: "+getHandToString());
    	String toChangeStr = Printer.prompt("Get desc of cards you'd like to change (e.g. S4|D4, up to 4 cards)");
    	toChange = toChangeStr.split("\\|");
    	if (toChange.length > 4) {
    		Printer.print("Please, notice that you may change only up to 4 cards. Please, try again.");
    		promptChange();
    		return;
    	}
    	
    	String msg = getHandToString()+"|"; //CHANGE|...|D4|D5
    	
    	for (int i=0; i<toChange.length; i++) {
    		String c = toChange[i];
    		if (c.length()!=2) {
    			Printer.print(c+" is not correct format of card.");
    			promptChange();
    			return;
    		}
    		if (!handIncludes(c)) {
    			Printer.print("You don't have such card. Are you trying to cheat?");
    			promptChange();
    			return;
    		}
    		msg+=c;
    		if (i!=toChange.length-1) msg+="|";
    	}
    	msgr.broadcast("CHANGE|"+msg); 
    	Printer.print("Wait for other players to change their hands.");
	}
	
	public void setMoneyAtBeginning() {}
	
	/**
	 * Documentation and usage available in Player class
	 * @see Player
	 */
	@Override
	public void promptBet(String data) {
		String params = Printer.prompt("RAW BET PROMPT: ");
		msgr.broadcast("SETBET|"+params);
	}
	@Override
	public String promptName() {
		String name = "Player";
		name = Printer.prompt("Enter your name:");
		return name;
	}
}
