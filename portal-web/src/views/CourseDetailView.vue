<template>
  <div class="detail">
    <div class="wrap">
      <nav class="crumb">
        <RouterLink to="/">首页</RouterLink>
        <span>/</span>
        <RouterLink to="/courses">课程</RouterLink>
        <span>/</span>
        <em>{{ course?.title || '课程详情' }}</em>
      </nav>

      <div v-if="loading" class="loading">正在加载课程…</div>

      <template v-else-if="course">
        <!-- 头部 -->
        <header class="hero">
          <div class="hero__media">
            <img v-if="course.cover" :src="course.cover" :alt="course.title" />
            <span v-else class="hero__noimg">暂无封面</span>
          </div>

          <div class="hero__info">
            <span class="eyebrow">{{ course.subjectParentTitle }} / {{ course.subjectTitle }}</span>
            <h1 class="hero__title">{{ course.title }}</h1>
            <p class="hero__desc">{{ course.description || '讲师还没有填写简介。' }}</p>

            <div class="hero__meta">
              <span class="mono">{{ course.lessonNum }} 课时</span>
              <span class="sep" />
              <span class="mono">{{ course.sectionCount }} 小节</span>
              <span class="sep" />
              <span class="mono">{{ course.viewCount }} 次浏览</span>
              <span class="sep" />
              <span class="mono">{{ course.buyCount }} 人学过</span>
            </div>

            <div class="hero__buy">
              <span class="price mono">{{ priceText }}</span>
              <button class="btn" type="button" :disabled="buying" @click="onPrimary">
                {{ primaryText }}
              </button>
              <span class="tip">{{ tipText }}</span>
            </div>
          </div>
        </header>

        <!-- 主体：大纲 + 讲师侧栏 -->
        <div class="body">
          <section class="outline">
            <div class="section-head">
              <div>
                <span class="eyebrow">Syllabus</span>
                <h2>课程大纲</h2>
                <p>共 {{ course.chapters.length }} 章 {{ course.sectionCount }} 节</p>
              </div>
            </div>

            <div v-if="!course.chapters.length" class="outline__empty">
              讲师还在编排大纲。
            </div>

            <article v-for="(ch, ci) in course.chapters" :key="ch.id" class="chapter">
              <header class="chapter__head">
                <span class="chapter__idx mono">{{ String(ci + 1).padStart(2, '0') }}</span>
                <h3 class="chapter__title">{{ ch.title }}</h3>
                <span class="chapter__count mono">{{ ch.sections.length }} 节</span>
              </header>

              <ul class="sections">
                <li v-for="(s, si) in ch.sections" :key="s.id" class="section">
                  <span class="section__idx mono">{{ ci + 1 }}.{{ si + 1 }}</span>
                  <span class="section__title">{{ s.title }}</span>
                  <span class="section__dur mono">{{ s.duration || '—' }}</span>
                </li>
              </ul>
            </article>
          </section>

          <aside class="aside">
            <div class="teacher-card">
              <span class="eyebrow">Instructor</span>
              <div class="teacher-card__main">
                <span class="teacher-card__avatar">
                  <img v-if="teacher?.avatar" :src="teacher.avatar" :alt="teacher.name" />
                  <template v-else>{{ course.teacherName?.slice(0, 1) }}</template>
                </span>
                <div>
                  <h3 class="teacher-card__name">{{ course.teacherName }}</h3>
                  <p class="teacher-card__level">{{ teacher?.level === 2 ? '高级讲师' : '讲师' }}</p>
                </div>
              </div>
              <p class="teacher-card__intro">{{ teacher?.intro || '这位讲师还没有填写简介。' }}</p>
              <RouterLink v-if="teacher" :to="`/teacher/${teacher.id}`" class="teacher-card__link">
                查看讲师主页 →
              </RouterLink>
            </div>

            <div class="facts">
              <div class="fact">
                <span class="fact__label">价格</span>
                <span class="fact__value mono">{{ priceText }}</span>
              </div>
              <div class="fact">
                <span class="fact__label">课时</span>
                <span class="fact__value mono">{{ course.lessonNum }}</span>
              </div>
              <div class="fact">
                <span class="fact__label">上线时间</span>
                <span class="fact__value mono">{{ course.gmtCreate?.slice(0, 10) }}</span>
              </div>
            </div>
          </aside>
        </div>
      </template>

      <div v-else class="empty">
        <span class="empty__mark">404</span>
        <p class="empty__title">课程不存在或已下架</p>
        <RouterLink to="/courses" class="empty__link">去看看其他课程 →</RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCourse, getTeacher, createOrder } from '@/api/portal'
