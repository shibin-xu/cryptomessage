<template>
	<div id="app">
		<h1>CryptoMessageXT</h1>
    <Connection v-model="connectObj" @transmit="doconnect"/>
    <Connection v-model="disconnectObj" @transmit="dodisconnect"/>
		<ChatBox @transmit="dosend"/>
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
        }
    }
  },
  mounted () {
      setInterval(() => { this.$electron.ipcRenderer.send('ping') }, 1000)
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
    }
  }
}
</script>

