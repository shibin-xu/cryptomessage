<template>
    <v-card height="100px" id="chatline">
        <v-icon>{{ line.icon }}</v-icon>
        {{ line.text }}
    </v-card>
</template>

<script>
export default {
  props: {
    line: {
      icon: Object,
      text: true,
      contactID: true,
      isConfirmed: true,
      isSent: true,
    }
  },
}
</script>

