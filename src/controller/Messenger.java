package controller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import model.*;

public class Messenger {
	private Class<?> listenerAdapter = null;
	private BufferedReader in;
	private PrintWriter out;
	private Object listener;
	private String msg;
	
	public Messenger(Object arg) {
		listener = arg;
		msg = "";

		Method getInput = null;
		Method getOutput = null;
		
		//Initialize input/output
		try {
			listenerAdapter = Class.forName(listener.getClass().getName());
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
			//if (!msg.equals("")) System.out.println(msg);
			
			if ("END".equals(msg)) {
            	if (listener.getClass().getName().equals("Player")) {
            		System.out.println("Server has been shut down.");
            		performMethod("finalize");
            	}
                return;
            }
            else if ("CONNECTED".equals(msg)) {
            	System.out.println("Client connected.");
            	broadcast("WELCOME");
            }
            else if (msg.startsWith("HAND")) {
            	msg = msg.replace("HAND|", "");
            	System.out.println("Player now #"+getID()+" has hand: "+msg);
            	performMethod("setHand", msg);     
            	try {
					Server.getHands().set(getID(), new Hand(msg));
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
            else if (msg.startsWith("CHANGE")) {
            	performMethod("changeHand", msg.toUpperCase());
            }
            else if (msg.startsWith("WINS")) {
            	String[] arr = msg.split("\\|");
            	Server.wins.set(getID(), Integer.parseInt(arr[1]));
            }
            else if ("WELCOME".equals(msg)) {
            	System.out.println("Connected to server. Waiting for other players to join...");
            }
            else if (msg.startsWith("SETHAND")) {
            	msg = msg.replace("SETHAND|", "");
            	performMethod("setHand", msg);
            }
            else if (msg.startsWith("PROMPTCHANGE")) {
            	performMethod("promptChange"); 
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
            	for (int i=1; i<arr.length; i++) {
            		System.out.println("Player #"+(i-1)+" has scored: "+arr[i]);
            	}
            }
            else if (msg.startsWith("ERROR")) {
            	String arr[] = msg.split("\\|");
            	if (arr[1].equals("CHEAT")) {
            		System.out.println("NO CHEATERS ALLOWED ON THIS SERVER. BYE!");
            	}
            	performMethod("finalize");
            }
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
