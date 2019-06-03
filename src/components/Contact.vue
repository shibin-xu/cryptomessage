<template>
  <v-list>
    <v-layout row align-center>
      <v-flex xs6>
        <v-subheader>{{contacts}}</v-subheader>
      </v-flex>
    </v-layout>
    <ContactFace v-for="face in contactList" @talkface="clickedContact" :key="face.contactID" :face="face"/>
    <v-list-tile @click="clickedAdd">
      <v-list-tile-action>
        <v-icon>{{ addIcon }}</v-icon>
      </v-list-tile-action>
      <v-list-tile-content>
        <v-list-tile-title class="grey--text">{{ addText }}</v-list-tile-title>
      </v-list-tile-content>
    </v-list-tile>
  </v-list>
</template>

<script>
import ContactFace from "./ContactFace.vue";
export default {
  components: {
    ContactFace
  },
  props: {
    contactList: Array
  },
  data: () => ({
    contacts: "Contact List",
    contacts_icon: "lightbulb_outline",
    addIcon: "add",
    addText: "Add Contact"
  }),
  methods: {
    clickedContact(alias, id) {
      this.$emit("talk", alias, id);
    },
    clickedAdd() {
      this.$emit("open");
    },
  }
};
</script>