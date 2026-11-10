#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
谷粒学苑 · 报告用 Visio 原生图（.vsdx）
======================================
生成 5 个真 Visio 文件（不是图片）：用例图 / 体系结构图 / E-R 图 / 活动图 / 部署图。
每个图形都是普通 Visio 形状（显式 Geometry 段），在 Visio 里可继续拖拽编辑。

坐标单位：英寸；y 轴向下（落盘时统一翻成 Visio 的 y-up）。
"""
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
SKILL = r'C:\Users\admin\.workbuddy\skills\vsdx-authoring\scripts'
sys.path.insert(0, SKILL)

from gen_vsdx import Model, build_vsdx, model_to_svg, A4L_W, A4L_H  # noqa: E402

OUT = os.path.join(HERE, 'vsdx')
TEMPLATE = r'C:\Users\admin\.workbuddy\binaries\python\envs\default\Lib\site-packages\vsdx\media\media.vsdx'

BLACK = '#000000'
GRAY = '#666666'
THIN = 0.01041666666666667

os.makedirs(OUT, exist_ok=True)


# ==========================================================================
# 1. 用例图
# ==========================================================================
def d_usecase():
    m = Model(A4L_W, A4L_H, '用例图')

    # 系统边界
    m.rect(2.30, 0.60, 3.90, 7.05, stroke=BLACK, sw=THIN,
           text='后台管理端', size=13, bold=True, valign=0,
           tmargin=(0.02, 0.02, 0.10, 0.02))
    m.rect(6.50, 0.60, 3.10, 7.05, stroke=BLACK, sw=THIN,
           text='前台门户', size=13, bold=True, valign=0,
           tmargin=(0.02, 0.02, 0.10, 0.02))

    # 参与者
    m.actor(1.45, 3.95, label='管理员', label_size=11)
    m.actor(10.28, 1.98, label='访客', label_size=11)
    m.actor(10.28, 5.20, label='学员', label_size=11)

    # ---- 后台用例 ----
    RX1, CX1 = 1.62, 4.25
    adm_rows = [1.55, 2.52, 3.49, 4.46, 5.43, 6.40]
    adm_names = ['登录后台', '讲师管理', '课程分类管理',
                 '课程列表·下架·删除', '维护首页 Banner', '发布课程（三步向导）']

    for r in adm_rows:
        m.line([(1.83, 4.40), (CX1 - RX1, r)])
    for r, nm in zip(adm_rows, adm_names):
        m.ellipse(CX1, r, RX1, 0.31, text=nm, size=10.5)

    # ---- 前台用例 ----
    RX2, CX2 = 1.42, 8.05
    front_rows = [1.30, 2.05, 2.80, 3.55, 4.90, 5.65, 6.40]
    front_names = ['浏览首页', '查看讲师列表 / 详情', '浏览课程列表 / 详情', '在线播放',
                   '注册账号', '登录 / 退出', '购买课程']

    # 访客：浏览 + 播放（免费课游客可看）；学员：注册/登录/购买
    for r in front_rows[:4]:
        m.line([(9.95, 2.43), (CX2 + RX2, r)])
    for r in front_rows[4:]:
        m.line([(9.95, 5.65), (CX2 + RX2, r)])
    for r, nm in zip(front_rows, front_names):
        m.ellipse(CX2, r, RX2, 0.31, text=nm, size=10.5)

    return m.crop()


# ==========================================================================
# 2. 体系结构图
# ==========================================================================
def d_architecture():
    m = Model(10.6, 7.6, '体系结构图')

    # ---- 客户端层 ----
    m.rect(0.20, 0.20, 10.20, 1.05, stroke=BLACK, sw=THIN)
    m.rect(0.32, 0.26, 0.92, 0.24, stroke=None, text='客户端层', size=8.5, bold=True)
    m.rect(0.55, 0.55, 4.50, 0.52, stroke=BLACK, sw=THIN,
           text='后台管理端 SPA（Vue 3 + Element Plus）', size=10)
    m.rect(5.35, 0.55, 4.70, 0.52, stroke=BLACK, sw=THIN,
           text='前台门户 SPA（Vue 3 + vue-router）', size=10)

    m.line([(5.30, 1.25), (5.30, 1.52)], arrow=True)

    # ---- 接入层 ----
    m.rect(0.20, 1.55, 10.20, 0.95, stroke=BLACK, sw=THIN)
    m.rect(0.32, 1.61, 0.92, 0.24, stroke=None, text='接入层', size=8.5, bold=True)
    m.rect(2.55, 1.83, 5.50, 0.48, stroke=BLACK, sw=THIN,
           text='Nginx :80　静态托管 · /api /admin 反向代理 · JWT 透传', size=9.5)

    m.line([(5.30, 2.50), (5.30, 2.78)], arrow=True)

    # ---- 应用层 ----
    m.rect(0.20, 2.80, 10.20, 3.45, stroke=BLACK, sw=THIN)
    m.rect(0.32, 2.86, 2.55, 0.22, stroke=None, text='应用层 · Spring Boot 4.1.1（JDK 21）', size=8, bold=True)

    layers = [
        ('Web 层', 3.08, ['Controller', 'JWT 拦截器', '统一返回', '全局异常', '参数校验']),
        ('业务层', 3.82, ['Teacher', 'Subject', 'Course', 'Chapter', 'Section', 'Member']),
        ('持久层', 4.56, ['MyBatis-Plus', '分页插件', '逻辑删除', '实体 Entity']),
        ('基础设施', 5.30, ['JwtUtil', 'OssUtil', 'Config', '统一异常码']),
    ]
    for name, y, items in layers:
        m.rect(0.42, y, 9.76, 0.62, stroke=GRAY, sw=THIN)
        m.rect(0.50, y + 0.20, 0.52, 0.22, stroke=None, text=name, size=7.5, bold=True)
        n = len(items)
        avail = 9.10
        bw = min(1.55, (avail - 0.12 * (n - 1)) / n)
        total = n * bw + 0.12 * (n - 1)
        x = 0.42 + 0.62 + (avail - total) / 2
        for it in items:
            m.rect(x, y + 0.13, bw, 0.36, stroke=BLACK, sw=THIN, text=it, size=8.5)
            x += bw + 0.12

    # 说明文字
    m.label(5.30, 6.02, 9.6, 0.24,
            'Controller 只做参数绑定与返回封装　·　ServiceImpl 承担业务规则与事务边界　·　Mapper 仅单表 CRUD',
            size=8.5, color=GRAY)

    m.line([(5.30, 6.25), (5.30, 6.52)], arrow=True)

    # ---- 数据层 ----
    m.rect(0.20, 6.55, 10.20, 0.95, stroke=BLACK, sw=THIN)
    m.rect(0.32, 6.61, 0.92, 0.24, stroke=None, text='数据层', size=8.5, bold=True)
    m.rect(0.55, 6.83, 5.00, 0.48, stroke=BLACK, sw=THIN,
           text='MySQL 8.0　库 guli（10 张表）', size=10)
    m.rect(5.95, 6.83, 4.10, 0.48, stroke=BLACK, sw=THIN,
           text='阿里云 OSS　图片存储', size=10)

    return m.crop()


# ==========================================================================
# 3. E-R 图
# ==========================================================================
def _table(m, x, y, w, title, rows, hh=0.34, rh=0.215, bold=False):
    h = hh + rh * len(rows)
    m.rect(x, y, w, h, stroke=BLACK, sw=THIN, text=None)
    m.rect(x, y, w, hh, stroke=BLACK, sw=THIN, text=title, size=9.5, bold=True)
    for i, txt in enumerate(rows):
        m.label(x + w / 2, y + hh + rh * (i + 0.5), w - 0.10, rh, txt,
                size=8, halign=0)
    return y + h


def d_er():
    m = Model(10.8, 8.0, 'E-R图')

    tw = 2.45
    # 三个主表
    _table(m, 0.20, 0.30, tw, 'edu_teacher 讲师',
           ['id  PK', 'name 姓名', 'career 资历', 'level 级别 1/2', 'avatar 头像'])
    _table(m, 3.15, 0.30, tw, 'edu_course 课程',
           ['id  PK', 'teacher_id  FK', 'subject_id  FK', 'title 标题',
            'price 价格', 'status 0/1'])
    _table(m, 6.10, 0.30, tw, 'edu_subject 分类',
           ['id  PK', 'title 名称', 'parent_id  FK', 'sort 排序'])

    _table(m, 3.15, 2.55, tw, 'edu_course_description',
           ['id  PK', 'course_id  FK', 'description 简介'])
    _table(m, 6.10, 2.55, tw, 'edu_chapter 章节',
           ['id  PK', 'course_id  FK', 'title 标题', 'sort 排序'])
    _table(m, 6.10, 4.20, tw, 'edu_section 小节',
           ['id  PK', 'chapter_id  FK', 'title 标题', 'duration 时长', 'sort 排序'])

    # 关系线
    m.line([(2.66, 1.05), (3.14, 1.05)], arrow=True)
    m.label(2.90, 0.90, 0.9, 0.18, '1 : N', size=8)
    m.line([(6.10, 1.05), (5.61, 1.05)], arrow=True)
    m.label(5.86, 0.90, 0.9, 0.18, '1 : N', size=8)

    m.line([(4.38, 1.90), (4.38, 2.54)], arrow=True)
    m.label(4.72, 2.22, 0.8, 0.18, '1 : 1', size=8)

    m.line([(5.60, 1.60), (5.98, 1.60), (5.98, 2.75), (6.10, 2.75)], arrow=True)
    m.label(6.20, 2.05, 0.7, 0.18, '1 : N', size=8)

    m.line([(7.33, 3.55), (7.33, 4.19)], arrow=True)
    m.label(7.95, 3.86, 0.7, 0.18, '1 : N', size=8)

    # 分类自关联
    m.line([(8.55, 0.65), (8.90, 0.65), (8.90, 0.15), (7.60, 0.15), (7.60, 0.30)],
           arrow=True)
    m.label(9.15, 0.40, 0.7, 0.18, '1 : N', size=8)

    # 订单（连接会员与课程）
    _table(m, 0.20, 4.20, tw, 'edu_order 订单',
           ['id  PK', 'order_no 订单号', 'amount 金额', 'status 0/1/2', 'pay_time 支付时间'])

    # 订单 → 课程（N:1，订单属于课程）
    m.line([(1.45, 4.20), (3.15, 1.60)], arrow=True)
    m.label(2.45, 2.75, 0.9, 0.18, 'N : 1', size=8)

    # 订单 → 会员（N:1，下单人）
    m.line([(2.65, 5.40), (2.85, 5.95)], arrow=True)
    m.label(3.15, 5.62, 0.9, 0.18, 'N : 1', size=8)

    # 独立实体
    iy = 6.15
    tw2, gap = 2.30, 0.35
    _table(m, 0.20, iy, tw2, 'sys_user 后台账号',
           ['id  PK', 'username 账号', 'password 摘要', 'is_deleted'])
    _table(m, 0.20 + (tw2 + gap), iy, tw2, 'ucenter_member 学员',
           ['id  PK', 'mobile 手机号', 'password 摘要', 'nickname 昵称'])
    _table(m, 0.20 + 2 * (tw2 + gap), iy, tw2, 'crm_banner 轮播',
           ['id  PK', 'image_url 图片', 'link_url 跳转', 'sort 排序'])
    _table(m, 0.20 + 3 * (tw2 + gap), iy, tw2, 'sys_login_log 日志',
           ['id  PK', 'username 账号', 'user_type 端', 'login_time 时间'])

    m.label(0.20 + tw2 / 2, iy - 0.22, 2.4, 0.22, '独立实体（不与课程聚合）', size=8, color=GRAY)

    return m.crop()


# ==========================================================================
# 4. 活动图 · 课程发布
# ==========================================================================
def d_activity():
    m = Model(5.4, 8.2, '活动图-课程发布')

    cx = 2.30
    W, H = 3.30, 0.52
    ys = [0.55, 1.30, 2.00, 2.70, 3.40, 4.10, 4.80, 5.50, 6.28, 7.00]
    # 0 开始 / 1 填写 / 2 校验 / 3 菱形 / 4 草稿 / 5 编排 / 6 确认+校验 / 7 菱形 / 8 发布 / 9 结束

    m.dot(cx, ys[0], 0.075)
    m.label(cx + 1.15, ys[0], 1.0, 0.22, '开始', size=9, halign=0)

    boxes = [
        (ys[1], '填写课程基本信息', '标题·讲师·分类·价格·封面·简介'),
        (ys[2], '必填项校验', '参数校验 + 分类必须为二级'),
        (ys[4], '保存草稿 status = 0', '写入课程与简介，返回 courseId'),
        (ys[5], '编排章节与小节', '增删改 + 排序，删除章节级联删小节'),
        (ys[6], '确认页核对 + 完整性校验', '封面/简介/至少 1 章 1 节'),
        (ys[8], '置 status = 1 并记录时间', '前台列表与详情立即可检索'),
    ]
    for y, t1, t2 in boxes:
        m.rect(cx - W / 2, y - H / 2, W, H, stroke=BLACK, sw=THIN,
               text=t1, size=9.5, bold=True, valign=1)

    # 菱形
    for y, txt in ((ys[3], '必填齐全？'), (ys[7], '信息完整？')):
        hw, hh = 0.72, 0.30
        m.line([(cx, y - hh), (cx + hw, y), (cx, y + hh), (cx - hw, y), (cx, y - hh)])
        m.label(cx, y, 1.5, 0.24, txt, size=9)

    # 主干箭头
    m.line([(cx, ys[0] + 0.08), (cx, ys[1] - H / 2)], arrow=True)
    m.line([(cx, ys[1] + H / 2), (cx, ys[2] - H / 2)], arrow=True)
    m.line([(cx, ys[2] + H / 2), (cx, ys[3] - 0.30)], arrow=True)
    m.line([(cx, ys[3] + 0.30), (cx, ys[4] - H / 2)], arrow=True)
    m.line([(cx, ys[4] + H / 2), (cx, ys[5] - H / 2)], arrow=True)
    m.line([(cx, ys[5] + H / 2), (cx, ys[6] - H / 2)], arrow=True)
    m.line([(cx, ys[6] + H / 2), (cx, ys[7] - 0.30)], arrow=True)
    m.line([(cx, ys[7] + 0.30), (cx, ys[8] - H / 2)], arrow=True)
    m.line([(cx, ys[8] + H / 2), (cx, ys[9] - 0.09)], arrow=True)

    # UML 结束节点：空心外圈（ellipse）+ 实心内圆（dot）
    m.ellipse(cx, ys[9], 0.105, 0.105, fill='#FFFFFF', stroke=BLACK)
    m.dot(cx, ys[9], 0.058)
    m.label(cx + 1.15, ys[9], 1.0, 0.22, '结束', size=9, halign=0)

    # 失败回环
    m.line([(cx + 0.72, ys[3]), (4.85, ys[3]), (4.85, ys[1]), (cx + W / 2, ys[1])],
           arrow=True)
    m.label(4.05, ys[3] - 0.22, 1.0, 0.20, '否 · 提示缺失', size=8, color='#B00000')

    m.line([(cx + 0.72, ys[7]), (4.85, ys[7]), (4.85, ys[5]), (cx + W / 2, ys[5])],
           arrow=True)
    m.label(4.05, ys[7] - 0.22, 1.1, 0.20, '否 · 缺失项清单', size=8, color='#B00000')

    m.label(cx + 0.42, ys[4] - 0.30, 0.5, 0.18, '是', size=8, color='#006600')
    m.label(cx + 0.42, ys[8] - 0.30, 0.5, 0.18, '是', size=8, color='#006600')

    return m.crop()


# ==========================================================================
# 5. 部署图
# ==========================================================================
def _node(m, x, y, w, h, title, sub=None, tsize=10, ssize=8.5):
    """带边框的节点：主名 + 副信息各占一行（不用 \n，避免 Visio 里不换行）"""
    m.rect(x, y, w, h, stroke=BLACK, sw=THIN)
    if sub:
        m.label(x + w / 2, y + h * 0.32, w - 0.10, 0.24, title, size=tsize, bold=True)
        m.label(x + w / 2, y + h * 0.66, w - 0.10, 0.22, sub, size=ssize, color=GRAY)
    else:
        m.label(x + w / 2, y + h / 2, w - 0.10, 0.26, title, size=tsize)


def d_deploy():
    m = Model(9.6, 4.9, '部署图')

    _node(m, 0.25, 1.50, 2.05, 1.05, '客户端浏览器', 'Chrome / Edge', tsize=9.5)
    m.label(1.27, 1.30, 1.6, 0.20, '«device»', size=8, color=GRAY)

    # 服务器边界
    m.rect(2.70, 0.70, 6.05, 2.45, stroke=BLACK, sw=THIN, dash=True)
    m.label(4.30, 0.92, 3.30, 0.22, '«device» 云服务器 122.51.6.69', size=8.5)
    m.label(4.30, 1.12, 3.30, 0.20, 'Ubuntu 24.04 · 4C4G · 40G', size=8, color=GRAY)

    _node(m, 2.95, 1.58, 1.65, 1.10, 'Nginx', ':80 静态 + 反代')
    _node(m, 4.85, 1.58, 1.85, 1.10, 'Spring Boot', ':8080 systemd')
    _node(m, 6.95, 1.58, 1.55, 1.10, 'MySQL 8.0', ':3306 库 guli')

    # 外部服务
    _node(m, 6.60, 3.70, 2.50, 0.95, '阿里云 OSS', '图片 avatar/cover/banner')
    m.label(7.85, 3.52, 2.0, 0.20, '«external service»', size=8, color=GRAY)

    # 连线
    m.line([(2.30, 2.05), (2.95, 2.05)], arrow=True)
    m.label(2.62, 1.80, 0.62, 0.18, 'HTTPS', size=7.5)
    m.line([(4.60, 2.05), (4.85, 2.05)], arrow=True)
    m.label(4.72, 1.80, 0.52, 0.18, 'proxy', size=7.5)
    m.line([(6.70, 2.05), (6.95, 2.05)], arrow=True)
    m.label(6.82, 1.80, 0.52, 0.18, 'JDBC', size=7.5)

    m.line([(5.75, 2.68), (5.75, 3.42), (6.60, 3.42), (6.60, 3.85)], arrow=True)
    m.label(6.25, 3.60, 1.5, 0.20, 'HTTPS 图片上传 / 回源', size=8)

    # 部署说明（拆成三行短句）
    m.label(1.27, 2.85, 2.00, 0.20, 'build 产物 → Nginx', size=7.5, color=GRAY)
    m.label(1.27, 3.05, 2.00, 0.20, 'jar → systemd :8080', size=7.5, color=GRAY)
    m.label(1.27, 3.25, 2.00, 0.20, '3306 仅本机监听', size=7.5, color=GRAY)

    return m.crop()


# ==========================================================================
DIAGRAMS = [
    ('用例图', d_usecase),
    ('体系结构图', d_architecture),
    ('E-R图', d_er),
    ('活动图-课程发布', d_activity),
    ('部署图', d_deploy),
]

if __name__ == '__main__':
    for name, fn in DIAGRAMS:
        model = fn()
        svg = os.path.join(HERE, f'preview_{name}.svg')
        model_to_svg(model, svg)
        out = os.path.join(OUT, f'{name}.vsdx')
        build_vsdx(model, TEMPLATE, out)
        print(f'{name:16s} {model.width:5.2f} x {model.height:5.2f} in  -> {out}')
