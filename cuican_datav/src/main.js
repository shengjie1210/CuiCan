import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import dataV from '@kjgl77/datav-vue3'
import '@kjgl77/datav-vue3/dist/style.css'
import 'bootstrap-icons/font/bootstrap-icons.css'
import './assets/styles/main.css'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.use(dataV)
app.mount('#app')
