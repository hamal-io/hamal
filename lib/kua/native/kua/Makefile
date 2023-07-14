JAVA_HOME?=/usr/lib/jvm/openjdk
LUA_LIB_DIR?=../lua
LUA_VERSION?=5.3
JNLUA_SUFFIX?=53
LUA_INC_DIR?=../lua
LUA_LIB_NAME?=lua
ARCH?=amd64
CFLAGS?=-O2 -DLUA_USE_POSIX
LDFLAGS?=

# Tools
CC?=gcc
LD=$(CC)

# Default arguments
CFLAGS+=-c -fno-strict-aliasing -fPIC -Wall -DNDEBUG -D_REENTRANT -DJNLUA_SUFFIX=$(JNLUA_SUFFIX)
CFLAGS+=-I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux
LDFLAGS+=-shared

all: clean build/libkua.so
libkua: build/libkua.so

build/libkua.so: build/kua.o
	$(LD) $(LDFLAGS) -Wl,-soname=build/libkua.so -o build/libkua.so -L$(LUA_LIB_DIR) build/kua.o -l$(LUA_LIB_NAME)

build/kua.o: kua.c
	mkdir -p $(dir $@)
	$(CC) $(CFLAGS) -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -I$(LUA_INC_DIR) -o build/kua.o kua.c

clean:
	-rm build/*.o
	-rm build/*.so
