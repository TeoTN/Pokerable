package models;

import play.mvc.WebSocket;

public class PlayPrinter {
	public static void print(String str, WebSocket.Out<String> out) {
		try {
			out.write(str);
		}
		catch (NullPointerException ex) {
			ex.printStackTrace();
		}
	}
}
