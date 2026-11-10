# WPS 流程图 · Mermaid 粘贴稿（5 张图）

> WPS 流程图的「导入流程图」弹窗第一栏就是 **粘贴 Mermaid 文本**。
> 用法：复制某一段代码（**不要带 ``` 这行**），粘进输入框 → 确定 → 生成图形 → 在 WPS 里拖一拖、换形状。
>
> ⚠️ **先说清楚它能做到什么**：Mermaid 由 dagre 自动排版，**我控制不了节点位置**。
> 导入后你会得到“内容完整、连线正确、但布局需要手动拖”的骨架。
> 其中 **用例图 / E-R 图** 出来的效果可直接用；**活动图 / 部署图** 偏窄长，需要在 WPS 里横向拉开。
>
> 如果你只想要“能直接插报告的图”，用 `out/preview_*.png`（2 倍高清）更快，不用在 WPS 里返工。

---

## 1. 用例图

```mermaid
flowchart LR
    Admin(["管理员"])
    Guest(["访客"])
    Member(["学员"])

    subgraph SYS1["后台管理端"]
        U1(["登录后台"])
        U2(["讲师管理"])
        U3(["课程分类管理"])
        U4(["课程列表 · 下架 · 删除"])
        U5(["维护首页 Banner"])
        U6(["发布课程（三步向导）"])
    end

    subgraph SYS2["前台门户"]
        F1(["浏览首页"])
        F2(["查看讲师列表 / 详情"])
        F3(["浏览课程列表 / 详情"])
        F4(["注册账号"])
        F5(["登录 / 退出"])
    end

    SYS1 ~~~ SYS2

    Admin --- U1
    Admin --- U2
    Admin --- U3
    Admin --- U4
    Admin --- U5
    Admin --- U6

    Guest --- F1
    Guest --- F2
    Guest --- F3

    Member --- F4
    Member --- F5
```

> 粘完把 `Admin / Guest / Member` 三个节点换成 WPS 的**火柴人（参与者）**形状即可；
> 学员不连「浏览课程」，因为学员是访客的子集，浏览能力由访客一侧表达。

---

## 2. 体系结构图

```mermaid
flowchart TB
    subgraph CLIENT["客户端层"]
        A1["后台管理端 SPA<br/>Vue 3 + Element Plus"]
        A2["前台门户 SPA<br/>Vue 3 + vue-router"]
    end

    NG["Nginx :80<br/>静态托管 · 反向代理 · JWT 透传"]

    subgraph APP["应用层 · Spring Boot 4.1.1（JDK 21）"]
        direction TB
        W["Web 层<br/>Controller · JWT 拦截器 · 统一返回 · 全局异常 · 参数校验"]
        S["业务层<br/>Teacher · Subject · Course · Chapter · Section · Member"]
        P["持久层<br/>MyBatis-Plus · 分页插件 · 逻辑删除 · Entity"]
        I["基础设施<br/>JwtUtil · OssUtil · Config · 统一异常码"]
    end

    subgraph DATA["数据层"]
        DB[("MySQL 8.0<br/>库 guli · 10 张表")]
        OSS["阿里云 OSS<br/>图片存储"]
    end

    CLIENT --> NG
    NG --> APP
    APP --> DATA
```

---

## 3. E-R 图

```mermaid
flowchart LR
    T["edu_teacher 讲师<br/>id PK<br/>name 姓名<br/>career 资历<br/>level 级别"]
    C["edu_course 课程<br/>id PK<br/>teacher_id FK<br/>subject_id FK<br/>title 标题<br/>price 价格<br/>status 0/1"]
    S["edu_subject 分类<br/>id PK<br/>title 名称<br/>parent_id FK<br/>sort 排序"]
    D["edu_course_description<br/>id PK<br/>course_id FK<br/>description 简介"]
    CH["edu_chapter 章节<br/>id PK<br/>course_id FK<br/>title 标题"]
    SE["edu_section 小节<br/>id PK<br/>chapter_id FK<br/>title 标题<br/>duration 时长"]

    T -->|1 : N 主讲| C
    S -->|1 : N 归属| C
    C -->|1 : 1| D
    C -->|1 : N| CH
    CH -->|1 : N| SE

    subgraph IND["独立实体（不与课程聚合）"]
        direction LR
        U["sys_user<br/>后台账号"]
        M["ucenter_member<br/>前台学员"]
        B["crm_banner<br/>首页轮播"]
        L["sys_login_log<br/>登录日志"]
    end
```

---

## 4. 活动图 · 课程发布

```mermaid
flowchart TD
    S((开始)) --> A["填写课程基本信息<br/>标题 · 讲师 · 分类 · 价格 · 封面 · 简介"]
    A --> B["必填项校验"]
    B --> C{"必填齐全？"}
    C -->|否 · 提示缺失字段| A
    C -->|是| D["保存草稿 status = 0<br/>并编排章节与小节"]
    D --> E{"发布前完整性校验<br/>封面 · 简介 · ≥1 章 · ≥1 节"}
    E -->|否 · 返回缺失项清单| D
    E -->|是| F["置 status = 1<br/>记录发布时间"]
    F --> G["前台列表与详情可检索"]
    G --> H(((结束)))
```

> 纵向流程图。插入 Word 时按 **9cm 宽**排（高约 16cm），正好一页放得下。

---

## 5. 部署图

```mermaid
flowchart TB
    B["客户端浏览器<br/>Chrome / Edge"]

    subgraph SRV["云服务器 122.51.6.69 · Ubuntu 24.04"]
        direction LR
        N["Nginx<br/>:80 静态托管 + 反向代理"] --> S["Spring Boot<br/>:8080 systemd 常驻"] --> M[("MySQL 8.0<br/>:3306 仅本机 · 库 guli")]
    end

    O["阿里云 OSS<br/>图片 avatar / cover / banner"]

    B -->|HTTPS| N
    S -->|JDBC| M
    S -->|HTTPS 图片上传| O
```

> 三层：浏览器 → 服务器（内部三件套横排）→ OSS。插入 Word 按 **14cm 宽**排。

---

## 如果 Mermaid 也失败

WPS 那三个入口的真实支持情况：

| 入口 | 说明 |
|---|---|
| **粘贴 Mermaid 文本** | 最稳，纯文本解析，不涉及文件格式校验 |
| 上传 POS / POSF | WPS 自家的流程图格式（ProcessOn 系），我们没有这个格式的生成器 |
| 上传 Visio | 大概率只吃旧版 `.vsd`（97-2003 二进制），**不吃 `.vsdx`**；这就是你传不上去的原因 |

所以顺序是：**先试英文名的 vsdx**（`vsdx/usecase.vsdx` 等，中文文件名也可能是上传失败原因）→ 不行就 **粘 Mermaid** → 都不行再照 `out/preview_*.png` 手动摆。

## 导出注意

- WPS 导出图片默认可能是 1x，贴进 Word 会糊：导出时选 **2x / 高清 / 300dpi**
- 导出完可以再「另存为 Visio」，手上就有 `.vsdx` 源文件，答辩问起来有交代
