<template>
	<div >
    <div id="chatbox" v-if="contentLines.length">
			<ChatContent
        
				v-for="line in contentLines"
        v-bind:class="{ 'confirm':line.isConfirmed, 'fromSelf':line.fromSelf }"
				:key="line.id"
				:line="line"
			/>
		</div>
		<EntryText v-model="draftText"/><EntryButton @transmit="entryTransmit"/>
	</div>
</template>

<script>
import EntryText from './EntryText.vue'
import EntryButton from './EntryButton.vue'
import ChatContent from './ChatContent.vue'

let nextLineId = 1

export default {
  components: {
    EntryText,
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
        this.contentText = ''
      }
    }
  }
}
</script>