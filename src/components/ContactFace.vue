<template>
  <v-list-tile @click="clickedTalk">
    <v-list-tile-action>
      <v-icon >{{ face.icon }}</v-icon>
    </v-list-tile-action>
    <v-list-tile-content>
      <v-list-tile-title>{{ face.alias }}</v-list-tile-title>
      {{face.contactID}}
    </v-list-tile-content>
  </v-list-tile>
</template>

<script>
export default {
  props: {
    face: {
      contactID: "abda11",
      alias: "Alice",
      icon: "widgets",
    }
  },
  methods: {
    clickedTalk() {
      this.$emit("talkface", this.face.alias, this.face.contactID);
    },
    delcontact() {
      
    }
  }
};
</script>

