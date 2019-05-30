<template>
	<div id="app">
    <md-toolbar class="md-primary">
        <h1 class="md-title">CryptoMessageXT</h1>
    </md-toolbar>
     <md-tabs>
      <md-tab id="tab-home" md-label="Home" md-icon="assets/gsvg/ic_home_48px.svg">
        <Connection v-model="connectObj" @transmit="doconnect"/>
        <Connection v-model="disconnectObj" @transmit="dodisconnect"/>
      </md-tab>
      <md-tab id="tab-pages" md-label="My Account" md-icon="assets/gsvg/ic_perm_identity_48px.svg">
        <Connection v-model="mystuffObj" @transmit="dostuff"/>
      </md-tab>
      <md-tab id="tab-posts" md-label="Add Friend" md-icon="assets/gsvg/ic_settings_ethernet_48px.svg">
        <Connection v-model="addfriendObj" @transmit="doaddfriend"/>
        <Connection v-model="remfriendObj" @transmit="doremfriend"/>
      </md-tab>
      <md-tab id="tab-favorites" md-label="Conversations" md-elevation="2" md-icon="assets/gsvg/ic_question_answer_48px.svg">
		    <ChatBox @transmit="dosend"/>
      </md-tab>
    </md-tabs>
	</div>
</template>

<script>
import ChatBox from './components/ChatBox.vue'
import Connection from './components/Connection.vue'

export default {
	components: {
		ChatBox,
		Connection
  },
  data () {
    return {
      myData: 2,
      connectObj:  
        {
          word: 'Connect',
          addr: '123',
        },
      disconnectObj:
        {
          word: 'Disconnect',
          addr: '123',
        },
      mystuffObj:
        {
          word: 'mystuff',
          addr: '123',
        },
      addfriendObj:
        {
          word: 'addfriend',
          addr: '123',
        },
      remfriendObj:
        {
          word: 'remfriend',
          addr: '123',
        },
      chatboxObj:
        {
          contentLines: [],
        }
    }
  },
  mounted () {
      setInterval(() => { this.$electron.ipcRenderer.send('ping') }, 1000)
      this.$electron.ipcRenderer.on('elecConfirmConnect', (_, data) => {
        this.connectObj.addr = '--';
      });
      this.$electron.ipcRenderer.on('elecConfirmDisconnect', (_, data) => {
        this.disconnectObj.addr = '--';
      });
      this.$electron.ipcRenderer.on('elecConfirmSend', (_, data) => {
        this.doremfriend(data)
      });
      this.$electron.ipcRenderer.on('elecRecieve', (_, data) => {
        this.doremfriend(data)
      });
      this.$electron.ipcRenderer.on('elecRaw', (_, data) => {
        
      });
  },
  methods: {
    sendData(dataName, value) {
      if (value) {
          this.$electron.ipcRenderer.send(dataName, value);
      } else {
          this.$electron.ipcRenderer.send(dataName);
      }
    },
    doconnect (addr) {
      this.sendData("connect",addr)
    },
    dodisconnect (addr) {
      this.sendData("disconnect",addr)
    },
    dosend (words) {
      this.sendData("send",words)
    },
    dostuff (words) {
      this.sendData("get",words)
    },
    doaddfriend (words) {
      this.sendData("add",words)
    },
    doremfriend (words) {
      this.sendData("rem",words)
    }
  }
}
</script>

