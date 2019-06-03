<template>
  <md-field>
      <label>{{local.first}}</label>
		  <md-input v-model="draftText"></md-input>
      <md-button class="md-raised md-accent" v-on:click="go()">Go</md-button>
   </md-field>
</template>

<script>
export default {
  props: ['value'],
  computed: {
    local() {
      return this.value ? this.value : { first: '-', second:'-' }
    },
  },
  data () {
    return {
      draftText: "x.."	,
    }
  },
  methods: {
    update(key, value) {
      
    },
    go: function () {
      this.$emit('transmit', this.local.second)
    }
  },
  
}
</script>

