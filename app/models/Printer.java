package models;

public class Printer {
	public static void print(String str) {
		if (Player.isGUIModeOn()) {
			
		}
		else {
			ConsolePrinter.println(str);
		}
	}
	
	public static String prompt(String str) {
		String out = "";
		if (Player.isGUIModeOn()) {
			
		}
		else {
			out = ConsolePrinter.prompt(str);
		}
		return out;
	}
}
