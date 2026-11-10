import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import * as Icons from '@element-plus/icons-vue'

import '@fontsource/playfair-display/600.css'
import '@fontsource/playfair-display/700.css'
import '@fontsource/noto-serif-sc/chinese-simplified-600.css'
import '@fontsource/noto-sans-sc/chinese-simplified-400.css'
import '@fontsource/noto-sans-sc/chinese-simplified-500.css'

import './styles/tokens.css'
import './styles/base.css'
import './styles/theme.css'

import App from './App.vue'
import router from './router'

const app = createApp(App)
for (const [name, comp] of Object.entries(Icons)) {
  app.component(name, comp)
}
app.use(ElementPlus, { locale: zhCn })
app.use(router)
app.mount('#app')
