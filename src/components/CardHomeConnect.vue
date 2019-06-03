<template>
  <md-card md-with-hover>
    <md-card-header>
      <md-card-header-text>
        <div class="md-title">Connect</div>
      </md-card-header-text>
    </md-card-header>
    <md-card-content>State: {{state}}
      <md-card-actions md-alignment="space-between">
        <div>
          <md-button v-on:click="loginaccount" :class=connectClass>Login</md-button>
          <md-button v-on:click="newaccount" :class=connectClass>New Account</md-button>
          <md-button v-on:click="disconnect" :class=disconnectClass>Disconnect</md-button>
        </div>
      </md-card-actions>
      <md-field>
        <label>Email Login</label>
        <md-input v-model="login" required/>
      </md-field>
      <md-field>
        <label>Password</label>
        <md-input v-model="password" type="password" required/>
      </md-field>
      <md-field>
        <label>Server IP</label>
        <md-input v-model="serverIP"/>
        
      </md-field>
    </md-card-content>
  </md-card>
</template>

<script>
export default {
  data() {
    return {
      serverIP: "10.2.3.4",
      login: "",
      password: "",
      state: "not connected.",
      connectClass: "md-raised md-accent",
      disconnectClass: "md-raised"
    };
  },
  methods: {
    loginaccount: function() {
      this.$emit("loginaccount",this.login, this.password);
      
      this.connectClass = "md-raised";
      this.disconnectClass = "md-raised md-accent";
    },
    newaccount: function() {
      this.$emit("newaccount",this.login, this.password);
      
      this.connectClass = "md-raised";
      this.disconnectClass = "md-raised md-accent";
    },
    disconnect: function() {
      this.$emit("disconnect","");
      
      this.disconnectClass = "md-raised";
      this.connectClass = "md-raised md-accent";
    }
  }
};
</script>

