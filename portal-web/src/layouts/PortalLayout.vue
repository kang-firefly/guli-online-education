<template>
  <div class="portal">
    <header class="header">
      <div class="wrap header__inner">
        <RouterLink to="/" class="brand" aria-label="谷粒学苑首页">
          <span class="brand__name">谷粒学苑</span>
          <span class="brand__sub">GULI ACADEMY</span>
        </RouterLink>

        <nav class="nav" aria-label="主导航">
          <RouterLink to="/" class="nav__item" exact-active-class="is-active">首页</RouterLink>
          <RouterLink to="/courses" class="nav__item" active-class="is-active">全部课程</RouterLink>
          <RouterLink to="/teachers" class="nav__item" active-class="is-active">名师</RouterLink>
        </nav>

        <div class="actions">
          <template v-if="!isLogin">
            <RouterLink to="/login" class="action-link">登录</RouterLink>
            <RouterLink to="/register" class="btn">免费注册</RouterLink>
          </template>

          <el-dropdown v-else trigger="click" @command="onCommand">
            <button class="user" type="button">
              <span class="user__avatar">{{ (user?.nickname || 'U').slice(0, 1) }}</span>
              <span class="user__name">{{ user?.nickname }}</span>
              <el-icon class="user__caret"><ArrowDown /></el-icon>
            </button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="me">我的资料</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <main class="main">
      <RouterView />
    </main>

    <footer class="footer">
      <div class="wrap footer__inner">
        <div class="footer__brand">
          <span class="footer__logo">谷粒学苑</span>
          <p class="footer__slogan">让每一节课都有始有终。</p>
        </div>
        <nav class="footer__nav">
          <RouterLink to="/courses">全部课程</RouterLink>
          <RouterLink to="/teachers">名师阵容</RouterLink>
          <RouterLink v-if="isLogin" to="/me">我的资料</RouterLink>
          <RouterLink v-else to="/login">学员登录</RouterLink>
        </nav>
        <p class="footer__copy mono">© 2026 GULI ACADEMY · 课程实践项目</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowDown } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { auth } from '@/store/auth'

const router = useRouter()
const isLogin = computed(() => auth.isLogin())
const user = computed(() => auth.user)

async function onCommand(cmd) {
  if (cmd === 'me') {
    router.push('/me')
    return
  }
  if (cmd === 'logout') {
    try {
      await ElMessageBox.confirm('确认退出当前账号？', '退出登录', {
        confirmButtonText: '退出', cancelButtonText: '取消', type: 'warning'
      })
    } catch { return }
    auth.clear()
    ElMessage.success('已退出')
    router.push('/')
  }
}
</script>

<style scoped>
.portal {
  position: relative;
  z-index: 2;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* ---------------- 顶栏 ---------------- */
.header {
  position: sticky;
  top: 0;
  z-index: 30;
  background: rgba(251, 248, 241, 0.86);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--line);
}

.header__inner {
  height: var(--header-h);
  display: flex;
  align-items: center;
  gap: var(--sp-7);
}

.brand {
  display: flex;
  flex-direction: column;
  line-height: 1.05;
}

.brand__name {
  font-family: var(--font-display);
  font-size: var(--fs-xl);
  letter-spacing: 0.06em;
  color: var(--green);
}

.brand__sub {
  font-size: 9px;
  letter-spacing: 0.26em;
  color: var(--muted-2);
}

.nav {
  display: flex;
  gap: var(--sp-6);
  margin-right: auto;
}

.nav__item {
  position: relative;
  font-size: var(--fs-md);
  color: var(--ink-2);
  padding: 6px 0;
  transition: color var(--dur-fast) var(--ease-out);
}

.nav__item::after {
  content: "";
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 1px;
  background: var(--amber);
  transform: scaleX(0);
  transform-origin: left;
  transition: transform var(--dur-base) var(--ease-out);
}

.nav__item:hover { color: var(--ink); }
.nav__item:hover::after,
.nav__item.is-active::after { transform: scaleX(1); }

.actions {
  display: flex;
  align-items: center;
  gap: var(--sp-4);
}

.action-link {
  font-size: var(--fs-sm);
  color: var(--ink-2);
  border-bottom: 1px solid transparent;
  padding-bottom: 2px;
  transition: border-color var(--dur-fast), color var(--dur-fast);
}
.action-link:hover { color: var(--green); border-color: var(--green); }

.btn {
  display: inline-flex;
  align-items: center;
  height: 38px;
  padding: 0 var(--sp-4);
  background: var(--green);
  color: var(--paper);
  font-size: var(--fs-sm);
  letter-spacing: 0.06em;
  border-radius: var(--r-xs);
  transition: background var(--dur-fast) var(--ease-out), transform var(--dur-fast);
}
.btn:hover { background: var(--green-2); }
.btn:active { transform: translateY(1px); }

.user {
  display: flex;
  align-items: center;
  gap: var(--sp-2);
  height: 38px;
  padding: 0 var(--sp-3);
  background: transparent;
  border: 1px solid var(--line);
  border-radius: var(--r-pill);
  font-family: var(--font-body);
  font-size: var(--fs-sm);
  color: var(--ink-2);
  cursor: pointer;
  transition: border-color var(--dur-fast);
}
.user:hover { border-color: var(--line-strong); }

.user__avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: var(--green);
  color: var(--paper);
  font-size: var(--fs-2xs);
  font-family: var(--font-serif);
}

.user__caret { font-size: 11px; color: var(--muted); }

/* ---------------- 主体 ---------------- */
.main { flex: 1; }

/* ---------------- 页脚 ---------------- */
.footer {
  margin-top: var(--sp-10);
  background: var(--green-dim);
  color: rgba(251, 248, 241, 0.72);
}

.footer__inner {
  display: grid;
  grid-template-columns: 1.2fr 1fr auto;
  gap: var(--sp-6);
  align-items: end;
  padding: var(--sp-8) var(--sp-5);
}

.footer__logo {
  font-family: var(--font-display);
  font-size: var(--fs-2xl);
  color: var(--paper);
  letter-spacing: 0.06em;
}

.footer__slogan {
  margin-top: var(--sp-2);
  font-size: var(--fs-sm);
  color: rgba(251, 248, 241, 0.5);
}

.footer__nav {
  display: flex;
  gap: var(--sp-5);
  font-size: var(--fs-sm);
}
.footer__nav a { transition: color var(--dur-fast); }
.footer__nav a:hover { color: var(--amber-2); }

.footer__copy {
  font-size: var(--fs-2xs);
  letter-spacing: 0.1em;
  color: rgba(251, 248, 241, 0.4);
}

@media (max-width: 860px) {
  .nav { display: none; }
  .header__inner { justify-content: space-between; }
  .footer__inner { grid-template-columns: 1fr; gap: var(--sp-4); }
}
</style>
