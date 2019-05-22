<template>
   <md-button class="md-raised md-primary" v-on:click="emit()">{{local.word}} to {{local.addr}}</md-button>
</template>

<script>
export default {
  props: ['value'],
  computed: {
    local() {
      return this.value ? this.value : { word: '-', addr:'192.1' }
    },
  },
  methods: {
    update(key, value) {
      
    },
    emit: function () {
      this.$emit('transmit', this.local.addr)
    }
  },
  
}
</script>

