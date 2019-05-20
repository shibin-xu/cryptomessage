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
    height: 600,
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
  core = spawn('java',  ['-cp','ipc/target/ipc-1.0-SNAPSHOT.jar','org.cloudguard.ipc.hwserver']);

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
  
  util.log("java okay")
}

function makeJson(a,b) {
  let obj = { cmd:a, payload:b }
  let result = JSON.stringify(obj);
  return result;
}
function readJson(raw) {
  let cmd = "hm"
  let payload = "paylo"
  let rawstring = raw.toString('ascii')
  console.log(rawstring)
  let obj = JSON.parse(rawstring);
  cmd = obj[0]
  payload = obj[1]
  return [cmd,payload]
}
function connectToZmq() {
  util.log("zmq starting")
  zmqClient = zeromq.socket('req')
  zmqClient.connect(`tcp://127.0.0.1:5555`);   
  ipcMain.on('ping', (event, val) => {
    event.sender.send('pong', Math.random())
  });
  ipcMain.on('connect', () => {
    let blob = makeJson('hwConnect')
    zmqClient.send(blob)
  });

  ipcMain.on('disconnect', () => {
    let blob = makeJson('hwDisconnect')
    zmqClient.send(blob)
  });

  ipcMain.on('send', (event, val) => {
    let blob = makeJson('hwSend',val)
    zmqClient.send(blob)
  });

  util.log("zmq okay")

  zmqClient.on(`message`, function (raw) {         
      let json = readJson(raw);
      let cmd = json[0]
      let payload = json[1]
      if(cmd == 'jsConnected')
      {
        mainWindow.webContents.send('elec-connect-response', payload);
      }
      else if(cmd == 'jsDisconnected')
      {
        mainWindow.webContents.send('elec-disconnect-response', payload);
      }
      else if(cmd == 'jsMsg')
      {
        mainWindow.webContents.send('elec-message-response', payload);
      }
      else
      {
        mainWindow.webContents.send('elec-raw-response', raw);
      }
  });     

  
  zmqClient.send(makeJson('hwConnect'))
}
