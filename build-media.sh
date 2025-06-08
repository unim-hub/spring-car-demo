#!/bin/bash

echo "NVM_DIR=$NVM_DIR"
echo "JAVA_HOME=$JAVA_HOME"
echo "PATH=$PATH"

echo "Building media-service ..."
pushd media-service
./mvnw clean install || exit 1
popd

echo "Build media-service complete."