import { auth } from '@/store/auth'

const route = useRoute()
const router = useRouter()
const loading = ref(true)
const course = ref(null)
const teacher = ref(null)

const priceText = computed(() => {
  const p = Number(course.value?.price ?? 0)
  return p === 0 ? '免费' : `¥ ${p.toFixed(2)}`
})

async function load(id) {
  loading.value = true
  try {
    course.value = await getCourse(id)
    if (course.value.teacherId) {
      teacher.value = await getTeacher(course.value.teacherId).catch(() => null)
    }
  } catch {
    course.value = null
  } finally {
    loading.value = false
  }
}

const buying = ref(false)

/** 主按钮文案：免费学习 / 继续学习 / 继续支付 / 立即购买 / 登录后购买 */
const primaryText = computed(() => {
  const c = course.value
  if (!c) return '立即报名'
  if (c.free) return '免费学习'
  if (c.purchased) return '继续学习'
  if (c.unpaidOrderNo) return '继续支付'
  const price = Number(c.price ?? 0).toFixed(0)
  return auth.isLogin() ? `立即购买 ¥${price}` : '登录后购买'
})

const tipText = computed(() => {
  const c = course.value
  if (!c) return ''
  if (c.free) return '免费课程，登录与否都可学习'
  if (c.purchased) return '已购买，可在「我的」中查看'
  if (c.unpaidOrderNo) return '你有一笔待支付订单'
  return '演示环境：模拟支付，不产生真实交易'
})

