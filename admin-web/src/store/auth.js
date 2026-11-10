import { reactive } from 'vue'

const TOKEN_KEY = 'guli_admin_token'
const USER_KEY = 'guli_admin_user'

function readUser() {
  try {
    return JSON.parse(localStorage.getItem(USER_KEY) || 'null')
  } catch {
    return null
  }
}

/**
 * 登录态响应式化：localStorage 没有响应式，
 * 直接用它喂 computed 会导致登录/退出后顶栏不刷新。
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
