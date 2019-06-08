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

        <v-window v-model="step">
          <v-window-item :value="1">
            <v-card-text>
              <span class="caption grey--text text--darken-1">Public Key: {{publicFilename}}</span>
              <div v-if="needsPublicKey">
                <v-btn color="generate" v-on:click="savePublic">Generate</v-btn>
                <label for="public-file-upload" class="custom-button">Find Public</label>
                <input
                  id="public-file-upload"
                  type="file"
                  prepend-icon="attach_file"
                  :accept="acceptPublic"
                  :multiple="false"
                  style="background-color: yellow;"
                  @change="onPublicFileChange"
                >
              </div>
            </v-card-text>
          </v-window-item>
          <v-window-item :value="2">
            <v-card-text>
              <span class="caption grey--text text--darken-1">Private Key: {{privateFilename}}</span>
              <div v-if="needsPrivateKey">
                <v-btn color="generate" v-on:click="savePrivate">Generate</v-btn>
                <label for="private-file-upload" class="custom-button">Find Private</label>
                <input
                  id="private-file-upload"
                  type="file"
                  prepend-icon="attach_file"
                  :accept="acceptPrivate"
                  :multiple="false"
                  style="background-color: orange;"
                  @change="onPrivateFileChange"
                >
              </div>
            </v-card-text>
          </v-window-item>
          <v-window-item :value="3">
            <div class="pa-3 text-xs-center">
              <v-img
                class="mb-3"
                contain
                height="128"
                src="https://cdn.vuetifyjs.com/images/logos/v.svg"
              ></v-img>
              <h3 class="title font-weight-light mb-2">Welcome to CryptoXT</h3>
            </div>
          </v-window-item>
        </v-window>

        <v-divider></v-divider>

        <v-card-actions>
          <v-btn flat @click="back">Back</v-btn>
          <v-spacer></v-spacer>
          <v-btn color="primary" depressed @click="done">Next</v-btn>
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
      email: "",
      passw: "",
      showPW: false,
      publicFilename: "",
      privateFilename: "",
      acceptPublic: ".pub, .key, .txt",
      acceptPrivate: ".ppk, .key, .txt",
      needsPublicKey: true,
      needsPrivateKey: true,
      nextkey: "abc123",
      rules: {
        required: value => !!value || "Required.",
        min: v => v.length >= 8 || "Min 8 characters"
      },
      step: 1
    };
  },
  computed: {
    currentTitle() {
      switch (this.step) {
        case 1:
          return "Public Key";
        case 2:
          return "Private Key";
        default:
          return "Connect";
      }
    }
  },
  methods: {
    savePublic: function() {
      const options = {
        defaultPath: "./mykey.pub",
      };
      dialog.showSaveDialog(null, options, this.writePublic);
    },
    savePrivate: function() {
      const options = {
        defaultPath: "./mykey.ppk",
      };
      dialog.showSaveDialog(null, options, this.writePrivate);
    },
    writePublic: function(path) {
      if (path.length > 0) {
        try {
          let buffer = new Buffer(this.nextkey, "utf8");
          let fd = fs.openSync(path, "w");
          fs.writeSync(fd, buffer, 0, buffer.length);
          fs.close(fd);
          this.publicFilename = path;
          this.needsPublicKey = false;
          this.contents = this.nextkey;
          
        } catch (e) {
          console.log("fail " + e);
        }
      }
    },
    writePrivate: function(path) {
      if (path.length > 0) {
        try {
          let buffer = new Buffer(this.nextkey, "utf8");
          let fd = fs.openSync(path, "w");
          fs.writeSync(fd, buffer, 0, buffer.length);
          fs.close(fd);
          this.privateFilename = path;
          this.needsPrivateKey = false;
          this.$emit("keyfile", this.publicFilename, this.privateFilename);
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
    onPublicFileChange($event) {
      const files = $event.target.files || $event.dataTransfer.files;
      console.log("pub file ");
      const form = this.getFormData(files);
      if (files) {
        if (files.length > 0) {
          this.publicFilename = [...files].map(file => file.name).join(", ");
        } else {
          this.publicFilename = null;
        }
      } else {
        this.publicFilename = $event.target.value.split("\\").pop();
      }
    },
    onPrivateFileChange($event) {
      const files = $event.target.files || $event.dataTransfer.files;
      
      console.log("pri file ");
      const form = this.getFormData(files);
      if (files) {
        if (files.length > 0) {
          this.privateFilename = [...files].map(file => file.name).join(", ");
        } else {
          this.privateFilename = null;
        }
      } else {
        this.privateFilename = $event.target.value.split("\\").pop();
      }
    },
    can_back: function() {
      return true;
    },
    back: function() {
      if (this.step == 1 && this.needsPublicKey == false) {
        this.needsPublicKey = true;
        this.publicFilename = "";
      } else if (this.step == 2 && this.needsPrivateKey == false) {
        this.needsPrivateKey = true;
        this.privateFilename = "";
      } else if(this.step == 1) {
        this.$emit("keyfile", this.publicFilename, this.privateFilename);
      } else {
        this.step--;
      }
    },
    can_next: function() {
      if (this.step == 1 && this.needsPublicKey == true) {
        return false;
      } else if (this.step == 2 && this.needsPrivateKey == true) {
        return false;
      }
      return true;
    },
    done: function() {
      this.step++;
      if (this.step == 4) {
        this.$emit("keyfile", this.publicFilename, this.privateFilename);
        this.step = 1;
      }
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