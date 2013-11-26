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
	private ArrayList<Socket> clients;
	private ArrayList<ClientThread> clientThreads;
	private int lastID = 0, round=1, games=0;
	private int port, players=0, allowedThread=0;
	Scanner input;
	private Deck deck;
	private Object lock;
	private ArrayList<Hand> hands;
	private int changedHands;
	private ArrayList<Integer> wins;
	/**
	 * Class responsible for Client maintenance.
	 *
	 */
	class ClientThread extends Thread {
		int id;
	    BufferedReader in = null;
	    PrintWriter out = null;
	    String msg = "";
	    Hand hand;
	    
	    /**
	     * Typical constructor.
	     * @param id Client's id
	     */
		ClientThread(int id) {
			this.id = id;
		}
		
		/**
		 * Input and output stream are being established and then thread starts to receive messages from thread.
		 */
		@Override
		public void run() {
			try {
	            in = new BufferedReader(new InputStreamReader(clients.get(id).getInputStream()));
	            out = new PrintWriter(clients.get(id).getOutputStream(), true);
	        } 
	        catch (IOException e) {
	            System.out.println("#"+id+": Unable to reach client.");
	        }
			receive();
		}
		
		/**
		 * Method responsible for receiving messages from server and calling methods connected to received message.
		 * Message scheme:
		 *     TYPE|arg0|arg1|arg2|...
		 *     SETHAND|S9|D9|H9|C3|C7
		 */
		public void receive() {
			while(msg != null) {
				try {
					msg = in.readLine();
		            //System.out.println("#"+id+": Received command: "+msg);
		            if ("END".equals(msg)) {
		            	finalize();
		                return;
		            }
		            else if ("CONNECTED".equals(msg)) {
		            	System.out.println("Client connected.");
		            	broadcast(new String[]{"WELCOME"});
		            }
		            else if (msg.startsWith("HAND")) {
		            	msg = msg.replace("HAND|", "");
		            	System.out.println("Player now #"+id+" has hand: "+msg);
		            	try {
							hand = new Hand(msg);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
		            	try {
							hands.set(id, hand);
						} catch (Exception e) {
							e.printStackTrace();
						}
		            	
		            }
		            else if (msg.startsWith("CHANGE")) {
		            	changeHand(msg.toUpperCase());
		            }
		            else if (msg.startsWith("WINS")) {
		            	String[] arr = msg.split("\\|");
		            	wins.set(id, Integer.parseInt(arr[1]));
		            }
		        } 
		        catch (IOException e) {
		        	finalize();
		            break;
		        }
			}
		}
		
		private void changeHand(String msg) {
			String receivedStr = msg.substring(7, 21);
			String localStr = hands.get(id).toString();
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
				broadcast(new String[]{"ERROR|CHEAT"});
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
        					try {
								arr[i] = deck.pullCard().toString();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
        				}
        			}
        		}
        		
        		if (changes < (arr.length-6)) {
        			String[] cmd = new String[]{"ERROR", "There was a number of incorrect cards to be changed."}; 
        			broadcast(cmd);
        		}
        		
        		System.out.println("Player #"+id+" decided to change "+(arr.length-6)+" cards.");
        		String[] newHand = new String[6];
            	newHand[0]="SETHAND";
            	for (int i=1; i<6; i++)
            		newHand[i] = arr[i];
            	broadcast(newHand);
        	}
        	synchronized (lock) {
        		allowedThread=(allowedThread+1)%(clientThreads.size());
        		lock.notifyAll();
        	}
        	changedHands++;
		}
		
		/**
		 * Broadcasts message to client.
		 * @param msg Array of arguments to be sent. E.g. {"SETHAND", "S9", "D9", "H9", "C3", "C7"}
		 */
		public void broadcast(String[] msg) {
			String s = "";
			for (int i=0; i<msg.length; i++)
				if (i==msg.length-1)
					s+=msg[i];
				else
					s+=msg[i]+"|";
			out.println(s);
		}
		
	    @Override
	    protected void finalize() {
	        System.out.println("Player #"+id+" has left the table.");
	        players--;
	        hands.remove(id);
	        clientThreads.remove(id);
	        clients.remove(id);
	        if (clientThreads.size()<2) {
	        	System.out.println("Less than 2 players. Ending the game.");
	        	end();
	        }
	        try {
	            super.finalize();
	        }
	        catch (Throwable ex) {}
	    }

		@Override
		public void interrupt() {
			String[] cmd = new String[]{"END"};
			broadcast(cmd);
			super.interrupt();
		}
		
		public void queueBroadcast(String[] msg) {
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
			broadcast(msg);
		}
		
		public void win() {
			broadcast(new String[]{"WIN"});
		}
		
		public void tie() {
			broadcast(new String[]{"TIE"});
		}
		
		public void lost() {
			broadcast(new String[]{"LOST"});
		}
		
	}/* END OF CLASS ClientThread */
	
	
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
	
	public void end() {
		for (ClientThread cth: clientThreads) {
			String[] msg = new String[]{"ERROR|Too many players left the game."};
			cth.broadcast(msg);
		}
		try {
			finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		System.exit(-1);
	}
	
	public void gameplay() {
 		 // Inform players about round
		 for (ClientThread currPlayer: clientThreads)
			currPlayer.broadcast(new String[]{"ROUND", String.valueOf(round)});
		 System.out.println("\nRound "+round);
		 
		 //Reset variables
		 changedHands = 0;
		 allowedThread = 0;
		 
		 //Create the Deck
		 deck = new Deck();
		 deck.shuffle();
		 
		 //Give players their hands
		 String[] cmd = new String[6];
		 cmd[0] = "SETHAND";
		 
		 for (ClientThread currPlayer: clientThreads) {
			 String handStr = "";
			 String ss;
			 //Pull 5 cards from deck 
			 for (int i=0; i<5; i++) {
				 try {
					 ss = deck.pullCard().toString();
					 cmd[i+1]= ss;
					 handStr += ss;
				 }
				 catch (Exception e) {
					e.printStackTrace();
				 }
			 }
			 try {
				hands.set(currPlayer.id, new Hand(handStr));
			 } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			 }
			 //Send Hand to player
			 currPlayer.broadcast(cmd);
		 }
		 
		 //Ask players if they want to change cards
		 cmd = new String[1];
		 cmd[0] = "PROMPTCHANGE";
		 for (ClientThread currPlayer: clientThreads) {
			 currPlayer.queueBroadcast(cmd);
		 }
		 
		 //Assessing hands
		 while (hands.contains(null) || changedHands!=players) {
			 try {
				sleep(1000);
			} catch (InterruptedException e) {}
		 }
		 ArrayList<HandRank> hrs = new ArrayList<HandRank>();
		 for (Hand h: hands) {
			 try {
			 hrs.add(new HandRank(h));
			 }
			 catch (Exception e) {
				 e.printStackTrace();
			 }
		 }
		 
		 //Inform about win/tie/lost
		 HandRank winning = Collections.max(hrs);
		 System.out.println("Winning hand: "+winning.assessedHand.toString());
		 ArrayList<Integer> winnersID = new ArrayList<Integer>();
		 for (ClientThread currPlayer: clientThreads) {
			 if (currPlayer.hand.toString().equals(winning.assessedHand.toString())) {
				 winnersID.add(currPlayer.id);
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
		hands = new ArrayList<Hand>();
		for (int i=0; i<players; i++) {
			hands.add(null);
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
		 for (ClientThread cth: clientThreads) {
			 cth.broadcast(new String[]{"GETWINS"});
		 }
		 while (wins.contains(null)) {}
		 
		 String[] msg = new String[players+1];
		 msg[0]="RESULT";
		 for (int i=0; i<wins.size(); i++) {
			 if (wins.get(i) == Collections.max(wins)) {
				 msg[i+1]=wins.get(i)+" (winner)";
				 System.out.println("Player #"+i+" is winner");
			 }
			 else
				 msg[i+1]=""+wins.get(i);
		 }
		 for (ClientThread cth: clientThreads) {
			 cth.broadcast(msg);
		 }
		 
		 System.out.println("GAME IS OVER.");
		 System.exit(0);
	}
}