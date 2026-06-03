@echo off
echo Cleaning old build files...
if exist *.class del *.class
if exist dummy rmdir /s /q dummy
mkdir dummy

echo Compiling the emergency room package...
javac -d . Doctor.java DoctorQueue.java Patient.java PatientHistoryBST.java PatientLookup.java SeverityLevel.java

if %errorlevel% neq 0 (
    echo.
    echo Error during package compilation.
    pause
    exit /b %errorlevel%
)

echo Compiling the triage system...
javac -sourcepath dummy -cp . TriageSystem.java

if %errorlevel% neq 0 (
    echo.
    echo Error during triage system compilation.
    pause
    exit /b %errorlevel%
)

echo Compilation successful! Launching the application...
echo.
java -cp . TriageSystem

pause
