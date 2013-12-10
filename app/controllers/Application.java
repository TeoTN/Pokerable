package controllers;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Pokerable"));
    }
    
    public static Result webSocketMgrJs(int id, String type) {
        return ok(views.js.WebSocketMgr.render(id, type));
    }
    
    /**
     * Handle the chat websocket.
     */
    public static WebSocket<String> webSocketMgr(final int id) {
        return new WebSocket<String>() {
            
            // Called when the Websocket Handshake is done.
            public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out){
            	PlayerManager.bindWS(id, in, out);
            	in.onMessage(new Callback<String>() {
                    public void invoke(String event) {
                        PlayerManager.broadcastWith(id, event);
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
