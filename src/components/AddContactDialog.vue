<template>
  <div style='position: absolute; z-index:1000; width:350px; top:20px; left: 50%; '>
    <div style='position: absolute; width:350px; left:-50%'>
      <v-card v-if='shouldRender'>
        <v-card-title class='title font-weight-regular justify-space-between'>
          <span>{{ currentTitle }}</span>
          <v-avatar
            color='primary lighten-2'
            class='subheading white--text'
            size='24'
            v-text='step'
          ></v-avatar>
        </v-card-title>

        <v-window v-model='step'>
          <v-window-item :value='1'>
            <v-card-text>
              <v-text-field
                outline
                label='Alias'
                value='codename'
                v-model='alias'
              ></v-text-field>
              <v-text-field
                outline
                label='PubKey'
                value=''
                v-model='pubkeyString'
              ></v-text-field>
              <label for='public-file-upload' class='custom-button'>Find Public</label>
                <input
                  id='public-file-upload'
                  type='file'
                  prepend-icon='attach_file'
                  :accept='acceptPublic'
                  :multiple='false'
                  style='background-color: yellow;'
                  @change='onPublicFileChange'
                >
                {{pubkeyFilename}}
            </v-card-text>
          </v-window-item>
          <v-window-item :value='2'>
            <div class='pa-3 text-xs-center'>
              <v-img
                class='mb-3'
                contain
                height='128'
                src='assets\xtmsg.svg'
              ></v-img>
              <span class='caption grey--text'>Thanks for adding {{alias }}</span>
            </div>
          </v-window-item>
        </v-window>

        <v-divider></v-divider>

        <v-card-actions>
          <v-btn flat @click='back'>Back</v-btn>
          <v-spacer></v-spacer>
          <v-btn :disabled=block_next color='primary' depressed @click='done'>Next</v-btn>
        </v-card-actions>
      </v-card>
    </div>
  </div>
</template>
<script>
export default {
  props: ['shouldRender'],
  data() {
    return {
      alias: 'Friend',
      pubkeyString: '',
      step: 1,
      pubkeyFilename:'',
      acceptPublic: '.pub, .key, .txt',
    }
  },
  computed: {
    currentTitle() {
      return 'Add Contact'
    },
    block_next: function() {
      if (this.pubkeyFilename.length == 0 && this.pubkeyString.length == 0) {
        return true
      }
      if (this.alias.length == 0) {
        return true
      }
      return false
    },
  },
  methods: {
    back: function() {
      if(this.step == 1) {
        this.$emit('add_by_string', '', this.alias)
      } else {
        this.step--
      }
    },
    done: function() {
      this.step++
      if (this.step == 3) {
        if(this.pubkeyFilename.length > 0) {
          this.$emit('add_by_file', this.pubkeyFilename, this.alias)
        } else {
          this.$emit('add_by_string', this.pubkeyString, this.alias)
        }
        this.step = 1
      }
    },
    getFormData(files) {
      const data = new FormData();
      [...files].forEach(file => {
        data.append('data', file, file.name) // currently only one file at a time
      })
      return data
    },
    onPublicFileChange($event) {
      const files = $event.target.files || $event.dataTransfer.files
      if (files) {
        if (files.length > 0) {
          this.pubkeyFilename = [...files].map(file => file.name).join(', ')
        } else {
          this.pubkeyFilename = null
        }
      } else {
        this.pubkeyFilename = $event.target.value.split('\\').pop()
      }
    },
  }
}
</script>
