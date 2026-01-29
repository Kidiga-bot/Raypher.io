# Raypher_Pro Build Helper Script
# This script sets the JAVA_HOME environment variable and runs the Gradle build

# Set JAVA_HOME for this session
$env:JAVA_HOME = 'C:\Program Files\Microsoft\jdk-21.0.9.10-hotspot'
$env:PATH = "$env:JAVA_HOME\bin;" + $env:PATH

Write-Host "✓ JAVA_HOME set to: $env:JAVA_HOME" -ForegroundColor Green
Write-Host ""

# Verify Java is accessible
try {
    $javaVersion = & java -version 2>&1 | Select-Object -First 1
    Write-Host "✓ Java Version: $javaVersion" -ForegroundColor Green
    Write-Host ""
} catch {
    Write-Host "✗ Java not accessible. Please verify JDK installation." -ForegroundColor Red
    exit 1
}

# Run the Gradle build
Write-Host "Starting Gradle build..." -ForegroundColor Cyan
Write-Host "=============================================" -ForegroundColor Cyan
Write-Host ""

./gradlew assembleDebug --stacktrace
