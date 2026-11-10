/**
 * 把 report-assets/*.html 渲染成 PNG（deviceScaleFactor=2，插入 Word 后放大不糊）
 * 用法: node report-assets/render.mjs
 */
import puppeteer from 'puppeteer-core'
import { readdirSync } from 'node:fs'
import { fileURLToPath } from 'node:url'
import path from 'node:path'

const dir = path.dirname(fileURLToPath(import.meta.url))
const CHROME = 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe'

// 08-tables.html 是给 Word 复制用的表格页，不参与出图
const files = readdirSync(dir).filter((f) => f.endsWith('.html') && !f.startsWith('08')).sort()

const browser = await puppeteer.launch({
  executablePath: CHROME,
  headless: 'new',
  args: ['--no-sandbox', '--allow-file-access-from-files', '--font-render-hinting=none']
})

for (const f of files) {
  const page = await browser.newPage()
  await page.setViewport({ width: 1560, height: 1200, deviceScaleFactor: 2 })
  await page.goto('file:///' + path.join(dir, f).replace(/\\/g, '/'), { waitUntil: 'networkidle0' })
  // 等字体真正加载完，否则截图会回退到系统字体
  await page.evaluate(() => document.fonts.ready)
  await new Promise((r) => setTimeout(r, 500))

  const el = await page.$('.canvas')
  const box = await el.boundingBox()
  const name = f.replace(/\.html$/, '.png')
  await el.screenshot({ path: path.join(dir, 'out', name) })
  console.log(`${name}  ${Math.round(box.width)}×${Math.round(box.height)} css px`)
  await page.close()
}

await browser.close()
