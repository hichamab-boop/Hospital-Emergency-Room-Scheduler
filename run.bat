@echo off
echo Compiling the triage system...
"C:\Program Files\Microsoft\jdk-21.0.11.10-hotspot\bin\javac.exe" TriageSystem.java

if %errorlevel% neq 0 (
    echo.
    echo Error during compilation.
    pause
    exit /b %errorlevel%
)

echo Compilation successful! Launching the application...
echo.
"C:\Program Files\Microsoft\jdk-21.0.11.10-hotspot\bin\java.exe" TriageSystem

pause
