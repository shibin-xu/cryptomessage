<template>
  <div style="position: absolute; z-index:1000; width:350px; top:20px; left: 50%; ">
    <div style="position: absolute; width:350px; left:-50%">
      <v-card v-if="shouldRender">
        <v-card-title class="title font-weight-regular justify-space-between">
          <span>{{ currentTitle }}</span>
        </v-card-title>

        <v-window>
          <v-window-item>
            <v-card-text>
              {{shortkeyString}}
              <v-text-field outline label="Alias" v-model="nextAlias"></v-text-field>
            </v-card-text>
          </v-window-item>
        </v-window>

        <v-card-actions>
          <v-btn flat @click="back">Back</v-btn>
          <v-spacer></v-spacer>
          <v-btn flat @click="rename">Rename</v-btn>
          <v-spacer></v-spacer>
          <v-btn flat @click="dele">Delete</v-btn>
        </v-card-actions>
      </v-card>
    </div>
  </div>
</template>
<script>
const { dialog, app } = require("electron").remote;
var fs = require("fs");
export default {
  props: {
    shouldRender: Boolean,
    alias: String,
    pubkeyString: String,
    shortkeyString: String
  },
  data() {
    return {
      nextAlias: "Friend"
    };
  },
  computed: {
    currentTitle() {
      return "Change Contact";
    }
  },
  methods: {
    back: function() {
      this.$emit("rename_by_key", "", "");
    },
    dele: function() {
      this.$emit("delete_by_key", this.pubkeyString, this.nextAlias);
    },
    rename: function() {
      this.$emit("rename_by_key", this.pubkeyString, this.nextAlias);
    }
  }
};
</script>
