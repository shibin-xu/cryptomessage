import Vue from 'vue'
import App from './App'

import VueMaterial from 'vue-material'

if (!process.env.IS_WEB) Vue.use(require('vue-electron'))
Vue.config.productionTip = false

Vue.use(VueMaterial)

new Vue(App).$mount('#app')