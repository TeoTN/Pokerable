package controller;

public class Msg {
	private String recipent;
	private String method;
	private String[] dataArr;
	
	public void setRecipent(String in) {
		recipent = in;
	}
	
	public void setMethod(String in) {
		method = in;
	}
	
	public void setData(String[] in) {
		dataArr = in;
	}
	
	public void setData(String in) {
		String[] arr = in.split("\\|");
		dataArr = arr;
	}
}
