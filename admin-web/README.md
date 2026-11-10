# 谷粒学苑 · 后台管理端（admin-web）

Vue 3 + Vite + Element Plus。已完成 **M2：登录 + 讲师管理**、**M3：课程分类 + 课程发布三步向导**。

## 定调说明（Design Direction）

| 项 | 决策 |
|---|---|
| **目的** | 给平台运营者用的内容管理后台：维护讲师、课程分类、课程大纲 |
| **受众** | 单个管理员，长时间对着表格与表单操作 |
| **调性** | **杂志编辑风（Editorial）** —— 只此一个方向，不混搭 |
| **展示字** | Playfair Display（拉丁）+ Noto Serif SC（中文标题） |
| **正文字** | Noto Sans SC（中文）+ Consolas（数字/时间，等宽） |
| **主色** | 深墨绿 `#123a2c`（侧栏、主文字、选中态） |
| **底色** | 暖白纸 `#f5f1e7`，卡片 `#fffdf7` |
| **强调色** | 琥珀 `#b07d2b` —— 全站只用一处：主操作按钮与选中细条 |
| **记忆点** | 深墨绿"书脊"侧栏 + 衬线大标题 + 发丝线分隔的表格；登录页左半幅的巨型描边"学"字压角 |

反 AI 味纪律：不用 Inter/Roboto/系统字体做展示字、不用紫蓝渐变、不做三卡并排首屏、
圆角收到 2px、表格不用斑马纹、强调色只留给真正的主操作。

## 目录

```
admin-web/
├── src/
│   ├── styles/
│   │   ├── tokens.css         # 颜色/字阶/间距/圆角/阴影/动效令牌
│   │   ├── base.css           # 重置、纸感氛围、焦点、动效降级
│   │   └── element-theme.css  # Element Plus 主题改写（压掉默认蓝+大圆角）
│   ├── api/                   # http 封装 + 各模块接口
│   ├── store/auth.js          # token 与用户信息（localStorage）
│   ├── router/index.js        # 路由 + 登录守卫
│   ├── layouts/AdminLayout.vue
│   └── views/
│       ├── LoginView.vue
│       └── TeacherView.vue
└── scripts/qa-shot.mjs        # 视觉 QA：真登录后自动截图
```

## 启动

```bash
# 1. 先启动后端（端口 8080）
cd .. && mvn spring-boot:run

# 2. 再启动前端
cd admin-web
npm install
npm run dev          # http://127.0.0.1:5173
```

`vite.config.js` 里已把 `/admin`、`/api` 代理到 `http://127.0.0.1:8080`，开发期不需要处理跨域。

演示账号：`admin` / `admin123`

## 动效说明

- 页面入场：`rise`（位移 18px + 淡入），4 组错峰 60/140/220/300ms；
- 路由切换：`page` 过渡 320ms，`cubic-bezier(0.16,1,0.3,1)`；
- 侧栏导航项逐条入场（间隔 60ms）；
- 悬停只动 `color` / `background` / `transform`，不触发布局；
- 全部动效在 `prefers-reduced-motion: reduce` 下关闭。

## 视觉 QA（`node scripts/qa-shot.mjs`）

| 关卡 | 结果 |
|---|---|
| 1440×900 登录页 | ✅ `shots/01-login.png` |
| 1440×900 讲师页（真登录后） | ✅ `shots/02-teacher.png` |
| 弹窗表单 | ✅ `shots/03-dialog.png` |
| 390×844 窄屏 | ✅ `shots/04-mobile.png`（侧栏收成图标、操作列换图标按钮） |
| 课程分类树 | ✅ `shots/05-subject.png` |
| 课程列表 | ✅ `shots/06-course.png` |
| 发布向导：① 基本信息 | ✅ `shots/07-wizard-step1.png` |
| 发布向导：② 课程大纲 | ✅ `shots/09-wizard-outline.png` |
| 发布向导：③ 确认发布 | ✅ `shots/10-wizard-confirm.png` |
| 对比度 | 正文 `#3d473e` / `#f5f1e7` ≈ 8.6:1；弱化文字 `#6f7a70` ≈ 4.7:1；均 ≥4.5:1 |
| 焦点可见 | 全局 `:focus-visible` 2px 琥珀描边 + 2px 偏移 |
| 点击目标 | 侧栏项 44px 高；表格内按钮行高 44px |
| 空态 / 加载 / 错误 | 表格空态自绘；`v-loading` 加载；接口失败走全局 Message |

## 后续（M3 / M4）

- 课程分类（两级树）、课程发布三步向导（步骤条 + 章节小节编辑）
- Banner 维护、前台门户
- 侧栏已预留 M3 / M4 入口（禁用态，避免做假按钮）
