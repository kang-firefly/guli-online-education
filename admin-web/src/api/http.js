import axios from 'axios'
import { ElMessage } from 'element-plus'
import { auth } from '@/store/auth'

/**
 * 统一请求封装
 * 后端返回结构：{ success, code, message, data }
 * 这里把 data 直接抛给调用方，业务层不用每次都 .data
 */
const http = axios.create({
  baseURL: '/',
  timeout: 20000
})

http.interceptors.request.use((config) => {
  const token = auth.token
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    const body = response.data

    if (body && body.success) {
      return body.data
    }

    // 未登录 / token 过期：清掉本地态并回登录页
    if (body && body.code === 28004) {
      auth.clear()
      // 后台部署在子路径（/admin-ui/）下，必须用 BASE_URL 拼，
      // 否则会跳到站根的 /login（那是前台门户的登录页）
      const loginPath = `${import.meta.env.BASE_URL}login`
      if (!location.pathname.startsWith(loginPath)) {
        ElMessage.warning(body.message || '登录已过期，请重新登录')
        location.assign(loginPath)
      }
      return Promise.reject(new Error(body.message || '未登录'))
    }

    ElMessage.error(body?.message || '请求失败')
    return Promise.reject(new Error(body?.message || '请求失败'))
  },
  (error) => {
    const msg = error.response
      ? `请求失败（${error.response.status}）`
      : '网络异常，请检查后端服务是否启动'
    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

export default http
