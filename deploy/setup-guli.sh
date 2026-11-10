#!/usr/bin/env bash
# 谷粒学苑 · 服务器配置脚本（systemd + nginx）
# 由部署流程生成，可重复执行
set -e

echo "==> 1/5 停掉手动启动的进程"
sudo pkill -f 'app.jar' 2>/dev/null || true
sleep 2

echo "==> 2/5 写入 systemd 单元"
sudo tee /etc/systemd/system/guli.service > /dev/null <<'EOF'
[Unit]
Description=Guli Online Education Platform (Spring Boot)
Documentation=http://122.51.6.69/
After=network.target

[Service]
Type=simple
User=ubuntu
Group=ubuntu
WorkingDirectory=/opt/guli
ExecStart=/usr/bin/java -Xms256m -Xmx512m -Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai -jar /opt/guli/app.jar --spring.profiles.active=prod
SuccessExitStatus=143
Restart=on-failure
RestartSec=5
StartLimitBurst=3
StartLimitIntervalSec=60
StandardOutput=append:/opt/guli/logs/stdout.log
StandardError=append:/opt/guli/logs/stderr.log

[Install]
WantedBy=multi-user.target
EOF

echo "==> 3/5 写入 nginx 站点配置"
sudo tee /etc/nginx/sites-available/guli.conf > /dev/null <<'NGINX'
# 谷粒学苑：前台跑在 /，后台跑在 /admin-ui/，接口反代到 127.0.0.1:8080
server {
    listen 80;
    listen [::]:80;
    server_name _;

    charset utf-8;
    client_max_body_size 10m;

    access_log /var/log/nginx/guli.access.log;
    error_log  /var/log/nginx/guli.error.log;

    # ---------- 前台门户 ----------
    root /var/www/guli/portal;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    # ---------- 后台管理端（子路径 + history 路由） ----------
    location /admin-ui/ {
        alias /var/www/guli/admin/;
        try_files $uri $uri/ /admin-ui/index.html;
    }

    # ---------- 后端接口 ----------
    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_http_version 1.1;
        proxy_set_header Host              $host;
        proxy_set_header X-Real-IP         $remote_addr;
        proxy_set_header X-Forwarded-For   $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connect_timeout 10s;
        proxy_read_timeout    60s;
    }

    location /admin/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_http_version 1.1;
        proxy_set_header Host              $host;
        proxy_set_header X-Real-IP         $remote_addr;
        proxy_set_header X-Forwarded-For   $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connect_timeout 10s;
        proxy_read_timeout    60s;
    }

    # ---------- 接口文档（答辩演示用） ----------
    location ~ ^/(v3/api-docs|swagger-ui) {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
    }

    # ---------- 静态资源缓存 ----------
    location /assets/ {
        expires 7d;
        add_header Cache-Control "public, max-age=604800";
    }
    location /admin-ui/assets/ {
        alias /var/www/guli/admin/assets/;
        expires 7d;
        add_header Cache-Control "public, max-age=604800";
    }
}
NGINX

echo "==> 4/5 启用站点并校验 nginx"
sudo ln -sf /etc/nginx/sites-available/guli.conf /etc/nginx/sites-enabled/guli.conf
sudo rm -f /etc/nginx/sites-enabled/default
sudo nginx -t
sudo systemctl reload nginx

echo "==> 5/5 启动后端服务"
sudo systemctl daemon-reload
sudo systemctl enable guli > /dev/null 2>&1
sudo systemctl restart guli
sleep 15
sudo systemctl is-active guli
echo "--- 服务状态 ---"
systemctl status guli --no-pager -l | head -12
