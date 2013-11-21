package model;
import java.net.*;
import java.util.Scanner;
import java.io.*;

import controller.Messenger;

/**
 *  @author Piotr Stani�w
 * @author Micha� Kie�bowicz
 *
 */
public abstract class Player extends Thread
{
	protected Messenger msgr;
	private int accountBalance;
	int wins;		//number of won games
	Hand hand;		//hand
	int account;	//amount of cash the player has
	int port;
	Socket socket = null;
	PrintWriter out = null;
	BufferedReader in = null;
	String msg = "";
	Scanner input;
	String host;
	
	Player() {
		this("localhost", 1700);
	}
	
	Player(String host, int port) {
		this.port = port;
		this.host = host;
		input = new Scanner(System.in);
	}
	
	
	/**
	 * Tries to connect to localhost server at given port.
	 * Establishes I/O streams.
	 * Broadcasts hello message to server.
	 * Begins to listen to server messages.
	 * @param port Port to be connected
	 */
	public void connect() {
	    try {
	        socket = new Socket(host, port);
	        out = new PrintWriter(socket.getOutputStream(), true);
	        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    }
	    catch (UnknownHostException e) {
	    	System.out.println("Unable to reach host: localhost");
	    	System.exit(-1);
	    }
	    catch (IOException e) {
	    	System.out.println("Connection refused.");
	    	System.exit(-1);
	    }
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
	    msgr = new Messenger(this);
	    msgr.broadcast("CONNECTED");
	    msgr.receive();
	}
	
	//TODO While interrupted broadcast message "DISCONNECTED"
	
	@Override
	public void interrupt() {
		msgr.broadcast("END");
		super.interrupt();
	}

	public BufferedReader getInputStream() {
		return in;
	}
	
	public PrintWriter getOutputStream() {
		return out;
	}
	
	/**
	 * Function MUST answer with message covering pattern: 
	 * 	CHANGE|[...]
	 * otherwise a deadlock is going to occur
	 */
	abstract public void promptChange();
	
	/**
	 * Function MUST answer with message covering pattern:
	 * 	BET|[typeofbet]|[money]
	 * e.g.: msgr.broadcast("BET|CALL|0")
	 * otherwise a deadlock is going to occur
	 * 
	 * Account balance is available in field accountBalance of type int
	 */
	abstract public void promptBet();

	public Hand getHand() {
		return hand;
	}
	
	public String getHandToString() {
		hand.sort();
		return hand.toString();
	}
	
	public boolean handIncludes(String c) {
		return hand.includes(c);
	}
	/**
	 * Function will change player's hand to that one given in parameter
	 * @param h New hand of player 
	 * @throws Exception 
	 */
	public void setHand(String s) {
		try {
			hand = new Hand(s);
		}
		catch (Exception ex) {
			System.err.println("Unable to create hand.");
			ex.printStackTrace();
		}
    	System.out.println("Your current hand is: "+getHandToString());
    	msgr.broadcast("HAND|"+getHandToString());
	}
	
    @Override
    public void finalize() {
        System.out.println("Server shut down");
        try {
            super.finalize();
        }
        catch (Throwable ex) {}
    }
    
    public void win() {
    	System.out.println("You won! Score: "+ (++wins));
    }
    
    public void tie() {
    	System.out.println("There was a tie! Your score: "+ (++wins));
    }
    
    public void lost() {
    	System.out.println("You've lost! Your score: "+ wins);
    }
    
    public void displayMoney(String msg) {
    	int m = Integer.parseInt(msg);
    	accountBalance = m;
    	System.out.println("Currently you have "+m+" pounds");
    }
    
    public void broadcastWins() {
    	msgr.broadcast("WINS|"+wins);
    }
}