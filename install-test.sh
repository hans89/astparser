#!/bin/bash
cd "tests/android.googlesource.com"
while read line; do 
	#git pull
    git clone "$line"
done < "../../remote-projects.txt"

