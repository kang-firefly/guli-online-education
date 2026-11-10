import { createRouter, createWebHistory } from 'vue-router'
import { auth } from '@/store/auth'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/LoginView.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '', redirect: '/teacher' },
      {
        path: 'teacher',
        name: 'teacher',
        component: () => import('@/views/TeacherView.vue'),
        meta: { title: '讲师管理', eyebrow: 'Teacher Management' }
      },
      {
        path: 'subject',
        name: 'subject',
        component: () => import('@/views/SubjectView.vue'),
        meta: { title: '课程分类', eyebrow: 'Course Taxonomy' }
      },
      {
        path: 'course',
        name: 'course',
        component: () => import('@/views/CourseView.vue'),
        meta: { title: '课程管理', eyebrow: 'Course Management' }
      },
      {
        path: 'banner',
        name: 'banner',
        component: () => import('@/views/BannerView.vue'),
        meta: { title: '首页运营', eyebrow: 'Home Operations' }
      }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/teacher' }
]

const router = createRouter({
  history: createWebHistory('/admin-ui/'),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

router.beforeEach((to) => {
  if (to.meta.requiresAuth && !auth.isLogin()) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (to.path === '/login' && auth.isLogin()) {
    return { path: '/teacher' }
  }
  document.title = to.meta.title ? `${to.meta.title} · 谷粒学苑后台` : '谷粒学苑 · 后台管理'
  return true
})

export default router
