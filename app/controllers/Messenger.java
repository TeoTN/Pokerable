package controllers;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import models.*;

public class Messenger {
	private Class<?> listenerAdapter = null;
	private BufferedReader in;
	private PrintWriter out;
	private Object listener;
	private String msg;
	private String className;
	private boolean GUImode = false;
	
	public Messenger(Object arg) {
		listener = arg;
		msg = "";

		Method getInput = null;
		Method getOutput = null;
		className = listener.getClass().getName();
		GUImode = Player.isGUIModeOn();
		//Initialize input/output
		try {
			listenerAdapter = Class.forName(className);
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			getInput = listenerAdapter.getMethod("getInputStream");
			getOutput = listenerAdapter.getMethod("getOutputStream");
		}
		catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		try {
			in = (BufferedReader) getInput.invoke(listener);
			out = (PrintWriter) getOutput.invoke(listener); 
		}
		catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
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
			}
			catch (IOException e) {
				//e.printStackTrace();
				performMethod("finalize");
				break;
			}
			
			if (GUImode && !className.contains("ClientThread") && !className.contains("Bot")) {
				dispatchGUI(msg);
				return;
			}
			
			if (GUImode && className.contains("Bot")) {
				performMethod("dispatchGUI", msg);
			}
			if (!msg.equals("")) System.out.println("  { DEBUG: "+msg+" in: "+className+"}"); // DEBUG
			if ("END".equals(msg)) {
				Class cl = listener.getClass();
				String nameOfClass = cl.getName();
            	if (nameOfClass.equals("Player")) {
            		System.out.println("Server has been shut down.");
            		performMethod("finalize");
            	}
                return;
            }
			//Messages from server
            else if (msg.startsWith("SETHAND")) {
            	msg = msg.replace("SETHAND|", "");
            	performMethod("setHand", msg);
            }
            else if (msg.startsWith("PROMPTCHANGE")) {
            	performMethod("promptChange"); 
            }
            else if (msg.startsWith("PROMPTBET")) {
            	msg = msg.replace("PROMPTBET|", "");
            	performMethod("setMoneyAtBeginning");
            	performMethod("promptBet", msg); 
            }
            else if (msg.startsWith("DISPLAYMONEY")) {
            	msg = msg.replace("DISPLAYMONEY|", "");
            	performMethod("displayMoney", msg); 
            }
            else if (msg.startsWith("PREVIOUSBET")) {
            	msg = msg.replace("PREVIOUSBET|", "");
            	performMethod("previousBet", msg); 
            }
            else if (msg.startsWith("WIN")) {
            	performMethod("win");
            }
            else if (msg.startsWith("TIE")) {
            	performMethod("tie");
            }
            else if (msg.startsWith("LOST")) {
            	performMethod("lost");
            }
            else if (msg.startsWith("ROUND")) {
            	String arr[] = msg.split("\\|");
            	System.out.println("\nRound: "+arr[1]);
            }
            else if (msg.startsWith("GETWINS")) {
            	performMethod("broadcastWins");
            }
            else if (msg.startsWith("RESULT")) {
            	String arr[] = msg.split("\\|");
            	System.out.println("\nRESULTS:");
            	for (int i=1; i<arr.length; i+=2) {
            		System.out.println("Player "+arr[i]+" has scored: "+arr[i+1]);
            	}
            }
            else if (msg.startsWith("ERROR")) {
            	String arr[] = msg.split("\\|");
            	if (arr[1].equals("CHEAT")) {
            		System.err.println("NO CHEATERS ALLOWED ON THIS SERVER. BYE!");
            		performMethod("finalize");
            	}
            	else if (arr[1].equals("BET")) {
            		System.out.println(arr[2]);
            		performMethod("promptBet", "");
            	}
            }
			//Messages from client
            else if (msg.startsWith("CONNECTED")) {
            	System.out.println("Client connected.");
            	msg = msg.replace("CONNECTED|", "");
            	performMethod("setPlayerName", msg);
            	broadcast("WELCOME");
            }
            else if (msg.startsWith("HAND")) {
            	msg = msg.replace("HAND|", "");
            	performMethod("setHand", msg);
            }
            else if (msg.startsWith("CHANGE")) {
            	msg = msg.replace("CHANGE|", "");
            	performMethod("changeHand", msg.toUpperCase());
            }
            else if (msg.startsWith("WINS")) {
            	String[] arr = msg.split("\\|");
            	Server.setWinsOfId(getID(), Integer.parseInt(arr[1]));
            	Server.setWinsSentOfId(getID());
            }
            else if ("WELCOME".equals(msg)) {
            	System.out.println("Connected to server. Waiting for other players to join...");
            }
            else if (msg.startsWith("SETBET")) {
            	msg = msg.replace("SETBET|", "");
            	performMethod("bet", msg);
            }
            else System.err.println(msg);
		}
	}
	
	private void dispatchGUI(String msg) {
		while(msg != null) {
			try {
				msg = in.readLine();
			}
			catch (IOException e) {
				//e.printStackTrace();
				performMethod("finalize");
				break;
			}
			if (msg.startsWith("SETHAND")) {
				msg = msg.replace("SETHAND|", "");
				msg = Hand.sortString(msg);
				performMethod("dispatchGUI", "SETHAND|"+msg);
	        	performMethod("setHand", msg);
	        }
	        else if (msg.startsWith("PROMPTCHANGE")) {
	        	performMethod("dispatchGUI", msg); 
	        }
	        else if (msg.startsWith("PROMPTBET")) {
	        	performMethod("dispatchGUI", msg); 
	        	msg = msg.replace("PROMPTBET|", "");
	        	performMethod("setMoneyAtBeginning");
	        }
	        else if (msg.startsWith("DISPLAYMONEY")) {
	        	performMethod("dispatchGUI", msg); 
	        	msg = msg.replace("DISPLAYMONEY|", "");
	        }
            else if (msg.startsWith("PREVIOUSBET")) {
            	msg = msg.replace("PREVIOUSBET|", "");
            	performMethod("previousBet", msg); 
            }
	        else if (msg.startsWith("WIN")) {
	        	performMethod("win");
	        }
	        else if (msg.startsWith("TIE")) {
	        	performMethod("tie");
	        }
	        else if (msg.startsWith("LOST")) {
	        	performMethod("lost");
	        }
	        else if (msg.startsWith("ROUND")) {
	        	performMethod("dispatchGUI", msg);
	        }
	        else if (msg.startsWith("GETWINS")) {
	        	performMethod("broadcastWins");
	        }
	        else if (msg.startsWith("RESULT")) {
	        	performMethod("dispatchGUI", msg);
	        }
	        else if (msg.startsWith("ERROR")) {
	        	performMethod("dispatchGUI", msg);
	        	String arr[] = msg.split("\\|");
	        	if (arr[1].equals("CHEAT")) {
	        		performMethod("finalize");
	        	}
	        	else if (arr[1].equals("BET")) {
	        		performMethod("promptBet", "");
	        	}
	        }
	        else System.err.println("Incorrect: "+msg);
		}
	}
	
	/**
	 * Broadcasts message to client.
	 * @param msg Array of arguments to be sent. E.g. {"SETHAND", "S9", "D9", "H9", "C3", "C7"}
	 */
	public void broadcast(String msg) {
		out.println(msg);
	}
	
	public void performMethod(String name, Object... params) {
		Method m = null;
		try {
			m = listenerAdapter.getMethod(name, new Class[]{String.class});
		} catch (NoSuchMethodException | SecurityException e) {
			try {
				m = listenerAdapter.getMethod(name);
			} catch (NoSuchMethodException | SecurityException e1) {
				e1.printStackTrace();
			}
		}
		try {
			m.invoke(listener, params);
		}
		catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			System.err.println(name+"   "+params.toString());
			e.printStackTrace();
		}
	}
	
	private int getID() {
		Method m = null;
    	int id = -1;
		try {
			m = listenerAdapter.getMethod("getID");
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		try {
			id = (int) m.invoke(listener);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		return id;
	}
}
