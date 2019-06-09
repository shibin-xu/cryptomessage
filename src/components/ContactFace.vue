<template>
  <v-list-tile @click="clickedTalk">
    <v-list-tile-action>
      <v-icon >{{ obj.icon }}</v-icon>
    </v-list-tile-action>
    <v-list-tile-content>
      <v-list-tile-title>{{ obj.alias }}</v-list-tile-title>
      {{obj.contactID}}
    </v-list-tile-content>
  </v-list-tile>
</template>

<script>
export default {
  props: {
    obj: {
      contactID: "abda11",
      alias: "Alice",
      icon: "widgets",
    }
  },
  methods: {
    clickedTalk() {
      this.$emit("talkface", this.obj.alias, this.obj.contactID);
    },
    delcontact() {
      
    }
  }
};
</script>

