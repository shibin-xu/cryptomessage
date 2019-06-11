<template>
  <v-card height="100px" id="chatline">
    <v-layout row>
      <v-flex grow xs-1>
        <v-card dark color="#444466">
          <v-icon>{{ speech.icon }}</v-icon>
          {{speech.timeNumber}}
        </v-card>
      </v-flex>
      <v-flex shrink xs-1>
        <v-card dark color="#444466">
          <v-icon>{{ previousVerified }}</v-icon>
          <v-icon>{{ signatureVerified }}</v-icon>
        </v-card>
      </v-flex>
    </v-layout>
    <br/>
    {{ speech.text }}
  </v-card>
</template>

<script>
export default {
  props: {
    speech: {
      icon: String,
      timeNumber: Number,
      timeStamp:String,
      shortIdentifier: String,
      totalIdentifier: String,
      text: true,
      contactID: true,
      isPreviousVerified: true,
      isSignatureVerified: true,
      isSent: true
    }
  },
  computed: {
    previousVerified() {
      if (this.speech.isPreviousVerified) {
        return "security";
      } else {
        return "sms_failed";
      }
    },
    signatureVerified() {
      if (this.speech.isSignatureVerified) {
        return "vpn_key";
      } else {
        return "warning";
      }
    }
  }
};
</script>

