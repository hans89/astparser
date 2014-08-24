#!/bin/bash
cd "tests/"
while read line; do 
	cd "$line"
	git pull
	cd "../../"
done < "local-projects.txt"

