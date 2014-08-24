#!/bin/bash
rm -rf "tests/android.googlesource.com"
mkdir "tests/android.googlesource.com"
cd "tests/android.googlesource.com"
while read line; do 
	#git pull
    git clone "$line"
done < "../remote-projects.txt"

