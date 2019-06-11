<template>
  <v-list-tile>
    <v-list-tile-action>
      <v-icon large @click="clickedTalk">{{ obj.icon }}</v-icon>
    </v-list-tile-action>
    <div style="flex:1 1 90; flex-direction: column;">
      <v-list-tile-title>{{ obj.alias }}</v-list-tile-title>
      <v-list-tile-sub-title>{{shortID}}</v-list-tile-sub-title>
    </div>
    <v-list-tile-action>
     <v-icon @click="clickedChange">settings</v-icon>
    </v-list-tile-action>
  </v-list-tile>
</template>

<script>
export default {
  props: {
    obj: {
      contactID: 'abc',
      alias: 'Abc',
      icon: 'widgets'
    }
  },
  computed: {
    shortID() {
      return 'pubkey: ' + this.obj.contactID.substr(44, 8)
    }
  },
  methods: {
    clickedTalk() {
      this.$emit('talkface', this.obj.alias, this.obj.contactID)
    },

    clickedChange() {
      this.$emit('changeface', this.obj.alias, this.obj.contactID)
    }
  }
}
</script>

