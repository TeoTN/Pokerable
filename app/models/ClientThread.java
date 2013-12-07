package models;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import controllers.Messenger;

/**
 * Class responsible for Client maintenance.
 *
 */
public class ClientThread extends Thread {
	private Socket socket;
	private Messenger msgr;
	
	int id;
    BufferedReader in = null;
    PrintWriter out = null;
    String msg = "";
    
	private Deck deck;
	private String myName;
	static int allowedThread=0;
	static Object lock = null;
	static int highestBet = 0, previousBet = 0, myPreviousBet = 0;
    
    /**
     * Constructor for instantiation
     */
    ClientThread() {}
    
    /**
     * Typical constructor.
     * @param id Client's id
     */
	ClientThread(int id) {
		this.id = id;
		deck = Deck.getInstance();
		//A lock for further synchronization of clients' threads is created
		if (lock == null) {
			lock = new Object();
		}
	}
	
	/**
	 * Input and output stream are being established and then thread starts to receive messages from thread.
	 */
	@Override
	public void run() {
		try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } 
        catch (IOException e) {
            System.out.println("#"+id+": Unable to reach client.");
        }
		msgr = new Messenger(this); //MUST BE CREATED AFTER INITIALIZING I/O
		msgr.receive();			
	}
	
	public void bindSocket(Socket newSocket) {
		socket = newSocket;
	}
	
	public BufferedReader getInputStream() {
		return in;
	}
	
	public PrintWriter getOutputStream() {
		return out;
	}
	
	public void changeHand(String msg) {
		String localStr = Server.getHandOfId(id);
		if (!msg.startsWith(localStr)) {
			msgr.broadcast("ERROR|CHEAT");
			System.out.println("Cheater (player #"+id+") kicked.");
			finalize();
		}
		
    	String arr[] = msg.split("\\|");
    	if (arr.length <= 5){
    		System.out.println("Player #"+id+" don't want to change hand.");
    		System.err.println("DBG: "+msg);
    	}
    	else {
    		int changes=0;
    		
    		for (int i=0; i<5; i++) {
    			for (int j=5; j<arr.length; j++) {
    				if (arr[i].equals(arr[j])) {
						changes++;
    					try {
							deck.pushCard(arr[i]);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						arr[i] = deck.pullCardToString();
					}
    			}
    		}
    		
    		if (changes < (arr.length-5)) {
    			String cmd = new String("ERROR|There was a number of incorrect cards to be changed."); 
    			msgr.broadcast(cmd);
    		}
    		
    		System.out.println("Player #"+id+" decided to change "+(arr.length-5)+" cards.");
    		String newHand = "SETHAND";
        	for (int i=0; i<5; i++)
        		newHand +="|"+arr[i];
        	msgr.broadcast(newHand);
    	}
    	moveLockToNextPlayer();
    	Server.changedHands++;
	}
	
	/**
	 * Expected arguments params specifies the type of bet and amount of money involved in it.
	 * Whenever CALL or FOLD is called (and similar), amount is ignored
	 * 
	 * Allowed thread is changed in order to prevent dead lock
	 * 
	 * @param param Data describing bet [type]|[amount]
	 */
	public void bet(String param)
	{
		String[] params = param.split("\\|");
		
		PlayerData pd;
		pd = Server.getPlayerData(id);
		
		int currentBalance = pd.getBalance(); 
		boolean isDone = false;
		int bet = 0;
		
		switch (params[0])
		{
			case "CHECK":
				if (highestBet != 0) {
					msgr.broadcast("ERROR|BET|Unable to check when previous bet is not zero");
				}
				else {
					System.out.println("Player "+myName+" is checking.");
					isDone = true;
				}
				break;
			case "BET":
				bet = Integer.parseInt(params[1]);
				if (currentBalance >= bet && highestBet == 0) {
					highestBet = bet;
					myPreviousBet = previousBet = bet;
					currentBalance -= bet;
					Server.incPot(bet);
					isDone = true;
					System.out.println("Player "+myName+" is betting ("+bet+")");
				}
				else if (currentBalance < bet) {
					msgr.broadcast("ERROR|BET|You have insufficient funds for such a bet.");
					System.out.println("Player "+myName+" couldn't bet.");
				}
				else {
					msgr.broadcast("ERROR|BET|You may not bet when bet is already done.");
					System.out.println("Player "+myName+" couldn't bet after betting");
				}
				break;
			case "RAISE":
				bet = Integer.parseInt(params[1]);
				if (currentBalance >= (highestBet - myPreviousBet + bet)) {
					Server.incPot(bet+highestBet - myPreviousBet);
					myPreviousBet = previousBet = highestBet + bet;
					highestBet += bet;
					currentBalance -= (highestBet - previousBet + bet);
					isDone = true;
					System.out.println("Player "+myName+" is raising ("+bet+")");
				}
				else {
					msgr.broadcast("ERROR|BET|You have insufficient funds to raise highest bet.");
				}
				break;
			case "CALL":
				if (currentBalance >= highestBet - myPreviousBet) {
					Server.incPot(highestBet - myPreviousBet);
					myPreviousBet = previousBet = highestBet;
					currentBalance -= highestBet - previousBet;
					isDone = true;
					System.out.println("Player "+myName+" is calling");
				}
				else {
					msgr.broadcast("ERROR|BET|You have insufficient funds to match previous bet.");
				}
				break;
			case "FOLD":
				pd.setIsBetting(false);
				isDone = true;
				System.out.println("Player "+myName+" is folding");
				break;
			case "ALLIN":
				if (currentBalance > 0) {
					highestBet = currentBalance;
					Server.incPot(currentBalance);
					myPreviousBet = previousBet = currentBalance;
					currentBalance = 0;
					isDone = true;
					System.out.println("Player "+myName+" is all-in");
				}
				else {
					msgr.broadcast("ERROR|BET|You have no money.");
					pd.setIsBetting(false);
					isDone=true;
				}
				break;
			default:
				msgr.broadcast("ERROR|BET|Incorrect command ("+params[0]+")");
				break;
		}
		//TODO Broadcast all queueBroadcast("LASTBET|"+myName+"|"+previousBet);
		if (isDone == true) {
			pd.setBalance(currentBalance);
			pd.setPreviousBet(myPreviousBet);
			Server.incNumberOfBets();
			moveLockToNextPlayer();
		}
	}
	
	public void moveLockToNextPlayer() {
		synchronized (lock) {
			int newID = (allowedThread+1)%(getServer().players);
			for (int i=0; i<getServer().players; i++) {
				if (!Server.getPlayerData(newID).isInGame())
					newID++;
			}
			
    		allowedThread=newID;
    		lock.notifyAll();
    	}
	}
	
	public void setPlayerName(String name) {
		if (name.equals("Player") || name.equals("Bot"))
			name+=" "+String.valueOf(id);
		Server.getPlayerData(id).setName(name);
		Server.incNumberOfHandshakes();
		myName = name;
		System.out.println("Player #"+id+" is now: "+name);
	}
	
	public Hand generateHand() throws Exception {
		String cmd = "SETHAND";
		 String handStr = "";
		 //Pull 5 cards from deck 
		 for (int i=0; i<5; i++) {
			 try {
				 handStr += deck.pullCardToString();
			 }
			 catch (Exception e) {
				e.printStackTrace();
			 }
			 if (i!=4) handStr+="|";
		 }
		 

		//Send Hand to player
		cmd += "|"+handStr;
		msgr.broadcast(cmd);
		return new Hand(handStr);
	}
	
	public void displayAccount(int money) {
		msgr.broadcast("DISPLAYMONEY|"+money);
	}
	
    @Override
    public void finalize() {
        System.out.println("Player #"+id+" has left the table.");
        getServer().detachPlayer(id);
        try {
            super.finalize();
        }
        catch (Throwable ex) {}
    }

	@Override
	public void interrupt() {
		msgr.broadcast("END");
		super.interrupt();
	}
	
	public void queueBroadcast(String msg) {
		synchronized (lock) {
			while (allowedThread != id) {
				try {
					lock.wait();
				}
				catch(InterruptedException ex) {}
			}
		}
		try {
			sleep(1000);
		} catch (InterruptedException e) {}
		msgr.broadcast(msg);
	}
	
	public Messenger getMsgr() {
		return msgr;
	}
	
	public void win() {
		msgr.broadcast("WIN");
	}
	
	public void tie() {
		msgr.broadcast("TIE");
	}
	
	public void lost() {
		msgr.broadcast("LOST");
	}
	
	public void setHand(String handStr) {
		getServer().setHandOfId(id, handStr);
	}

	public int getID() {
		return id;
	}
	
	public Server getServer() {
		return Server.getInstance();
	}

	public static int getHighestBet() {
		return highestBet;
	}
	
	public static void resetBet() {
		previousBet = 0;
		highestBet = 0;
	}

	public static void resetAllowedThread() {
		allowedThread = 0;
	}
}/* END OF CLASS ClientThread */
