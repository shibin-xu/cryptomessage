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

if (isDevMode) enableLiveReload();

const createWindow = async () => {
  
  spawnCore();
  connectToZmq();
  util.log("window starting")
  // Create the browser window.
  mainWindow = new BrowserWindow({
    width: 800,
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

function makeJson(a,b,c) {
  let obj = { relayType:a, relayContent:b, time:c }
  let result = JSON.stringify(obj);
  return result;
}
function readJson(raw) {
  let cmd = "hm"
  let payload = "paylo"
  let timestamp = 1
  let rawstring = raw.toString('ascii')
  let obj = JSON.parse(rawstring);
  cmd = obj["relayType"]
  payload = obj["relayContent"]
  timestamp = obj["time"]
  return [cmd,payload,timestamp]
}
function connectToZmq() {
  util.log("zmq starting")
  zmqClient = zeromq.socket('req')
  zmqClient.connect(`tcp://127.0.0.1:5555`);   
  ipcMain.on('ping', (event, val) => {
    event.sender.send('pong', Math.random())
  });
  ipcMain.on('connect', () => {
    let blob = makeJson('CRYPTOOpenConnectionToServer')
    zmqClient.send(blob)
  });

  ipcMain.on('disconnect', () => {
    let blob = makeJson('CRYPTODisconnectFromServer')
    zmqClient.send(blob)
  });

  ipcMain.on('login-new', (event, val) => {
    let blob = makeJson('CRYPTOLoginNewAccount', val)
    zmqClient.send(blob)
  });
  
  ipcMain.on('login-existing', (event, val) => {
    let blob = makeJson('CRYPTOLoginExistingAccount', val)
    zmqClient.send(blob)
  });
  
  ipcMain.on('set-file-path', (event, val) => {
    let blob = makeJson('CRYPTOSetFilePathOfKey', val)
    zmqClient.send(blob)
  });
  
  ipcMain.on('add-user', (event, val) => {
    let blob = makeJson('CRYPTOAddUser', val)
    zmqClient.send(blob)
  });
  ipcMain.on('rem-user', (event, val) => {
    let blob = makeJson('CRYPTORemoveUser', val)
    zmqClient.send(blob)
  });
  
  ipcMain.on('get-all-user', () => {
    let blob = makeJson('CRYPTOGetAllUser')
    zmqClient.send(blob)
  });
  
  ipcMain.on('get-archive-user', (event, val) => {
    let blob = makeJson('CRYPTOGetUserArchive', val)
    zmqClient.send(blob)
  });
  ipcMain.on('send', (event, val) => {
    let blob = makeJson('CRYPTOSend',val)
    zmqClient.send(blob)
  });
  ipcMain.on('get', () => {
    let blob = makeJson('CRYPTOFakeReceive')
    zmqClient.send(blob)
  });


  zmqClient.on(`message`, function (raw) {         
      let json = readJson(raw);
      let cmd = json[0]
      let payload = json[1]
      let timestamp = json[2]
      if(cmd == 'UIResultForConnect')
      {
        mainWindow.webContents.send('elecConfirmConnect', payload);
      }
      else if(cmd == 'UIResultForDisconnect')
      {
        mainWindow.webContents.send('elecConfirmDisconnect', payload);
      }
      else if(cmd == 'UIResultForMessageSend')
      {
        mainWindow.webContents.send('elecConfirmSend', payload);
      }
      else if(cmd == 'UIMessageReceive')
      {
        mainWindow.webContents.send('elecRecieve', payload);
      }
      else
      {
        mainWindow.webContents.send('elecRaw', raw);
      }
  });     
}
