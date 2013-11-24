package model;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import controller.Messenger;

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
	static int allowedThread=0;
	static Object lock = null;
	static int highestBet = 0;
    
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
		if (lock == null)
			lock = new Object();
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
    	synchronized (lock) {
    		allowedThread=(allowedThread+1)%(Server.clientThreads.size());
    		lock.notifyAll();
    	}
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
	public void bet(String param) {
		String[] params = param.split("\\|");
		PlayerData pd;
		pd = Server.getPlayerData(id);
		int currentBalance = pd.getBalance(); 
		switch (params[0]) {
			case "CHECK":

				break;
			case "BET":
				break;
			case "RAISE":
				break;
			case "CALL":
				break;
			case "FOLD":
				break;
			case "ALLIN":
				break;
		}
		synchronized (lock) {
    		allowedThread=(allowedThread+1)%(Server.clientThreads.size());
    		lock.notifyAll();
    	}
	}
	
	public void setPlayerName(String name) {
		if (name.equals("Player") || name.equals("Bot"))
			name+=" "+String.valueOf(id);
		Server.getPlayerData(id).setName(name);
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
}/* END OF CLASS ClientThread */
