<template>
  <div class="play">
    <!-- 顶部：返回 + 课程名 -->
    <header class="topbar">
      <RouterLink :to="`/course/${courseId}`" class="topbar__back">← 返回课程</RouterLink>
      <span class="topbar__title">{{ course?.title || '加载中…' }}</span>
      <span class="topbar__count mono" v-if="course">{{ chapters.length }} 章 · {{ totalSections }} 节</span>
    </header>

    <div class="layout">
      <!-- ================= 左：播放区 ================= -->
      <main class="stage">
        <div class="screen">
          <!-- ① 有视频：B 站播放器 -->
          <iframe
            v-if="play?.embedUrl"
            :key="play.embedUrl"
            :src="play.embedUrl"
            class="screen__frame"
            frameborder="0"
            allowfullscreen
            scrolling="no"
            allow="autoplay; fullscreen; encrypted-media"
          />

          <!-- ② 锁定：封面 + 购买引导 -->
          <div v-else-if="play?.locked" class="screen__locked">
            <img :src="play.cover" class="screen__bg" alt="" />
            <div class="locked-mask">
              <svg class="locked-mask__icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6">
                <rect x="4" y="10" width="16" height="11" rx="2" />
                <path d="M8 10V7a4 4 0 0 1 8 0v3" />
              </svg>
              <p class="locked-mask__text">{{ play.reason }}</p>
              <button class="btn" type="button" @click="goBuy">
                ¥{{ priceText }} 购买后解锁
              </button>
            </div>
          </div>

          <!-- ③ 已解锁但没配视频：封面占位 -->
          <div v-else-if="play" class="screen__placeholder">
            <img :src="play.cover" class="screen__bg" alt="" />
            <span class="placeholder-text">本节暂无视频</span>
          </div>

          <!-- ④ 加载中 -->
          <div v-else class="screen__loading">加载中…</div>
        </div>

        <!-- 当前小节信息 -->
        <div class="meta">
          <div class="meta__left">
            <span class="meta__no mono">{{ currentNo }}</span>
            <span class="meta__title">{{ play?.title || '—' }}</span>
          </div>
          <span class="meta__dur mono">{{ currentDuration }}</span>
        </div>
      </main>

      <!-- ================= 右：课程大纲 ================= -->
      <aside class="outline">
        <div class="outline__head">
          <span class="eyebrow">Syllabus</span>
          <h2 class="outline__title">课程大纲</h2>
        </div>

        <div class="outline__body">
          <div v-for="(ch, ci) in chapters" :key="ch.id" class="chapter">
            <div class="chapter__title">
              <span class="chapter__no mono">{{ String(ci + 1).padStart(2, '0') }}</span>
              <span>{{ ch.title }}</span>
            </div>

            <button
              v-for="(s, si) in ch.sections"
              :key="s.id"
              type="button"
              class="item"
              :class="{
                'is-active': s.id === currentSectionId,
                'is-locked': !canPlay
              }"
              @click="selectSection(s)"
            >
              <span class="item__no mono">{{ ci + 1 }}.{{ si + 1 }}</span>
              <span class="item__title">{{ s.title }}</span>
              <span class="item__dur mono">{{ s.duration }}</span>
              <svg v-if="!canPlay" class="item__lock" viewBox="0 0 24 24" fill="none"
                   stroke="currentColor" stroke-width="1.8">
                <rect x="5" y="11" width="14" height="9" rx="2" />
                <path d="M8 11V8a4 4 0 0 1 8 0v3" />
              </svg>
            </button>
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCourse, getSectionPlay, createOrder } from '@/api/portal'
import { auth } from '@/store/auth'

const route = useRoute()
const router = useRouter()

const courseId = computed(() => Number(route.params.id))
const course = ref(null)
const play = ref(null)
const loading = ref(false)

const chapters = computed(() => course.value?.chapters || [])
const totalSections = computed(() =>
  chapters.value.reduce((n, c) => n + (c.sections?.length || 0), 0))

/** 整门课是否可学：免费课 purchased 也为 true */
const canPlay = computed(() => !!course.value?.purchased)

const currentSectionId = computed(() => Number(route.params.sectionId) || play.value?.sectionId || null)

const currentDuration = computed(() => {
  for (const c of chapters.value) {
    const s = c.sections?.find((x) => x.id === currentSectionId.value)
    if (s) return s.duration || ''
  }
  return ''
})

/** 当前是第几章第几节，显示成 1.2 */
const currentNo = computed(() => {
  for (let ci = 0; ci < chapters.value.length; ci++) {
    const list = chapters.value[ci].sections || []
    const si = list.findIndex((x) => x.id === currentSectionId.value)
    if (si >= 0) return `${ci + 1}.${si + 1}`
  }
  return ''
})

const priceText = computed(() => {
  const p = Number(course.value?.price ?? 0)
  return p === 0 ? '免费' : p.toFixed(0)
})

/** 默认打开第一节 */
function firstSectionId() {
  for (const c of chapters.value) {
    if (c.sections?.length) return c.sections[0].id
  }
  return null
}

async function loadCourse() {
  course.value = await getCourse(courseId.value)
}

async function loadPlay(sectionId) {
  if (!sectionId) return
  loading.value = true
  try {
    play.value = await getSectionPlay(sectionId)
  } catch (e) {
    // 未登录访问付费课不会报错，这里只兜住网络异常
    if (!e?.silent) console.warn(e)
  } finally {
    loading.value = false
  }
}

