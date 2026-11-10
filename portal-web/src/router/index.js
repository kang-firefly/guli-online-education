import { createRouter, createWebHistory } from 'vue-router'
import { auth } from '@/store/auth'

const routes = [
  {
    path: '/',
    component: () => import('@/layouts/PortalLayout.vue'),
    children: [
      { path: '', name: 'home', component: () => import('@/views/HomeView.vue'), meta: { title: '首页' } },
      { path: 'courses', name: 'courses', component: () => import('@/views/CourseListView.vue'), meta: { title: '全部课程' } },
      { path: 'course/:id', name: 'course', component: () => import('@/views/CourseDetailView.vue'), meta: { title: '课程详情' } },
      { path: 'course/:id/play/:sectionId?', name: 'play', component: () => import('@/views/PlayView.vue'), meta: { title: '在线学习' } },
      { path: 'order/:orderNo', name: 'order', component: () => import('@/views/OrderPayView.vue'), meta: { title: '订单确认', requiresAuth: true } },
      { path: 'teachers', name: 'teachers', component: () => import('@/views/TeacherListView.vue'), meta: { title: '名师' } },
      { path: 'teacher/:id', name: 'teacher', component: () => import('@/views/TeacherDetailView.vue'), meta: { title: '讲师详情' } },
      { path: 'login', name: 'login', component: () => import('@/views/LoginView.vue'), meta: { title: '登录' } },
      { path: 'register', name: 'register', component: () => import('@/views/RegisterView.vue'), meta: { title: '注册' } },
      { path: 'me', name: 'me', component: () => import('@/views/MemberView.vue'), meta: { title: '我的', requiresAuth: true } }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: (to, from, saved) => saved || { top: 0 }
})

router.beforeEach((to) => {
  if (to.meta.requiresAuth && !auth.isLogin()) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  // 已登录的人不该再看到登录/注册页
  if ((to.path === '/login' || to.path === '/register') && auth.isLogin()) {
    return { path: '/' }
  }
  document.title = to.meta.title ? `${to.meta.title} · 谷粒学苑` : '谷粒学苑'
  return true
})

export default router
