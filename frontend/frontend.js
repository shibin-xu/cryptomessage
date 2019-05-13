const ipc = require('electron').ipcRenderer;
let output;
let errorHeader;

function sendData(dataName, value) {
    hideError();
    if (value) {
        ipc.send(dataName, value);
    } else {
        ipc.send(dataName);
    }
}
function addLi(text) {
    const liElem = document.createElement('li');
    liElem.textContent = text;
    output.appendChild(liElem);
}

function writeError(msg) {
    errorHeader.textContent = msg;
    if (msg === '') {
        hideError();
    } else {
        showError();
    }
}

function showError() {
    errorHeader.classList.remove('hidden');
}

function hideError() {
    errorHeader.classList.add('hidden');
}


let elementConnect;
let elementDisconnect;
let elementTextEntry;

document.addEventListener('DOMContentLoaded', function() {

    output = document.getElementById('output');
    errorHeader = document.getElementById('error');

    elementConnect = document.getElementById('connect');
    elementDisconnect = document.getElementById('disconnect');
    elementSend = document.getElementById('send');
    elementTextEntry = document.getElementById('textentry');
    elementTextEntry.value = "Greetings~";

    addConnect();
    addTalk();
});

function addConnect() {
    elementConnect.addEventListener('click', () => sendData('connect') );
    ipc.on('elec-connect-response', (_ ,data) => addLi(data) );
    elementDisconnect.addEventListener('click', () => sendData('disconnect') );
    ipc.on('elec-disconnect-response', (_ ,data) => addLi(data) );
}


function addTalk() {
    ipc.on('elec-message-response', (_ ,data) => addLi(data) );
    ipc.on('elec-raw-response', (_ ,data) => addLi(data) );
    
    elementSend.addEventListener('click', () => sendData('send', elementTextEntry.value) );    
}
