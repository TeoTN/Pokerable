import java.net.*;
import java.util.Scanner;
import java.io.*;

/**
 *  @author Piotr Staniów
 * @author Micha³ Kie³bowicz
 *
 */
public abstract class Player extends Thread
{
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
	    broadcast("CONNECTED");
	    receive();
	}
	
	//TODO While interrupted broadcast message "DISCONNECTED"
	
	/**
	 * Broadcasts message to server
	 */
	public void broadcast(String msg) {
		out.println(msg);
	}
	
	/**
	 * Is waiting for message from server until "END" command is reached or was unable to receive anything.
	 * @throws Exception 
	 */
	public void receive() {
		while(msg != null && !"END".equals(msg)) {
			try {
				msg = in.readLine();
	            //System.out.println("Received command from server: "+msg);
	            if ("END".equals(msg)) {
	            	System.out.println("Server has been shut down.");
	            	finalize();
	                return;
	            }
	            else if ("WELCOME".equals(msg)) {
	            	System.out.println("Connected to server. Waiting for other players to join...");
	            }
	            else if (msg.startsWith("SETHAND")) {
	            	msg = msg.replace("SETHAND|", "");
	            	setHand(msg);
	            	System.out.println("Your current hand is: "+getHandToUnicode());
	            	broadcast("HAND|"+getHandToString());
	            }
	            else if (msg.startsWith("PROMPTCHANGE")) {
	            	promptChange(); //Different in Human/Bot 
	            }
	            else if (msg.startsWith("WIN")) {
	            	System.out.println("You won! Score: "+ (++wins));
	            }
	            else if (msg.startsWith("TIE")) {
	            	System.out.println("There was a tie! Your score: "+ (++wins));
	            }
	            else if (msg.startsWith("LOST")) {
	            	System.out.println("You've lost! Your score: "+ wins);
	            }
	            else if (msg.startsWith("ROUND")) {
	            	String arr[] = msg.split("\\|");
	            	System.out.println("\nRound: "+arr[1]);
	            }
	            else if (msg.startsWith("GETWINS")) {
	            	broadcast("WINS|"+wins);
	            }
	            else if (msg.startsWith("RESULT")) {
	            	String arr[] = msg.split("\\|");
	            	System.out.println("\nRESULTS:");
	            	for (int i=1; i<arr.length; i++) {
	            		System.out.println("Player #"+(i-1)+" has scored: "+arr[i]);
	            	}
	            }
	            else if (msg.startsWith("ERROR")) {
	            	String arr[] = msg.split("\\|");
	            	if (arr[1].equals("CHEAT")) {
	            		System.out.println("NO CHEATERS ALLOWED ON THIS SERVER. BYE!");
	            	}
	            }
	        } 
	        catch (IOException e) {
	        	System.out.println("Disconnected from server.");
	            break;
	        }
		}
	}
	
	@Override
	public void interrupt() {
		broadcast("END");
		super.interrupt();
	}

	abstract void promptChange();

	public Hand getHand() {
		return hand;
	}
	
	public String getHandToString() {
		return hand.toString();
	}
	
	public String getHandToUnicode() {
		String ss=getHandToString();
		ss.replaceAll("S", "\u2660");
		ss.replaceAll("H", "\u2665");
		ss.replaceAll("D", "\u2666");
		ss.replaceAll("C", "\u2667");
		return ss;
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
	}
	
    @Override
    protected void finalize() {
        System.out.println("Server shut down");
        try {
            super.finalize();
        }
        catch (Throwable ex) {}
    }
}