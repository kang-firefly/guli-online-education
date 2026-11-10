import http from './http'

/** 分页 + 条件查询 */
export const pageTeachers = (params) => http.get('/admin/teacher/page', { params })

/** 全部讲师（下拉用） */
export const allTeachers = () => http.get('/admin/teacher/all')

export const getTeacher = (id) => http.get(`/admin/teacher/${id}`)

export const createTeacher = (data) => http.post('/admin/teacher', data)

export const updateTeacher = (data) => http.put('/admin/teacher', data)

export const removeTeacher = (id) => http.delete(`/admin/teacher/${id}`)

export const removeTeachers = (ids) => http.delete('/admin/teacher/batch', { data: ids })

/**
 * 上传图片到 OSS，返回公网 URL
 * @param {File} file
 * @param {'avatar'|'cover'|'banner'} dir
 */
export const uploadImage = (file, dir = 'avatar') => {
  const form = new FormData()
  form.append('file', file)
  return http.post(`/admin/upload/image?dir=${dir}`, form, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
