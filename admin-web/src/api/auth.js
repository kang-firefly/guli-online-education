import http from './http'

/** 后台登录 */
export const login = (data) => http.post('/admin/login', data)

/** 当前登录管理员 */
export const getAdminInfo = () => http.get('/admin/info')

/** 退出（JWT 无状态，仅通知后端记日志，本地清 token 即可） */
export const logout = () => http.post('/admin/logout')
