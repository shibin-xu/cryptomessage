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
              <v-text-field
                outline
                label="Email"
                append-outer-icon="mail"
                value="john@place.com"
                v-model="email"
              ></v-text-field>
              <span
                class="caption grey--text text--darken-1"
              >This is the email you will use to login to your Vuetify account</span>
            </v-card-text>
          </v-window-item>

          <v-window-item :value="2">
            <v-card-text>
              <v-text-field
                :append-icon="showPW ? 'visibility' : 'visibility_off'"
                :rules="[rules.required, rules.min]"
                :type="showPW ? 'text' : 'password'"
                v-model="passw"
                name="input-10-2"
                label="Password"
                hint="At least 8 characters"
                value="wqfasds"
                class="input-group--focused"
                @click:append="showPW = !showPW"
              ></v-text-field>
              <span
                class="caption grey--text text--darken-1"
              >Please enter a password for your account</span>
            </v-card-text>
          </v-window-item>
          <v-window-item :value="3">
            <v-card-text>
              <span class="caption grey--text text--darken-1">Keyfile: {{filename}}</span>
              <div v-if="needsKey">
                <v-btn color="generate" v-on:click="save">Generate</v-btn>
                <label for="file-upload" class="custom-button">Find File</label>
                <input
                  id="file-upload"
                  type="file"
                  prepend-icon="attach_file"
                  :accept="accept"
                  :multiple="false"
                  style="background-color: yellow;"
                  @change="onFileChange"
                >
              </div>
            </v-card-text>
          </v-window-item>
          <v-window-item :value="4">
            <div class="pa-3 text-xs-center">
              <v-img
                class="mb-3"
                contain
                height="128"
                src="https://cdn.vuetifyjs.com/images/logos/v.svg"
              ></v-img>
              <h3 class="title font-weight-light mb-2">Welcome to CryptoXT</h3>
              <span class="caption grey--text">Thanks for signing up!</span>
            </div>
          </v-window-item>
        </v-window>

        <v-divider></v-divider>

        <v-card-actions>
          <v-btn :disabled="step === 1" flat @click="back">Back</v-btn>
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
      filename: "",
      accept: ".pub, .key, .txt",
      showPW: false,
      needsKey: true,
      nextkey: "a324b2341b4",
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
          return "Sign-up";
        case 2:
          return "Create a password";
        case 3:
          return "Public Key";
        default:
          return "Connect";
      }
    }
  },
  methods: {
    save: function() {
      const options = {
        defaultPath: "./mykey.pub"
      };
      dialog.showSaveDialog(null, options, this.write);
    },
    write: function(path) {
      if (path.length > 0) {
        try {
          let buffer = new Buffer(this.nextkey, "utf8");
          let fd = fs.openSync(path, "w");
          fs.writeSync(fd, buffer, 0, buffer.length);
          fs.close(fd);
          this.filename = path;
          this.contents = this.nextkey;
          this.needsKey = false;
          this.$emit("keyfile", this.filename, this.contents);
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
    onFileChange($event) {
      const files = $event.target.files || $event.dataTransfer.files;
      const form = this.getFormData(files);
      if (files) {
        if (files.length > 0) {
          this.filename = [...files].map(file => file.name).join(", ");
        } else {
          this.filename = null;
        }
      } else {
        this.filename = $event.target.value.split("\\").pop();
      }
      this.needsKey = false;
      this.$emit("keyfile", this.filename, this.contents);
    },
    back: function() {
      if (this.step == 3 && this.needsKey == false) {
        this.needsKey = true;
        this.filename = "";
      } else {
        this.step--;
      }
    },
    done: function() {
      this.step++;
      if (this.step == 5) {
        this.$emit("connect", this.email, this.passw);
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