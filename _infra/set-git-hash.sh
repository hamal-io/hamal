#!/usr/bin/env bash
set -e
commit_hash=$(git rev-parse --verify HEAD | tr -d '\n\t\r ')

SCRIPT_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &>/dev/null && pwd)
BASE_DIR="$SCRIPT_DIR/.."

declare -a StringArray=("fn-guru" "hamal-backend" "hamal-mono" "hamal-runner" "web3proxy")
for PROJECT in "${StringArray[@]}"; do
  echo -n "$commit_hash" > "$BASE_DIR/app/$PROJECT/src/main/resources/git_commit.txt"
done

