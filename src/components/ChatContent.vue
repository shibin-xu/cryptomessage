<template>
    <div id="chatline">
        {{ line.id }}
        <br/>
        {{ line.text }}
    </div>
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

