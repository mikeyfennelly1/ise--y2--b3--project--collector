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
cd "${BASEDIR}"

source "${BASEDIR}"/.env
SERVICE_NAME="collector"

var_must_exist() {
  for var_name in "$@"; do
    if [ -z "${!var_name+x}" ]; then
      echo "environment variable ${var_name} is unset. Exiting..."
      exit 1
    fi
  done
}

################################################
# NATS is healthy
################################################
var_must_exist NATS_PORT NATS_HOST

nc  -z -v -w 2 "${NATS_HOST}" "${NATS_PORT}"
if [ $? -eq 0 ]; then
  echo "${SERVICE_NAME}: NATS is healthy on http://${NATS_HOST}:${NATS_PORT}"
else
  echo "${SERVICE_NAME}: ERROR: NATS is unhealthy. Exiting..."
  exit 1
fi

################################################
# Start API
################################################
echo "${SERVICE_NAME}: Starting collector API..."
"${BASEDIR}"/gradlew bootRun
