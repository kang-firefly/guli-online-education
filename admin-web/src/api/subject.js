import http from './http'

/** 两级分类树 */
export const subjectTree = () => http.get('/admin/subject/tree')

export const createSubject = (data) => http.post('/admin/subject', data)

export const updateSubject = (data) => http.put('/admin/subject', data)

export const removeSubject = (id) => http.delete(`/admin/subject/${id}`)
