<template>
  <div ref="conDiv"
   style=" background: gray; overflow: auto; height:12vh; width: 65vw"
    >

    <v-container  ref="conDiv">
      <v-layout
        v-scroll:#scroll-target="onScroll"
        column
        align-left
        justify-top
      >
        <ConsoleLine v-for="line in consoleLines" :key="line.id" :line="line"/>
      </v-layout>
    </v-container>
  </div>
</template>

<script>
import ConsoleLine from "./ConsoleLine.vue";
export default {
  components: {
    ConsoleLine
  },
  props: {
    consoleLines: Array
  },
  updated(){              
    var elem = this.$refs.conDiv;
    elem.scrollTop = elem.scrollHeight;
  },
};
</script>