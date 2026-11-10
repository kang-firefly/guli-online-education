<template>
  <div class="home">
    <!-- ================= Hero ================= -->
    <section class="hero">
      <div class="hero__bg">
        <div
          v-for="(b, i) in banners"
          :key="b.id"
          class="hero__img"
          :class="{ 'is-active': i === bannerIndex }"
          :style="b.imageUrl ? { backgroundImage: `url(${b.imageUrl})` } : {}"
        />
        <div class="hero__shade" />
      </div>

      <div class="wrap hero__inner">
        <div class="hero__card reveal">
          <span class="eyebrow">EST. 2026 · ONLINE EDUCATION</span>
          <h1 class="hero__title">让每一节课<br />都有始有终</h1>
          <p class="hero__desc">
            前端、后端、数据库三条主线，讲师把踩过的坑整理成章。
            不追热点，只讲能落到代码里的东西。
          </p>

          <div class="hero__cta">
            <RouterLink to="/courses" class="btn-primary">浏览全部课程</RouterLink>
            <RouterLink to="/teachers" class="btn-ghost">认识讲师 →</RouterLink>
          </div>

          <div class="hero__stats">
            <span><b class="mono">{{ courseTotal }}</b> 门课程</span>
            <span class="hero__sep" aria-hidden="true" />
            <span><b class="mono">{{ teacherTotal }}</b> 位讲师</span>
            <span class="hero__sep" aria-hidden="true" />
            <span><b class="mono">{{ sectionTotal }}</b> 节小课</span>
          </div>
        </div>
      </div>

      <div v-if="banners.length > 1" class="hero__dots">
        <button
          v-for="(b, i) in banners"
          :key="b.id"
          class="dot"
          :class="{ 'is-active': i === bannerIndex }"
          type="button"
          :aria-label="`切换到第 ${i + 1} 张`"
          @click="bannerIndex = i"
        />
      </div>
    </section>

    <!-- ================= 正文 ================= -->
    <div class="home__body">
      <!-- 分类 -->
      <section v-if="subjects.length" class="cats reveal reveal-1">
        <RouterLink
          v-for="s in subjects"
          :key="s.id"
          class="cat"
          :to="{ path: '/courses', query: { parent: s.id } }"
        >
          <span class="cat__title">{{ s.title }}</span>
          <span class="cat__sub">{{ s.children.map((c) => c.title).join(' · ') }}</span>
        </RouterLink>
      </section>

      <!-- 热门课程 -->
      <section class="block">
        <div class="section-head">
          <div>
            <span class="eyebrow">Courses</span>
            <h2>热门课程</h2>
            <p>按学员购买数排序，都是被反复听过的课。</p>
          </div>
          <RouterLink to="/courses" class="section-head__more">查看全部 {{ courseTotal }} 门 →</RouterLink>
        </div>

        <div v-if="loading" class="grid-courses">
          <div v-for="n in 4" :key="n" class="skeleton" />
        </div>
        <div v-else class="grid-courses">
          <CourseCard
            v-for="(c, i) in hotCourses"
            :key="c.id"
            :course="c"
            class="reveal"
            :class="`reveal-${Math.min(i + 1, 5)}`"
          />
        </div>
      </section>

      <!-- 名师 -->
      <section class="block">
        <div class="section-head">
          <div>
            <span class="eyebrow">Faculty</span>
            <h2>名师阵容</h2>
            <p>写过大流量的系统，也带过零基础的班。</p>
          </div>
          <RouterLink to="/teachers" class="section-head__more">全部讲师 →</RouterLink>
        </div>

        <div class="grid-teachers">
          <TeacherCard
            v-for="(t, i) in hotTeachers"
            :key="t.id"
            :teacher="t"
            class="reveal"
            :class="`reveal-${Math.min(i + 1, 5)}`"
          />
        </div>
      </section>

      <!-- CTA 横幅：登录态不同，文案与动作都不同 -->
      <section class="cta">
        <div class="cta__inner">
          <div>
            <span class="eyebrow cta__eyebrow">{{ isLogin ? 'Welcome back' : 'Start here' }}</span>
            <h2 class="cta__title">
              <template v-if="isLogin">挑一门课，<br />从头听到尾。</template>
              <template v-else>从一个念头，<br />到一门能讲完的课。</template>
            </h2>
          </div>
          <div class="cta__right">
            <p v-if="isLogin" class="cta__desc">
              欢迎回来，{{ nickname }}。<br />
              课程大纲随时可查，账号信息在「我的」里。
            </p>
            <p v-else class="cta__desc">
              注册后即可收藏课程、记录学习进度。<br />
              所有课程都附完整章节大纲，学之前先看清全貌。
            </p>

            <RouterLink v-if="isLogin" to="/courses" class="btn-light">浏览全部课程 →</RouterLink>
            <RouterLink v-else to="/register" class="btn-light">免费注册 →</RouterLink>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { getIndex } from '@/api/portal'
