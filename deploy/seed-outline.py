# -*- coding: utf-8 -*-
"""为 6 门课补全章节与小节（内容贴合各课程技术栈）"""
import pymysql

OUTLINE = {
    1: [  # Vue 3 从入门到实战
        ("第一章 起步与环境搭建", [
            ("为什么选择 Vue 3：组合式 API 的由来", "12:40"),
            ("用 Vite 创建第一个 Vue 项目", "15:10"),
            ("模板语法与指令系统", "18:25"),
            ("响应式系统初探：ref 与 reactive", "13:05"),
            ("开发工具与调试技巧", "10:30"),
        ]),
        ("第二章 核心概念", [
            ("计算属性 computed 与侦听器 watch", "16:20"),
            ("组件注册与 props 传递", "19:15"),
            ("自定义事件 emit 与 v-model", "17:40"),
            ("生命周期钩子全解", "14:55"),
            ("条件渲染与列表渲染的性能陷阱", "12:10"),
            ("表单绑定与修饰符", "11:35"),
        ]),
        ("第三章 组件进阶", [
            ("插槽：默认、具名与作用域插槽", "21:30"),
            ("动态组件与 keep-alive", "15:45"),
            ("provide / inject 跨层通信", "13:20"),
            ("Teleport 与模态框最佳实践", "12:50"),
            ("异步组件与代码分割", "14:15"),
        ]),
        ("第四章 工程化实战", [
            ("Vue Router 4 路由与导航守卫", "22:10"),
            ("Pinia 状态管理", "18:40"),
            ("Axios 封装与统一错误处理", "16:05"),
            ("打包、部署与性能分析", "13:30"),
        ]),
    ],
    2: [  # Spring Boot 企业级开发实战
        ("第一章 工程搭建", [
            ("Spring Boot 4 与 JDK 21 环境准备", "13:20"),
            ("从 start.spring.io 到可运行的服务", "15:40"),
            ("分层结构：controller / service / mapper", "19:10"),
            ("统一返回体与全局异常处理", "17:25"),
            ("配置管理：application.yml 与 profile", "14:50"),
        ]),
        ("第二章 数据访问", [
            ("MyBatis-Plus 快速上手", "18:30"),
            ("条件构造器与分页插件", "16:45"),
            ("逻辑删除与字段自动填充", "12:20"),
            ("事务边界：@Transactional 的正确用法", "15:10"),
            ("多表关联与 VO 组装", "20:35"),
        ]),
        ("第三章 Web 开发", [
            ("RESTful 接口设计规范", "14:40"),
            ("参数校验：@Valid 与自定义校验器", "17:05"),
            ("拦截器与过滤器的区别", "16:30"),
            ("文件上传与 OSS 集成", "21:15"),
            ("跨域处理与前后端分离部署", "12:45"),
        ]),
        ("第四章 认证与安全", [
            ("JWT 原理与令牌签发", "18:20"),
            ("拦截器实现登录校验", "15:35"),
            ("BCrypt 密码加密与加盐", "10:50"),
            ("令牌刷新与退出登录", "13:10"),
            ("常见越权漏洞与防御", "19:40"),
        ]),
        ("第五章 部署与运维", [
            ("打可执行 jar 与启动参数调优", "12:30"),
            ("Nginx 反向代理配置", "16:15"),
            ("systemd 托管与开机自启", "14:05"),
            ("日志分级与滚动策略", "11:45"),
            ("线上排障：从日志到线程栈", "18:25"),
        ]),
    ],
    3: [  # MySQL 性能优化实战
        ("第一章 索引原理", [
            ("B+ 树为什么适合做索引", "19:30"),
            ("聚簇索引与二级索引", "17:15"),
            ("最左前缀原则与联合索引", "16:40"),
            ("覆盖索引与回表", "14:20"),
            ("索引失效的六种场景", "18:05"),
        ]),
        ("第二章 执行计划", [
            ("EXPLAIN 输出逐列解读", "21:10"),
            ("type 等级与访问类型", "15:35"),
            ("rows 与 filtered 的估算逻辑", "13:50"),
            ("慢查询日志与 pt-query-digest", "16:25"),
            ("SHOW PROFILE 与性能剖析", "12:40"),
        ]),
        ("第三章 优化实践", [
            ("深分页的三种优化方案", "18:15"),
            ("JOIN 顺序与驱动表选择", "17:40"),
            ("大表 DDL 与在线变更", "20:05"),
            ("分区表的适用场景与陷阱", "14:30"),
            ("读写分离与主从延迟", "16:50"),
        ]),
    ],
    4: [  # 前端工程化与 Vite 深度实践
        ("第一章 模块化与构建", [
            ("从 CommonJS 到 ESM", "16:20"),
            ("打包器做了什么：依赖图与产物", "19:45"),
            ("Tree Shaking 的生效条件", "14:10"),
            ("为什么 Webpack 启动越来越慢", "12:35"),
        ]),
        ("第二章 Vite 核心机制", [
            ("基于 ESM 的按需编译", "17:30"),
            ("依赖预构建与缓存", "15:20"),
            ("插件机制与生命周期", "20:40"),
            ("HMR 的实现原理", "16:55"),
            ("环境变量与模式", "11:25"),
        ]),
        ("第三章 工程实践", [
            ("多环境配置与接口代理", "14:45"),
            ("代码分割与分包策略", "18:10"),
            ("构建优化：压缩、CDN 与缓存", "16:30"),
            ("从构建产物到部署上线", "12:50"),
        ]),
    ],
    5: [  # Java 并发编程精讲
        ("第一章 线程基础", [
            ("线程的三种创建方式与取舍", "14:25"),
            ("线程生命周期与状态转换", "16:40"),
            ("中断机制与优雅停止", "15:15"),
            ("守护线程与用户线程", "10:35"),
            ("线程池入门：核心参数怎么定", "19:20"),
        ]),
        ("第二章 Java 内存模型", [
            ("JMM 与可见性问题", "18:30"),
            ("happens-before 规则", "16:05"),
            ("volatile 的两个语义", "14:50"),
            ("指令重排与内存屏障", "17:40"),
        ]),
        ("第三章 锁与同步", [
            ("synchronized 的锁升级过程", "20:15"),
            ("ReentrantLock 与 AQS", "15:35"),
            ("CAS 与 ABA 问题", "17:20"),
            ("死锁的四个条件与排查", "16:45"),
            ("读写锁与 StampedLock", "13:10"),
        ]),
        ("第四章 并发工具", [
            ("CountDownLatch 与 CyclicBarrier", "14:30"),
            ("Semaphore 限流实践", "12:55"),
            ("ConcurrentHashMap 的分段演进", "18:40"),
            ("CompletableFuture 编排异步任务", "21:05"),
        ]),
    ],
    6: [  # Vite 5 工程化实战
        ("第一章 Vite 5 新特性", [
            ("更快的冷启动与 HMR", "15:20"),
            ("环境 API 与配置热重载", "13:45"),
            ("弃用项与迁移清单", "12:10"),
            ("与 Vite 4 的性能对比实测", "14:35"),
        ]),
        ("第二章 实战配置", [
            ("别名、代理与基础配置", "16:50"),
            ("构建产物分析与优化", "18:25"),
            ("插件选型：常用十个插件", "20:10"),
            ("SSR 与预渲染", "17:35"),
            ("部署到 Nginx 的注意事项", "11:40"),
        ]),
    ],
}

