<template>
  <div ref="conDiv"
   style=" background: white; overflow: auto; position: absolute; z-index:10; height: 10%; width:94%; left:3%; bottom:3%; max-height: 10%"
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