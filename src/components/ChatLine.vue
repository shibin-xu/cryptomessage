<template>
    <v-card height="100px" id="chatline">
        <v-icon>{{ speech.icon }}</v-icon>
        {{ speech.identifier}}:
        {{ speech.text }}
    </v-card>
</template>

<script>
export default {
  props: {
    speech: {
      icon: String,
      identifier: String,
      text: true,
      contactID: true,
      isConfirmed: true,
      isSent: true,
    }
  },
}
</script>

