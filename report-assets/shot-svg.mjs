import puppeteer from 'puppeteer-core'
import { readdirSync } from 'node:fs'
import path from 'node:path'
const dir = process.cwd()
const files = readdirSync(dir).filter(f => f.startsWith('preview_') && f.endsWith('.svg'))
const b = await puppeteer.launch({ executablePath: 'C://Program Files\\Google\\Chrome\\Application\\chrome.exe', headless: 'new', args: ['--no-sandbox','--allow-file-access-from-files'] })
for (const f of files) {
  const p = await b.newPage()
  await p.goto('file:///' + path.join(dir, f).replace(/\\/g,'/'), { waitUntil: 'networkidle0' })
  const box = await p.evaluate(() => {
    const s = document.querySelector('svg')
    return { w: +s.getAttribute('width'), h: +s.getAttribute('height') }
  })
  await p.setViewport({ width: Math.ceil(box.w), height: Math.ceil(box.h), deviceScaleFactor: 2 })
  await p.screenshot({ path: path.join(dir, 'out', f.replace('preview_','preview_').replace('.svg','.png')) })
  console.log(f, box.w, 'x', box.h)
  await p.close()
}
await b.close()
