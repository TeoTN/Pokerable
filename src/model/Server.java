package model;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	private static List<PlayerData> pData;
	static List<ClientThread> clientThreads;
	//private static List<Hand> hands;
	//private static List<Integer> accounts;
	static int changedHands=0;
	//public static List<Integer> wins;
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
			cth.displayAccount(getPlayerData(id).getBalance());
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
				getPlayerData(currPlayer.id).setHand(h);
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
		 for (PlayerData pd: pData) {
			 while (pd.getHand() == null  || changedHands<players) {
				 try {
					sleep(1000);
				 }
				 catch (InterruptedException e) {}
			 }
		 }

		 /* Get player with the strongest Hand
		  * new Comparator is set due to fact that PlayerData is already Comparable,
		  * but compareTo returns which player has more wins 
		  */
		 PlayerData winning = Collections.max(pData, new Comparator<PlayerData>() {
			    @Override
			    public int compare(PlayerData first, PlayerData second) {
			        return first.getHand().compareTo(second.getHand());
			    }
			});
		 
		 //Inform about win/tie/lost
		 String winningHandStr = winning.getHandToString();
		 System.out.println("Winning hand: "+winningHandStr);
		 
		 //We get IDs of players that actually have winning hand in order to decide whether acclaim tie or win
		 ArrayList<Integer> winnersID = new ArrayList<Integer>();
		 
		 for (ClientThread currPlayer: clientThreads) {
			 int id = currPlayer.getID();
			 String handOfCurrPlayer = getPlayerData(id).getHandToString();
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
				if (games<1) throw new NumberFormatException();
			}
			catch (NumberFormatException e) {
				games = 0;
				System.err.println("Incorrect number of rounds was provided. Try again.");
				continue;
			}
		}
		
		//Prompt user to specify how much money players will initially have
		while (initialMoney == 0) {
			System.out.println("How much money shall players initially have (min. 10)?");
			String r = input.next();
			try {
				initialMoney = Integer.parseInt(r);
				if (initialMoney < 10)
					throw new NumberFormatException();
			}
			catch (NumberFormatException e) {
				initialMoney = 0;
				System.err.println("Incorrect amount of money was provided. Try again.");
				continue;
			}
		}
		
		//Create an ArrayList that's going to hold players' threads. 
		clientThreads = new ArrayList<ClientThread>(players);
		pData = new ArrayList<PlayerData>(players);
		
		//Initialize PlayerData structure holding number of wins, hands, money in accounts
		for (int i=0; i<players; i++) {
			pData.add(new PlayerData());
			getPlayerData(i).setBalance(initialMoney);
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
		 for (PlayerData pd: pData) {
			 while (pd.isSentWins() == false) {}
		 }
		 
		 String msg = "RESULT";
		 //Maximal number of wins throughout the game
		 PlayerData pd;
		 int maxWins = Collections.max(pData).getWins();
		 int winsOfPlayer = 0;
		 String name;
		 for (int i=0; i<players; i++) {
			 pd = getPlayerData(i);
			 winsOfPlayer = pd.getWins();
			 name = pd.getName();
			 if (winsOfPlayer == maxWins) {
				 msg += "|"+name+"|"+winsOfPlayer+" (winner)";
				 System.out.println("Player "+name+" is winner");
			 }
			 else
				 msg +="|"+name+"|"+winsOfPlayer;
		 }
		 broadcastAll(msg);
		 
		 System.out.println("GAME IS OVER.");
		 System.exit(0);
	}
	
	public void setHandOfId(int id, String handStr) {
    	System.out.println("Player "+getPlayerData(id).getName()+" now has hand: "+handStr);
		Hand hand = null;
		try {
			hand = new Hand(handStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		getPlayerData(id).setHand(hand);
	}
	
	public static String getHandOfId(int id) {
		Hand hand = getPlayerData(id).getHand();
		hand.sort();
		return hand.toString();
	}
	
	public static void setWinsOfId(int id, int win) {
    	getPlayerData(id).setWins(win);
	}
	
	public void detachPlayer(int id) {
		clientThreads.remove(id);
        if (clientThreads.size()<2) {
        	System.out.println("Less than 2 players. Ending the game.");
        	onServerExit();
        }
        players--;
        pData.remove(id);
        clientThreads.remove(id); //TODO Removal method
	}
	
	public static PlayerData getPlayerData(int id) {
		return pData.get(id);
	}

	public static void setWinsSentOfId(int id) {
		getPlayerData(id).setSentWins();
	}
}