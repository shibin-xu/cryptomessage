<template>
	<div id="app">
    <md-toolbar class="md-primary">
        <h1 class="md-title">CryptoMessageXT</h1>
    </md-toolbar>
     <md-tabs>
      <md-tab id="tab-home" md-label="Home" md-icon="assets/gsvg/ic_home_48px.svg">
        <ButtonFirstToSecond v-model="connectObj" @transmit="doConnect"/><br/>
        <ButtonSimple v-model="disconnectObj" @transmit="doDisconnect"/><br/>
        <ButtonBox v-model="filePathObj" @transmit="doFilePath"/><br/>
        <ButtonBox v-model="loginNewObj" @transmit="doLoginNew"/><br/>
        <ButtonBox v-model="loginExistingObj" @transmit="doLoginExisting"/><br/>
      </md-tab>
      <md-tab id="tab-pages" md-label="My Account" md-icon="assets/gsvg/ic_perm_identity_48px.svg">
        <ButtonSimple v-model="mystuffObj" @transmit="doGet"/>
      </md-tab>
      <md-tab id="tab-posts" md-label="Add Contact" md-icon="assets/gsvg/ic_settings_ethernet_48px.svg">
        <ButtonFirstToSecond v-model="addContactObj" @transmit="doAddContact"/><br/>
        <ButtonFirstToSecond v-model="removeContactObj" @transmit="doRemoveContact"/><br/>
        <ButtonFirstToSecond v-model="renameContactObj" @transmit="doRenameContact"/><br/>
        <ButtonSimple v-model="getAllContactObj" @transmit="doGetAllContact"/><br/>
        <ButtonFirstToSecond v-model="talkToContactObj" @transmit="doTalkToContact"/><br/>
      </md-tab>
      <md-tab id="tab-favorites" md-label="Conversations" md-elevation="2" md-icon="assets/gsvg/ic_question_answer_48px.svg">
		    <ChatBox @transmit="doSend" :contentLines="contentLines"/>
      </md-tab>
    </md-tabs>
	</div>
</template>

<script>
import ChatBox from './components/ChatBox.vue'
import ButtonSimple from './components/ButtonSimple.vue'
import ButtonFirstToSecond from './components/ButtonFirstToSecond.vue'
import ButtonBox from './components/ButtonBox.vue'
let nextLineId = 1

export default {
	components: {
		ChatBox,
    ButtonSimple,
    ButtonFirstToSecond,
    ButtonBox
  },
  data () {
    return {
      myData: 2,
      connectObj:  
        {
          first: 'Connect',
          second: '123.1',
        },
      disconnectObj:
        {
          first: 'Disconnect',
          second: '-',
        },
      filePathObj:
        {
          first: 'Enter File Path',
          second: 'c:',
        },
      loginNewObj:
        {
          first: 'Create New Login',
          second: 'alice',
        },
      loginExistingObj:
        {
          first: 'Login Existing',
          second: 'alice',
        },
      addContactObj:
        {
          first: 'Add Contact',
          second: 'bob',
        },
      removeContactObj:
        {
          first: 'Remove Contact',
          second: 'bob',
        },
      renameContactObj:
        {
          first: 'Rename Contact',
          second: 'bob',
        },
      getAllContactObj:
        {
          first: 'Get All Contacts',
          second: '-',
        },
      talkToContactObj:
        {
          first: 'Talk To Contact',
          second: 'bob',
        },
      mystuffObj:
        {
          first: 'Rx Msg',
          second: '-',
        },
      contentLines: [
        {
          id: nextLineId++,
          text: 'first',
          isConfirmed: true,
          fromSelf: false
        },
        {
          id: nextLineId++,
          text: 'second',
          isConfirmed: true,
          fromSelf: true
        },
      ],
    }
  },
  mounted () {
      setInterval(() => { this.$electron.ipcRenderer.send('ping') }, 1000)
      this.$electron.ipcRenderer.on('UIResultForConnect', (cmd, primary, secondary, timestamp) => {
        this.disconnectObj.addr = primary;
        this.connectObj.addr = '--';
      });
      this.$electron.ipcRenderer.on('UIResultForDisconnect',(cmd, primary, secondary, timestamp) => {
        this.connectObj.addr = primary;
        this.disconnectObj.addr = '--';
      });
      this.$electron.ipcRenderer.on('UIResultForMessageSend',(cmd, primary, secondary, timestamp) => {
        
      });
      this.$electron.ipcRenderer.on('UIMessageReceive', (cmd, primary, secondary, timestamp) => {
        this.contentLines.push({
          id: nextLineId++,
          text: primary,
          alias: secondary,
          isConfirmed: true,
          fromSelf: false
        })
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
    doConnect (addr) {
      this.sendData("doConnect",addr)
    },
    doDisconnect () {
      this.sendData("doDisconnect")
    },
    doFilePath (words) {
      this.sendData("doFilePath",words)
    },
    doLoginNew (words) {
      this.sendData("doLoginNew",words)
    },
    doLoginExisting (words) {
      this.sendData("doLoginExisting",words)
    },
    doAddContact (words) {
      this.sendData("doAddContact",words)
    },
    doRemoveContact (words) {
      this.sendData("doRemoveContact",words)
    },
    doRenameContact (words) {
      this.sendData("doRenameContact",words)
    },
    doGetAllContact () {
      this.sendData("doGetAllContact")
    },
    doTalkToContact (words) {
      this.sendData("doTalkToContact",words)
    },
    doSend (words) {
      let secondary = "self";
      this.contentLines.push({
          id: nextLineId++,
          text: words,
          alias: secondary,
          isConfirmed: false,
          fromSelf: true
        })
      this.sendData("doSend",words)
    },
    doGet () {
      this.sendData("doGet")
    }
  }
}
</script>

