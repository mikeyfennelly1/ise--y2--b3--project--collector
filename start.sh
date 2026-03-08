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

source "${BASEDIR}"/../.env.local
source "${BASEDIR}"/../scripts/helpers.sh

SERVICE_NAME="collector"

################################################
# NATS is healthy
################################################
if [[ "$1" != "retry-nats" ]]; then

	var_must_exist NATS_PORT NATS_HOST

	nc  -z -v -w 2 "${NATS_HOST}" "${NATS_PORT}"
	if [ $? -eq 0 ]; then
	  echo "${SERVICE_NAME}: NATS is healthy on http://${NATS_HOST}:${NATS_PORT}"
	else
	  echo "${SERVICE_NAME}: ERROR: NATS is unhealthy. Exiting..."
	  exit 1
	fi
else
	echo "script configured to run in retry-nats mode..."
fi

################################################
# Start API
################################################
echo "${SERVICE_NAME}: Starting API..."
var_must_exist COLLECTOR_LISTEN_PORT
export COLLECTOR_LISTEN_PORT
"${BASEDIR}"/gradlew bootRun --args='--collector.id=1'
