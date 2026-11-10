import puppeteer from 'puppeteer-core'
import { fileURLToPath } from 'node:url'
import path from 'node:path'
const outDir = path.dirname(fileURLToPath(import.meta.url)) + '/../shots'
const sleep = (ms) => new Promise(r => setTimeout(r, ms))
const b = await puppeteer.launch({ executablePath: 'C://Program Files\\Google\\Chrome\\Application\\chrome.exe', headless: 'new', args: ['--no-sandbox'] })
const p = await b.newPage()
await p.setViewport({ width: 1440, height: 900 })
p.on('pageerror', e => console.log('[pageerror]', e.message))

// 未登录：CTA 应是「免费注册」
await p.goto('http://127.0.0.1:5175/', { waitUntil: 'networkidle2' })
await p.evaluate(() => localStorage.clear())
await p.reload({ waitUntil: 'networkidle2' })
await sleep(1000)
console.log('[guest] cta =', await p.$eval('.cta', e => e.innerText.replace(/\n/g, ' | ')))

// 登录（SPA 内跳转，不刷新）
await p.goto('http://127.0.0.1:5175/login', { waitUntil: 'networkidle2' })
await p.type('input[autocomplete="username"]', '13800000000', { delay: 15 })
await p.type('input[autocomplete="current-password"]', '123456', { delay: 15 })
await p.click('button[type="submit"]')
await sleep(2000)
console.log('[member] cta =', await p.$eval('.cta', e => e.innerText.replace(/\n/g, ' | ')))
await p.evaluate(() => document.querySelector('.cta')?.scrollIntoView({ block: 'center' }))
await sleep(600)
await p.screenshot({ path: `${outDir}/p10-cta-logged-in.png` })

// 已登录访问 /login 应被弹回首页
await p.goto('http://127.0.0.1:5175/login', { waitUntil: 'networkidle2' })
await sleep(900)
console.log('member visit /login =>', p.url())

await b.close()
