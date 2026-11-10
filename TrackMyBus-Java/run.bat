@echo off
rem Needs JDK 17 or newer.  Double-click, then open http://localhost:8080
if not exist out mkdir out
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt && java -cp out com.trackmybus.Main
