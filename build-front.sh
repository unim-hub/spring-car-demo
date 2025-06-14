#!/bin/bash

echo "NVM_DIR=$NVM_DIR"
echo "JAVA_HOME=$JAVA_HOME"
echo "PATH=$PATH"

echo "Building ui-front..."
pushd ui-front
npm install
npm run build || exit 1
popd

# Optionally, copy frontend build into Spring Boot static resources
echo "Copying ui-front build to ui-service..."
rm -rf ui-service/src/main/resources/static/js/car_demo.js
rm -rf ui-service/src/main/resources/static/css/car_demo.css
mkdir -p ui-service/src/main/resources/static/js
cp -r ui-front/build/static/js/*.js ui-service/src/main/resources/static/js/car_demo.js
mkdir -p ui-service/src/main/resources/static/css
cp -r ui-front/build/static/css/*.css ui-service/src/main/resources/static/css/car_demo.css

echo "Build ui-front complete."