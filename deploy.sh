#!/bin/bash

set -e

SCRIPT=$(readlink -f "$0")
BASEDIR=$(dirname "$SCRIPT")

./gradlew clean build

cd $BASEDIR/platform/backend/admin/src/main/resources
npm install
npm run build

cd $BASEDIR

cd app/fn-guru/src/main/resources
npm install
npm run build

cd $BASEDIR
pwd

./gradlew dockerBuildImage

docker push hamalio/hamal-backend
docker push hamalio/hamal-mono
docker push hamalio/hamal-runner
docker push hamalio/fn-guru
docker push hamalio/web3proxy