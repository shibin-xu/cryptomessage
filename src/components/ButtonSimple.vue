<template>
   <md-button class="md-raised md-primary" v-on:click="emit()">{{local.first}}</md-button>
</template>

<script>
export default {
  props: ['value'],
  computed: {
    local() {
      return this.value ? this.value : { first: '-', second:'-' }
    },
  },
  methods: {
    update(key, value) {
      
    },
    emit: function () {
      this.$emit('transmit', this.local.second)
    }
  },
  
}
</script>

