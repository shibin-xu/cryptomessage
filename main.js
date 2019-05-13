const {app, BrowserWindow} = require('electron');
const ipc = require('electron').ipcMain;
const zeromq = require("zeromq");
const fs = require('fs');
const { spawn } = require('child_process');
const util = require("util");


let ui;
let core;
let zmqClient;

app.on('window-all-closed', () => {
    if (core) {
        core.stdin.pause();
        core.kill();
    }
    app.quit();
});

app.on('ready', () => {
    spawnCore();
    connectToZmq();
    ui = new BrowserWindow({
        height: 800,
        width: 800,
        resizable: true
    });
    ui.loadURL('file://' + __dirname + '/frontend/index.html');

    ui.on('closed', () => {
        app.quit();
    });
});


function spawnCore(hookIn, hookOut, hookErr) {
    core = spawn('java',  ['-cp','ipc/target/ipc-1.0-SNAPSHOT.jar','org.cloudguard.ipc.hwserver']);

    util.log("x")
    core.stdout.on('data',function(chunk){
        var textChunk = chunk.toString('utf8');
        util.log(textChunk);
    });
    core.stderr.on('data',function(chunk){
        var textChunk = chunk.toString('utf8');   
        util.log(textChunk);
    });
    core.on('close', (code, signal) => {
        console.log(`child error: ${code}, ${signal}`);
    });
    core.on('error', (err) => console.error(err));
}


function connectToZmq() {
    zmqClient = zeromq.socket('req')

       
    zmqClient.connect(`tcp://127.0.0.1:5555`);   

    zmqClient.on(`message`, function (payload) {         
        ui.webContents.send('rx-response', payload);
        console.log(`Received '${payload}'. `);
    });
    
    zmqClient.send(`greeting`);        
    console.log('Sent greeting')
    
    var array = getInt64Bytes(1023);    
    array[0] = 0
    array[1] = 1
    zmqClient.send(array);        
    console.log('Sent array')
}

function makeJson(a,b) {
    obj = { cmd:a, payload:b }
    result = JSON.stringify(obj);
    return result;
}
function readJson(raw) {
    cmd = "hm"
    payload = "paylo"
    rawstring = raw.toString('ascii')
    console.log(rawstring)
    obj = JSON.parse(rawstring);
    cmd = obj[0]
    payload = obj[1]
    return [cmd,payload]
}
function connectToZmq() {
    zmqClient = zeromq.socket('req')
    zmqClient.connect(`tcp://127.0.0.1:5555`);   
 
    ipc.on('connect', () => {
        blob = makeJson('hwConnect')
        zmqClient.send(blob)
    });

    ipc.on('disconnect', () => {
        blob = makeJson('hwDisconnect')
        zmqClient.send(blob)
    });

    ipc.on('send', (event, val) => {
        blob = makeJson('hwSend',val)
        zmqClient.send(blob)
    });


    zmqClient.on(`message`, function (raw) {         
        json = readJson(raw);
        cmd = json[0]
        payload = json[1]
        if(cmd == 'jsConnected')
        {
            ui.webContents.send('elec-connect-response', payload);
        }
        else if(cmd == 'jsDisconnected')
        {
            ui.webContents.send('elec-disconnect-response', payload);
        }
        else if(cmd == 'jsMsg')
        {
            ui.webContents.send('elec-message-response', payload);
        }
        else
        {
            ui.webContents.send('elec-raw-response', raw);
        }
    });     
}
