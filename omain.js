const {app, BrowserWindow} = require('electron')
const ipc = require('electron').ipcMain
const installExtension, { VUEJS_DEVTOOLS } = require('electron-devtools-installer')
const zeromq = require("zeromq")
const fs = require('fs')
const { spawn } = require('child_process')
const util = require('util')
const path = require('path')


let mainWindow;
let core;
let zmqClient;
const isDevMode = process.execPath.match(/[\\/]electron/);

if (isDevMode) enableLiveReload();

app.on('window-all-closed', () => {
    if (core) {
        core.stdin.pause();
        core.kill();
    }
    // On OS X it is common for applications and their menu bar
    // to stay active until the user quits explicitly with Cmd + Q
    if (process.platform !== 'darwin') {
        app.quit();
    }
});

app.on('ready', () => {
    spawnCore();
    connectToZmq();
    mainWindow = new BrowserWindow({
        height: 800,
        width: 600,
        resizable: true
    });
    // Load the HTML file directly from the webpack dev server if
    // hot reload is enabled, otherwise load the local file.
    const mainURL = process.env.HOT
      ? `http://localhost:${process.env.PORT}/main.html`
      : 'file://' + path.join(__dirname, 'main.html')
  
    mainWindow.loadURL(mainURL)
  
    // Open the DevTools.
    if (isDevMode) {
      
      mainWindow.webContents.openDevTools();
      await installExtension(VUEJS_DEVTOOLS);
    }
  
    // Emitted when the window is closed.
    mainWindow.on('closed', () => {
      // Dereference the window object, usually you would store windows
      // in an array if your app supports multi windows, this is the time
      // when you should delete the corresponding element.
      mainWindow = null;
    });
});

app.on('activate', () => {
    // On OS X it's common to re-create a window in the app when the
    // dock icon is clicked and there are no other windows open.
    if (mainWindow === null) {
      createWindow();
    }
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
