@echo off
cd /d "%~dp0"
echo ========================================
echo      INICIANDO TRES EN RAYA (SWING)
echo ========================================
echo.
echo Compilando...
if not exist bin mkdir bin
javac -d bin src\*.java
if %errorlevel% neq 0 (
    echo [ERROR] No se pudo compilar el juego.
    pause
    exit /b %errorlevel%
)

if exist "C:\Program Files\Java\jdk-24\bin\jar.exe" (
    "C:\Program Files\Java\jdk-24\bin\jar.exe" --create --file TresEnRaya.jar --main-class TresEnRaya -C bin . META-INF play_intro.ps1 play_music.ps1 recursos >nul 2>&1
)

echo Abriendo menú principal del juego...
java -cp bin TresEnRaya
if %errorlevel% neq 0 (
    pause
)
