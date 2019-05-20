<template>
   <button v-on:click="emit()">Send</button>
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

