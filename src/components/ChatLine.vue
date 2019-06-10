<template>
    <v-card height="100px" id="chatline">
        <v-icon>{{ speech.icon }}</v-icon>
        {{ speech.shortIdentifier }}:
        {{ speech.text }}
    </v-card>
</template>

<script>
export default {
  props: {
    speech: {
      icon: String,
      shortIdentifier: String,
      totalIdentifier: String,
      text: true,
      contactID: true,
      isConfirmed: true,
      isSent: true,
    }
  },
}
</script>

