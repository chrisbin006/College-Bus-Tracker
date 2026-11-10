#!/bin/sh
# Needs JDK 17 or newer.  Usage: ./run.sh   then open http://localhost:8080
mkdir -p out && javac -d out $(find src -name '*.java') && java -cp out com.trackmybus.Main
