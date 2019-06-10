<template>
  <v-container>
    <v-layout column justify-top>
      <v-flex>
        <h1 class="centered">{{contactName}} {{count}}</h1>
        
      </v-flex>
      <div ref="chatDiv" style=" background: #525D68; overflow: auto; height:50vh; width: 65vw">
        <v-container>
          <ChatLine
            v-for="speech in speechObjects"
            v-bind:class="{ 'confirm':speech.isSignatureVerified, 'fromSelf':speech.isSent }"
            :key="speech.totalIdentifier"
            :speech="speech"
          />
        </v-container>
      </div>
      <v-text-field
        v-model="blah"
        append-outer-icon="send"
        :prepend-icon="icon"
        box
        clear-icon="delete"
        clearable
        label="Message"
        type="text"
        v-on:keyup.enter="sendMessage"
        @click:append-outer="sendMessage"
        @click:prepend="changeIcon"
        @click:clear="clearMessage"
      ></v-text-field>
    </v-layout>
  </v-container>
</template>

<script>
import ChatLine from "./ChatLine.vue";
export default {
  components: {
    ChatLine
  },
  props: {
    contactName: String,
    contactID: String,
    speechObjects: Array
  },
  data: () => ({
    blah: "blah",
    message: "Hey!",
    iconIndex: 0,
    icons: [
      "sentiment_very_satisfied",
      "sentiment_neutral",
      "sentiment_very_dissatisfied"
    ]
  }),
  updated() {
    var elem = this.$refs.chatDiv;
    elem.scrollTop = elem.scrollHeight;
  },
  mounted: function() {
    this.$refs.chatDiv.scrollTop = this.$refs.chatDiv.scrollHeight;
  },
  computed: {
    icon() {
      return this.icons[this.iconIndex];
    },
    count() {
      return this.speechObjects.length;
    }
  },
  methods: {
    handleScroll: function(evt, el) {
      if (window.scrollY > 50) {
        el.setAttribute(
          "style",
          "opacity: 1; transform: translate3d(0, -10px, 0)"
        );
      }
      return window.scrollY > 100;
    },
    sendMessage() {
      const trimmedText = this.blah.trim();
      if (trimmedText) {
        this.$emit("transmit", trimmedText, this.contactID);
      }
      this.blah = "";
    },
    clearMessage() {
      this.blah = "";
    },
    resetIcon() {
      this.iconIndex = 0;
    },
    changeIcon() {
      this.iconIndex === this.icons.length - 1
        ? (this.iconIndex = 0)
        : this.iconIndex++;
    }
  }
};
</script>