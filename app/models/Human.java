package models;
public class Human extends Player {
	private Printer printer;
	public Human(String host, int port) {
		super(host, port);
		printer = super.getPrinter();
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
    	printer.print("This is your hand: "+getHandToString());
    	String toChangeStr = printer.prompt("Get desc of cards you'd like to change (e.g. S4|D4, up to 4 cards)");
    	toChange = toChangeStr.split("\\|");
    	if (toChange.length > 4) {
    		printer.print("Please, notice that you may change only up to 4 cards. Please, try again.");
    		promptChange();
    		return;
    	}
    	
    	String msg = getHandToString()+"|"; //CHANGE|...|D4|D5
    	
    	for (int i=0; i<toChange.length; i++) {
    		String c = toChange[i];
    		if (c.length()!=2) {
    			printer.print(c+" is not correct format of card.");
    			promptChange();
    			return;
    		}
    		if (!handIncludes(c)) {
    			printer.print("You don't have such card. Are you trying to cheat?");
    			promptChange();
    			return;
    		}
    		msg+=c;
    		if (i!=toChange.length-1) msg+="|";
    	}
    	msgr.broadcast("CHANGE|"+msg); 
    	printer.print("Wait for other players to change their hands.");
	}
	
	public void setMoneyAtBeginning() {}
	
	/**
	 * Documentation and usage available in Player class
	 * @see Player
	 */
	@Override
	public void promptBet(String data) {
		String params = printer.prompt("RAW BET PROMPT: ");
		if (!params.equals(""))
			msgr.broadcast("SETBET|"+params);
	}
	@Override
	public String promptName() {
		String name = "Player";
		name = printer.prompt("Enter your name:");
		return name;
	}
}
