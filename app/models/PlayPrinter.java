package models;

import java.util.ArrayList;
import java.util.Map;

public class PlayPrinter {
	public static ArrayList<String> input = new ArrayList<String>();
	public static ArrayList<String> output = new ArrayList<String>();
	public static void print(String str) {
		output.add(str);
	}
	public static String prompt(String str) {
		return input.remove(0);
	}
}
