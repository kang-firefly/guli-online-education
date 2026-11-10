# -*- coding: utf-8 -*-
"""把 报告第五部分.md 转成排版好的 docx（三线表 + 自动插图）"""
import re, io, os
from docx import Document
from docx.shared import Pt, Cm, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml.ns import qn
from docx.oxml import OxmlElement

SRC = '报告第五部分.md'
OUT = os.environ.get('OUT_NAME', '报告第五部分.docx')
START_AT = os.environ.get('START_AT', '')   # 如 '## 5.2'，只生成该章之后的内容
CONTENT_W = 14.5           # A4 21cm - 左右边距 6.34cm

FIGS = {
    '图 1': ('report-assets/out/preview_用例图.png', 14.5, '图 1　系统用例图'),
    '图 2': ('report-assets/out/preview_体系结构图.png', 14.5, '图 2　平台体系结构图'),
    '图 3': ('report-assets/out/preview_E-R图.png', 14.5, '图 3　数据库 E-R 图'),
    '图 4': ('report-assets/out/图4-类图.png', 14.5, '图 4　核心类图（IDEA 自动生成）'),
    '图 5': ('report-assets/out/preview_活动图-课程发布.png', 8.5, '图 5　课程发布活动图'),
    '图 6': ('report-assets/out/preview_部署图.png', 14.5, '图 6　部署图'),
    '图 7': ('report-assets/out/combo-ui.png', 14.0, '图 7　系统主要界面'),
}

# 各列宽（cm），按表格列数索引
COL_RATIOS = {
    2: [0.32, 0.68],
    3: [0.18, 0.32, 0.50],
    4: [0.08, 0.33, 0.13, 0.46],
    5: [0.13, 0.23, 0.14, 0.39, 0.11],
    6: [0.07, 0.24, 0.18, 0.13, 0.27, 0.11],
}

doc = Document()
sec = doc.sections[0]
sec.top_margin = Cm(2.54); sec.bottom_margin = Cm(2.54)
sec.left_margin = Cm(3.17); sec.right_margin = Cm(3.17)

normal = doc.styles['Normal']
normal.font.name = 'Times New Roman'
normal.font.size = Pt(12)
normal.element.rPr.rFonts.set(qn('w:eastAsia'), '宋体')
normal.paragraph_format.line_spacing = 1.5
normal.paragraph_format.space_before = Pt(0)
normal.paragraph_format.space_after = Pt(0)


def set_font(run, cn='宋体', size=12, bold=False, color=None):
    run.font.name = 'Times New Roman'
    run.font.size = Pt(size)
    run.bold = bold
    run._element.rPr.rFonts.set(qn('w:eastAsia'), cn)
    if color:
        run.font.color.rgb = color



def add_rich(paragraph, text, size=12, bold=False, cn='宋体', color=None):
    """把 **加粗** 与 `等宽代码` 解析成多个 run"""
    for part in re.split(r'(\*\*.+?\*\*|`[^`]+?`)', text):
        if not part:
            continue
        if part.startswith('**') and part.endswith('**') and len(part) > 4:
            set_font(paragraph.add_run(part[2:-2]), cn, size, True, color)
        elif part.startswith('`') and part.endswith('`') and len(part) > 2:
            r = paragraph.add_run(part[1:-1])
            set_font(r, 'Consolas', size - 0.5, bold, color)
            r.font.name = 'Consolas'
        else:
            set_font(paragraph.add_run(part), cn, size, bold, color)

def para(text='', size=12, bold=False, cn='宋体', align=None, indent=True,
         before=0, after=0, color=None, line=1.5, left_indent=None, first_indent=None):
    p = doc.add_paragraph()
    pf = p.paragraph_format
    pf.line_spacing = line
    pf.space_before = Pt(before)
    pf.space_after = Pt(after)
    if left_indent is not None:
        pf.left_indent = Pt(left_indent)
    if first_indent is not None:
        pf.first_line_indent = Pt(first_indent)
    elif indent:
        pf.first_line_indent = Pt(size * 2)
    if align is not None:
        p.alignment = align
    add_rich(p, text, size, bold, cn, color)
    return p


def heading(text, level):
    sizes = {1: 15, 2: 14, 3: 12}
    p = doc.add_paragraph()
    pf = p.paragraph_format
    pf.space_before = Pt(14 if level == 1 else 10)
    pf.space_after = Pt(6)
    pf.line_spacing = 1.5
    pf.keep_with_next = True
    set_font(p.add_run(text), '黑体', sizes[level], True)
    return p


def _cell_border(cell, edge, sz):
    tcPr = cell._tc.get_or_add_tcPr()
    borders = tcPr.find(qn('w:tcBorders'))
    if borders is None:
        borders = OxmlElement('w:tcBorders'); tcPr.append(borders)
    tag = qn(f'w:{edge}')
    e = borders.find(tag)
    if e is None:
        e = OxmlElement(f'w:{edge}'); borders.append(e)
    if sz == 0:
        e.set(qn('w:val'), 'nil')
    else:
        e.set(qn('w:val'), 'single')
        e.set(qn('w:sz'), str(sz))
        e.set(qn('w:color'), '000000')


