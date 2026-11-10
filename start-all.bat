@echo off
chcp 65001 >nul
cd /d "%~dp0"

echo ============================================
echo   Guli Academy - start all services
echo ============================================
echo.

echo [1/3] Backend  : Spring Boot on 8080
start "guli-backend" cmd /k "mvn -DskipTests spring-boot:run -Dspring-boot.run.arguments=--server.port=8080"

echo [2/3] Admin    : Vite on 5173
if not exist "admin-web\node_modules" (
  echo   node_modules missing, running npm install ...
  pushd admin-web
  call npm install --no-audit --no-fund
  popd
)
start "guli-admin-web" cmd /k "cd /d %~dp0admin-web && npm run dev"

echo [3/3] Portal   : Vite on 5175
if not exist "portal-web\node_modules" (
  echo   node_modules missing, running npm install ...
  pushd portal-web
  call npm install --no-audit --no-fund
  popd
)
start "guli-portal-web" cmd /k "cd /d %~dp0portal-web && npm run dev"

echo.
echo Waiting for services to boot (about 30s) ...
ping -n 31 127.0.0.1 >nul

echo.
echo --------------------------------------------
echo   Portal  : http://127.0.0.1:5175
echo   Admin   : http://127.0.0.1:5173   admin / admin123
echo   Swagger : http://localhost:8080/swagger-ui.html
echo --------------------------------------------
echo.
echo If a port is already in use, run stop-all.bat first.
echo.
start "" http://127.0.0.1:5175
pause
