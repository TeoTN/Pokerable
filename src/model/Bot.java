package model;
import java.util.List;

public class Bot extends Player
{
	private HandRankBot hrb;
	private List<Card> toChange;

	Bot(String host, int port) {
		super(host, port);
	}
	
	public void run() {
		connect();
	}

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
		String msg = getHandToString(); //"HA|SA|CA|DA|HK";
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
	 */
	@Override
	public void promptBet() {
		// TODO Auto-generated method stub
		
	}
}
