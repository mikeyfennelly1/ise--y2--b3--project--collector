#!/bin/bash

################################################
# SETUP
################################################
OS=$(uname)
if [[ "$OS" == "Darwin" ]]; then
	# OSX uses BSD readlink
	BASEDIR="$(dirname "$0")"
else
	BASEDIR=$(readlink -e "$(dirname "$0")/")
fi
pushd "${BASEDIR}/.."

set -eou pipefail

./gradlew bootJar
cp ./build/libs/*SNAPSHOT.jar ./app.jar
IMAGE_NAME="mikeyfennelly/cotccollector:latest"
docker build -t "${IMAGE_NAME}" .

docker login
docker push "${IMAGE_NAME}"

popd