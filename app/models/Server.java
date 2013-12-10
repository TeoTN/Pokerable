package models;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
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
	static int changedHands=0;
	private static Server instance;
	private static int pot=0;
	private static int numberOfBets = 0;
	private static int numberOfHandshakes = 0;
	private int roundRobin = 0;
	int blind = 0;
	
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
		
		pot = 0;
		
		//Inform players about their current account status
		for (ClientThread cth: clientThreads) {
			int id = cth.getID();
			int bal = getPlayerData(id).getBalance();
			//Wpisowe
			
			if(bal-blind <= 0) {
				cth.finalize();
				cth = null;
				continue;
			}
			
			getPlayerData(id).setBalance(bal - blind);
			incPot(blind);
			cth.displayAccount(bal);
		}
		
		//Set isBetting field to 'true' for all players at the very beginning
		for (PlayerData pd: pData) {
			pd.setIsBetting();
		}
		
		
		//Reset variables
		changedHands = 0;
		numberOfBets = 0;
		//move round-robin
		roundRobin=(roundRobin+1)%players;
		
		System.out.println("START");
		
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
		
		//Ask players for BET whenever anyone in game is below highest bet 
		boolean isNotOnePlayer1 = bet(1);
		ClientThread.resetAllowedThread();
		System.out.println("Nie ma pan siusiaka");
		
		if(isNotOnePlayer1)
		{
			//Ask players if they want to change cards
			for (ClientThread currPlayer: clientThreads) {
				
				if(getPlayerData(currPlayer.id).isInGame()){
					currPlayer.queueBroadcast("PROMPTCHANGE");
				}
			}
			System.out.println("Słoiki nadziewane sedeseeem");
			//Ask players for second bet
			boolean isNotOnePlayer2 = bet(2);
			ClientThread.resetAllowedThread();
			System.out.println("Ale kiełbasę wali Stachu w młynie boli go");
			if(isNotOnePlayer2)
			{
				//Assessing hands
				for (PlayerData pd: pData) {
					while (pd.getHand() == null  || changedHands<players) {
						try {
							sleep(1000);
						}
						catch (InterruptedException e) {}
					}
				}
			}
		}
		
		System.out.println("Kancelaria! Kancelaria! Kancelaria");
		
		/* Get player with the strongest Hand
		 * new Comparator is set due to fact that PlayerData is already Comparable,
		 * but compareTo returns which player has more wins 
		 */
		PlayerData winning = Collections.max(pData, new Comparator<PlayerData>() {
			   @Override
			   public int compare(PlayerData first, PlayerData second)
			   {
				   if(!first.getIsBetting() && !first.getIsBetting()){
					   return 0;
				   }
				   
				   if(!first.getIsBetting()){
					   return -1;
				   }
				   if(!second.getIsBetting()){
					   return 1;
				   }
				   
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
		
		//Increase account balance of winners, acclaim winners
		if (winnersID.size()==1) {
			int id = winnersID.get(0);
			clientThreads.get(id).win();
			getPlayerData(id).addBalance(pot);
		}
		else {
			for (int id: winnersID) {
				clientThreads.get(id).tie();
				getPlayerData(id).addBalance(pot/(winnersID.size()));
			}
		}
		//Exclude from game people with no money
		for (PlayerData pd: pData) {
			if (pd.getBalance() < blind) {
				pd.setInGame(false);
			}
			pd.reset();
		}
		
		broadcastAll("PROMPTRETURN");
	}

	private boolean bet(int i)
	{
		//numberOfBets will be ZERO unless all players either have the same bet or are not in game 
		
		int cnt_isBetting = 0;
		PlayerData last = null;
		for (PlayerData pd: pData)
		{
			if(pd.getIsBetting()){
				cnt_isBetting ++;
				last = pd;
			}
		}
		
		if(cnt_isBetting <= 1){
			System.out.println("Szli z garem! 1");
			last.addBalance(getPot());
			pot = 0;
			return false;
		}
		
		
		numberOfBets = 0;
		int betTurn = 0; //betTurn is number of bet within one bidding set however I feel sorry for those reading the code
		ClientThread.moveLockToPlayer(roundRobin);
		while (numberOfBets == 0)
		{
			for (int j=0; j<players; j++) {
				ClientThread currPlayer = clientThreads.get((j+roundRobin)%players);
				System.err.println("    {DEBUG: roundRobin "+(j+roundRobin)%players+" }");
				PlayerData pd = getPlayerData(currPlayer.getID());
				if (pd.getPreviousBet() < ClientThread.getHighestBet() || pd.getPreviousBet() == 0) {
					String permittedBets = "";
					if (betTurn == 0) {
						permittedBets = "CHECK|BET|FOLD|ALLIN";
					}
					else permittedBets = "RAISE|CALL|FOLD|ALLIN";
					currPlayer.queueBroadcast("PROMPTBET|"+i+"|"+getPot()+"|"+permittedBets);
					betTurn++;
				}
				else numberOfBets++;
			}
			
			System.out.print("Waiting for bets");
			while (numberOfBets < players) {
				System.out.print(".");
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("\n");
			
			for (ClientThread cth: clientThreads)
			{
				PlayerData pd = getPlayerData(cth.getID());
				
				//System.err.println(pd.isInGame() + ", "+ pd.getPreviousBet()+", "+ ClientThread.getHighestBet());
				
				if (pd.getIsBetting() == true && pd.getPreviousBet() < ClientThread.getHighestBet()) {
					numberOfBets = 0;
				}
			}
		}
		ClientThread.resetBet();
		resetBet();
		ClientThread.resetAllowedThread();
		
		cnt_isBetting = 0;
		for (PlayerData pd: pData)
		{
			if(pd.getIsBetting()){
				cnt_isBetting ++;
			}
		}
		
		if(cnt_isBetting <= 1){
			System.out.println("Szli z garem! 2");
			return false;
		}
		
		System.out.println("Coli z wódką dać!");
		return true;
	}

	private void resetBet() {
		for (PlayerData pd: pData) {
			pd.setPreviousBet(0);
		}
	}

	public void run()
	{
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
		
		//Prompt user to specify how much money players will spend on blind
		while (blind == 0)
		{
			System.out.println("How much money shall players spend on entrance (min. 1)?");
			String r = input.next();
			try {
				blind = Integer.parseInt(r);
				if(blind < 1) {
					throw new NumberFormatException();
				}
			}
			catch(NumberFormatException e) {
				blind = 0;
				System.err.println("Incorrect amount of money was provided. Try again.");
			}
		}
		
		//Create an ArrayList that's going to hold players' threads. 
		clientThreads = new ArrayList<ClientThread>(players);
		pData = new ArrayList<PlayerData>(players);
		Random generator = new Random();
		roundRobin = generator.nextInt(players);
		
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
		}
		
		//Wait for handshake & player names
		System.out.print("Waiting for handshakes");
		while (numberOfHandshakes < players) {
			System.out.print(".");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	
		System.out.println("\nExpected number of players has connected.");
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

	public static int getPot() {
		return pot;
	}

	public static void incPot(int pot) {
		Server.pot += pot;
	}
	
	public static void incNumberOfBets() {
		numberOfBets++;
	}
	
	public static void incNumberOfHandshakes() {
		numberOfHandshakes++;
	}
}
