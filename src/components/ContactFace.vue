<template>
  <v-list-tile @click="clickedTalk">
    <v-list-tile-action>
      <v-icon >{{ obj.icon }}</v-icon>
    </v-list-tile-action>
    <v-list-tile-content>
      <v-list-tile-title>{{ obj.alias }}</v-list-tile-title>
      {{shortID}}
    </v-list-tile-content>
  </v-list-tile>
</template>

<script>
export default {
  props: {
    obj: {
      contactID: "abc",
      alias: "Abc",
      icon: "widgets",
    }
  },
  computed: {
    shortID() {
      return this.obj.contactID.substr(48, 12);
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

