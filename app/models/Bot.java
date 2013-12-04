package models;
import java.util.List;

public class Bot extends Player
{
	private HandRankBot hrb;
	private List<Card> toChange;
	private int moneyAtBeginning = 0;

	public Bot(String host, int port) {
		super(host, port);
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
		Printer.print("I will change "+toChange.size() + " cards.");
		String msg = getHandToString();
		if (toChange.size()>=0 && toChange.size() <=4){
			for (Card c: toChange) {
				msg+="|"+c.toString();
			}
			if (toChange.size()>0)
				Printer.print(msg.substring(15, msg.length()));
		}
		msgr.broadcast("CHANGE|"+msg); 
	}

	/**
	 * Documentation and usage available in Player class
	 * @see Player
	 * @param String
	 *		Nr licytacji | Początkowy stan konta (z początku rundy) | Aktualna stawka
	 */
	@Override
	public void promptBet(String data) 
	{
		String[] arr = data.split("\\|");
		int a = Integer.parseInt(arr[0]);
		int b = Integer.parseInt(arr[1]);
		String out = BotStrategy.strategy1(accountBalance, a, getMoneyAtBeginning(), b, false, hrb);
		msgr.broadcast("SETBET|" + out);
	}

	@Override
	public String promptName() {
		return "Bot";
	}

	public int getMoneyAtBeginning() {
		return moneyAtBeginning;
	}

	public void setMoneyAtBeginning() {
		this.moneyAtBeginning = account;
	}
}
