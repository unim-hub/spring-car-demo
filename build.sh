#!/bin/bash

echo "NVM_DIR=$NVM_DIR"
echo "JAVA_HOME=$JAVA_HOME"
echo "PATH=$PATH"



popd
./build-vehicle.sh

./build-media.sh

./build-ui.sh

echo "Build complete."