c = pymysql.connect(host='122.51.6.69', user='root', password='chuangshu', database='guli', charset='utf8mb4')
cur = c.cursor()

total_ch = total_sec = 0
for course_id, chapters in OUTLINE.items():
    for ci, (ch_title, sections) in enumerate(chapters, start=1):
        cur.execute("INSERT INTO edu_chapter(course_id, title, sort) VALUES(%s,%s,%s)",
                    (course_id, ch_title, ci))
        ch_id = cur.lastrowid
        total_ch += 1
        for si, (s_title, dur) in enumerate(sections, start=1):
            cur.execute("INSERT INTO edu_section(course_id, chapter_id, title, duration, sort) "
                        "VALUES(%s,%s,%s,%s,%s)", (course_id, ch_id, s_title, dur, si))
            total_sec += 1

c.commit()
print(f"写入完成：{total_ch} 章 / {total_sec} 节")

cur.execute("""SELECT co.id, co.title, COUNT(DISTINCT ch.id) chs, COUNT(s.id) secs
               FROM edu_course co
               LEFT JOIN edu_chapter ch ON ch.course_id = co.id
               LEFT JOIN edu_section s ON s.course_id = co.id
               GROUP BY co.id, co.title ORDER BY co.id""")
print("\n各课程大纲：")
for r in cur.fetchall():
    print(f"  [{r[0]}] {r[1]:<32} {r[2]} 章 / {r[3]} 节")
c.close()
