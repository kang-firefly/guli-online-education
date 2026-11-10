/**
 * 校验 mermaid-for-wps.md 里的代码块能否被 mermaid 解析并渲染
 * 输出截图到 out/mermaid-*.png，方便目视检查版面
 */
import puppeteer from 'puppeteer-core'
import { readFileSync, writeFileSync } from 'node:fs'
import path from 'node:path'

const dir = process.cwd()
const md = readFileSync(path.join(dir, 'mermaid-for-wps.md'), 'utf8')

const blocks = [...md.matchAll(/```mermaid\r?\n([\s\S]*?)```/g)].map((m) => m[1].trim())
console.log('找到代码块:', blocks.length)

const html = `<!doctype html><meta charset="utf-8">
<style>body{margin:0;background:#fff;font-family:"Microsoft YaHei"} .box{padding:12px}</style>
<div class="box" id="host"></div>
<script src="file:///${path.join(dir, 'node_modules/mermaid/dist/mermaid.min.js').replace(/\\/g, '/')}"></script>
<script>
window.mermaid.initialize({ startOnLoad: false, theme: 'default', flowchart: { htmlLabels: true, curve: 'basis' } });
window.renderOne = async (code, id) => {
  try {
    await window.mermaid.parse(code);
  } catch (e) {
    return { ok: false, err: String(e.message || e) };
  }
  try {
    const { svg } = await window.mermaid.render(id, code);
    document.getElementById('host').innerHTML = svg;
    return { ok: true };
  } catch (e) {
    return { ok: false, err: 'render: ' + String(e.message || e) };
  }
};
</script>`

writeFileSync(path.join(dir, 'mermaid-validate.html'), html)

const b = await puppeteer.launch({
  executablePath: 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe',
  headless: 'new',
  args: ['--no-sandbox', '--allow-file-access-from-files']
})
const p = await b.newPage()
await p.setViewport({ width: 1400, height: 1000, deviceScaleFactor: 2 })
await p.goto('file:///' + path.join(dir, 'mermaid-validate.html').replace(/\\/g, '/'), { waitUntil: 'networkidle0' })

const names = ['usecase', 'architecture', 'er', 'activity', 'deploy']
for (let i = 0; i < blocks.length; i++) {
  const r = await p.evaluate((code, id) => window.renderOne(code, id), blocks[i], 'm' + i)
  if (!r.ok) {
    console.log(`[FAIL] #${i + 1} ${names[i] || ''}: ${r.err}`)
    continue
  }
  await new Promise((res) => setTimeout(res, 300))
  const el = await p.$('#host svg')
  const box = await el.boundingBox()
  const file = path.join(dir, 'out', `mermaid-${names[i] || i + 1}.png`)
  await el.screenshot({ path: file })
  console.log(`[ OK ] #${i + 1} ${names[i] || ''}  ${Math.round(box.width)}x${Math.round(box.height)}`)
}
await b.close()
