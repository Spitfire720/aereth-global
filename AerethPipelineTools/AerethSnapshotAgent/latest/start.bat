@echo off
title FragmentEngine Server (Java 21)

REM --- CHANGE THIS PATH ---
cd /d "C:\Users\Bernardo\Desktop\server"

echo Starting server with Eclipse Adoptium Java 21...
"C:\Program Files\Eclipse Adoptium\jdk-21.0.9.10-hotspot\bin\java.exe" ^
 -Xms4G ^
 -Xmx8G ^
 -jar paper.jar nogui

pause
