@echo off
chcp 65001 >nul
echo Stopping services on ports 8080 / 5173 / 5175 ...

for %%P in (8080 5173 5175) do (
  for /f "tokens=5" %%I in ('netstat -ano ^| findstr ":%%P" ^| findstr LISTENING') do (
    echo   kill PID %%I on port %%P
    taskkill /F /T /PID %%I >nul 2>&1
  )
)

echo.
echo Done.
pause
