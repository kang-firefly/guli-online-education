# 报告配图（report-assets）

## 先看这一条：图必须“过工具”

老师的要求是**用工具画**，不是“图好看就行”：

| 模型 | 老师要求 | 本目录怎么给 |
|---|---|---|
| 用例图 | **VISIO 手动绘制** | `vsdx/用例图.vsdx` |
| 体系结构图 | 绘制（建议工具生成） | `vsdx/体系结构图.vsdx` |
| 类图 | **IDEA 插件自动生成** | ⚠️ 必须你在 IDEA 里点，见下文 |
| E-R 图 | 绘制 | `vsdx/E-R图.vsdx` |
| 活动图 | 绘制 | `vsdx/活动图-课程发布.vsdx` |
| 部署图 | 绘制 | `vsdx/部署图.vsdx` |

## 一、Visio 图（5 个 .vsdx）

`vsdx/` 下是 **Visio 原生文件**（真形状、可拖拽编辑，不是把图片塞进画布）。

用法：

1. 用装了 Visio 的电脑（或 **Visio 网页版** office.com）打开 `.vsdx`；
2. 微调：拖动形状、改文字、调字号，都是普通 Visio 形状；
3. **另存为一次**（同名或新名都行）—— 之后文件就是“Visio 本体签出”的版本；
4. 导出 PNG 插报告：`文件 → 导出 → 更改文件类型 → PNG`，或直接复制画布粘到 Word。

> 为什么要“另存一次”：这些文件是在没有 Visio 的机器上按 Visio 的 OOXML 规范生成的，
> 结构和形状都标准，但另存一版能消除兼容性疑虑，也让文件属性里留下 Visio 的编辑记录。

本机没有 Visio（`Program Files` 下无 `VISIO.EXE`，`.vsdx` 只挂了个 Visio Viewer 的桩），
所以没有本地 Visio 截图可对照；版面是用**同一份几何数据**另出 SVG 预览确认的（`out/preview_*.png`）。

## 二、类图（必须 IDEA 生成）

IDEA 是 **Ultimate 2024.1.4**（`D:\develop\IntelliJ IDEA 2024.1.4`），自带 Diagrams：

1. 打开项目（选 `pom.xml` 按 Maven 导入），等右下角索引跑完；
2. 左侧 Project 面板定位到 `src/main/java/com/guli`；
3. **多选** `controller`、`service`、`mapper`、`entity` 四个包（Ctrl+点击）；
4. 右键 → **Diagrams → Show Diagram…**（快捷键 `Ctrl+Alt+Shift+U`）；
5. 图内右键 → **Show Categories**，勾 `Fields`、`Methods`、`Constructors`、`Dependencies`；
6. 图内右键 → **Layout** 选 `Organic` 或 `Hierarchical`（类多时 Hierarchical 更整齐）；
7. 右上角工具栏 **Export Diagram** → 导出 PNG（≥2x）或 SVG。

> 82 个类全画会糊成一片，建议分两张：
> ① `entity` + `mapper`（数据层）；② `controller` + `service`（业务层）。
> 报告正文放 ② 更像“构件级设计”，① 可放附录。

## 三、旧的 PNG（`out/01~07`）

之前用 HTML/SVG 手绘的版本，**版面可以抄，但别当交付物**：

- 用途：对照排版、给 Visio 里的位置做参照；
- `out/preview_*.png` 是 5 个 vsdx 的同源预览（同一份几何数据），可用来核对 vsdx 内容。

## 四、表设计（`08-tables.html`）

浏览器打开 → `Ctrl+A` → `Ctrl+C` → 粘到 Word，10 张表直接成型。
内容是 python 解析 `sql/init.sql` 生成的，字段/类型/可空/默认值/索引与真实建表语句一致。

## 五、关于 WPS

本机 WPS 是标准三件套（WPS/ET/WPP），**没有装“流程图”组件**，`.vsdx` 也没有关联到 WPS。
WPS 流程图是独立的会员组件（在线版），不是 Microsoft Visio；它能导出 vsdx，但**打开别人的 vsdx 兼容性没保证**（连线、组合形状容易散）。
所以优先 Visio 桌面版或 Visio 网页版；实在没有，再用 WPS 流程图对着 `out/preview_*.png` 重画一遍。

## 六、重新生成

```bash
# Visio 图（改 gen_guli_vsdx.py 里的坐标/文字后重跑）
python gen_guli_vsdx.py
node shot-svg.mjs          # 出同源预览到 out/preview_*.png

# 旧的 HTML 图（仅作参照）
node render.mjs
```

坐标系是英寸、y 轴向下，画布 A4 横向（11.69×8.27），`crop()` 自动裁到内容边界。