import { auth } from '@/store/auth'
import CourseCard from '@/components/CourseCard.vue'
import TeacherCard from '@/components/TeacherCard.vue'

const loading = ref(true)
const banners = ref([])
const hotCourses = ref([])
const hotTeachers = ref([])
const subjects = ref([])
const courseTotal = ref(0)
const teacherTotal = ref(0)

// 登录态：响应式 store，登录/退出后首页 CTA、顶栏都会即时更新
const isLogin = computed(() => auth.isLogin())
const nickname = computed(() => auth.user?.nickname || '同学')

const bannerIndex = ref(0)
let timer = null

const sectionTotal = computed(() =>
  hotCourses.value.reduce((n, c) => n + (c.lessonNum || 0), 0)
)

function startCarousel() {
  if (banners.value.length < 2) return
  timer = setInterval(() => {
    bannerIndex.value = (bannerIndex.value + 1) % banners.value.length
  }, 5000)
}

onMounted(async () => {
  try {
    const d = await getIndex()
    banners.value = d.banners || []
    hotCourses.value = d.hotCourses || []
    hotTeachers.value = d.hotTeachers || []
    subjects.value = d.subjects || []
    courseTotal.value = d.courseTotal || 0
    teacherTotal.value = d.teacherTotal || 0
    startCarousel()
  } finally {
    loading.value = false
  }
})

onUnmounted(() => clearInterval(timer))
</script>

<style scoped>
/* ---------------- Hero ---------------- */
.hero {
  position: relative;
  min-height: 560px;
  display: flex;
  align-items: flex-end;
  overflow: hidden;
  background: var(--green-dim);
}

.hero__bg { position: absolute; inset: 0; }

.hero__img {
  position: absolute;
  inset: 0;
  background-size: cover;
  background-position: center;
  opacity: 0;
  transform: scale(1.02);
  transition: opacity 900ms var(--ease-out), transform 6000ms linear;
  /* 轮播图只当"色块氛围"，轻微模糊 + 压暗，避免和卡片上的大标题抢主角 */
  filter: blur(10px) brightness(0.72) saturate(1.05);
}
.hero__img.is-active { opacity: 1; transform: scale(1.18); }

.hero__shade {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(100deg, rgba(9, 28, 21, 0.93) 0%, rgba(9, 28, 21, 0.72) 42%, rgba(9, 28, 21, 0.5) 100%),
    linear-gradient(to top, rgba(9, 28, 21, 0.6), transparent 45%);
}

.hero__inner {
  padding-top: var(--sp-9);
  padding-bottom: var(--sp-9);
  display: flex;
}

/* 纸白卡片压在图片上：整页的记忆点 */
.hero__card {
  width: min(560px, 100%);
  background: var(--paper);
  padding: var(--sp-7);
  border-left: 3px solid var(--amber);
  box-shadow: var(--shadow-3);
  transform: translateY(48px);
}

.hero__title {
  font-size: clamp(38px, 4.6vw, var(--fs-hero));
  line-height: 1.04;
  letter-spacing: -0.03em;
  margin: var(--sp-3) 0 var(--sp-4);
  color: var(--ink);
}

.hero__desc {
  font-size: var(--fs-md);
  line-height: 1.9;
  color: var(--ink-2);
  max-width: 40ch;
}

.hero__cta {
  display: flex;
  align-items: center;
  gap: var(--sp-5);
  margin-top: var(--sp-5);
}

.btn-primary {
  display: inline-flex;
  align-items: center;
  height: 46px;
  padding: 0 var(--sp-5);
  background: var(--green);
  color: var(--paper);
  font-size: var(--fs-sm);
  letter-spacing: 0.08em;
  transition: background var(--dur-fast), transform var(--dur-fast);
}
.btn-primary:hover { background: var(--green-2); }
.btn-primary:active { transform: translateY(1px); }

.btn-ghost {
  font-size: var(--fs-sm);
  color: var(--green);
  border-bottom: 1px solid var(--green);
  padding-bottom: 2px;
  transition: color var(--dur-fast), border-color var(--dur-fast);
}
.btn-ghost:hover { color: var(--amber); border-color: var(--amber); }

