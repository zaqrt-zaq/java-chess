#!/bin/bash

# Create output directory if it doesn't exist
mkdir -p out

# Compile all Java files
echo "Compiling Java files..."
javac -d out src/*.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo "Starting Chess game..."
    # Run the game
    cd out && java Chess
else
    echo "Compilation failed!"
    exit 1
fi
