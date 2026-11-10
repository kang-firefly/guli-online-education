<template>
  <div class="shell">
    <!-- 侧栏：墨绿底 + 发丝线，编辑风的"书脊" -->
    <aside class="sidebar">
      <div class="sidebar__brand">
        <h1 class="sidebar__logo">谷粒</h1>
        <span class="sidebar__sub">GULI ACADEMY · 后台</span>
      </div>

      <nav class="sidebar__nav" aria-label="主导航">
        <p class="sidebar__group eyebrow eyebrow--invert">Content</p>

        <RouterLink
          v-for="(item, i) in menus"
          :key="item.path || item.label"
          :to="item.path || $route.path"
          class="nav-item"
          :class="{ 'is-active': item.path && $route.path.startsWith(item.path), 'is-disabled': !item.path }"
          :style="{ animationDelay: `${80 + i * 60}ms` }"
          @click="onDisabled(item, $event)"
        >
          <el-icon class="nav-item__icon"><component :is="item.icon" /></el-icon>
          <span class="nav-item__label">{{ item.label }}</span>
          <span v-if="item.tag" class="nav-item__tag mono">{{ item.tag }}</span>
        </RouterLink>
      </nav>

      <div class="sidebar__foot">
        <span class="mono">v1.0</span>
        <span>{{ user?.nickname || user?.username || '管理员' }}</span>
      </div>
    </aside>

    <!-- 右侧主区 -->
    <div class="main">
      <header class="topbar">
        <div class="topbar__title">
          <span class="eyebrow">{{ $route.meta.eyebrow || 'Console' }}</span>
          <h2>{{ $route.meta.title || '后台' }}</h2>
        </div>

        <div class="topbar__right">
          <span class="topbar__date mono">{{ today }}</span>
          <el-dropdown trigger="click" @command="onCommand">
            <button class="user" type="button">
              <span class="user__avatar">
                <img v-if="user?.avatar" :src="user.avatar" alt="" />
                <template v-else>{{ (user?.nickname || 'A').slice(0, 1) }}</template>
              </span>
              <span class="user__name">{{ user?.nickname || user?.username }}</span>
              <el-icon class="user__caret"><ArrowDown /></el-icon>
            </button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <main class="content">
        <RouterView v-slot="{ Component }">
          <transition name="page" mode="out-in">
            <component :is="Component" />
          </transition>
        </RouterView>
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Files, Grid, Picture, ArrowDown } from '@element-plus/icons-vue'
import { auth } from '@/store/auth'
import { logout } from '@/api/auth'

const router = useRouter()
const user = computed(() => auth.user)

// 已实现 + 后续里程碑（disabled 项用来交代进度，不做假按钮）
const menus = [
  { label: '讲师管理', path: '/teacher', icon: User },
  { label: '课程分类', path: '/subject', icon: Grid },
  { label: '课程管理', path: '/course', icon: Files },
  { label: '首页运营', path: '/banner', icon: Picture }
]

