<template>
   <button v-on:click="emit()">{{local.word}} to {{local.addr}}</button>
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

