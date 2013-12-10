@(id:Int, typeStr:String)

$(function() {
    var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket
 	var chatSocket = new WS("@routes.Application.webSocketMgr(id).webSocketURL(request)")
	if (chatSocket.readyState == 3)
		alert("Unable to establish webSocket connection");
	var hand = "";
	$('#bet').hide();
	var pot = 0;
	var previousBet = "nobody";
	
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
        		previousBet = 'nobody';
        	break;
        	
        	case "PROMPTCHANGE":
        		@if(typeStr!="bot") {
        		$('#handDesc').text('Select cards to change (up to 4):');
        		$('#gameplay').append('<button id="change">Change cards</button>');
        		$('.card').addClass('clickable');
        		bindChanger();
        		}
        	break;
        	
        	case "ROUND":
				$('#round').text(msgArray[1]);
        	break;
        	
        	case "PROMPTBET":
        		@if(typeStr!="bot") {
        		var b = $('#bet');
        		b.empty();
        		b.append("<p>Let's gamble</p>");
        		b.append('<p>Current pot:'+pot+'<br>Previous bet was by '+previousBet+'</p>');
        		b.append('<button id="'+msgArray[3]+'" class="bet">'+msgArray[3]+'</button>');
        		b.append('<button id="'+msgArray[4]+'" class="bet">'+msgArray[4]+'</button>');
        		b.append('<button id="'+msgArray[5]+'" class="bet">'+msgArray[5]+'</button>');
        		b.append('<button id="'+msgArray[6]+'" class="bet">'+msgArray[6]+'</button>');
				bindBet();
				b.show();
				}
        	break;
        	
        	case "PREVIOUSBET":
        		pot = msgArray[3];
        		previousBet = msgArray[2] + ": " + msgArray[4];
        	break;
        	
        	case "DISPLAYMONEY":
        		$('#money').text(msgArray[1]);
        	break;
        	
        	case "WIN":
        		@if(typeStr!="bot") {
        		alert("You've won the round!");
        		}
        	break;
        	
        	case "TIE":
        	@if(typeStr!="bot") {
        		alert("There was a tie!");
        	}
        	break;
        	
        	case "LOST":
        	@if(typeStr!="bot") {
        		alert("You've lost the round!");
        	}
        	break;
        	
        	case "ERROR":
        		alert("ERROR\n"+msgArray[1]);
        	break;
        	
        	case "ROUND":
        		$('#round').text(msgArray[1]);
        	break;
        }
    }
    
    function bindBet() {
    	$('.bet').on("click", function(e) {
    		var g = $(this).attr('id');
    		var b = $('#bet');
    		if (g == 'RAISE' || g == 'BET') {
    			b.empty();
    			b.append('<input type="text" id="valueOfBet" placeholder="Value of your bet"><button id="setBet">SET</button>');
    			$('#setBet').on("click", function(e) {
    				v = $('#valueOfBet').val();
    				msg = "SETBET|"+g+"|"+v;
    				chatSocket.send(msg);
					var el = $('<div class="sent"></div>')
	        		$(el).text(msg)
	        		$('#console').append(el)
	        		$('#bet').hide()
    			});
    		}
    		else {
    		    msg = "SETBET|"+g;
    			chatSocket.send(msg);
				var el = $('<div class="sent"></div>')
	        	$(el).text(msg)
	        	$('#console').append(el)
	        	$('#bet').hide()
	        }
    	});
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
