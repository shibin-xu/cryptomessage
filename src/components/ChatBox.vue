<template>
	<md-content >
    <div id="chatbox" v-if="contentLines.length">
			<ChatContent
        
				v-for="line in contentLines"
        v-bind:class="{ 'confirm':line.isConfirmed, 'fromSelf':line.fromSelf }"
				:key="line.id"
				:line="line"
			/>
		</div>
    <md-field>
      <label>Chat</label>
		  <md-textarea v-model="draftText"></md-textarea><EntryButton @transmit="entryTransmit"/>
    </md-field>
	</md-content>
</template>

<script>
import EntryButton from './EntryButton.vue'
import ChatContent from './ChatContent.vue'

export default {
  components: {
    EntryButton,
    ChatContent,
  },
  props: {
    contentLines:Array,
  },
  data () {
    return {
      draftText: "Say.."	,
    }
  },
  methods: {
    entryTransmit (count) {
      const trimmedText = this.draftText.trim()
      if(trimmedText)
      {
        this.$emit('transmit', trimmedText)
        this.draftText = ''
      }
    }
  }
}
</script>