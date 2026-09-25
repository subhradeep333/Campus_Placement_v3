#!/bin/bash

# ==========================================================
# Pure OOP Java Backend - Campus Placement Portal Build & Run
# ==========================================================

set -e

echo "🎓 Compiling Pure OOP Java Backend..."
mkdir -p bin

javac -d bin -cp "lib/*" $(find src/main/java -name "*.java")

echo "✅ Compilation successful!"
echo "📡 Starting OOP Java Server (JDBC -> MySQL)..."
echo "-------------------------------------------------"

java -cp "bin:lib/*" com.example.placement.Main
