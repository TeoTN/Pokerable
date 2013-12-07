package models;

public class Printer {
	private Player author;
	public Printer(Player p) {
		author = p;
	}
	public void print(String str) {
		if (Player.isGUIModeOn()) {
			PlayPrinter.print(str, author.getWSout());
		}
		else {
			ConsolePrinter.println(str);
		}
	}
	
	public String prompt(String str) {
		String out = "";
		out = ConsolePrinter.prompt(str);
		return out;
	}
}
