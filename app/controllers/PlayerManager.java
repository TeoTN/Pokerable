package controllers;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import play.*;
import play.libs.Json;
import play.mvc.*;
import views.html.*;
import models.*;

/**
 * Singleton class used to create Player client as a response to proper HTTP Request
 * @author Piotr Staniów
 *
 */
public class PlayerManager extends Controller {
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
		Player instance = players.get(id);
		final Map<String, String[]> values = request().body().asFormUrlEncoded();
		String name = values.get("playerName")[0];
		instance.sendPlayerName(name);
		instance.setGUIconnected();
		return ok(gameplay.render(id));
	}
	
	public static void broadcastWith(int id, String msg) {
		Player instance = players.get(id);
		instance.broadcastRemotely(msg);
	}
	
	public static void bindWS(final int id, WebSocket.In<String> in, WebSocket.Out<String> out) {
		players.get(id).bindWS(in, out);
	}
}
