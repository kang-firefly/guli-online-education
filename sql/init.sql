-- ============================================================
-- 谷粒学苑 guli 在线教育平台 · 初始化脚本
-- 数据库：guli   字符集：utf8mb4   引擎：InnoDB
-- 执行：mysql -uroot -p guli < init.sql
-- 说明：本脚本可重复执行（先 DROP 再 CREATE），仅用于开发/演示环境
-- ============================================================

CREATE DATABASE IF NOT EXISTS `guli` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `guli`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- 1. 讲师表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `edu_teacher`;
CREATE TABLE `edu_teacher` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '讲师ID',
  `name`         VARCHAR(20)  NOT NULL COMMENT '讲师姓名',
  `intro`        VARCHAR(500) DEFAULT NULL COMMENT '讲师简介',
  `career`       VARCHAR(500) DEFAULT NULL COMMENT '讲师资历/职业经历',
  `level`        TINYINT      NOT NULL DEFAULT 1 COMMENT '1=讲师 2=高级讲师',
  `avatar`       VARCHAR(500) DEFAULT NULL COMMENT '头像URL（OSS）',
  `sort`         INT          NOT NULL DEFAULT 0 COMMENT '排序，越小越前',
  `gmt_create`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted`   TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除 0=正常 1=删除',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='讲师';

-- ------------------------------------------------------------
-- 2. 课程分类表（两级树，parent_id=0 为一级）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `edu_subject`;
CREATE TABLE `edu_subject` (
  `id`           BIGINT      NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `title`        VARCHAR(20) NOT NULL COMMENT '分类名称',
  `parent_id`    BIGINT      NOT NULL DEFAULT 0 COMMENT '父ID，0=一级分类',
  `sort`         INT         NOT NULL DEFAULT 0 COMMENT '排序',
  `gmt_create`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程分类';

-- ------------------------------------------------------------
-- 3. 课程表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `edu_course`;
CREATE TABLE `edu_course` (
  `id`                BIGINT        NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `teacher_id`        BIGINT        NOT NULL COMMENT '讲师ID',
  `subject_id`        BIGINT        NOT NULL COMMENT '二级分类ID',
  `subject_parent_id` BIGINT        NOT NULL COMMENT '一级分类ID（冗余，便于筛选）',
  `title`             VARCHAR(50)   NOT NULL COMMENT '课程标题',
  `price`             DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '价格',
  `lesson_num`        INT           NOT NULL DEFAULT 0 COMMENT '课时数',
  `cover`             VARCHAR(500)  DEFAULT NULL COMMENT '封面URL（OSS）',
  `status`            TINYINT       NOT NULL DEFAULT 0 COMMENT '0=未发布 1=已发布',
  `buy_count`         BIGINT        NOT NULL DEFAULT 0 COMMENT '购买数（演示用）',
  `view_count`        BIGINT        NOT NULL DEFAULT 0 COMMENT '浏览数',
  `gmt_create`        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted`        TINYINT       NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_teacher` (`teacher_id`),
  KEY `idx_subject` (`subject_id`),
  KEY `idx_parent` (`subject_parent_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程';