.hero__stats {
  display: flex;
  align-items: center;
  gap: var(--sp-4);
  margin-top: var(--sp-5);
  padding-top: var(--sp-4);
  border-top: 1px solid var(--line);
  font-size: var(--fs-xs);
  color: var(--muted);
}
.hero__stats b {
  font-size: var(--fs-xl);
  color: var(--green);
  margin-right: 4px;
  font-weight: 400;
}
.hero__sep { width: 1px; height: 14px; background: var(--line-strong); }

.hero__dots {
  position: absolute;
  right: var(--sp-6);
  bottom: var(--sp-6);
  display: flex;
  gap: var(--sp-2);
  z-index: 3;
}
.dot {
  width: 26px;
  height: 2px;
  border: 0;
  padding: 0;
  background: rgba(251, 248, 241, 0.35);
  cursor: pointer;
  transition: background var(--dur-fast);
}
.dot.is-active { background: var(--amber-2); }

/* ---------------- 正文 ---------------- */
.home__body {
  padding-top: calc(var(--sp-9) + 8px);
}

/* 分类条 */
.cats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 1px;
  background: var(--line);
  border: 1px solid var(--line);
  margin-bottom: var(--sp-9);
}

.cat {
  background: var(--paper-2);
  padding: var(--sp-4) var(--sp-5);
  display: flex;
  flex-direction: column;
  gap: 2px;
  transition: background var(--dur-fast);
}
.cat:hover { background: var(--paper-3); }

.cat__title {
  font-family: var(--font-serif);
  font-size: var(--fs-lg);
  font-weight: 600;
  color: var(--ink);
}
.cat:hover .cat__title { color: var(--green); }

.cat__sub {
  font-size: var(--fs-xs);
  color: var(--muted);
  letter-spacing: 0.02em;
}

.block { margin-bottom: var(--sp-10); }

.grid-courses {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--sp-5);
}

.grid-teachers {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--sp-4);
}

.skeleton {
  aspect-ratio: 3 / 4;
  background: linear-gradient(100deg, var(--paper-3) 30%, #f7f2e7 50%, var(--paper-3) 70%);
  background-size: 200% 100%;
  animation: shimmer 1.4s infinite linear;
}
@keyframes shimmer {
  from { background-position: 200% 0; }
  to { background-position: -200% 0; }
}

/* ---------------- CTA ---------------- */
.cta {
  background: var(--green-dim);
  color: var(--paper);
  padding: var(--sp-8) var(--sp-7);
}

.cta__inner {
  display: grid;
  grid-template-columns: 1.1fr 1fr;
  gap: var(--sp-8);
  align-items: center;
  max-width: var(--wrap);
  margin: 0 auto;
}

.cta__eyebrow { color: rgba(251, 248, 241, 0.5); }

.cta__title {
  font-size: clamp(28px, 3.2vw, 44px);
  line-height: 1.16;
  margin-top: var(--sp-3);
  color: var(--paper);
}

.cta__desc {
  font-size: var(--fs-sm);
  line-height: 1.9;
  color: rgba(251, 248, 241, 0.66);
  margin-bottom: var(--sp-5);
}

.btn-light {
  display: inline-flex;
  align-items: center;
  height: 46px;
  padding: 0 var(--sp-5);
  background: var(--amber);
  color: #fff;
  font-size: var(--fs-sm);
  letter-spacing: 0.08em;
  transition: background var(--dur-fast);
}
.btn-light:hover { background: var(--amber-2); }

/* ---------------- 响应式 ---------------- */
@media (max-width: 1100px) {
  .grid-courses { grid-template-columns: repeat(2, 1fr); }
  .grid-teachers { grid-template-columns: 1fr; }
}

@media (max-width: 860px) {
  .hero { min-height: 0; }
  .hero__inner { padding-top: var(--sp-7); padding-bottom: var(--sp-7); }
  .hero__card {
    transform: none;
    padding: var(--sp-5);
    border-left-width: 2px;
  }
  .home__body { padding-top: var(--sp-7); }
  .cta { padding: var(--sp-6) var(--sp-5); }
  .cta__inner { grid-template-columns: 1fr; gap: var(--sp-5); }
  .block { margin-bottom: var(--sp-8); }
}

@media (max-width: 560px) {
  .grid-courses { grid-template-columns: 1fr; }
  .hero__dots { display: none; }
}
</style>
