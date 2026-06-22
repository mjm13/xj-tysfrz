import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import App from './App.vue'
import router from './router'
import { useAuthStore } from '@/stores/auth'

import '@/assets/styles/tokens.css'
import '@/assets/styles/global.scss'
import '@/assets/styles/element-overrides.scss'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.use(ElementPlus)

useAuthStore(pinia).bindTokenGetter()

app.mount('#app')
