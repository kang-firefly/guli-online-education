import http from './http'

/* ---------- 首页 ---------- */
export const getIndex = () => http.get('/api/index')

/* ---------- 课程 ---------- */
export const pageCourses = (params) => http.get('/api/course/page', { params })
export const getCourse = (id) => http.get(`/api/course/${id}`)

/* ---------- 讲师 ---------- */
export const pageTeachers = (params) => http.get('/api/teacher/page', { params })
export const getTeacher = (id) => http.get(`/api/teacher/${id}`)

/* ---------- 会员 ---------- */
export const register = (data) => http.post('/api/member/register', data)
export const login = (data) => http.post('/api/member/login', data)
export const me = () => http.get('/api/member/me')

/* ---------- 小节播放 ---------- */
export const getSectionPlay = (sectionId) => http.get(`/api/section/${sectionId}/play`)

/* ---------- 订单 ---------- */
export const createOrder = (courseId) => http.post('/api/order', { courseId })
export const getOrder = (orderNo) => http.get(`/api/order/${orderNo}`)
export const payOrder = (orderNo) => http.post(`/api/order/${orderNo}/pay`)
export const cancelOrder = (orderNo) => http.post(`/api/order/${orderNo}/cancel`)
export const myCourses = () => http.get('/api/order/my')
