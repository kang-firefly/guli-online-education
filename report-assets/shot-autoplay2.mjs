import puppeteer from 'puppeteer-core'
const B = 'http://122.51.6.69'
const sleep = (ms) => new Promise(r => setTimeout(r, ms))
const b = await puppeteer.launch({ executablePath: 'C://Program Files\\Google\\Chrome\\Application\\chrome.exe',
  headless: 'new', args: ['--no-sandbox', '--disable-dev-shm-usage'] })
const p = await b.newPage()
await p.setViewport({ width: 1440, height: 900 })

// 模拟真实路径：详情页点"开始学习"→ 播放页
await p.goto(B + '/course/4', { waitUntil: 'networkidle2', timeout: 45000 })
await sleep(2000)
const btn = await p.$eval('.hero__buy .btn', el => el.innerText.trim()).catch(() => 'none')
console.log('详情页按钮:', btn)
await p.click('.hero__buy .btn')
await sleep(6000)
await p.screenshot({ path: 'out/autoplay-final.png' })
console.log('跳转后 URL:', p.url())
console.log('静音提示存在:', await p.$('.meta__mute') ? '是' : '否')
await b.close()
