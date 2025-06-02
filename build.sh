#!/bin/bash

echo "NVM_DIR=$NVM_DIR"
echo "JAVA_HOME=$JAVA_HOME"
echo "PATH=$PATH"

echo "Building ui-service ..."
pushd ui-service
./mvnw clean install || exit 1
popd

echo "Building vehicle-service ..."
pushd vehicle-service
./mvnw clean install || exit 1

popd

echo "Building media-service ..."
pushd media-service
./mvnw clean install || exit 1
popd

echo "Building ui-front..."
pushd ui-front
npm install
npm run build || exit 1
popd

# Optionally, copy frontend build into Spring Boot static resources
echo "Copying ui-front build to ui-service..."
rm -rf ui-service/src/main/resources/static/js/car_demo.js
cp -r ui-front/build/static/js/*.js ui-service/src/main/resources/static/js/car_demo.js

echo "Build complete."