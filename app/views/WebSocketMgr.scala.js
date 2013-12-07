@(id:Int)

$(function() {
    var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket
 	var chatSocket = new WS("@routes.Application.webSocketMgr(id).webSocketURL(request)")

    var sendMessage = function() {
        chatSocket.send(JSON.stringify($("#talk").val()))
        $("#talk").val('')
    }

    var receiveEvent = function(event) {
        // Create the message element
        var el = $('<div class="message"></div>')
        $(el).text(event.data)
        $('#console').append(el)
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
