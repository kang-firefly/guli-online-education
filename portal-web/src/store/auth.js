import { reactive } from 'vue'

const TOKEN_KEY = 'guli_member_token'
const USER_KEY = 'guli_member_user'

function readUser() {
  try {
    return JSON.parse(localStorage.getItem(USER_KEY) || 'null')
  } catch {
    return null
  }
}

/**
 * 登录态必须是响应式的：
 * 之前用 getter 直接读 localStorage，localStorage 本身没有响应式，
 * 组件里的 computed(() => auth.isLogin()) 会永远缓存第一次的值，
 * 导致登录/退出后顶栏不刷新（必须手动刷新页面才变）。
 */
const state = reactive({
  token: localStorage.getItem(TOKEN_KEY) || '',
  user: readUser()
})

export const auth = {
  get token() {
    return state.token
  },
  get user() {
    return state.user
  },
  set(token, user) {
    state.token = token
    state.user = user || {}
    localStorage.setItem(TOKEN_KEY, token)
    localStorage.setItem(USER_KEY, JSON.stringify(state.user))
  },
  clear() {
    state.token = ''
    state.user = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  },
  isLogin() {
    return !!state.token
  }
}
