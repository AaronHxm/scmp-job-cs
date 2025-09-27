@echo off
setlocal enabledelayedexpansion

REM 编译和打包成可执行JAR文件
call mvn clean package assembly:single

REM 检查打包是否成功
if %errorlevel% neq 0 (
    echo 可执行JAR文件打包失败，请检查错误信息。
    exit /b 1
)

echo 可执行JAR文件打包成功！

REM 查找生成的JAR文件
for /f "delims=" %%i in ('dir /b /s target\*.jar ^| findstr /v /i "sources javadoc"') do (
    set JAR_FILE=%%i
)

echo 生成的JAR文件: %JAR_FILE%

REM 复制JavaFX模块到target\modules
if not exist "target\modules" mkdir target\modules
xcopy /Y /E "%JAVA_HOME%\jmods\javafx.*.jmod" target\modules\

REM 使用jpackage创建EXE文件
echo 正在使用jpackage打包为EXE...
set JPACKAGE_CMD=jpackage ^
    --name scmp-job-cs ^
    --input target ^
    --main-jar "%JAR_FILE%" ^
    --main-class com.scmp.JavaFxApp ^
    --type app-image ^
    --module-path "%JAVA_HOME%\jmods;target\modules" ^
    --add-modules javafx.controls,javafx.fxml ^
    --win-console ^
    --win-shortcut

echo 执行命令: %JPACKAGE_CMD%
%JPACKAGE_CMD%

if %errorlevel% equ 0 (
    echo EXE打包成功！输出目录: scmp-job-cs
) else (
    echo EXE打包失败，请检查错误信息
    exit /b 1
)