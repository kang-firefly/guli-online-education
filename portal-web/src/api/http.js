import axios from 'axios'
import { ElMessage } from 'element-plus'
import { auth } from '@/store/auth'

const http = axios.create({ baseURL: '/', timeout: 20000 })

http.interceptors.request.use((config) => {
  if (auth.token) {
    config.headers.Authorization = `Bearer ${auth.token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    const body = response.data
    if (body && body.success) return body.data

    // 前台接口的未登录不该跳转，只提示
    if (body && body.code === 28004) {
      auth.clear()
      return Promise.reject(Object.assign(new Error(body.message || '未登录'), { silent: true }))
    }
    ElMessage.error(body?.message || '请求失败')
    return Promise.reject(new Error(body?.message || '请求失败'))
  },
  (error) => {
    const msg = error.response ? `请求失败（${error.response.status}）` : '网络异常，请稍后重试'
    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

export default http
