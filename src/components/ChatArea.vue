<template>
  <v-container>
    <v-layout column>
      <v-flex>
        <h1 class="centered">{{contactName}}</h1>
        <h4 class="centered">{{contactID}}</h4>
      </v-flex>
      <div ref="chatDiv" style=" background: gray; overflow: auto; height:60vh; width: 65vw">

        <v-container>
          <ChatLine 
          v-for="line in chatLines" 
          v-bind:class="{ 'confirm':line.isConfirmed, 'fromSelf':line.isSent }"
          :key="line.id" :line="line"/>
        </v-container>
      </div>
      <v-form>
        <v-container>
          <v-layout row wrap>
            <v-flex xs12>
              <v-text-field
                v-model="message"
                append-outer-icon="send"
                :prepend-icon="icon"
                box
                clear-icon="delete"
                clearable
                label="Message"
                type="text"
                @click:append-outer="sendMessage"
                @click:prepend="changeIcon"
                @click:clear="clearMessage"
              ></v-text-field>
            </v-flex>
          </v-layout>
        </v-container>
      </v-form>
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
    chatLines: Array
  },
  data: () => ({
    message: "Hey!",
    iconIndex: 0,
    icons: [
      "sentiment_very_satisfied",
      "sentiment_neutral",
      "sentiment_very_dissatisfied"
    ]
  }),
  updated(){              
    var elem = this.$refs.chatDiv
    elem.scrollTop = elem.scrollHeight;
  },
  mounted: function () {
    this.$refs.chatDiv.scrollTop = this.$refs.chatDiv.scrollHeight;
  },
  computed: {
    icon() {
      return this.icons[this.iconIndex];
    }
  },
  methods: {
    handleScroll: function (evt, el) {
      if (window.scrollY > 50) {
        el.setAttribute(
          'style',
          'opacity: 1; transform: translate3d(0, -10px, 0)'
        )
      }
      return window.scrollY > 100
    },
    sendMessage() {
      const trimmedText = this.message.trim();
      if (trimmedText) {
        this.$emit("transmit", trimmedText, this.contactID);
      }
      this.clearMessage();
    },
    clearMessage() {
      this.message = "";
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