def table(rows):
    ncol = len(rows[0])
    t = doc.add_table(rows=len(rows), cols=ncol)
    t.alignment = WD_TABLE_ALIGNMENT.CENTER
    t.autofit = False
    # 固定布局，否则 Word 会重新分配列宽
    tblPr = t._tbl.tblPr
    layout = OxmlElement('w:tblLayout'); layout.set(qn('w:type'), 'fixed'); tblPr.append(layout)

    # 表 1 分工表用模板的三列结构，分工列要宽
    if ncol == 3 and rows[0][:3] == ['序号', '姓名', '分工']:
        ratios = [0.10, 0.14, 0.76]
    else:
        ratios = COL_RATIOS.get(ncol) or [1 / ncol] * ncol
    widths = [Cm(CONTENT_W * r) for r in ratios]

    for i, row in enumerate(rows):
        for j in range(ncol):
            cell = t.cell(i, j)
            cell.width = widths[j]
            cell.text = ''
            p = cell.paragraphs[0]
            p.paragraph_format.line_spacing = 1.15
            p.paragraph_format.space_before = Pt(2)
            p.paragraph_format.space_after = Pt(2)
            p.paragraph_format.first_line_indent = Pt(0)
            p.alignment = WD_ALIGN_PARAGRAPH.CENTER if (i == 0 or j == 0) else WD_ALIGN_PARAGRAPH.LEFT
            add_rich(p, row[j] if j < len(row) else '', 10.5, i == 0)
            _cell_border(cell, 'top', 12 if i == 0 else 0)
            _cell_border(cell, 'bottom', 6 if (i == 0 or i == len(rows) - 1) else 0)
            _cell_border(cell, 'left', 0)
            _cell_border(cell, 'right', 0)

    trPr = t.rows[0]._tr.get_or_add_trPr()
    th = OxmlElement('w:tblHeader'); th.set(qn('w:val'), 'true'); trPr.append(th)
    doc.add_paragraph().paragraph_format.space_after = Pt(0)
    return t


lines = io.open(SRC, encoding='utf-8').read().split('\n')
if START_AT:
    for k, l in enumerate(lines):
        if l.startswith(START_AT):
            lines = lines[k:]
            break
i = 0
while i < len(lines):
    line = lines[i].rstrip()
    if not line.strip():
        i += 1; continue

    m = re.match(r'^(#{1,3})\s+(.*)$', line)
    if m:
        heading(m.group(2), len(m.group(1))); i += 1; continue

    if line.startswith('|'):
        block = []
        while i < len(lines) and lines[i].strip().startswith('|'):
            block.append(lines[i].strip()); i += 1
        rows = []
        for b in block:
            if re.match(r'^\|[\s:|-]+\|$', b):
                continue
            rows.append([c.strip() for c in b.strip('|').split('|')])
        if rows:
            table(rows)
        continue

    if line.startswith('**（此处插入'):
        for key, (path, width, caption) in FIGS.items():
            if key in line:
                if os.path.exists(path):
                    p = doc.add_paragraph()
                    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
                    p.paragraph_format.space_before = Pt(8)
                    p.paragraph_format.space_after = Pt(4)
                    p.add_run().add_picture(path, width=Cm(width))
                    cap = doc.add_paragraph()
                    cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
                    cap.paragraph_format.space_after = Pt(12)
                    add_rich(cap, caption, 10.5, False, '宋体', RGBColor(0x33, 0x33, 0x33))
                break
        i += 1; continue

    if line.startswith('**表 ') and line.endswith('**'):
        para(line, size=10.5, align=WD_ALIGN_PARAGRAPH.CENTER, indent=False,
             before=10, after=4, line=1.0)
        i += 1; continue

    if line.startswith('> '):
        para(line[2:], size=10.5, cn='楷体', indent=False, before=6, after=6,
             color=RGBColor(0x44, 0x44, 0x44), line=1.35)
        i += 1; continue

    if re.match(r'^\d+\.\s', line) or line.startswith('- '):
        txt = re.sub(r'^(\d+\.|-)\s+', '', line)
        para(txt, before=3, after=3, left_indent=24, first_indent=-12)
        i += 1; continue

    para(line, before=4, after=4)
    i += 1

# 目标文件可能被编辑器占用：先写临时文件再覆盖
import tempfile, shutil
tmp_out = os.path.join(tempfile.gettempdir(), 'guli_part5.docx')
doc.save(tmp_out)
try:
    shutil.copyfile(tmp_out, OUT)
    os.remove(tmp_out)
except PermissionError:
    OUT = '报告第五部分-新.docx'
    shutil.copyfile(tmp_out, OUT)
    os.remove(tmp_out)
    print('※ 原文件被占用，已另存:', OUT)
print('生成:', OUT, f'{os.path.getsize(OUT)/1024:.0f}KB',
      f'段落 {len(doc.paragraphs)} 表格 {len(doc.tables)}')
