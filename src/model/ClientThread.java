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
    Hand hand;
	private Deck deck;
    
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
		String receivedStr = msg.substring(7, 21);
		String localStr = Server.getHands().get(id).toString();
		Hand receivedHand = null;
		Hand localHand = null;
		try {
			receivedHand = new Hand(receivedStr);
			localHand = new Hand(localStr);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		receivedHand.sort();
		localHand.sort();
		receivedStr = receivedHand.toString();
		localStr = localHand.toString();
		if (!localStr.equals(receivedStr)) {
			msgr.broadcast("ERROR|CHEAT");
			System.out.println("Cheater (player #"+id+") kicked.");
			finalize();
		}
		
    	String arr[] = msg.split("\\|");
    	if (arr.length == 6){
    		System.out.println("Player #"+id+" don't want to change hand.");
    	}
    	else {
    		int changes=0;
    		
    		for (int i=1; i<6; i++) {
    			for (int j=6; j<arr.length; j++) {
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
    		
    		if (changes < (arr.length-6)) {
    			String cmd = new String("ERROR|There was a number of incorrect cards to be changed."); 
    			msgr.broadcast(cmd);
    		}
    		
    		System.out.println("Player #"+id+" decided to change "+(arr.length-6)+" cards.");
    		String newHand = "SETHAND";
        	for (int i=1; i<6; i++)
        		newHand +="|"+arr[i];
        	msgr.broadcast(newHand);
    	}
    	synchronized (Server.lock) {
    		Server.allowedThread=(Server.allowedThread+1)%(Server.clientThreads.size());
    		Server.lock.notifyAll();
    	}
    	Server.changedHands++;
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
	
    @Override
    protected void finalize() {
        System.out.println("Player #"+id+" has left the table.");
        Server.players--;
        Server.getHands().remove(id);
        Server.clientThreads.remove(id); //TODO Removal method
        if (Server.clientThreads.size()<2) {
        	System.out.println("Less than 2 players. Ending the game.");
        	//TODO Server.end();
        }
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
		synchronized (Server.lock) {
			while (Server.allowedThread != id) {
				try {
					Server.lock.wait();
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
	
	public void setHand(String h) {
		try {
			hand = new Hand(h);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Server.getHands().set(getID(), new Hand(h));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getHand() {
		hand.sort();
		return hand.toString();
	}

	public int getID() {
		return id;
	}
}/* END OF CLASS ClientThread */
