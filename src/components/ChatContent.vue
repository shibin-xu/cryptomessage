<template>
    <md-content  id="chatline">
      <md-avatar class="md-avatar-icon md-primary">
        <md-icon>face</md-icon>
      </md-avatar>
      <div>
        {{ line.text }}
      </div>
    </md-content>
</template>

<script>
export default {
  props: {
    line: {
      type: Object,
      required: true
    }
  },
}
</script>

