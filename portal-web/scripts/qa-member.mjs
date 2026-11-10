import puppeteer from 'puppeteer-core'
import { fileURLToPath } from 'node:url'
import path from 'node:path'
const outDir = path.dirname(fileURLToPath(import.meta.url)) + '/../shots'
const sleep = (ms) => new Promise(r => setTimeout(r, ms))
const b = await puppeteer.launch({ executablePath: 'C://Program Files\\Google\\Chrome\\Application\\chrome.exe', headless: 'new', args: ['--no-sandbox'] })
const p = await b.newPage()
await p.setViewport({ width: 1440, height: 900 })
p.on('pageerror', e => console.log('[pageerror]', e.message))
await p.goto('http://127.0.0.1:5175/login', { waitUntil: 'networkidle2' })
await p.type('input[autocomplete="username"]', '13800000000', { delay: 20 })
await p.type('input[autocomplete="current-password"]', '123456', { delay: 20 })
await p.click('button[type="submit"]')
await sleep(2200)
console.log('after login url =', p.url())
console.log('token stored =', await p.evaluate(() => !!localStorage.getItem('guli_member_token')))
await p.goto('http://127.0.0.1:5175/me', { waitUntil: 'networkidle2' })
await sleep(1500)
await p.screenshot({ path: `${outDir}/p8-member.png` })
console.log('shot p8-member')
await b.close()
