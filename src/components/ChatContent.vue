<template>
    <md-content  id="chatline">
      <md-avatar class="md-avatar-icon md-primary">
        <md-icon md-src="assets/gsvg/ic_face_48px.svg"/>
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

