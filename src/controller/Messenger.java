package controller;
import java.io.BufferedReader;
import java.io.PrintWriter;

import model.*;

public class Messenger {
	BufferedReader in;
	PrintWriter out;
	public static void receive(Object listener) {
		//in = listener.getInputStream();
	}
	public static void broadcast(Object sender, String recipent, Msg msg) {
		//out = sender.getOutputStream();
	}
}
