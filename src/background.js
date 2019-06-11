import { app, BrowserWindow } from 'electron';
import { ipcMain } from 'electron';
import installExtension, { VUEJS_DEVTOOLS } from 'electron-devtools-installer';
import { enableLiveReload } from 'electron-compile';
import { spawn } from 'child_process'
import zeromq from 'zeromq'
import path from 'path'
import util from 'util'

// Keep a global reference of the window object, if you don't, the window will
// be closed automatically when the JavaScript object is garbage collected.
let mainWindow;
let core;
let zmqClient;

const isDevMode = process.execPath.match(/[\\/]electron/);

let d = new Date();
if (isDevMode) enableLiveReload();

const createWindow = async () => {
  
  spawnCore();
  connectToZmq();
  util.log("window starting")
  // Create the browser window.
  mainWindow = new BrowserWindow({
    width: 900,
    height: 770,
  });

  // Load the HTML file directly from the webpack dev server if
  // hot reload is enabled, otherwise load the local file.
  const mainURL = process.env.HOT
    ? `http://localhost:${process.env.PORT}/main.html`
    : 'file://' + path.join(__dirname, 'main.html')

  mainWindow.loadURL(mainURL)

  // Open the DevTools.
  if (isDevMode) {
    
    //mainWindow.webContents.openDevTools();
    await installExtension(VUEJS_DEVTOOLS);
  }

  // Emitted when the window is closed.
  mainWindow.on('closed', () => {
    // Dereference the window object, usually you would store windows
    // in an array if your app supports multi windows, this is the time
    // when you should delete the corresponding element.
    mainWindow = null;
  });
};

// This method will be called when Electron has finished
// initialization and is ready to create browser windows.
// Some APIs can only be used after this event occurs.
app.on('ready', createWindow);

// Quit when all windows are closed.
app.on('window-all-closed', () => {
  // On OS X it is common for applications and their menu bar
  // to stay active until the user quits explicitly with Cmd + Q
  if (process.platform !== 'darwin') {
    app.quit();
  }
});

app.on('activate', () => {
  // On OS X it's common to re-create a window in the app when the
  // dock icon is clicked and there are no other windows open.
  if (mainWindow === null) {
    createWindow();
  }
});



function spawnCore() {
  util.log("java starting")
  core = spawn('java',  ['-cp','ipc/target/ipc-1.0-SNAPSHOT.jar','org.cloudguard.ipc.Main']);

  util.log(core.pid)
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

function makeJson(a,b,c,t) {
  let obj = { relayType:a, primary:b, secondary:c,  time:t }
  let result = JSON.stringify(obj);
  return result;
}
function readJson(raw) {
  let rawstring = raw.toString('ascii')
  let obj = JSON.parse(rawstring);
  let cmd = obj["relayType"];
  let primary = obj["primary"];
  let secondary = obj["secondary"];
  let timestamp = obj["time"];
  return [cmd,primary,secondary,timestamp]
}
function connectToZmq() {
  util.log("zmq starting")
  zmqClient = zeromq.socket('req')
  zmqClient.connect(`tcp://127.0.0.1:5555`);  
  ipcMain.on('doDisconnect', () => {
    let blob = makeJson('CRYPTODisconnectFromServer', "-", "-", d.getTime())
    zmqClient.send(blob)
  });

  ipcMain.on('doFilePath', (event, valone, valtwo) => {
    let blob = makeJson('CRYPTOConnectWithKeys', valone, valtwo, d.getTime())
    zmqClient.send(blob)
  });
  
  ipcMain.on('doAddContactString', (event, valone, valtwo) => {
    let blob = makeJson('CRYPTOAddContactString', valone, valtwo, d.getTime())
    zmqClient.send(blob)
  });
  ipcMain.on('doAddContactFile', (event, valone, valtwo) => {
    let blob = makeJson('CRYPTOAddContactFile', valone, valtwo, d.getTime())
    zmqClient.send(blob)
  });
  ipcMain.on('doRemoveContact', (event, valone, valtwo) => {
    let blob = makeJson('CRYPTORemoveContact', valone, valtwo, d.getTime())
    zmqClient.send(blob)
  });
  
  ipcMain.on('doRenameContact', (event, valone, valtwo) => {
    let blob = makeJson('CRYPTORenameContact', valone, valtwo, d.getTime())
    zmqClient.send(blob)
  });
  ipcMain.on('doTalkToContact', (event, valone, valtwo) => {
    let blob = makeJson('CRYPTOGetContactArchive', valone, valtwo, d.getTime())
    zmqClient.send(blob)
  });
  ipcMain.on('doSend', (event, valone, valtwo) => {
    let blob = makeJson('CRYPTOSend', valone, valtwo, d.getTime())
    zmqClient.send(blob)
  });
  ipcMain.on('doSave', (event, valone, valtwo) => {
    let blob = makeJson('CRYPTOSave', valone, "", d.getTime())
    zmqClient.send(blob)
  });
  ipcMain.on('doLoad', (event, valone, valtwo) => {
    let blob = makeJson('CRYPTOLoad', valone, "", d.getTime())
    zmqClient.send(blob)
  });
  ipcMain.on('doFakeSpam', () => {
    let blob = makeJson('CRYPTOFakeSpam',"","This is spam", d.getTime())
    zmqClient.send(blob)
  });
  ipcMain.on('doFakeReal', (event, pubkey) => {
    let blob = makeJson('CRYPTOFakeReceive',pubkey,"This is uncertain", d.getTime())
    zmqClient.send(blob)
  });

  ipcMain.on('doTick', (event, pubkey) => {
    let blob = makeJson('CRYPTOTick',pubkey,"", d.getTime())
    zmqClient.send(blob)
  });

  zmqClient.on(`message`, function (a,b,c,d,e) {     
    
    for (var i=0; i < arguments.length; i++) {
      let json = readJson(arguments[i]);
      if(json)
      {
        let cmd = json[0]
        let primary = json[1]
        let secondary = json[2]
        let timestamp = json[3]
        
        mainWindow.webContents.send(cmd, primary, secondary, timestamp);
      }
    }
  });     
}
