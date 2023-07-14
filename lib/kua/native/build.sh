#!/bin/bash

ROOT=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &>/dev/null && pwd)

make clean

cd $ROOT/lua

MY_GCC="gcc"
MY_STRIP="strip"
MY_LIB_SUFFIX="so"

MY_CFLAGS="-fPIC -O2 -m64"
MY_LDFLAGS="-m64 -static-libgcc"
LUA_TYPE="posix"

make CC=gcc CFLAGS="$MY_CFLAGS $MY_LUA_CFLAGS" LDFLAGS="$MY_LDFLAGS" $LUA_TYPE

cd $ROOT/kua

make libkua
