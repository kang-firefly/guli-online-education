import http from './http'
import { uploadImage } from './teacher'

/** 轮播列表（按 sort 升序） */
export const listBanners = () => http.get('/admin/banner/list')

export const createBanner = (data) => http.post('/admin/banner', data)

export const updateBanner = (id, data) => http.put(`/admin/banner/${id}`, data)

export const removeBanner = (id) => http.delete(`/admin/banner/${id}`)

/** 上传轮播图，复用 OSS 上传接口（dir=banner） */
export const uploadBannerImage = (file) => uploadImage(file, 'banner')