-- ------------------------------------------------------------
-- 4. 课程简介表（与课程 1:1）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `edu_course_description`;
CREATE TABLE `edu_course_description` (
  `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '简介ID',
  `course_id`   BIGINT   NOT NULL COMMENT '课程ID',
  `description` TEXT     COMMENT '课程详细介绍',
  `gmt_create`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程简介';

-- ------------------------------------------------------------
-- 5. 章节表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `edu_chapter`;
CREATE TABLE `edu_chapter` (
  `id`           BIGINT      NOT NULL AUTO_INCREMENT COMMENT '章节ID',
  `course_id`    BIGINT      NOT NULL COMMENT '课程ID',
  `title`        VARCHAR(50) NOT NULL COMMENT '章节标题',
  `sort`         INT         NOT NULL DEFAULT 0 COMMENT '排序',
  `gmt_create`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_course` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程章节';

-- ------------------------------------------------------------
-- 6. 小节表（不含视频，只记标题与时长）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `edu_section`;
CREATE TABLE `edu_section` (
  `id`           BIGINT      NOT NULL AUTO_INCREMENT COMMENT '小节ID',
  `course_id`    BIGINT      NOT NULL COMMENT '课程ID',
  `chapter_id`   BIGINT      NOT NULL COMMENT '章节ID',
  `title`        VARCHAR(50) NOT NULL COMMENT '小节标题',
  `duration`     VARCHAR(20) DEFAULT NULL COMMENT '时长，如 12:30',
  `sort`         INT         NOT NULL DEFAULT 0 COMMENT '排序',
  `gmt_create`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_chapter` (`chapter_id`),
  KEY `idx_course` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程小节';

-- ------------------------------------------------------------
-- 7. 首页轮播表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `crm_banner`;
CREATE TABLE `crm_banner` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '轮播ID',
  `title`        VARCHAR(50)  NOT NULL COMMENT '标题',
  `image_url`    VARCHAR(500) NOT NULL COMMENT '图片URL（OSS）',
  `link_url`     VARCHAR(500) DEFAULT NULL COMMENT '跳转地址',
  `sort`         INT          NOT NULL DEFAULT 0 COMMENT '排序',
  `is_deleted`   TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `gmt_create`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='首页轮播';

-- ------------------------------------------------------------
-- 8. 后台账号表（仅管理员）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username`     VARCHAR(50)  NOT NULL COMMENT '登录名',
  `password`     VARCHAR(100) NOT NULL COMMENT 'BCrypt 密码',
  `nickname`     VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
  `avatar`       VARCHAR(500) DEFAULT NULL COMMENT '头像',
  `gmt_create`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted`   TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台账号';

-- ------------------------------------------------------------
-- 9. 前台学员表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `ucenter_member`;
CREATE TABLE `ucenter_member` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '学员ID',
  `nickname`     VARCHAR(50)  NOT NULL COMMENT '昵称',
  `mobile`       VARCHAR(20)  NOT NULL COMMENT '手机号（唯一）',
  `password`     VARCHAR(100) NOT NULL COMMENT 'BCrypt 密码',
  `avatar`       VARCHAR(500) DEFAULT NULL COMMENT '头像',
  `gmt_create`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted`   TINYINT      NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_mobile` (`mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='前台学员';

-- ------------------------------------------------------------
-- 10. 登录日志表（报告加分项：可写进"详细设计"）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log` (
  `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username`   VARCHAR(50)  NOT NULL COMMENT '登录名',
  `user_type`  VARCHAR(10)  NOT NULL DEFAULT 'admin' COMMENT 'admin/member',
  `ip`         VARCHAR(50)  DEFAULT NULL COMMENT '登录IP',
  `user_agent` VARCHAR(500) DEFAULT NULL COMMENT 'UA',
  `success`    TINYINT      NOT NULL DEFAULT 1 COMMENT '1=成功 0=失败',
  `login_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_username` (`username`),
  KEY `idx_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志';

-- ============================================================
-- 演示数据
-- ============================================================

-- 讲师（4 个）
INSERT INTO `edu_teacher` (`id`,`name`,`intro`,`career`,`level`,`avatar`,`sort`) VALUES
(1,'张明远','前某头部互联网公司资深架构师，专注分布式与高并发系统设计，带过 200+ 人技术团队。','12 年 Java 研发经验，主导过日活千万级电商中台建设。',2,NULL,1),
(2,'李思成','数据库领域专家，擅长 MySQL 调优与分库分表方案落地。','10 年 DBA + 后端开发经验，处理过 TB 级数据迁移。',2,NULL,2),
(3,'王雨晴','前端工程师，Vue / React 双栈，专注前端工程化与性能优化。','8 年前端经验，参与多个中后台与 C 端项目从 0 到 1。',1,NULL,3),
(4,'陈立成','Java 后端工程师，对 JVM、并发编程有深入研究。','7 年一线开发经验，长期负责交易链路核心服务。',1,NULL,4);

-- 课程分类（3 个一级 + 6 个二级）
INSERT INTO `edu_subject` (`id`,`title`,`parent_id`,`sort`) VALUES
(1,'前端开发',0,1),
(2,'后端开发',0,2),
(3,'数据库',0,3),
(11,'Vue',1,1),
(12,'React',1,2),
(21,'Java',2,1),
(22,'Spring Boot',2,2),
(31,'MySQL',3,1);

-- 课程（5 门，其中 3 门已发布）
INSERT INTO `edu_course` (`id`,`teacher_id`,`subject_id`,`subject_parent_id`,`title`,`price`,`lesson_num`,`cover`,`status`,`buy_count`,`view_count`) VALUES
(1,3,11,1,'Vue 3 从入门到实战',199.00,32,NULL,1,1286,9821),
(2,1,21,2,'Spring Boot 企业级开发实战',299.00,48,NULL,1,864,7433),
(3,2,31,3,'MySQL 性能优化实战',129.00,24,NULL,1,432,5120),
(4,3,11,1,'前端工程化与 Vite 深度实践',0.00,16,NULL,0,0,0),
(5,4,21,2,'Java 并发编程精讲',159.00,28,NULL,0,0,0);

-- 课程简介
INSERT INTO `edu_course_description` (`course_id`,`description`) VALUES
(1,'本课程从 Vue 3 组合式 API 讲起，逐步完成一个完整的在线教育前台项目。涵盖响应式原理、组件通信、路由、状态管理、接口封装与打包部署，适合有 HTML/CSS/JS 基础的同学。'),
(2,'以真实的在线教育平台为背景，讲解 Spring Boot 的分层设计、统一返回与异常处理、MyBatis-Plus 持久层、JWT 认证、文件上传与接口文档，最终完成可上线的后端服务。'),
(3,'从执行计划、索引原理讲到慢查询定位与参数调优，配合大量真实案例，帮助你建立系统的 MySQL 性能优化方法论。'),
(4,'讲解 Vite 的构建原理、插件机制与工程化配置，包含 ESLint/Prettier 规范、环境变量、按需加载与产物分析。'),
(5,'深入 JMM、synchronized、AQS、线程池与并发容器，结合源码分析常见并发问题的成因与解决思路。');

-- 课程 1 的大纲
INSERT INTO `edu_chapter` (`id`,`course_id`,`title`,`sort`) VALUES
(1,1,'第一章 起步',1),
(2,1,'第二章 核心概念',2);
INSERT INTO `edu_section` (`course_id`,`chapter_id`,`title`,`duration`,`sort`) VALUES
(1,1,'课程介绍与环境准备','08:20',1),
(1,1,'创建第一个 Vue 3 项目','12:45',2),
(1,1,'模板语法与数据绑定','15:10',3),
(1,2,'组合式 API 与 setup','18:30',1),
(1,2,'组件通信的四种方式','21:05',2),
(1,2,'路由与状态管理','19:40',3);

-- 课程 2 的大纲
INSERT INTO `edu_chapter` (`id`,`course_id`,`title`,`sort`) VALUES
(3,2,'第一章 工程搭建',1),
(4,2,'第二章 基础设施',2);
INSERT INTO `edu_section` (`course_id`,`chapter_id`,`title`,`duration`,`sort`) VALUES
(2,3,'项目结构与分层设计','14:00',1),
(2,3,'统一返回与全局异常','16:25',2),
(2,4,'MyBatis-Plus 快速上手','20:15',1),
(2,4,'JWT 登录认证','18:50',2);

-- 首页轮播（图片稍后上传 OSS 后自动可用）
INSERT INTO `crm_banner` (`id`,`title`,`image_url`,`link_url`,`sort`) VALUES
(1,'Vue 3 从入门到实战','https://gulia1.oss-cn-beijing.aliyuncs.com/banner/banner1.png','/course/1',1),
(2,'Spring Boot 企业级开发','https://gulia1.oss-cn-beijing.aliyuncs.com/banner/banner2.png','/course/2',2),
(3,'MySQL 性能优化','https://gulia1.oss-cn-beijing.aliyuncs.com/banner/banner3.png','/course/3',3);

-- 后台管理员：admin / admin123
INSERT INTO `sys_user` (`id`,`username`,`password`,`nickname`) VALUES
(1,'admin','$2b$10$gp5xNL4i5uy8drOMfEh/gus5kiV7JG2dG2DHNqxb5QBzlObvBGj8u','超级管理员');

-- 前台演示学员：13800000000 / 123456
INSERT INTO `ucenter_member` (`id`,`nickname`,`mobile`,`password`) VALUES
(1,'张三','13800000000','$2b$10$ybJFSgUsT7mx2G8w3IrWAuHw37GnPZKyMwGL4y8IGqi1K2ogyNjky');

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- 课程订单（M7 新增）
-- 说明：course_title / cover 为下单时的快照字段，
--       课程改名或换封面后，历史订单仍显示下单时的信息。
--       不建 (member_id, course_id) 唯一索引，因为允许"取消后重新下单"，
--       判断是否已购靠 status=1 的查询。
-- ----------------------------
CREATE TABLE `edu_order` (
  `id`           BIGINT        NOT NULL AUTO_INCREMENT,
  `order_no`     VARCHAR(32)   NOT NULL COMMENT '订单号：yyyyMMddHHmmss + 6位随机',
  `member_id`    BIGINT        NOT NULL COMMENT '下单会员',
  `course_id`    BIGINT        NOT NULL COMMENT '课程ID',
  `course_title` VARCHAR(50)   NOT NULL COMMENT '下单时课程标题快照',
  `cover`        VARCHAR(500)  DEFAULT NULL COMMENT '下单时封面快照',
  `amount`       DECIMAL(10,2) NOT NULL COMMENT '成交金额',
  `status`       TINYINT       NOT NULL DEFAULT 0 COMMENT '0待支付 1已支付 2已取消',
  `pay_type`     VARCHAR(20)   DEFAULT NULL COMMENT 'MOCK=模拟支付',
  `pay_time`     DATETIME      DEFAULT NULL COMMENT '支付时间',
  `gmt_create`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_member_course` (`member_id`, `course_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程订单';
