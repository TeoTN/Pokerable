@(id:Int)

$(function() {
    var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket
 	var chatSocket = new WS("@routes.Application.webSocketMgr(id).webSocketURL(request)")
	
    var sendMessage = function() {
    	var msg = $("#talk").val()
        chatSocket.send(msg)
        var el = $('<div class="sent"></div>')
        $(el).text(msg)
        $('#console').append(el)
        $("#talk").val('')
    }

    var receiveEvent = function(event) {
        // Create the message element
        var el = $('<div class="message"></div>')
        var msg = event.data;
        $(el).text(msg)
        $('#console').append(el)
        
        var msgArray = msg.split('|');
        switch (msgArray[0]) {
        	case "SETHAND":
        		$('#hand').empty();
        		$('#hand').append('<img src="/assets/images/cards/'+msgArray[1]+'.png" class="card">');
        		$('#hand').append('<img src="/assets/images/cards/'+msgArray[2]+'.png" class="card">');
        		$('#hand').append('<img src="/assets/images/cards/'+msgArray[3]+'.png" class="card">');
        		$('#hand').append('<img src="/assets/images/cards/'+msgArray[4]+'.png" class="card">');
        		$('#hand').append('<img src="/assets/images/cards/'+msgArray[5]+'.png" class="card">');
        	break;
        }
    }

    var handleReturnKey = function(e) {
        if(e.charCode == 13 || e.keyCode == 13) {
            e.preventDefault()
            sendMessage()
        }
    }

    $("#talk").keypress(handleReturnKey)

    chatSocket.onmessage = receiveEvent

})
