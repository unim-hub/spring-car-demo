#!/bin/bash

echo "NVM_DIR=$NVM_DIR"
echo "JAVA_HOME=$JAVA_HOME"
echo "PATH=$PATH"

echo "Building ui-service ..."
pushd ui-service
./mvnw clean install || exit 1
popd

echo "Building ui-front..."
./build-front.sh

echo "Build UI complete."