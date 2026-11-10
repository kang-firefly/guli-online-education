import puppeteer from 'puppeteer-core'
import { fileURLToPath } from 'node:url'
import path from 'node:path'
const outDir = path.dirname(fileURLToPath(import.meta.url)) + '/../shots'
const sleep = (ms) => new Promise(r => setTimeout(r, ms))
const b = await puppeteer.launch({ executablePath: 'C://Program Files\\Google\\Chrome\\Application\\chrome.exe', headless: 'new', args: ['--no-sandbox'] })
const p = await b.newPage()
await p.setViewport({ width: 1440, height: 900 })
p.on('pageerror', e => console.log('[pageerror]', e.message))

// 1) 先清掉登录态
await p.goto('http://127.0.0.1:5175/', { waitUntil: 'networkidle2' })
await p.evaluate(() => localStorage.clear())
await p.reload({ waitUntil: 'networkidle2' })
await sleep(800)
console.log('before login header =', (await p.$eval('.actions', e => e.innerText)).replace(/\n/g, ' | '))

// 2) 登录（SPA 内跳转，不刷新）
await p.goto('http://127.0.0.1:5175/login', { waitUntil: 'networkidle2' })
await p.type('input[autocomplete="username"]', '13800000000', { delay: 15 })
await p.type('input[autocomplete="current-password"]', '123456', { delay: 15 })
await p.click('button[type="submit"]')
await sleep(2000)
console.log('url after login =', p.url())
console.log('after login header =', (await p.$eval('.actions', e => e.innerText)).replace(/\n/g, ' | '))
await p.screenshot({ path: `${outDir}/p9-logged-in.png` })

// 3) 进 /me 并退出，验证顶栏恢复
await p.goto('http://127.0.0.1:5175/me', { waitUntil: 'networkidle2' })
await sleep(1200)
const btns = await p.$$eval('button', els => els.map(e => e.textContent.trim()))
console.log('member page buttons =', btns.join(' / '))
await p.evaluate(() => {
  const b2 = [...document.querySelectorAll('button')].find(x => x.textContent.includes('退出'))
  b2?.click()
})
await sleep(1500)
console.log('after logout url =', p.url())
console.log('after logout header =', (await p.$eval('.actions', e => e.innerText)).replace(/\n/g, ' | '))
await b.close()
