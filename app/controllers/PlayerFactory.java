package controllers;
import play.*;
import play.mvc.*;
import views.html.*;
import models.*;

/**
 * Singleton class used to create Player client as a response to proper HTTP Request
 * @author Piotr Staniów
 *
 */
public class PlayerFactory extends Controller {
	private static Player instance;
	
	public static synchronized Result newInstance(String type) {
		type = type.toLowerCase();
		if (instance == null) {
			if (type.equals("human")) {
				instance = new Human("localhost", 1700);
			}
			else if (type.equals("bot")) {
				instance = new Bot("localhost", 1700);
			}
			else {
				instance = new Bot("localhost", 1700); //TODO Error throw
			}
		}
		instance.start();
		return ok(player.render(type));
	}
}
