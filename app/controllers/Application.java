package controllers;
import com.fasterxml.jackson.databind.JsonNode;

import play.*;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Pokerable"));
    }
    
    public static Result webSocketMgrJs(int id) {
    	//return ok(views.html.WebSocketMgr.render(id)).as("application/javascript");
        return ok(views.js.WebSocketMgr.render(id));
    }
    
    /**
     * Handle the chat websocket.
     */
    public static WebSocket<JsonNode> webSocketMgr(final int id) {
        return new WebSocket<JsonNode>() {
            
            // Called when the Websocket Handshake is done.
            public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out){
            	in.onMessage(new Callback<JsonNode>() {
                    public void invoke(JsonNode event) {
                        String eventStr = event.get("text").asText();
                        System.out.println("DBG: ("+String.valueOf(id)+") "+eventStr);
                    } 
                 });
                 
                 // When the socket is closed.
                 in.onClose(new Callback0() {
                    public void invoke() {
                        //TODO Send a Quit message to the room. 
                    	System.out.println("Disconnected");
                    }
                 });
            }
        };
    }
}
