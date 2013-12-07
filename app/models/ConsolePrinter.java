package models;

import java.util.Scanner;

public class ConsolePrinter {
	static Scanner input = new Scanner(System.in); 
	public static void println(String str) {
		System.out.println(str);
	}
	public static void print(String str) {
		System.out.print(str);
	}
	public static String prompt(String str) {
		String out = "";
		System.out.println(str);
		out = input.nextLine();
		return out;
	}
	public static String nextLine() {
		return input.nextLine();
	}
	public static String next() {
		return input.next();
	}
}
