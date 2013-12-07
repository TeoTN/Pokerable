package models;
import java.net.*;
import java.io.*;

import play.mvc.WebSocket;
import play.mvc.WebSocket.Out;
import controllers.Messenger;


public abstract class Player extends Thread
{
	protected Messenger msgr;
	protected int accountBalance;
	int wins;		//number of won games
	Hand hand;		//hand
	int account;	//amount of cash the player has
	int port;
	Socket socket = null;
	PrintWriter out = null;
	BufferedReader in = null;
	String msg = "";
	String host;
	private static boolean isGUIModeOn = false;
	private boolean GUIconnected = false;
	public static int lastID = 0;
	private WebSocket.In<String> WSin;
	private WebSocket.Out<String> WSout;
	private Printer printer;
	Player() {
		this("localhost", 1700);
	}
	
	Player(String host, int port) {
		this.port = port;
		this.host = host;
		printer = new Printer(this);
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
	    	printer.print("Unable to reach host: localhost");
	    	System.exit(-1);
	    }
	    catch (IOException e) {
	    	printer.print("Connection refused.");
	    	System.exit(-1);
	    }
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
	    msgr = new Messenger(this);
	    if (!Player.isGUIModeOn()) {
	    	String name = promptName();
	    	sendPlayerName(name);
	    }
	    else {
	    	while (GUIconnected != true) {
	    		try {
					sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	    	}
	    }
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
	
	public abstract String promptName();
	
	/**
	 * Function MUST answer with message covering pattern: 
	 * 	CHANGE|[...]
	 * otherwise a deadlock is going to occur
	 */
	abstract public void promptChange();
	
	/**
	 * Function MUST answer with message covering pattern:
	 * 	SETBET|[typeofbet]|[money]
	 * e.g.: msgr.broadcast("SETBET|CALL|0")
	 * otherwise a deadlock is going to occur
	 * 
	 * Available types of bet:
	 * CHECK BET RAISE CALL FOLD ALLIN
	 * 
	 * Account balance is available in field accountBalance of type int
	 */
	abstract public void promptBet(String params);

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
			printer.print("Unable to create hand.");
			ex.printStackTrace();
		}
		printer.print("Your current hand is: "+getHandToString());
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
    	printer.print("You won! Score: "+ (++wins));
    }
    
    public void tie() {
    	printer.print("There was a tie! Your score: "+ (++wins));
    }
    
    public void lost() {
    	printer.print("You've lost! Your score: "+ wins);
    }
    
    public abstract void setMoneyAtBeginning();
    
    public void displayMoney(String msg) {
    	int m = Integer.parseInt(msg);
    	accountBalance = m;
    	if (m!=0) {
    		printer.print("Currently you have "+m+" pounds");
    	}
    	else {
    		printer.print("You are out of game due to lack of money.");
    		finalize();
    	}
    }
    
    public void broadcastWins() {
    	msgr.broadcast("WINS|"+wins);
    }

	public static boolean isGUIModeOn() {
		return isGUIModeOn;
	}

	public static void setGUIMode(boolean isGUIModeOn) {
		Player.isGUIModeOn = isGUIModeOn;
	}
	
	public void sendPlayerName(String name) {
		msgr.broadcast("CONNECTED|"+name);
	}
	
	public void setGUIconnected() {
		GUIconnected = true;
	}
	
	public void broadcastRemotely(String msg) {
		msgr.broadcast(msg);
	}
	
	public void bindWS(WebSocket.In<String> in, WebSocket.Out<String> out) {
		WSin = in;
		WSout = out;
	}
	
	public Printer getPrinter() {
		return printer;
	}
	
	public void dispatchGUI(String msg) {
		System.out.println("DBG: "+msg);
		printer.print(msg);
	}
	
	public WebSocket.Out<String> getWSout() {
		return WSout;
	}
}