const today = new Intl.DateTimeFormat('zh-CN', {
  year: 'numeric',
  month: '2-digit',
  day: '2-digit'
})
  .format(new Date())
  .replace(/\//g, ' · ')

function onDisabled(item, e) {
  if (!item.path) {
    e.preventDefault()
    ElMessage.info(`${item.label} 将在 ${item.tag} 阶段开放`)
  }
}

async function onCommand(cmd) {
  if (cmd !== 'logout') return
  try {
    await ElMessageBox.confirm('确认退出当前账号？', '退出登录', {
      confirmButtonText: '退出',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  await logout().catch(() => {})
  auth.clear()
  router.replace('/login')
}
</script>

<style scoped>
.shell {
  position: relative;
  z-index: 2;
  display: grid;
  grid-template-columns: var(--sidebar-w) 1fr;
  min-height: 100vh;
}

/* ---------------- 侧栏 ---------------- */
.sidebar {
  position: sticky;
  top: 0;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--green-dim);
  color: var(--paper-2);
  padding: var(--sp-6) var(--sp-5) var(--sp-5);
}

.sidebar__brand {
  padding-bottom: var(--sp-5);
  border-bottom: 1px solid var(--line-invert);
}

.sidebar__logo {
  font-family: var(--font-display);
  font-size: var(--fs-2xl);
  letter-spacing: 0.08em;
  color: var(--paper-2);
  line-height: 1.1;
}

.sidebar__sub {
  display: block;
  margin-top: 6px;
  font-size: 10px;
  letter-spacing: 0.2em;
  color: rgba(255, 253, 247, 0.42);
}

.sidebar__nav {
  flex: 1;
  padding-top: var(--sp-5);
}

.sidebar__group {
  margin-bottom: var(--sp-3);
  color: rgba(255, 253, 247, 0.34);
}

.nav-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: var(--sp-3);
  height: 44px;
  padding: 0 var(--sp-3);
  margin-bottom: 2px;
  color: rgba(255, 253, 247, 0.66);
  font-size: var(--fs-md);
  border-radius: var(--r-xs);
  transition: color var(--dur-fast) var(--ease-out),
              background-color var(--dur-fast) var(--ease-out);
  animation: rise var(--dur-slow) var(--ease-out) both;
}

.nav-item:hover {
  color: var(--paper-2);
  background: rgba(255, 253, 247, 0.05);
}

/* 选中态：左侧琥珀细条 + 淡底，不做圆角胶囊 */
.nav-item.is-active {
  color: var(--paper-2);
  background: rgba(255, 253, 247, 0.07);
}
.nav-item.is-active::before {
  content: "";
  position: absolute;
  left: 0;
  top: 10px;
  bottom: 10px;
  width: 2px;
  background: var(--amber);
}

.nav-item.is-disabled {
  color: rgba(255, 253, 247, 0.32);
  cursor: not-allowed;
}

.nav-item__icon { font-size: 16px; }
.nav-item__label { flex: 1; }
.nav-item__tag {
  font-size: 10px;
  letter-spacing: 0.1em;
  color: rgba(255, 253, 247, 0.3);
  border: 1px solid rgba(255, 253, 247, 0.16);
  padding: 1px 4px;
  border-radius: var(--r-xs);
}

.sidebar__foot {
  display: flex;
  justify-content: space-between;
  font-size: var(--fs-2xs);
  letter-spacing: 0.1em;
  color: rgba(255, 253, 247, 0.38);
  padding-top: var(--sp-4);
  border-top: 1px solid var(--line-invert);
}

/* ---------------- 主区 ---------------- */
.main {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.topbar {
  position: sticky;
  top: 0;
  z-index: 10;
  height: var(--topbar-h);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--sp-7);
  background: rgba(245, 241, 231, 0.82);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--line);
}

.topbar__title h2 {
  font-size: var(--fs-xl);
  letter-spacing: -0.01em;
  margin-top: 2px;
}

.topbar__right {
  display: flex;
  align-items: center;
  gap: var(--sp-5);
}

.topbar__date {
  font-size: var(--fs-xs);
  color: var(--muted);
  letter-spacing: 0.08em;
}

.user {
  display: flex;
  align-items: center;
  gap: var(--sp-2);
  height: 40px;
  padding: 0 var(--sp-3);
  background: transparent;
  border: 1px solid transparent;
  border-radius: var(--r-xs);
  color: var(--ink-2);
  font-family: var(--font-body);
  font-size: var(--fs-sm);
  cursor: pointer;
  transition: border-color var(--dur-fast) var(--ease-out),
              background-color var(--dur-fast) var(--ease-out);
}
.user:hover {
  border-color: var(--line);
  background: var(--paper-2);
}

.user__avatar {
  width: 28px;
  height: 28px;
  border-radius: var(--r-pill);
  overflow: hidden;
  display: grid;
  place-items: center;
  background: var(--green);
  color: var(--paper-2);
  font-size: var(--fs-xs);
  font-family: var(--font-serif);
  flex: none;
}
.user__avatar img { width: 100%; height: 100%; object-fit: cover; }

.user__caret { font-size: 12px; color: var(--muted); }

.content {
  flex: 1;
  padding: var(--sp-6) var(--sp-7) var(--sp-8);
  max-width: 1480px;
  width: 100%;
}

@media (max-width: 1024px) {
  .shell { grid-template-columns: 68px 1fr; }
  .sidebar { padding: var(--sp-5) var(--sp-2); }
  .sidebar__logo { font-size: var(--fs-md); text-align: center; }
  .sidebar__sub,
  .sidebar__group,
  .nav-item__label,
  .nav-item__tag,
  .sidebar__foot { display: none; }
  .nav-item { justify-content: center; }
  .topbar, .content { padding-left: var(--sp-5); padding-right: var(--sp-5); }
}

@media (max-width: 640px) {
  .topbar__date { display: none; }
  .topbar { padding: 0 var(--sp-4); }
  .topbar__title .eyebrow { display: none; }
  .topbar__title h2 { font-size: var(--fs-lg); margin-top: 0; }
  .user__name { display: none; }
  .topbar__right { gap: var(--sp-2); }
  .content { padding: var(--sp-4); }
}
</style>
