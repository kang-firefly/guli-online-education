# 谷粒学苑 · 前台门户（portal-web）

Vue 3 + Vite。**M4：首页 / 课程 / 名师 / 注册登录**。

## 定调说明

与后台**同源但不同调**：同一套墨绿 + 纸感 + 衬线字体，前台更亮、更松、图更大。

| 项 | 决策 |
|---|---|
| **目的** | 让访客在 10 秒内看懂"有什么课、谁来讲"，并愿意注册 |
| **调性** | 教育刊物 / 杂志门户（与后台一致的 Editorial，但不做后台那种高密度） |
| **展示字** | Playfair Display + Noto Serif SC |
| **正文** | Noto Sans SC（行高 1.8，比后台更松） |
| **主色** | 深墨绿 `#123a2c`（顶栏 logo、按钮、价格、页脚） |
| **底色** | 暖纸 `#fbf8f1`，比后台的 `#f5f1e7` 再亮一档 |
| **强调色** | 琥珀 `#bd7d1e`：主 CTA、分类选中下划线、价格旁按钮 |
| **记忆点** | 首屏「纸白卡片压在模糊色场上」的错位构图 —— 背景是后台维护的 Banner，做成柔焦色块，卡片才是主角 |

刻意为之：
- Banner 图**故意模糊 10px 压暗**，因为它自带标题，不处理会和卡片大标题打架；
- 课程卡不用圆角阴影堆叠，用 1px 发丝边框 + hover 位移；
- 分类筛选用**文字 chip + 下划线**，不用圆角胶囊；
- 大纲用 `01 / 1.1` 的编号体系，像目录页。

## 目录

```
portal-web/src/
├── styles/tokens.css       # 令牌（比后台大一号的字阶与间距）
├── styles/base.css         # 重置、纸感氛围、section-head、入场动画
├── api/portal.js           # 首页 / 课程 / 讲师 / 会员
├── layouts/PortalLayout.vue
├── components/CourseCard.vue, TeacherCard.vue
└── views/
    ├── HomeView.vue         # Hero + 分类条 + 热门课程 + 名师 + CTA
    ├── CourseListView.vue   # 分类 chip + 排序 + 分页
    ├── CourseDetailView.vue # 课程信息 + 大纲 + 讲师侧栏
    ├── TeacherListView.vue
    ├── TeacherDetailView.vue
    ├── LoginView.vue / RegisterView.vue / MemberView.vue
```

## 启动

```bash
# 后端先起（8080）
cd .. && mvn spring-boot:run
# 前台
cd portal-web && npm install && npm run dev   # http://127.0.0.1:5175
```

`/api` 已代理到 `localhost:8080`。演示学员：`13800000000` / `123456`。

## 视觉 QA（`node scripts/qa-m4.mjs` / `qa-member.mjs`）

| 页面 | 截图 |
|---|---|
| 首页（1440 全页） | `shots/p1-home.png` |
| 全部课程 | `shots/p2-courses.png` |
| 课程详情 + 大纲 | `shots/p3-course-detail.png` |
| 名师列表 | `shots/p4-teachers.png` |
| 讲师详情 | `shots/p5-teacher-detail.png` |
| 登录 | `shots/p6-login.png` |
| 首页（390 宽） | `shots/p7-home-mobile.png` |
| 我的资料（已登录） | `shots/p8-member.png` |

已修正：Banner 与标题抢视觉（改柔焦）、登录页巨型描边字被裁成噪点（改成琥珀细线）。

## 尚未实现（如实说明）

- 「立即报名」目前只弹提示，**没有订单/支付**（选题方案里也没纳入）。
- 没有课程搜索框（列表支持分类与排序，关键字接口已支持 `keyword`，前端未放开入口）。
