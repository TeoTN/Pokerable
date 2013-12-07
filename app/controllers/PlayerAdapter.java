package controllers;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import play.*;
import play.mvc.*;
import views.html.*;
import models.*;

/**
 * Singleton class used to create Player client as a response to proper HTTP Request
 * @author Piotr Staniów
 *
 */
public class PlayerAdapter extends Controller {
	public static int lastID = 0;
	static List<Player> players = new ArrayList<Player>();
	
	public static Result newInstance(String type) {
		type = type.toLowerCase();
		Player.setGUIMode(true);
		Player instance = null;
		if (type.equals("human")) {
			instance = new Human("localhost", 1700);
		}
		else if (type.equals("bot")) {
			instance = new Bot("localhost", 1700);
		}
		else {
			instance = new Bot("localhost", 1700); //TODO Error throw
		}
		players.add(lastID++, instance);
		instance.start();
		return ok(player.render(type, lastID-1));
		//return ok(player.render(type, instance));
	}
	
	public static Result connected(Integer id) {
		final Map<String, String[]> values = request().body().asFormUrlEncoded();
		String name = values.get("playerName")[0];
		players.get(id).sendPlayerName(name);
		return ok(gameplay.render());
	}
	
	/*public static Result gameplay() {
		return ok(gameplay.render());
	}*/
}
