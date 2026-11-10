<template>
  <div class="auth">
    <div class="auth__form">
      <div class="auth__inner">
        <span class="eyebrow">Create account</span>
        <h1 class="auth__title">注册谷粒学苑</h1>
        <p class="auth__desc">注册后即可查看完整大纲、收藏课程。</p>

        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="onSubmit">
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="form.nickname" size="large" maxlength="20" placeholder="2~20 个字符" />
          </el-form-item>
          <el-form-item label="手机号" prop="mobile">
            <el-input v-model="form.mobile" size="large" maxlength="11" placeholder="用作登录账号" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" size="large" show-password placeholder="6~20 位" />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirm">
            <el-input
              v-model="form.confirm"
              type="password"
              size="large"
              show-password
              placeholder="再输入一次"
              @keyup.enter="onSubmit"
            />
          </el-form-item>

          <button class="submit" type="submit" :disabled="loading">
            {{ loading ? '提交中…' : '注 册' }}
          </button>
        </el-form>

        <p class="auth__switch">
          已有账号？
          <RouterLink to="/login">直接登录 →</RouterLink>
        </p>
      </div>
    </div>

    <aside class="auth__aside">
      <div class="auth__quote">
        <span class="eyebrow eyebrow--invert">Why join</span>
        <ul class="perks">
          <li>完整章节大纲，学之前先看清全貌</li>
          <li>讲师主页与主讲课程一览</li>
          <li>按分类、价格、热度自由筛选</li>
        </ul>
      </div>
    </aside>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/portal'
import { auth } from '@/store/auth'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const form = reactive({ nickname: '', mobile: '', password: '', confirm: '' })

const rules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称 2~20 个字符', trigger: 'blur' }
  ],
  mobile: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码 6~20 位', trigger: 'blur' }
  ],
  confirm: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (_, value, cb) => {
        if (value !== form.password) cb(new Error('两次输入的密码不一致'))
        else cb()
      },
      trigger: 'blur'
    }
  ]
}

async function onSubmit() {
  const ok = await formRef.value.validate().catch(() => false)
  if (!ok) return
  loading.value = true
  try {
    const d = await register({ nickname: form.nickname, mobile: form.mobile, password: form.password })
    auth.set(d.token, { id: d.userId, nickname: d.nickname, mobile: d.username })
    ElMessage.success('注册成功，已自动登录')
    router.replace('/')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth {
  position: relative;
  z-index: 2;
  display: grid;
  grid-template-columns: 1fr 46%;
  min-height: calc(100vh - var(--header-h));
}

.auth__form { display: grid; place-items: center; padding: var(--sp-8) var(--sp-5); }
.auth__inner { width: min(380px, 100%); }

.auth__title { font-size: clamp(30px, 3.6vw, 44px); margin: var(--sp-2) 0 var(--sp-2); }
.auth__desc { font-size: var(--fs-sm); color: var(--muted); margin-bottom: var(--sp-5); }

.submit {
  width: 100%;
  height: 50px;
  margin-top: var(--sp-2);
  background: var(--green);
  color: var(--paper);
  border: 0;
  font-family: var(--font-body);
  font-size: var(--fs-md);
  letter-spacing: 0.2em;
  cursor: pointer;
  transition: background var(--dur-fast), opacity var(--dur-fast);
}
.submit:hover { background: var(--green-2); }
.submit:disabled { opacity: 0.6; cursor: not-allowed; }

.auth__switch { margin-top: var(--sp-4); font-size: var(--fs-sm); color: var(--muted); }
.auth__switch a { color: var(--green); border-bottom: 1px solid var(--green); padding-bottom: 1px; }

.auth__aside {
  background: var(--green-dim);
  display: grid;
  place-items: center;
  padding: var(--sp-8);
  position: relative;
  overflow: hidden;
}

.eyebrow--invert { color: rgba(251, 248, 241, 0.5); }

.perks {
  list-style: none;
  margin: var(--sp-5) 0 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: var(--sp-4);
}

.perks li {
  font-family: var(--font-serif);
  font-size: var(--fs-lg);
  color: var(--paper);
  padding-left: var(--sp-5);
  position: relative;
  line-height: 1.7;
}

.perks li::before {
  content: "";
  position: absolute;
  left: 0;
  top: 12px;
  width: 14px;
  height: 1px;
  background: var(--amber-2);
}

@media (max-width: 900px) {
  .auth { grid-template-columns: 1fr; }
  .auth__aside { display: none; }
}
</style>
