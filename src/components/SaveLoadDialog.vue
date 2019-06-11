<template>
  <div style="position: absolute; z-index:1000; width:350px; top:20px; left: 50%; ">
    <div style="position: absolute; width:350px; left:-50%">
      <v-card v-if="shouldRender">
        <v-card-title class="title font-weight-regular justify-space-between">
          <span>{{ currentTitle }}</span>
          <v-avatar
            color="primary lighten-2"
            class="subheading white--text"
            size="24"
            v-text="step"
          ></v-avatar>
        </v-card-title>

        <v-window>
          <v-window-item :value="1">
            <v-card-text>
              <span class="caption grey--text text--darken-1">File: {{dataFilename}}</span>
              <div>
                <v-btn color="generate" v-on:click="createData">Create File</v-btn>
                <label for="data-file-upload" class="custom-button">Find File</label>
                <input
                  id="data-file-upload"
                  type="file"
                  prepend-icon="attach_file"
                  :accept="acceptData"
                  :multiple="false"
                  style="background-color: yellow;"
                  @change="onDataFileChange"
                >
              </div>
            </v-card-text>
          </v-window-item>
        </v-window>

        <v-divider></v-divider>

        <v-card-actions>
          <v-btn :disabled="block_back" flat @click="back">Back</v-btn>
          <v-spacer></v-spacer>
          <v-btn :disabled="block_next" color="primary" depressed @click="save">Save</v-btn>
          <v-btn :disabled="block_next" color="secondary" depressed @click="load">Load</v-btn>
        </v-card-actions>
      </v-card>
    </div>
  </div>
</template>
<script>
const { dialog, app } = require("electron").remote;
var fs = require("fs");
export default {
  props: ["shouldRender"],
  data() {
    return {
      dataFilename: "",
      acceptData: ".dat",
      rules: {
        required: value => !!value || "Required.",
        min: v => v.length >= 8 || "Min 8 characters"
      }
    };
  },
  computed: {
    currentTitle() {
      return "Save / Load";
    },
    block_back: function() {
      return false;
    },
    block_next: function() {
      return this.dataFilename.length == 0;
    }
  },
  methods: {
    createData: function() {
      const options = {
        defaultPath: "./messagedata.dat"
      };
      dialog.showSaveDialog(null, options, this.writeData);
    },
    writeData: function(path) {
      console.log("writeData " + path);
      if (path && path.length > 0) {
        try {
          let buffer = new Buffer("abc", "utf8");
          let fd = fs.openSync(path, "w");
          fs.writeSync(fd, buffer, 0, buffer.length);
          fs.close(fd);
          this.dataFilename = path;
        } catch (e) {
          console.log("fail " + e);
        }
      }
    },
    getFormData(files) {
      const data = new FormData();
      [...files].forEach(file => {
        data.append("data", file, file.name); // currently only one file at a time
      });
      return data;
    },
    onDataFileChange($event) {
      const files = $event.target.files || $event.dataTransfer.files;

      const form = this.getFormData(files);
      if (files) {
        if (files.length > 0) {
          this.dataFilename = [...files].map(file => file.name).join(", ");
        } else {
          this.dataFilename = null;
        }
      } else {
        this.dataFilename = $event.target.value.split("\\").pop();
      }
      console.log("data file " + this.dataFilename);
    },
    back: function() {
      this.$emit("load", "");
    },
    save: function() {
      this.$emit("save", this.dataFilename);
    },
    load: function() {
      this.$emit("load", this.dataFilename);
    }
  }
};
</script>

<style>
input[type="file"] {
  display: none;
}
.custom-button {
  box-shadow: 0px 3px 1px -2px rgba(0, 0, 0, 0.2),
    0px 2px 2px 0px rgba(0, 0, 0, 0.14), 0px 1px 5px 0px rgba(0, 0, 0, 0.12);
  align-items: center;
  padding: 0 16px;
  border-radius: 2px;
  display: inline-flex;
  cursor: pointer;
  height: 36px;
  flex: 0 0 auto;
  font-size: 14px;
  font-weight: 500;
  justify-content: center;
  margin: 6px 8px;
  min-width: 88px;
  outline: 0;
  text-transform: uppercase;
  text-decoration: none;
  transition: 0.3s cubic-bezier(0.25, 0.8, 0.5, 1), color 1ms;
  position: relative;
  vertical-align: middle;
  color: rgba(0, 0, 0, 0.87);
  background-color: #f5f5f5;
  border-style: none;
  -webkit-font-smoothing: antialiased;
  -webkit-tap-highlight-color: rgba(0, 0, 0, 0);
  -webkit-appearance: button;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;
}
.custom-file-upload::selection {
  background-color: #b3d4fc;
  color: #000;
  text-shadow: none;
}
</style>