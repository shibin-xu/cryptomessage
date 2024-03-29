<template>
  <v-app id="crypxt">
    <v-navigation-drawer
      class="blue darken-3"
      width="240"
      style="min-height:97vh; max-height:97vh"
      permanent
      dark
      app
    >
      <v-list>
        <v-layout row align-center>
          <v-flex xs8>
            <v-img class="mb-3" contain height="128" src="assets\xtmsg.svg"></v-img>
          </v-flex>
        </v-layout>
        <v-list-tile>
          <v-flex xs4>
            <v-btn @click="connect_action" :color="connect_color" dark>
              {{ connect_text }}
              <v-icon dark right>{{ connect_icon }}</v-icon>
            </v-btn>
            <v-btn :disabled="isConnected == false" @click="toggle_tick" :color="tick_color" dark>
              {{ tick_text }}
              <v-icon dark right>{{ tick_icon }}</v-icon>
            </v-btn>
            <v-content></v-content>
          </v-flex>
        </v-list-tile>
        <Contact
          @talk="do_talkToContact"
          @change="do_changeToContact"
          @open="contact_action"
          :selfKey="selfContactShort"
          :contactObjects="contactObjects"
        />
        <v-list-tile @click="saveload_action">
          <v-list-tile-action>
            <v-icon>save</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>Save / Load</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
        <v-list-tile @click="settings_action">
          <v-list-tile-action>
            <v-icon>{{ settings_icon }}</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>{{ settings }}</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
        <v-list-tile @click="fakespam_action">
          <v-list-tile-action>
            <v-icon>{{ widgets }}</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>Fake From Spam</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
        <v-list-tile @click="fakecontact_action">
          <v-list-tile-action>
            <v-icon>{{ wallpaper }}</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>Fake From Contact</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
      </v-list>
    </v-navigation-drawer>
    <v-content style="min-height:97vh; max-height:97vh">
      <v-container fluid fill-height class="grey lighten-4">
        <v-layout align-center justify-top column>
          <v-flex>
            <ConnectDialog :shouldRender="showConnect" @keyfile="do_keyfile"/>
          </v-flex>
          <v-flex>
            <SaveLoadDialog :shouldRender="showSaveLoad" @save="do_save" @load="do_load"/>
          </v-flex>
          <v-flex>
            <AddContactDialog
              :shouldRender="showAdd"
              @add_by_string="do_addContactString"
              @add_by_file="do_addContactFile"
            />
          </v-flex>
          <v-flex>
            <ChangeContactDialog
              :shouldRender="showChange"
              :alias="chatAlias"
              :pubkeyString="chatContactID"
              :shortKeyString="chatContactShort"
              @delete_by_key="do_deleteContact"
              @rename_by_key="do_renameContact"
            />
          </v-flex>
          <v-flex grow>
            <ChatArea
              @transmit="do_send"
              :contactName="chatAlias"
              :contactID="chatContactID"
              :speechObjects="speechObjects"
            />
          </v-flex>
          <v-spacer></v-spacer>
          <v-flex grow>
            <Console :shouldRender="isConsoleEnabled" :consoleLines="consoleLines"/>
          </v-flex>
        </v-layout>
      </v-container>
    </v-content>
  </v-app>
</template>

