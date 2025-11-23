#!/bin/bash

if [ -z "$1" ]; then
    echo "Error: No argument provided."
    exit 1
fi

mvn exec:java -Dexec.args="$1"