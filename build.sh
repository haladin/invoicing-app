#!/bin/bash

cd invoicing_app
npm install
rm -rf dist
ng build

cd dist/invoising_app/

rm -rf ../../../backend/src/main/resources/static/
mkdir -p ../../../backend/src/main/resources/static/
cp -a * ../../../backend/src/main/resources/static/

cd ../../../backend
./gradlew clean
./gradlew build
