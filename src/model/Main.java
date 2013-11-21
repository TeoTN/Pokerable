package model;
import java.util.Scanner;

/**
 * Main class of Pokerable game.
 * @author Piotr Staniów
 * @author Micha³ Kie³bowicz
 */
final class Main
{
	private static Scanner input;
	/**
	 * Default constructor.
	 */
	private Main() {
	}
	/**
	 * @param args define whether program should be client or server
	 * @throws Exception Lack of arguments
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("Welcome to Pokerable the game.");
		input = new Scanner(System.in);
		
		try {
			args[0] = args[0].toLowerCase();
			args[1] = args[1].toLowerCase();
			args[2] = args[2].toLowerCase();
		}
		catch (Exception e) {}
		
		if (args.length == 0)
			throw new Exception("You didn't specify whether want to run either as server or client.");
		if (args[0].equals("client")) {
			int port=1700;
			String host="localhost";
			if (args.length==3) {
				try {
					port = Integer.parseInt(args[2]);
				}
				catch (NumberFormatException ex) {
					System.err.println("Incorrect port number");
					ex.printStackTrace();
				}
				host = args[1];
			}
			
			System.out.println("Do you wish to be bot or human?");
			String s = input.next();
			
			if (s.equals("human")) {
				System.out.println("Trying to act like a human...");
				try {
					Player c = new Human(host, port);
					c.start();
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			else if (s.equals("bot")) {
				System.out.println("Bot mechanics are getting involved in...");
				try {
					Player c = new Bot(host, port);
					c.start();
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			else {
				System.out.println("Incorrect input - you'd rather write \"bot\" or \"human\".");
				throw new Exception("Incorrect type of player selected");
			}
		}
		else if (args[0].equals("server")) {
			System.out.println("Trying to run server...");
			//TODO jUnit test
			Server s = null;
			int port = 1700;
			if (args.length==2) {
				try {
					port = Integer.parseInt(args[1]);
				}
				catch (NumberFormatException ex) {
					System.err.println("Incorrect port number");
					ex.printStackTrace();
				}
			}
			try {
				s = new Server(port);
			}
			catch (Exception e) {
				System.err.println("Unable to start server on specified port. Trying to start on 1700");
				try {
					s = new Server();
				}
				catch (Exception ex) {
					System.err.println("Unable to start server on port 1700. Closing...");
					e.printStackTrace();
					System.exit(-1);
				}
			}
			s.start();
		}
		else {
			System.out.println("Incorrect parameter. Game will be shut down. " + args[0]);
			return;
		}
	}

}
