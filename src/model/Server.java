package model;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * @author Piotr Staniów
 * @author Micha³ Kie³bowicz
 * Equivalent of Table class
 */
public class Server extends Thread
{
	private ServerSocket server = null;
	private int lastID = 0, round=1, games=0;
	private int port;
	Scanner input;
	static int allowedThread=0, players=0;
	static ArrayList<Socket> clients;
	static ArrayList<ClientThread> clientThreads;
	static Deck deck;
	static Object lock;
	private static ArrayList<Hand> hands;
	static int changedHands=0;
	public static ArrayList<Integer> wins;
	
	
	Server() throws Exception {
		this(1700);
	}
	
	Server(int port) throws Exception {
		lock = new Object();
		this.port = port;
		clients = new ArrayList<Socket>(4);
		clientThreads = new ArrayList<ClientThread>(4);
		input = new Scanner(System.in);
		init();
	}
	
	public void init() throws Exception {
		try {
            server = new ServerSocket(port);
        }
        catch (IOException ex) {
            throw new Exception("Error: Unable to create server listening on port: "+port);
        }
	} 
	
	public void broadcastAll(String msg) {
		for (ClientThread cth: clientThreads) {
			cth.getMsgr().broadcast(msg);
		}
	}
	
	public void end() {
		broadcastAll("ERROR|Too many players left the game.");
		try {
			finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		System.exit(-1);
	}
	
	public void gameplay() {
 		// Inform players about round
		broadcastAll("ROUND|"+String.valueOf(round));
		 System.out.println("\nRound "+round);
		 
		 //Reset variables
		 changedHands = 0;
		 allowedThread = 0;
		 
		 //Create the Deck
		 deck = new Deck();
		 deck.shuffle();
		 
		 //Give players their hands
		 for (ClientThread currPlayer: clientThreads) {
			 String cmd = "SETHAND";
			 String handStr = "";
			 //Pull 5 cards from deck 
			 for (int i=0; i<5; i++) {
				 try {
					 handStr += deck.pullCard().toString();
				 }
				 catch (Exception e) {
					e.printStackTrace();
				 }
				 if (i!=4) handStr+="|";
			 }
			 
			 try {
				getHands().set(currPlayer.id, new Hand(handStr));
			 } catch (Exception e) {
				e.printStackTrace();
			 }
			 //Send Hand to player
			 cmd += "|"+handStr;
			 currPlayer.getMsgr().broadcast(cmd);
		 }
		 
		 //Ask players if they want to change cards
		 for (ClientThread currPlayer: clientThreads) {
			 currPlayer.queueBroadcast("PROMPTCHANGE");
		 }
		 
		 //Assessing hands
		 while (getHands().contains(null) || changedHands!=players) {
			 try {
				sleep(1000);
			} catch (InterruptedException e) {}
		 }
		 ArrayList<HandRank> hrs = new ArrayList<HandRank>();
		 for (Hand h: getHands()) {
			 try {
				 hrs.add(new HandRank(h));
			 }
			 catch (Exception e) {
				 e.printStackTrace();
			 }
		 }
		 
		 //Inform about win/tie/lost
		 HandRank winning = Collections.max(hrs);
		 System.out.println("Winning hand: "+winning.getAssessedHand().toString());
		 ArrayList<Integer> winnersID = new ArrayList<Integer>();
		 
		 for (ClientThread currPlayer: clientThreads) {
			 if (currPlayer.getHand().equals(winning.getAssessedHand().toString())) {
				 winnersID.add(currPlayer.getID());
			 }
			 else currPlayer.lost();
		 }
		 
		 if (winnersID.size()==1)
			 clientThreads.get(winnersID.get(0)).win();
		 else
			 for (int id: winnersID)
				 clientThreads.get(id).tie();
	}

	public void run() {
		System.out.println("Server successfully created.");
		while (players<2 || players>4) {
			System.out.println("How many players will be playing? (2-4)");
			String p = input.next();
			try {
				players = Integer.parseInt(p);
			}
			catch (NumberFormatException ex) {
				System.out.println("Incorrect input. Please give a number of players between 2 and 4.");
				continue;
			}
		}
		
		 while (games == 0) {
			 System.out.println("How many rounds would you like to play? (max. 10)");
			 String r = input.next();
			 try {
				 games = Integer.parseInt(r);
			 }
			 catch (NumberFormatException e) {
				 System.out.println("Incorrect number of rounds was provided.");
				 continue;
			 }
			 if (games > 10) {
				 System.out.println("Too many rounds were set.");
				 games=0;
			 }
		 }
		wins = new ArrayList<Integer>();
		setHands(new ArrayList<Hand>());
		for (int i=0; i<players; i++) {
			getHands().add(null);
			wins.add(null);
		}
		
		
		while (clients.size() != players) {
			 System.out.println("Still waiting for " + (players-clients.size()) + " players to join.");
			 System.out.println("Please connect on port " + port);
	         try {
	        	 clients.add(lastID, server.accept());
	             clientThreads.add(lastID, new ClientThread( lastID ));
	             clientThreads.get(lastID).start();
	             lastID++;
	         }
	         catch (IOException e) {
	        	 System.out.println("Error: unable to accept connection @"+port);
	         }
	         catch (NullPointerException ex) {
	        	 ex.printStackTrace();
	         }
	            
	         try {
	        	 Thread.sleep(1000);
	         }
	         catch (InterruptedException ex) {
	        	 System.out.println("\nSevere error: interrupted server thread. Exit.");
	        	 System.exit(-1);
	         }
		 } /* END OF WHILE - WAITING FOR CLIENTS */
		
		 System.out.println("Expected number of players has connected.");
		 System.out.println("Let's play the game.");
		 
		 while (round<=games) {
			 gameplay();
			 round++;
		 }
		 
		 //Send info about winners, display winner on server console
		 System.out.println("");
		 broadcastAll("GETWINS");
		 while (wins.contains(null)) {}
		 
		 String msg = "RESULT";
		 for (int i=0; i<wins.size(); i++) {
			 if (wins.get(i) == Collections.max(wins)) {
				 msg += "|"+wins.get(i)+" (winner)";
				 System.out.println("Player #"+i+" is winner");
			 }
			 else
				 msg +="|"+wins.get(i);
		 }
		 broadcastAll(msg);
		 
		 System.out.println("GAME IS OVER.");
		 System.exit(0);
	}

	public static ArrayList<Hand> getHands() {
		return hands;
	}

	public static void setHands(ArrayList<Hand> hands) {
		Server.hands = hands;
	}
}