<script>
import ConnectDialog from "./components/ConnectDialog.vue";
import SaveLoadDialog from "./components/SaveLoadDialog.vue";
import AddContactDialog from "./components/AddContactDialog.vue";
import ChangeContactDialog from "./components/ChangeContactDialog.vue";
import Contact from "./components/Contact.vue";
import ChatArea from "./components/ChatArea.vue";
import Console from "./components/Console.vue";
export default {
  components: {
    ConnectDialog,
    SaveLoadDialog,
    AddContactDialog,
    ChangeContactDialog,
    Contact,
    ChatArea,
    Console
  },
  data: () => ({
    drawer: null,
    ticking: null,
    title: "CryptoXT",
    connect_text: "No connection",
    tick_text: "Tick Disabled",
    connect_icon: "signal_wifi_off",
    tick_icon: "sync_disabled",
    isConnected: false,
    isConsoleEnabled: false,
    showConnect: true,
    showSaveLoad: false,
    showAdd: false,
    showChange: false,
    settings: "Console",
    settings_icon: "settings_remote",
    contacts: "Contacts",
    chatAlias: "",
    chatContactID: "0",
    chatContactShort: "0",
    selfContactID: "--",
    selfContactShort: "--",
    connect_color:"grey",
    tick_color:"grey",
    consoleLines: [{ icon: "launch", text: "Start" }],
    contactCollection: new Map(),
    speechCollection: new Map(),
    contactObjects: [],
    speechObjects: []
  }),
  created() {},
  beforeDestroy() {
    clearInterval(this.ticking);
  },
  mounted() {
    this.$electron.ipcRenderer.on(
      "UIResultForConnect",
      (evt, primary, secondary, timestamp) => {
        this.rx("", primary, secondary);
      }
    );
    this.$electron.ipcRenderer.on(
      "UIResultForDisconnect",
      (evt, primary, secondary, timestamp) => {
        this.rx("", primary, secondary);
      }
    );
    this.$electron.ipcRenderer.on(
      "UIResultForSpeechSend",
      (evt, primary, secondary, timestamp) => {
        this.rx("sent", primary, secondary);
      }
    );
    this.$electron.ipcRenderer.on(
      "UIResultFoundContact",
      (evt, primary, secondary, timestamp) => {
        this.contactCollection.set(primary, {
          contactID: primary,
          alias: secondary,
          icon: "face"
        });
        this.make_good_contacts();
        this.rx("contact", primary, secondary);
      }
    );
    this.$electron.ipcRenderer.on(
      "UISpeechUpdate",
      (evt, primary, secondary, timestamp) => {
        this.rx("", primary, secondary);
        let speech_blob = primary;
        let archive_key = secondary;
        if(speech_blob.length > 1) {
          try {
            let speech = JSON.parse(speech_blob);
            let wasRcv = speech["senderKey"] == archive_key;
            let totalIdentifier = speech["identifier"];
            let shortIdentifier = totalIdentifier.substr(0,4);
            var d = new Date(speech["time"]);
            let timeStamp = d.toLocaleString();
            let timeNumber = d.getTime();
            this.speechCollection.set(totalIdentifier, {
              icon: "sentiment_very_satisfied",
              totalIdentifier: totalIdentifier,
              shortIdentifier: shortIdentifier,
              timeNumber: timeNumber,
              timeStamp: timeStamp,
              text: speech["content"],
              contactID: archive_key,
              isPreviousVerified: speech["previousVerified"],
              isSignatureVerified: speech["signatureVerified"],
              isSent: !wasRcv
            });
          } catch (err) {
            console.log("speech err "+err);
          }
        }
        this.make_good_speech();
      }
    );
    this.$electron.ipcRenderer.on(
      "UIPublicKey",
      (evt, primary, secondary, timestamp) => {
        this.selfContactID = primary;
        this.selfContactShort = primary.substr(44, 12);
        this.connect_icon = "signal_cellular_4_bar";
        this.connect_color = "green";
        this.connect_text = "Connected";
        this.isConnected = true;
        this.doTick();
        this.rx("", primary, secondary);
      }
    );

    this.$electron.ipcRenderer.on(
      "UISecurity",
      (evt, primary, secondary, timestamp) => {
        this.rx("", primary, secondary);
      }
    );
  },
  methods: {
    doTick() {
      this.tick_text = "Tick Enabled";
      this.tick_color = "green";
      this.tick_icon = "sync";
      this.ticking = setInterval(() => {
        if (this.isConnected) {
          this.send("doTick", this.chatContactID);
        }
      }, 5000);
    },
    toggle_tick() {
      if (this.ticking == null) {
        this.doTick();
      } else {
        clearInterval(this.ticking);
        this.tick_text = "Tick Disabled";
        this.tick_color = "grey";
        this.tick_icon = "sync_disabled";
        this.ticking = null;
      }
    },
    rx(dataName, primary, secondary) {
      if(this.isConsoleEnabled) {
        this.consoleLines.push({
          text: dataName + ":  " + primary + "," + secondary,
          icon: "sync"
        });
      }
    },
    send(dataName, primary, secondary) {
      this.consoleLines.push({
        text: dataName + ":  " + primary + "," + secondary,
        icon: "laptop"
      });
      this.$electron.ipcRenderer.send(dataName, primary, secondary);
    },
    make_good_contacts() {
      let arr = Array.from(this.contactCollection);
      this.contactObjects.length = 0;
      for (var elem in arr) {
        let key = arr[elem][0];

        let obj = this.contactCollection.get(key);
        this.contactObjects.push(obj);
      }
    },
    make_good_speech() {
      let arr = Array.from(this.speechCollection);
      this.speechObjects.length = 0;
      for (var elem in arr) {
        let key = arr[elem][0];
        let obj = this.speechCollection.get(key);
        if (obj.contactID == this.chatContactID) {
          this.speechObjects.push(obj);
        }
      }
      this.speechObjects.sort(function(a, b) { return a.timeNumber - b.timeNumber; })
    },
    connect_action() {
      this.showConnect = true;
      return;
    },
    settings_action() {
      this.isConsoleEnabled = !this.isConsoleEnabled;
    },
    saveload_action() {
      this.showSaveLoad = true;
    },
    contact_action() {
      console.log("contact_action");
      this.showAdd = true;
      return;
    },
    fakespam_action() {
      this.send("doFakeSpam", this.chatContactID);
      return;
    },
    fakecontact_action() {
      this.send("doFakeReal", this.chatContactID);
      return;
    },
    do_keyfile(pubfile, prifile) {
      if (pubfile.length > 0) {
        this.send("doFilePath", pubfile, prifile);
      }
      this.showConnect = false;
      return;
    },
    do_addContactString(pubkey, alias) {
      this.showAdd = false;
      if (pubkey.length > 0) {
        this.send("doAddContactString", pubkey, alias);
      }
    },
    do_addContactFile(pubfile, alias) {
      this.showAdd = false;
      if (pubfile.length > 0) {
        this.send("doAddContactFile", pubfile, alias);
      }
    },
    do_deleteContact(pubkey, alias) {
      if(pubkey.length > 0) {
        this.send("doRemoveContact", pubkey, alias);
      }
      this.showChange = false;
    },
    do_renameContact(pubkey, alias) {
      if(pubkey.length > 0) {
        
        console.log("do_renameContact");
        this.send("doRenameContact", pubkey, alias);
      }
      this.showChange = false;
    },
    
    do_changeToContact(nextAlias, nextID) {
      this.do_talkToContact(nextAlias, nextID);
      console.log("do_changeToContact");
      this.showChange = true;
    },
    do_talkToContact(nextAlias, nextID) {
      this.chatAlias = nextAlias;
      if (this.contactCollection.has(this.chatContactID)) {
        let prev = this.contactCollection.get(this.chatContactID);
        prev.icon = "face";
      }
      this.contactCollection[this.chatContactID];
      this.chatContactID = nextID;
      this.chatContactShort = nextID.substr(44, 12);
      if (this.contactCollection.has(this.chatContactID)) {
        let next = this.contactCollection.get(this.chatContactID);
        next.icon = "chat_bubble";
      }
      this.speechObjects.length = 0;
      this.make_good_contacts();
      this.send("doTalkToContact", nextID, nextAlias);
    },
    do_send(words, secondary) {
      this.send("doSend", words, secondary);
    },
    do_save(savefile) {
      if (savefile.length > 0) {
        this.send("doSave", savefile);
      }
      this.showSaveLoad = false;
    },
    do_load(loadfile) {
      if (loadfile.length > 0) {
        this.send("doLoad", loadfile);
      }
      this.showSaveLoad = false;
    }
  }
};
</script>
