<template>
  <md-card md-with-hover>
    <md-card-header>
      <md-card-header-text>
        <div class="md-title">Key Store</div>
      </md-card-header-text>
    </md-card-header>
    <md-card-content>Key: {{contents}}
      <md-card-actions md-alignment="space-between">
        <div>
          <md-button v-on:click="save">Generate Key</md-button>
        </div>
        <div>
          <md-field>
            <label>{{labelTitle}}</label>
            <md-file @md-change="loadList" accept=".pub, .key, .txt"/>
          </md-field>
        </div>
      </md-card-actions>
    </md-card-content>
  </md-card>
</template>

<script>
const { dialog, app } = require('electron').remote
var fs = require("fs");
export default {
  props: ["labelTitle"],

  data() {
    return {
      filepath: null,
      contents: ".",
      nextkey: "a324b2341b4",
    };
  },
  methods: {
    loadList: function(filelist) {
      if (filelist.length > 0) {
        let filedata = filelist[0];
        this.filepath = filedata.path;
        var reader = new FileReader();
        reader.onload = this.loadedReader;
        reader.readAsText(filedata);
      }
    },
    loadedReader: function(e) {
      let lines = e.target.result;
      this.contents = lines;
      this.$emit("transmit", this.filepath, this.contents);
    },
    save: function() {
      const options = {
        defaultPath: './mykey.pub',
      }
      dialog.showSaveDialog(null, options, this.write);
    },
    write: function(path) {
      try { 
        let buffer = new Buffer( this.nextkey, "utf8");
        let fd = fs.openSync(path, 'w');
        fs.writeSync(fd, buffer, 0, buffer.length);
        fs.close(fd);
        this.filepath = path;
        this.contents = this.nextkey;
        this.$emit("transmit", this.filepath, this.contents);
      }
      catch(e) { console.log("fail "+e); }
    },
  }
};
</script>


