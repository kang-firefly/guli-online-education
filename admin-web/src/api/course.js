import http from './http'

export const pageCourses = (params) => http.get('/admin/course/page', { params })

export const getCourse = (id) => http.get(`/admin/course/${id}`)

export const createCourse = (data) => http.post('/admin/course', data)

export const updateCourse = (data) => http.put('/admin/course', data)

export const publishCourse = (id) => http.post(`/admin/course/${id}/publish`)

export const offlineCourse = (id) => http.put(`/admin/course/${id}/offline`)

export const removeCourse = (id) => http.delete(`/admin/course/${id}`)

/* ---------- 大纲 ---------- */

export const chapterList = (courseId) => http.get(`/admin/chapter/list/${courseId}`)

export const createChapter = (data) => http.post('/admin/chapter', data)

export const updateChapter = (data) => http.put('/admin/chapter', data)

export const removeChapter = (id) => http.delete(`/admin/chapter/${id}`)

export const createSection = (data) => http.post('/admin/section', data)

export const updateSection = (data) => http.put('/admin/section', data)

export const removeSection = (id) => http.delete(`/admin/section/${id}`)

/** 设置/清除小节视频链接（只支持 B 站完整链接，传空字符串表示清除） */
/** 后台大纲：含每节的视频链接（前台接口不返回） */
export const listChapters = (courseId) => http.get(`/admin/chapter/list/${courseId}`)

export const setSectionVideo = (sectionId, videoUrl) =>
  http.put(`/admin/chapter/section/${sectionId}/video`, { videoUrl })
