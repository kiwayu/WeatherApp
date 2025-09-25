@echo off
echo Hyper-Detailed Weather Card JavaFX Application
echo =============================================
echo.

REM Check if JavaFX SDK path is set
if not defined JAVAFX_HOME (
    echo ERROR: JAVAFX_HOME environment variable is not set.
    echo.
    echo Please download JavaFX SDK from https://openjfx.io/
    echo Extract it to a folder and set JAVAFX_HOME to that path.
    echo.
    echo Example:
    echo   set JAVAFX_HOME=C:\javafx-sdk-23
    echo   %0
    echo.
    pause
    exit /b 1
)

REM Check if JavaFX lib directory exists
if not exist "%JAVAFX_HOME%\lib" (
    echo ERROR: JavaFX lib directory not found at %JAVAFX_HOME%\lib
    echo Please check your JAVAFX_HOME path.
    echo.
    pause
    exit /b 1
)

echo Compiling JavaFX application...
javac --module-path "%JAVAFX_HOME%\lib" --add-modules javafx.controls,javafx.fxml src/*.java

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Compilation failed.
    echo.
    pause
    exit /b 1
)

echo Compilation successful!
echo.
echo Launching Hyper-Detailed Weather Card...
echo.

java --module-path "%JAVAFX_HOME%\lib" --add-modules javafx.controls,javafx.fxml -cp src HyperDetailedWeatherApp

echo.
echo Application closed.
pause
