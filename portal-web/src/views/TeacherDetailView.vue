<template>
  <div class="tdetail">
    <div class="wrap">
      <nav class="crumb">
        <RouterLink to="/">首页</RouterLink>
        <span>/</span>
        <RouterLink to="/teachers">名师</RouterLink>
        <span>/</span>
        <em>{{ teacher?.name || '讲师' }}</em>
      </nav>

      <div v-if="loading" class="loading">正在加载…</div>

      <template v-else-if="teacher">
        <header class="profile">
          <span class="profile__avatar">
            <img v-if="teacher.avatar" :src="teacher.avatar" :alt="teacher.name" />
            <template v-else>{{ teacher.name?.slice(0, 1) }}</template>
          </span>

          <div class="profile__info">
            <span class="eyebrow">{{ teacher.level === 2 ? 'Senior Instructor' : 'Instructor' }}</span>
            <h1 class="profile__name">{{ teacher.name }}</h1>
            <p class="profile__career">{{ teacher.career }}</p>
            <p class="profile__intro">{{ teacher.intro }}</p>
            <p class="profile__count mono">{{ teacher.courses.length }} 门已发布课程</p>
          </div>
        </header>

        <section class="courses">
          <div class="section-head">
            <div>
              <span class="eyebrow">Taught by {{ teacher.name }}</span>
              <h2>主讲课程</h2>
            </div>
            <RouterLink to="/courses" class="section-head__more">看全部课程 →</RouterLink>
          </div>

          <div v-if="teacher.courses.length" class="grid">
            <CourseCard v-for="c in teacher.courses" :key="c.id" :course="c" />
          </div>
          <p v-else class="none">这位讲师暂时还没有已上线的课程。</p>
        </section>
      </template>

      <div v-else class="empty">
        <span class="empty__mark">404</span>
        <p class="empty__title">讲师不存在</p>
        <RouterLink to="/teachers" class="empty__link">看看其他讲师 →</RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getTeacher } from '@/api/portal'
import CourseCard from '@/components/CourseCard.vue'

const route = useRoute()
const teacher = ref(null)
const loading = ref(true)

async function load(id) {
  loading.value = true
  try {
    teacher.value = await getTeacher(id)
  } catch {
    teacher.value = null
  } finally {
    loading.value = false
  }
}

onMounted(() => load(route.params.id))
watch(() => route.params.id, (id) => id && load(id))
</script>

<style scoped>
.tdetail {
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
  padding: var(--sp-3) 0 var(--sp-6);
}
.crumb em { font-style: normal; color: var(--ink-2); }

.loading { padding: var(--sp-9) 0; color: var(--muted); }

.profile {
  display: grid;
  grid-template-columns: 180px 1fr;
  gap: var(--sp-7);
  align-items: start;
  padding-bottom: var(--sp-7);
  border-bottom: 1px solid var(--line-strong);
}

.profile__avatar {
  width: 180px;
  height: 180px;
  border-radius: 50%;
  overflow: hidden;
  display: grid;
  place-items: center;
  background: var(--paper-3);
  border: 1px solid var(--line);
  font-family: var(--font-serif);
  font-size: 64px;
  color: var(--green);
}
.profile__avatar img { width: 100%; height: 100%; object-fit: cover; }

.profile__name {
  font-size: clamp(32px, 4vw, 52px);
  margin: var(--sp-2) 0 var(--sp-2);
}

.profile__career {
  font-size: var(--fs-md);
  color: var(--green);
  font-weight: 500;
  margin-bottom: var(--sp-3);
}

.profile__intro {
  font-size: var(--fs-md);
  line-height: 1.9;
  color: var(--ink-2);
  max-width: 60ch;
}

.profile__count {
  margin-top: var(--sp-4);
  font-size: var(--fs-xs);
  color: var(--muted);
  letter-spacing: 0.08em;
}

.courses { padding-top: var(--sp-7); }

.grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--sp-5);
}

.none {
  padding: var(--sp-6) 0;
  font-size: var(--fs-sm);
  color: var(--muted);
}

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
  .profile { grid-template-columns: 120px 1fr; gap: var(--sp-5); }
  .profile__avatar { width: 120px; height: 120px; font-size: 44px; }
  .grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 620px) {
  .profile { grid-template-columns: 1fr; }
  .grid { grid-template-columns: 1fr; }
}
</style>
