package model;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * @author Piotr Staniów
 * @author Micha³ Kie³bowicz
 * Equivalent of Table class
 */
public class Server extends Thread
{
	private ServerSocket server = null;
	private int lastID = 0, round=1, games=0, initialMoney = 0;
	private int port;
	Scanner input;
	int players=0;
	static List<ClientThread> clientThreads;
	private static List<Hand> hands;
	private static List<Integer> accounts;
	static int changedHands=0;
	public static List<Integer> wins;
	private static Server instance;
	
	private Server() throws Exception {
		this(1700);
	}
	
	private Server(int port) throws Exception {
		this.port = port;
		input = new Scanner(System.in);
		init();
	}
	
	public static synchronized Server getInstance() {
		if (instance == null) {
			try {
				instance = new Server();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return instance;
	}
	
	public static synchronized Server getInstance(int port) {
		if (instance == null) {
			try {
				instance = new Server(port);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return instance;
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
	
	public void onServerExit() {
		broadcastAll("END|Server was shut down or severe error occured.");
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
		
		//Inform players about their current account status
		for (ClientThread cth: clientThreads) {
			int id = cth.getID();
			cth.displayAccount(accounts.get(id));
		}
		 
		 //Reset variables
		 changedHands = 0;
		 
		 //Give players their hands
		 for (ClientThread currPlayer: clientThreads) {
			Hand h = null;
			try {
				h = currPlayer.generateHand();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				getHands().set(currPlayer.id, h);
			} catch (Exception e) {
				e.printStackTrace();
			}
		 }
		 
		 //Ask players for BET
		 for (ClientThread currPlayer: clientThreads) {
			 currPlayer.queueBroadcast("PROMPTBET");
		 }
		 
		 //Ask players if they want to change cards
		 for (ClientThread currPlayer: clientThreads) {
			 currPlayer.queueBroadcast("PROMPTCHANGE");
		 }
		 
		 //Assessing hands
		 while (getHands().contains(null) || changedHands<players) {
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
		 
		 String winningHandStr = winning.getAssessedHand().toString();
		 for (ClientThread currPlayer: clientThreads) {
			 String handOfCurrPlayer = getHandOfId(currPlayer.getID());
			 if (handOfCurrPlayer.equals(winningHandStr)) {
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
		//Prompt user to specify a number of players that will join the game
		while (players<2 || players>4) {
			System.out.println("How many players will be playing? (2-4)");
			String p = input.next();
			try {
				players = Integer.parseInt(p);
			}
			catch (NumberFormatException ex) {
				System.err.println("Incorrect input was specified. Please give a number of players between 2 and 4.\n Try again.");
				continue;
			}
		}
		
		//Prompt user to specify how many rounds will be played.
		while (games == 0) {
			System.out.println("How many rounds would you like to play?");
			String r = input.next();
			try {
				games = Integer.parseInt(r);
			}
			catch (NumberFormatException e) {
				System.err.println("Incorrect number of rounds was provided. Try again.");
				continue;
			}
		}
		
		//Prompt user to specify how much money players will initially have
		while (initialMoney == 0) {
			System.out.println("How much money shall players initially have?");
			String r = input.next();
			try {
				initialMoney = Integer.parseInt(r);
			}
			catch (NumberFormatException e) {
				System.err.println("Incorrect amount of money was provided. Try again.");
				continue;
			}
		}
		
		//Create an ArrayList that's going to hold players' threads. 
		clientThreads = new ArrayList<ClientThread>(players);
		
		//Initialize empty arrays holding number of wins, hands, money in accounts
		wins = new ArrayList<Integer>();
		hands = new ArrayList<Hand>();
		accounts = new ArrayList<Integer>();
		for (int i=0; i<players; i++) {
			getHands().add(null);
			wins.add(null);
			accounts.add(initialMoney);
		}
		
		//Wait for clients to join
		while (lastID != players) {
			 System.out.println("Still waiting for " + (players-lastID) + " players to join.");
			 System.out.println("Please connect on port " + port);
	         try {
	             clientThreads.add(lastID, new ClientThread( lastID ));
	             clientThreads.get(lastID).bindSocket(server.accept());
	             clientThreads.get(lastID).start();
		         lastID++;
	         }
	         catch (IOException e) {
	        	 System.err.println("Error: unable to accept connection @"+port);
	         }
	         catch (NullPointerException ex) {
	        	 ex.printStackTrace();
	         }
	         
	         try {
	        	 Thread.sleep(1000);
	         }
	         catch (InterruptedException ex) {
	        	 System.err.println("\nSevere error: interrupted server thread. Exit.");
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

	public static List<Hand> getHands() {
		return hands;
	}

	/*public static void setHands(ArrayList<Hand> hands) {
		Server.hands = hands;
	}*/
	
	public void setHandOfId(int id, String handStr) {
		Hand hand = null;
		try {
			hand = new Hand(handStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		hands.set(id, hand);
	}
	
	public static String getHandOfId(int id) {
		Hand hand = hands.get(id);
		hand.sort();
		return hand.toString();
	}
	
	public static void setWinsOfId(int id, int win) {
    	wins.set(id, win);
	}
	
	public void detachPlayer(int id) {
		clientThreads.remove(id);
        if (clientThreads.size()<2) {
        	System.out.println("Less than 2 players. Ending the game.");
        	onServerExit();
        }
        players--;
        getHands().remove(id);
        clientThreads.remove(id); //TODO Removal method
	}
}