function selectSection(s) {
  if (s.id === currentSectionId.value) return
  if (!canPlay.value) {
    ElMessage.warning('购买后即可观看本节')
    return
  }
  router.replace({ name: 'play', params: { id: courseId.value, sectionId: s.id } })
}

async function goBuy() {
  if (!auth.isLogin()) {
    ElMessage.warning('请先登录')
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  const order = await createOrder(courseId.value)
  router.push({ name: 'order', params: { orderNo: order.orderNo } })
}

onMounted(async () => {
  await loadCourse()
  const sid = route.params.sectionId ? Number(route.params.sectionId) : firstSectionId()
  if (sid) {
    if (!route.params.sectionId) {
      router.replace({ name: 'play', params: { id: courseId.value, sectionId: sid } })
    }
    await loadPlay(sid)
  }
})

// 切换小节时重新取播放信息
watch(() => route.params.sectionId, (v) => {
  if (v) loadPlay(Number(v))
})
</script>

<style scoped>
.play {
  min-height: 100vh;
  background: var(--paper);
  padding-bottom: var(--sp-6);
}

/* ---------- 顶部条 ---------- */
.topbar {
  display: flex;
  align-items: center;
  gap: var(--sp-4);
  padding: var(--sp-3) var(--sp-5);
  border-bottom: 1px solid var(--line);
  background: var(--paper-2);
}
.topbar__back {
  font-size: var(--fs-xs);
  color: var(--muted);
  text-decoration: none;
  white-space: nowrap;
}
.topbar__back:hover { color: var(--green); }
.topbar__title {
  font-family: var(--font-display);
  font-size: var(--fs-lg);
  color: var(--ink);
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.topbar__count { color: var(--muted-2); white-space: nowrap; }

/* ---------- 布局 ---------- */
.layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 360px;
  gap: var(--sp-5);
  max-width: 1440px;
  margin: var(--sp-5) auto 0;
  padding: 0 var(--sp-5);
  align-items: start;
}

/* ---------- 播放区 ---------- */
.screen {
  position: relative;
  width: 100%;
  aspect-ratio: 16 / 9;
  background: #0d1512;
  border: 1px solid var(--line);
  overflow: hidden;
}
.screen__frame {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  border: 0;
}
.screen__bg {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: blur(2px) brightness(0.5);
}
.screen__locked,
.screen__placeholder {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}
.locked-mask {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--sp-3);
  color: #fff;
  text-align: center;
  padding: var(--sp-4);
}
.locked-mask__icon { width: 40px; height: 40px; opacity: 0.9; }
.locked-mask__text { margin: 0; font-size: var(--fs-sm); opacity: 0.85; }
.locked-mask .btn { margin-top: var(--sp-2); }
.placeholder-text {
  position: relative;
  color: rgba(255, 255, 255, 0.75);
  font-size: var(--fs-sm);
  letter-spacing: 0.08em;
}
.screen__loading {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--muted-2);
  font-size: var(--fs-sm);
}

/* 当前小节信息 */
.meta {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: var(--sp-4);
  padding: var(--sp-4) 0 var(--sp-2);
  border-bottom: 1px solid var(--line);
}
.meta__left { display: flex; align-items: baseline; gap: var(--sp-3); min-width: 0; }
.meta__no { color: var(--muted-2); font-size: var(--fs-xs); }
.meta__title {
  font-family: var(--font-display);
  font-size: var(--fs-md);
  color: var(--ink);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.meta__dur { color: var(--muted); font-size: var(--fs-xs); flex-shrink: 0; }

/* ---------- 右侧大纲 ---------- */
.outline {
  border: 1px solid var(--line);
  background: var(--paper-2);
  max-height: calc(100vh - 140px);
  display: flex;
  flex-direction: column;
  position: sticky;
  top: var(--sp-4);
}
.outline__head {
  padding: var(--sp-4);
  border-bottom: 1px solid var(--line);
}
.outline__title {
  font-family: var(--font-display);
  font-size: var(--fs-lg);
  margin: 4px 0 0;
  color: var(--ink);
}
.outline__body { overflow-y: auto; padding: var(--sp-2) 0; }

.chapter + .chapter { border-top: 1px solid var(--line); }
.chapter__title {
  display: flex;
  gap: var(--sp-3);
  padding: var(--sp-3) var(--sp-4);
  font-size: var(--fs-xs);
  color: var(--muted);
  letter-spacing: 0.02em;
}
.chapter__no { color: var(--muted-2); }

.item {
  display: grid;
  grid-template-columns: 30px minmax(0, 1fr) auto auto;
  align-items: center;
  gap: var(--sp-2);
  width: 100%;
  padding: 8px var(--sp-4);
  border: 0;
  background: none;
  text-align: left;
  cursor: pointer;
  font-size: var(--fs-sm);
  color: var(--ink-2);
  transition: background var(--dur-fast);
}
.item:hover { background: var(--paper-3); }
.item__no { color: var(--muted-2); font-size: var(--fs-2xs); }
.item__title {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.item__dur { color: var(--muted-2); font-size: var(--fs-2xs); }
.item__lock { width: 14px; height: 14px; color: var(--muted-2); }

.item.is-active {
  background: var(--green);
  color: var(--paper-2);
}
.item.is-active .item__no,
.item.is-active .item__dur { color: rgba(255, 255, 255, 0.6); }
.item.is-active .item__lock { color: rgba(255, 255, 255, 0.6); }

.item.is-locked { color: var(--muted-2); }

/* ---------- 响应式 ---------- */
@media (max-width: 1100px) {
  .layout { grid-template-columns: 1fr; }
  .outline { position: static; max-height: none; }
}
</style>
