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

let nextLineId = 1

export default {
  components: {
    EntryButton,
    ChatContent,
  },
  data () {
    return {
      draftText: "a"	,
      contentLines: [ 
        {
          id: nextLineId++,
          text: 'first',
          isConfirmed: true,
          fromSelf: false
        },
        {
          id: nextLineId++,
          text: 'second',
          isConfirmed: true,
          fromSelf: true
        },
      ]
    }
  },
  methods: {
    entryTransmit (count) {
      const trimmedText = this.draftText.trim()
      if(trimmedText)
      {
        this.contentLines.push({
          id: nextLineId++,
          text: trimmedText,
          isConfirmed: false,
          fromSelf: true
        })
        this.$emit('transmit', trimmedText)
        this.draftText = ''
      }
    }
  }
}
</script>