#!/bin/bash

set -e

SCRIPT=$(readlink -f "$0")
BASEDIR=$(dirname "$SCRIPT")

/bin/bash $BASEDIR/_infra/set-git-hash.sh

./gradlew clean build

cd $BASEDIR/platform/admin/src/main/resources
npm install
npm run build

cd $BASEDIR

cd app/fn-guru/src/main/frontend
npm install
npm run deploy

cd $BASEDIR
pwd

./gradlew dockerBuildImage

docker push hamalio/hamal-backend
docker push hamalio/hamal-mono
docker push hamalio/hamal-runner
docker push hamalio/fn-guru
docker push hamalio/web3proxy
