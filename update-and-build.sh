#!/bin/sh
#git pull -v
./gradlew clean build
unzip build/distributions/whatsappdirect-0.1.zip -d build/distributions/