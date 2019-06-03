<template>
	<div id="app">
    <md-toolbar class="md-primary">
        <h1 class="md-title">CryptoMessageXT</h1>
    </md-toolbar>
     <md-tabs>
      <md-tab id="tab-home" md-label="Home" md-icon="assets/gsvg/ic_home_48px.svg">
        <CardHomeKeys @transmit="doFilePath" :labelTitle="fileLabelTitle"/><br/>
        <CardHomeConnect @loginaccount="doLoginAccount" @newaccount="doNewAccount" @disconnect="doDisconnect"/>
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
import CardHomeConnect from './components/CardHomeConnect.vue'
import CardHomeKeys from './components/CardHomeKeys.vue'
import ButtonBox from './components/ButtonBox.vue'
let nextLineId = 1

export default {
	components: {
		ChatBox,
    ButtonSimple,
    ButtonFirstToSecond,
    CardHomeConnect,
    CardHomeKeys,
    ButtonBox
  },
  data () {
    return {
      myData: 2,
      fileLabelTitle: 'Path to Public Key:',
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
      this.$electron.ipcRenderer.on('UIResultForConnect', (cmd, primary, secondary, timestamp) => {
 
      });
      this.$electron.ipcRenderer.on('UIResultForDisconnect',(cmd, primary, secondary, timestamp) => {

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
    sendData(dataName, primary, secondary) {
      console.log(dataName);
      console.log(primary);
      console.log(secondary);
      this.$electron.ipcRenderer.send(dataName, primary, secondary);
    },
    doLoginAccount (login, password) {
      if(login.length > 0) 
      {
        this.sendData("doLoginAccount",login, password);
      }
    },
    doNewAccount (login, password) {
      if(login.length > 0) 
      {
        this.sendData("doNewAccount",login, password);
      }
    },
    doDisconnect () {
      this.sendData("doDisconnect");
    },
    doFilePath (filename, pubkey) {
      this.sendData("doFilePath",filename, pubkey)
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

