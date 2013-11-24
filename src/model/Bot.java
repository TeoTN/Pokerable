package model;
import java.util.List;

public class Bot extends Player
{
	private HandRankBot hrb;
	private List<Card> toChange;
	private boolean cheating;

	Bot(String host, int port) {
		super(host, port);
		cheating = false;
	}
	
	public void run() {
		connect();
	}

	/**
	 * Documentation and usage available in Player class
	 * @see Player
	 */
	@Override
	public void promptChange()
	{
		try {
			hrb = new HandRankBot(getHandToString());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		toChange = hrb.getChangeList(getHandToString());
		System.out.println("I will change "+toChange.size() + " cards.");
		String msg = getHandToString();
		if (toChange.size()>=0 && toChange.size() <=4){
			for (Card c: toChange) {
				msg+="|"+c.toString();
			}
			if (toChange.size()>0)
				System.out.println(msg.substring(15, msg.length()));
		}
		msgr.broadcast("CHANGE|"+msg); 
	}

	/**
	 * Documentation and usage available in Player class
	 * @see Player
	 * @param String
	 *		Nr licytacji | Pocz�tkowy stan konta (z pocz�tku rundy) | Aktualna stawka
	 */
	@Override
	public void promptBet() 
	{
		String out = BotStrategy.strategy1(accountBalance, 0, 0, 0, cheating, hrb);
		
		if(out.charAt(out.length()-1) == 'F'){
			cheating = true;
			out = out.substring(0, out.length()-1);
		}
		
		msgr.broadcast("BET|" + out);
	}

	@Override
	public String promptName() {
		return "Bot";
	}
}
