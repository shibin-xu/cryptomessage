<template>
    <v-list>
      <v-layout row align-center>
        <v-flex xs6>
          <v-subheader>{{contacts}}</v-subheader>
        </v-flex>
      </v-layout>
      <div ref="contacDiv" style=" background: #0F4D8E; overflow: auto; height:25vh; width: 65vw">
      <ContactFace
        v-for="obj in contactObjects"
        @talkface="clickedContact"
        :obj="obj"
      />
      </div>
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
    contactObjects: Array,
    selfKey: String
  },
  data: () => ({
    contacts: "Contact List",
    contacts_icon: "lightbulb_outline",
    addIcon: "add",
    addText: "Add Contact",
    refreshIcon: "adjust",
    refreshText: "Refresh Contact"
  }),
  methods: {
    clickedContact(alias, id) {
      this.$emit("talk", alias, id);
    },
    clickedAdd() {
      this.$emit("open");
    },
    clickedRefresh() {
      this.$emit("refresh");
    }
  }
};
</script>