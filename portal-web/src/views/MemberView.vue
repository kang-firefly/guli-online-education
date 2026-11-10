<template>
  <div class="member">
    <div class="wrap">
      <header class="head">
        <span class="eyebrow">My account</span>
        <h1 class="head__title">我的资料</h1>
      </header>

      <div v-if="profile" class="card">
        <span class="card__avatar">{{ profile.nickname?.slice(0, 1) }}</span>

        <dl class="card__list">
          <div class="row">
            <dt>昵称</dt>
            <dd>{{ profile.nickname }}</dd>
          </div>
          <div class="row">
            <dt>手机号</dt>
            <dd class="mono">{{ profile.mobile }}</dd>
          </div>
          <div class="row">
            <dt>账号 ID</dt>
            <dd class="mono">{{ profile.id }}</dd>
          </div>
        </dl>

        <div class="card__ops">
          <button class="btn" type="button" @click="onLogout">退出登录</button>
        </div>
      </div>

      <div v-else class="loading">正在加载…</div>

      <!-- 我的课程：已支付订单 -->
      <section class="mycourses" v-if="profile">
        <header class="mycourses__head">
          <span class="eyebrow">My courses</span>
          <h2 class="mycourses__title">我的课程</h2>
          <span class="mycourses__count mono">{{ courses.length }} 门</span>
        </header>

        <ul class="mycourses__list" v-if="courses.length">
          <li v-for="o in courses" :key="o.orderNo" class="course">
            <RouterLink :to="`/course/${o.courseId}/play`" class="course__link">
              <img :src="o.cover" class="course__cover" alt="" />
              <div class="course__info">
                <span class="course__title">{{ o.courseTitle }}</span>
                <span class="course__meta mono">
                  ¥ {{ Number(o.amount).toFixed(2) }} · 购于 {{ (o.payTime || '').slice(0, 10) }}
                </span>
              </div>
              <span class="course__go">继续学习 →</span>
            </RouterLink>
          </li>
        </ul>
        <p v-else class="mycourses__empty">
          还没有购买课程。<RouterLink to="/courses">去看看课程 →</RouterLink>
        </p>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { me, myCourses } from '@/api/portal'
import { auth } from '@/store/auth'

const router = useRouter()
const profile = ref(null)
const courses = ref([])

function onLogout() {
  auth.clear()
  router.replace('/')
}

onMounted(async () => {
  try {
    profile.value = await me()
    courses.value = await myCourses()
  } catch {
    auth.clear()
    router.replace('/login')
  }
})
</script>

<style scoped>
.member {
  position: relative;
  z-index: 2;
  padding: var(--sp-8) 0 var(--sp-9);
}

.head {
  padding-bottom: var(--sp-5);
  border-bottom: 1px solid var(--line-strong);
  margin-bottom: var(--sp-6);
}

.head__title { font-size: clamp(30px, 3.6vw, 44px); margin-top: var(--sp-2); }

.card {
  max-width: 620px;
  background: var(--paper-2);
  border: 1px solid var(--line);
  padding: var(--sp-6);
}

.card__avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: var(--green);
  color: var(--paper);
  font-family: var(--font-serif);
  font-size: var(--fs-2xl);
}

.card__list { margin: var(--sp-5) 0 0; }

.row {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  padding: var(--sp-3) 0;
  border-bottom: 1px solid var(--line);
}
.row:last-child { border-bottom: 0; }

.row dt { font-size: var(--fs-sm); color: var(--muted); }
.row dd { margin: 0; font-size: var(--fs-md); color: var(--ink); }

.card__ops { margin-top: var(--sp-5); padding-top: var(--sp-4); border-top: 1px solid var(--line); }

.btn {
  height: 42px;
  padding: 0 var(--sp-5);
  background: transparent;
  border: 1px solid var(--line-strong);
  font-family: var(--font-body);
  font-size: var(--fs-sm);
  color: var(--ink-2);
  cursor: pointer;
  transition: border-color var(--dur-fast), color var(--dur-fast);
}
.btn:hover { border-color: var(--danger); color: var(--danger); }

.loading { padding: var(--sp-7) 0; color: var(--muted); }

/* ---------- 我的课程 ---------- */
.mycourses { margin-top: var(--sp-7); }
.mycourses__head {
  display: flex;
  align-items: baseline;
  gap: var(--sp-3);
  padding-bottom: var(--sp-4);
  border-bottom: 1px solid var(--line-strong);
  margin-bottom: var(--sp-4);
}
.mycourses__title {
  font-family: var(--font-display);
  font-size: var(--fs-xl);
  margin: 0;
  color: var(--ink);
}
.mycourses__count { color: var(--muted-2); margin-left: auto; }

.mycourses__list { list-style: none; margin: 0; padding: 0; }
.course + .course { border-top: 1px solid var(--line); }
.course__link {
  display: flex;
  align-items: center;
  gap: var(--sp-4);
  padding: var(--sp-3) 0;
  text-decoration: none;
  color: inherit;
}
.course__link:hover .course__title { color: var(--green); }
.course__cover {
  width: 128px;
  height: 72px;
  object-fit: cover;
  border: 1px solid var(--line);
  flex-shrink: 0;
}
.course__info { display: flex; flex-direction: column; gap: 4px; min-width: 0; flex: 1; }
.course__title {
  font-family: var(--font-display);
  font-size: var(--fs-md);
  color: var(--ink);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.course__meta { color: var(--muted-2); font-size: var(--fs-2xs); }
.course__go { color: var(--green); font-size: var(--fs-xs); white-space: nowrap; }

.mycourses__empty { color: var(--muted); font-size: var(--fs-sm); padding: var(--sp-4) 0; }
.mycourses__empty a { color: var(--green); }

/* ---------- 我的课程 ---------- */
.mycourses { margin-top: var(--sp-7); }
.mycourses__head {
  display: flex;
  align-items: baseline;
  gap: var(--sp-3);
  padding-bottom: var(--sp-4);
  border-bottom: 1px solid var(--line-strong);
  margin-bottom: var(--sp-4);
}
.mycourses__title {
  font-family: var(--font-display);
  font-size: var(--fs-xl);
  margin: 0;
  color: var(--ink);
}
.mycourses__count { color: var(--muted-2); margin-left: auto; }

.mycourses__list { list-style: none; margin: 0; padding: 0; }
.course + .course { border-top: 1px solid var(--line); }
.course__link {
  display: flex;
  align-items: center;
  gap: var(--sp-4);
  padding: var(--sp-3) 0;
  text-decoration: none;
  color: inherit;
}
.course__link:hover .course__title { color: var(--green); }
.course__cover {
  width: 128px;
  height: 72px;
  object-fit: cover;
  border: 1px solid var(--line);
  flex-shrink: 0;
}
.course__info { display: flex; flex-direction: column; gap: 4px; min-width: 0; flex: 1; }
.course__title {
  font-family: var(--font-display);
  font-size: var(--fs-md);
  color: var(--ink);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.course__meta { color: var(--muted-2); font-size: var(--fs-2xs); }
.course__go { color: var(--green); font-size: var(--fs-xs); white-space: nowrap; }

.mycourses__empty { color: var(--muted); font-size: var(--fs-sm); padding: var(--sp-4) 0; }
.mycourses__empty a { color: var(--green); }
</style>