async function onPrimary() {
  const c = course.value
  if (!c) return

  // 可学（免费 或 已购）→ 直接进播放页
  if (c.free || c.purchased) {
    router.push({ name: 'play', params: { id: c.id } })
    return
  }
  if (!auth.isLogin()) {
    ElMessage.warning('请先登录')
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  // 已有待支付订单 → 直接去支付页
  if (c.unpaidOrderNo) {
    router.push({ name: 'order', params: { orderNo: c.unpaidOrderNo } })
    return
  }
  // 下单
  buying.value = true
  try {
    const order = await createOrder(c.id)
    router.push({ name: 'order', params: { orderNo: order.orderNo } })
  } finally {
    buying.value = false
  }
}

onMounted(() => load(route.params.id))
watch(() => route.params.id, (id) => id && load(id))
</script>

<style scoped>
.detail {
  position: relative;
  z-index: 2;
  padding: var(--sp-5) 0 var(--sp-9);
}

.crumb {
  display: flex;
  align-items: center;
  gap: var(--sp-2);
  font-size: var(--fs-xs);
  color: var(--muted);
  padding: var(--sp-3) 0 var(--sp-5);
}
.crumb a:hover { color: var(--green); }
.crumb em { font-style: normal; color: var(--ink-2); }

.loading { padding: var(--sp-9) 0; color: var(--muted); }

/* 头部 */
.hero {
  display: grid;
  grid-template-columns: 420px 1fr;
  gap: var(--sp-7);
  padding-bottom: var(--sp-6);
  border-bottom: 1px solid var(--line-strong);
}

.hero__media {
  aspect-ratio: 16 / 10;
  background: var(--paper-3);
  border: 1px solid var(--line);
  overflow: hidden;
  display: grid;
  place-items: center;
}
.hero__media img { width: 100%; height: 100%; object-fit: cover; }
.hero__noimg { font-size: var(--fs-xs); color: var(--muted-2); }

.hero__title {
  font-size: clamp(28px, 3.4vw, 44px);
  line-height: 1.14;
  margin: var(--sp-2) 0 var(--sp-3);
}

.hero__desc {
  font-size: var(--fs-md);
  line-height: 1.9;
  color: var(--ink-2);
  max-width: 52ch;
}

.hero__meta {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
  margin-top: var(--sp-5);
  font-size: var(--fs-xs);
  color: var(--muted);
}
.sep { width: 1px; height: 12px; background: var(--line-strong); }

.hero__buy {
  display: flex;
  align-items: center;
  gap: var(--sp-5);
  margin-top: var(--sp-5);
  padding-top: var(--sp-5);
  border-top: 1px solid var(--line);
}

.price {
  font-size: var(--fs-3xl);
  color: var(--green);
  line-height: 1;
}

.btn {
  height: 46px;
  padding: 0 var(--sp-6);
  background: var(--amber);
  color: #fff;
  border: 0;
  font-family: var(--font-body);
  font-size: var(--fs-sm);
  letter-spacing: 0.08em;
  cursor: pointer;
  transition: background var(--dur-fast), transform var(--dur-fast);
}
.btn:hover { background: var(--amber-2); }
.btn:active { transform: translateY(1px); }

.tip { font-size: var(--fs-2xs); color: var(--muted-2); }

/* 主体 */
.body {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: var(--sp-7);
  padding-top: var(--sp-7);
}

.outline .section-head { margin-bottom: var(--sp-5); }

.outline__empty {
  padding: var(--sp-6) 0;
  color: var(--muted);
  font-size: var(--fs-sm);
}

.chapter { border-bottom: 1px solid var(--line); }

.chapter__head {
  display: flex;
  align-items: baseline;
  gap: var(--sp-3);
  padding: var(--sp-4) 0;
}

.chapter__idx {
  font-size: var(--fs-xs);
  color: var(--amber);
  letter-spacing: 0.1em;
}

.chapter__title {
  font-family: var(--font-serif);
  font-size: var(--fs-xl);
  font-weight: 600;
  flex: 1;
}

.chapter__count { font-size: var(--fs-2xs); color: var(--muted); }

.sections { list-style: none; margin: 0 0 var(--sp-3); padding: 0 0 0 var(--sp-6); }

.section {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
  padding: var(--sp-2) 0;
  font-size: var(--fs-sm);
  color: var(--ink-2);
  border-bottom: 1px dashed var(--line);
}
.section:last-child { border-bottom: 0; }

.section__idx { color: var(--muted-2); font-size: var(--fs-2xs); min-width: 34px; }
.section__title { flex: 1; }
.section__dur { color: var(--muted); font-size: var(--fs-xs); }

/* 侧栏 */
.aside { display: flex; flex-direction: column; gap: var(--sp-5); }

.teacher-card {
  background: var(--paper-2);
  border: 1px solid var(--line);
  padding: var(--sp-5);
}

.teacher-card__main {
  display: flex;
  align-items: center;
  gap: var(--sp-3);
  margin-top: var(--sp-3);
}

.teacher-card__avatar {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  overflow: hidden;
  display: grid;
  place-items: center;
  background: var(--paper-3);
  border: 1px solid var(--line);
  font-family: var(--font-serif);
  color: var(--green);
}
.teacher-card__avatar img { width: 100%; height: 100%; object-fit: cover; }

.teacher-card__name { font-family: var(--font-serif); font-size: var(--fs-lg); font-weight: 600; }
.teacher-card__level { font-size: var(--fs-2xs); color: var(--amber); letter-spacing: 0.1em; }

.teacher-card__intro {
  margin-top: var(--sp-3);
  font-size: var(--fs-xs);
  color: var(--muted);
  line-height: 1.8;
}

.teacher-card__link {
  display: inline-block;
  margin-top: var(--sp-3);
  font-size: var(--fs-xs);
  color: var(--green);
  border-bottom: 1px solid var(--green);
  padding-bottom: 1px;
}

.facts { border-top: 1px solid var(--line); }

.fact {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  padding: var(--sp-3) 0;
  border-bottom: 1px solid var(--line);
}
.fact__label { font-size: var(--fs-xs); color: var(--muted); }
.fact__value { font-size: var(--fs-md); color: var(--ink); }

/* 空态 */
.empty {
  padding: var(--sp-9) 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--sp-2);
}
.empty__mark { font-family: var(--font-mono); font-size: 44px; color: var(--line-strong); }
.empty__title { font-family: var(--font-serif); font-size: var(--fs-xl); color: var(--ink-2); }
.empty__link { font-size: var(--fs-sm); color: var(--green); border-bottom: 1px solid var(--green); }

@media (max-width: 980px) {
  .hero { grid-template-columns: 1fr; gap: var(--sp-5); }
  .body { grid-template-columns: 1fr; }
}
</style>
