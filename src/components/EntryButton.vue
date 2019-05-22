<template>
   <md-button class="md-raised md-accent" v-on:click="emit()">Send</md-button>
</template>

<script>
export default {
  model: {
    event: 'transmit'
  },
  data: function () {
    return {
      count: 0
    }
  },
  methods: {
    emit: function (msg) {
      this.count++
      this.$emit('transmit', this.count)
    }
  }
  
}
</script>

