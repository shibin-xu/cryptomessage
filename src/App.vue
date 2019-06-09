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
            <v-btn @click="connect_action" color="indigo" dark>
              {{ connect_text }}
              <v-icon dark right>{{ connect_icon }}</v-icon>
            </v-btn>
            <v-btn :disabled="isConnected == false" @click="toggle_tick" color="indigo" dark>
              {{ tick_text }}
              <v-icon dark right>{{ tick_icon }}</v-icon>
            </v-btn>
            <v-content></v-content>
          </v-flex>
        </v-list-tile>
        <Contact
          @talk="do_talkToContact"
          @open="contact_action"
          :selfKey="selfContactShort"
          :contactObjects="contactObjects"
        />
        <v-list-tile @click="settings_action">
          <v-list-tile-action>
            <v-icon>{{ settings_icon }}</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>{{ settings }}</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
        <v-list-tile @click="fakeget_action">
          <v-list-tile-action>
            <v-icon>{{ widgets }}</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>Fake Get</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
        <v-list-tile @click="fakefill_action">
          <v-list-tile-action>
            <v-icon>{{ wallpaper }}</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>Fake Fill</v-list-tile-title>
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
            <AddContactDialog
              :shouldRender="showContact"
              @add_by_string="do_addContactString"
              @add_by_file="do_addContactFile"
            />
          </v-flex>
          <v-flex grow>
            <ChatArea
              @transmit="do_send"
              :contactName="chatAlias"
              :contactID="chatContactID"
              :nextIdentifier="chatNextIdentifier"
              :speechObjects="speechObjects"
            />
          </v-flex>
          <v-spacer></v-spacer>
          <v-flex grow>
            <Console :consoleLines="consoleLines"/>
          </v-flex>
        </v-layout>
      </v-container>
    </v-content>
  </v-app>
</template>

<script>
import ConnectDialog from "./components/ConnectDialog.vue";
import AddContactDialog from "./components/AddContactDialog.vue";
import Contact from "./components/Contact.vue";
import ChatArea from "./components/ChatArea.vue";
import Console from "./components/Console.vue";
export default {
  components: {
    ConnectDialog,
    AddContactDialog,
    Contact,
    ChatArea,
    Console
  },
  data: () => ({
    drawer: null,
    ticking: null,
    title: "CryptoXT",
    connect_text: "No connection",
    tick_text: "Enable Tick",
    connect_icon: "signal_wifi_off",
    tick_icon: "sync_disabled",
    isConnected: false,
    showConnect: true,
    showContact: false,
    settings: "Settings",
    settings_icon: "settings",
    contacts: "Contacts",
    chatAlias: "-",
    chatContactID: "0",
    chatNextIdentifier: 1,
    selfContactID: "--",
    selfContactShort: "--",
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
      "UISpeechNextIdentifier",
      (evt, primary, secondary, timestamp) => {
        this.rx("nextid", primary, secondary);

        let archive_key = secondary;
        if (archive_key == this.chatContactID) {
          this.chatNextIdentifier = parseInt(primary);
        }
      }
    );
    this.$electron.ipcRenderer.on(
      "UIResultForContact",
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
        try {
          let speech = JSON.parse(speech_blob);
          let wasRcv = speech["senderKey"] == archive_key;
          let totalKey = speech["identifier"] + "," + speech["senderKey"];
          this.speechCollection.set(totalKey, {
            icon: "sentiment_very_satisfied",
            identifier: speech["identifier"],
            text: speech["content"],
            contactID: archive_key,
            isConfirmed: speech["signatureVerified"],
            isSent: !wasRcv
          });
          this.make_good_speech();
        } catch (err) {
          return;
        }
      }
    );
    this.$electron.ipcRenderer.on(
      "UIPublicKey",
      (evt, primary, secondary, timestamp) => {
        this.selfContactID = primary;
        this.selfContactShort = primary.substr(48, 12);
        this.connect_icon = "signal_cellular_4_bar";
        this.connect_text = "Connected";
        this.isConnected = true;
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
      this.tick_text = "Disable Tick";
      this.tick_icon = "sync";
      this.ticking = setInterval(() => {
        if (this.isConnected) {
          this.send("doTick", this.chatContactID);
        }
      }, 3000);
    },
    toggle_tick() {
      if (this.ticking == null) {
        this.doTick();
      } else {
        clearInterval(this.ticking);
        this.tick_text = "Enable Tick";
        this.tick_icon = "sync_disabled";
        this.ticking = null;
      }
    },
    rx(dataName, primary, secondary) {
      this.consoleLines.push({
        text: dataName + ":  " + primary + "," + secondary,
        icon: "sync"
      });
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
    },
    connect_action() {
      this.showConnect = true;
      return;
    },
    settings_action() {
      //My Public Key: {{selfContactID}}
    },
    contact_action() {
      this.showContact = true;
      return;
    },
    fakeget_action() {
      this.send("doFakeGet", this.chatContactID);
      return;
    },
    fakefill_action() {
      this.send("doFakeFill");
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
      this.showContact = false;
      if (pubkey.length > 0) {
        this.send("doAddContactString", pubkey, alias);
      }
    },
    do_addContactFile(pubfile, alias) {
      this.showContact = false;
      if (pubfile.length > 0) {
        this.send("doAddContactFile", pubfile, alias);
      }
    },
    do_removeContact(pubkey, alias) {
      this.send("doRemoveContact", pubkey, alias);
    },
    do_renameContact(pubkey, alias) {
      this.send("doRenameContact", pubkey, alias);
    },
    do_talkToContact(nextAlias, nextID) {
      this.chatAlias = nextAlias;
      if (this.contactCollection.has(this.chatContactID)) {
        let prev = this.contactCollection.get(this.chatContactID);
        prev.icon = "face";
      }
      this.contactCollection[this.chatContactID];
      this.chatContactID = nextID;
      if (this.contactCollection.has(this.chatContactID)) {
        let next = this.contactCollection.get(this.chatContactID);
        next.icon = "chat_bubble";
      }
      this.make_good_contacts();
      this.send("doTalkToContact", nextID, nextAlias);
    },
    do_send(words, secondary) {
      this.send("doSend", words, secondary);
    }
  }
};
</script>
