@(id:Int)

$(function() {
    var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket
 	var chatSocket = new WS("@routes.Application.webSocketMgr(id).webSocketURL(request)")
	
	var hand = "";
	
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
        		hand = msgArray[1]+"|"+msgArray[2]+"|"+msgArray[3]+"|"+msgArray[4]+"|"+msgArray[5];
        		$('#hand').empty();
        		$('#hand').append('<img src="/assets/images/cards/'+msgArray[1]+'.png" class="card" id="'+msgArray[1]+'">');
        		$('#hand').append('<img src="/assets/images/cards/'+msgArray[2]+'.png" class="card" id="'+msgArray[2]+'">');
        		$('#hand').append('<img src="/assets/images/cards/'+msgArray[3]+'.png" class="card" id="'+msgArray[3]+'">');
        		$('#hand').append('<img src="/assets/images/cards/'+msgArray[4]+'.png" class="card" id="'+msgArray[4]+'">');
        		$('#hand').append('<img src="/assets/images/cards/'+msgArray[5]+'.png" class="card" id="'+msgArray[5]+'">');
        	break;
        	
        	case "PROMPTCHANGE":
        		$('#handDesc').text('Select cards to change (up to 4):');
        		$('#gameplay').append('<button id="change">Change cards</button>');
        		$('.card').addClass('clickable');
        		bindChanger();
        	break;
        }
    }
    
    function bindChanger() {
	    $(".clickable").on("click", function(e) {
	    	$(this).toggleClass('toChange');
	    	if ($('.toChange').length > 4) {
	    		alert("You may change only up to 4 cards");
	    		$(this).removeClass('toChange');
	    	}
	    });
		
		$("#change").on("click", function(e) {
			var msg="CHANGE|"+hand;
			$('.toChange').each(function() {
				msg += "|"+ $(this).attr('id');
			});
			chatSocket.send(msg);
			$('.clickable').removeClass('clickable');
			$('#handDesc').text('Your hand:');
			$(this).remove();
			
			 var el = $('<div class="sent"></div>')
	        $(el).text(msg)
	        $('#console').append(el)
		});
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
