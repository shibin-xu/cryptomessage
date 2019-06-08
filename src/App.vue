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
          <v-flex xs6>
            <v-subheader>{{title}}</v-subheader>
          </v-flex>
        </v-layout>
        <v-divider dark class="my-3"></v-divider>
        <v-list-tile @click="connect_action">
          <v-list-tile-action>
            <v-icon>{{ connect_icon }}</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>{{ connect }}</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
        <v-divider dark class="my-3"></v-divider>
        <Contact @talk="do_talkToContact" @open="contact_action" @refresh="contact_refresh" :contactList="contactList"/>
        <v-divider dark class="my-3"></v-divider>
        <v-list-tile @click="settings_action">
          <v-list-tile-action>
            <v-icon>{{ settings_icon }}</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            <v-list-tile-title>{{ settings }}</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
        <v-list-tile>
          {{selfContactID}}
        </v-list-tile>
      </v-list>
    </v-navigation-drawer>
    <v-content style="min-height:97vh; max-height:97vh">
      <v-container fluid fill-height class="grey lighten-4">
        <v-layout align-center justify-space-around column>
          <v-flex>
            <ConnectDialog :shouldRender="showConnect" @keyfile="do_keyfile"/>
          </v-flex>
          <v-flex>
            <AddContactDialog :shouldRender="showContact" @add="do_addContact"/>
          </v-flex>
          <v-flex grow>
            <ChatArea
              @transmit="do_send"
              :contactName="chatAlias"
              :contactID="chatContactID"
              :chatLines="chatLines"
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
    title: "CryptoXT",
    connect: "Connection",
    connect_icon: "lightbulb_outline",
    showConnect: false,
    showContact: false,
    settings: "Settings",
    settings_icon: "settings",
    contacts: "Contacts",
    chatAlias: "fred",
    chatContactID: "76864873658734b",
    selfContactID: "abda11",
    consoleLines: [{ icon: "launch", text: "Start" }],
    contactList: [
      {
        contactID: "76864873658734b",
        alias: "fred",
        icon: "face"
      }
    ],
    contentLines: [
      
      
    ]
  }),
  computed: {
    chatLines: function() {
      let good = [];
      for (var i in this.contentLines) {
        let content = this.contentLines[i];
        if (content.contactID == this.chatContactID) {
          good.push(content);
        }
      }
      return good;
    }
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
      "UIResultForMessageSend",
      (evt, primary, secondary, timestamp) => {
        this.rx("sent", primary, secondary);
      }
    );
    this.$electron.ipcRenderer.on(
      "UIMessageReceive",
      (evt, primary, secondary, timestamp) => {
        this.rx("recieved", primary, secondary);
        this.contentLines.push({
          text: primary,
          contactID: secondary,
          isConfirmed: true,
          isSent: false
        });
      }
    );
    this.$electron.ipcRenderer.on(
      "UIResultForContact",
      (evt, primary, secondary, timestamp) => {
        
        this.contactList.push({
          contactID: primary,
          alias: secondary,
          icon: "face"
        })
        this.rx("contact", primary, secondary);
      }
    );
    this.$electron.ipcRenderer.on(
      "UIResultForArchive",
      (evt, primary, secondary, timestamp) => {
        this.rx("", primary, secondary);
        this.contentLines.push({
          text: "hi jim, this is alice",
          contactID: "ab324b324b12b41",
          isConfirmed: true,
          isSent: false
        })
      }
    );
    this.$electron.ipcRenderer.on(
      "UIPublicKey",
      (evt, primary, secondary, timestamp) => {
        this.selfContactID = primary;
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
    connect_action() {
      this.showConnect = true;
      return;
    },
    
    contact_action() {
      console.log("hi");
      this.showContact = true;
      return;
    },
    contact_refresh() {
      this.send("doGetAllContact");
      return;
    },
    settings_action() {

      this.send("doGet");
      return;
    },
    do_keyfile(pubfile, prifile) {
      if (pubfile.length > 0) {
        this.send("doFilePath", pubfile, prifile);
      }
      this.showConnect = false;
      return;
    },
    do_addContact(alias, pubkey) {
      this.showContact = false;
      if(alias.length > 0) {
        this.send("doAddContact", alias, pubkey);
      }
    },
    do_removeContact(words) {
      this.send("doRemoveContact", words);
    },
    do_renameContact(words) {
      this.send("doRenameContact", words);
    },
    do_talkToContact(nextAlias, nextID) {
      this.chatAlias = nextAlias;
      this.chatContactID = nextID;
      for (var i in this.contactList) {
        if (this.contactList[i].contactID == this.chatContactID) {
          this.contactList[i].icon = "chat_bubble";
        } else {
          this.contactList[i].icon = "face";
        }
      }
      this.send("doTalkToContact", nextID, nextAlias);
    },
    do_send(words, secondary) {
      this.contentLines.push({
        text: words,
        contactID: secondary,
        isConfirmed: false,
        isSent: true
      });
      this.send("doSend", words, secondary);
    }
  }
};
</script>
