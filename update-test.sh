#!/bin/bash
while read line; do 
	cd "$line"
	git pull
done < "local-projects.txt"

