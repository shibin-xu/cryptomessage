<template>
  <div style="position: absolute; z-index:1000; width:350px; top:20px; left: 50%; ">
    <div style="position: absolute; width:350px; left:-50%">
      <v-card v-if="shouldRender">
        <v-card-title class="title font-weight-regular justify-space-between">
          <span>{{ currentTitle }}</span>
          <v-avatar
            color="primary lighten-2"
            class="subheading white--text"
            size="24"
            v-text="step"
          ></v-avatar>
        </v-card-title>

        <v-window v-model="step">
          <v-window-item :value="1">
            <v-card-text>
              <v-text-field
                outline
                label="Alias"
                value="codename"
                v-model="alias"
              ></v-text-field>
              <v-text-field
                outline
                label="PubKey"
                value=""
                v-model="pubkey"
              ></v-text-field>
            </v-card-text>
          </v-window-item>
          <v-window-item :value="2">
            <div class="pa-3 text-xs-center">
              <v-img
                class="mb-3"
                contain
                height="128"
                src="assets\xtmsg.svg"
              ></v-img>
              <span class="caption grey--text">Thanks for adding {{alias }}</span>
            </div>
          </v-window-item>
        </v-window>

        <v-divider></v-divider>

        <v-card-actions>
          <v-btn :disabled="step === 1" flat @click="back">Back</v-btn>
          <v-spacer></v-spacer>
          <v-btn color="primary" depressed @click="done">Next</v-btn>
        </v-card-actions>
      </v-card>
    </div>
  </div>
</template>
<script>
const { dialog, app } = require("electron").remote;
var fs = require("fs");
export default {
  props: ["shouldRender"],
  data() {
    return {
      alias: "",
      pubkey: "",
      step: 1
    };
  },
  computed: {
    currentTitle() {
      return "Add Contact";
    }
  },
  methods: {
    back: function() {
      this.step--;
    },
    done: function() {
      this.step++;
      if (this.step == 3) {
        this.$emit("add", this.pubkey, this.alias);
        this.step = 1;
      }
    }
  }
};
</script>
