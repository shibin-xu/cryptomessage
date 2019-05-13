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
    if (spawnedChild) {
        spawnedChild.stdin.pause();
        spawnedChild.kill();
    }
    app.quit();
});

app.on('ready', () => {
    spawnCore();
    connectToZmq();
    ui = new BrowserWindow({
        height: 1024,
        width: 1075,
        resizable: true
    });
    ui.loadURL('file://' + __dirname + '/frontend/index.html');

    ui.on('closed', () => {
        app.quit();
    });
});


function spawnCore(hookIn, hookOut, hookErr) {
    core = spawn('java',  ['-cp','ipc/target/ipc-1.0-SNAPSHOT.jar','org.cloudguard.ipc.hwserver']);
}


function connectToZmq() {
    zmqClient = zeromq.socket('req')

    zmqClient.bindSync(`tcp://127.0.0.1:5555`);   
 
    zmqClient.on(`connect`, function () {    
        ui.webContents.send('connect-response', payload);     
        console.log(`Connected`); 
    });

    zmqClient.on(`disconnect`, function () {       
        ui.webContents.send('disconnect-response', payload);  
        console.log(`Disconnected`); 
    });

    zmqClient.on(`message`, function (payload) {         
        ui.webContents.send('rx-response', payload);
        console.log(`Received '${payload}'. `);
    });
    zmqClient.on(`tx`, function (payload) { 
        ui.webContents.send('tx-response', payload);        
        console.log(`Sent '${payload}'. `);
    });

    
    zmqClient.send(`greeting`);        
    console.log('Sent greeting')
}