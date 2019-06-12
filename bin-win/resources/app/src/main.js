import Vue from 'vue'
import App from './App'
import Vuetify from 'vuetify'

if (!process.env.IS_WEB) Vue.use(require('vue-electron'))
Vue.config.productionTip = false

Vue.use(Vuetify)

new Vue(App).$mount('#app')