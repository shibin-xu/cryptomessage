<template>
    <div>
        <v-icon>{{ line.icon }}</v-icon>
        {{ line.text }}
    </div>
</template>

<script>
export default {
  props: {
    line: {
      icon: Object,
      text: true
    }
  },
}
</script>

