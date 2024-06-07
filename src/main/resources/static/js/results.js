var stompClient = null;
var socket = new SockJS('/ws');
stompClient = Stomp.over(socket);
stompClient.connect({}, function(frame) {
    console.log(frame);
    stompClient.subscribe('/all/messages', function(result) {
        showMessage1(JSON.parse(result.body));
    });
    stompClient.subscribe('/all/slot2', function(result2) {
        showMessage2(JSON.parse(result2.body));
        });
        //fetchLastMessage();
});

// Function to display message in modal dialog
 // Function to display message in first box
function showMessage1(message) {
    var messageBox1 = document.getElementById('messageBox1');
    messageBox1.innerHTML = '<p>' + message.text + '</p>';
}

// Function to display message in second box
function showMessage2(message) {
    var messageBox2 = document.getElementById('messageBox2');
    messageBox2.innerHTML = '<p>' + message.text + '</p>';
}

function fetchLastMessage1() {
    fetch('http://arrowenterprise.co.in/api/v1/teer/last-message/slot1')
        .then(response => response.json())
        .then(message => {
            showMessage1(message);
        });
}

function fetchLastMessage2() {
    fetch('http://arrowenterprise.co.in/api/v1/teer/last-message/slot2')
        .then(response => response.json())
        .then(message => {
            showMessage2(message);
        });
}

window.onload = fetchLastMessage1();
window.onload = fetchLastMessage2();
