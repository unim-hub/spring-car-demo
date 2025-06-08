#!/bin/bash

echo "NVM_DIR=$NVM_DIR"
echo "JAVA_HOME=$JAVA_HOME"
echo "PATH=$PATH"

echo "Building vehicle-service ..."
pushd vehicle-service
./mvnw clean install || exit 1
popd

echo "Build vehicle-service complete."