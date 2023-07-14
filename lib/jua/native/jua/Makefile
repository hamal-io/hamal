JAVA_HOME?=/usr/lib/jua_vm/openjdk
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

all: clean build/libjua.so
libjua: build/libjua.so

build/libjua.so: build/jua.o
	$(LD) $(LDFLAGS) -Wl,-soname=build/libjua.so -o build/libjua.so -L$(LUA_LIB_DIR) build/jua.o -l$(LUA_LIB_NAME)

build/jua.o: jua.c
	mkdir -p $(dir $@)
	$(CC) $(CFLAGS) -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -I$(LUA_INC_DIR) -o build/jua.o jua.c

build/jua_vm.o: jua_vm.c jua_vm.h
	mkdir -p $(dir $@)
	$(CC) $(CFLAGS) -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -I$(LUA_INC_DIR) -o build/jua_vm.o jua_vm.c

clean:
	-rm build/*.o
	-rm build/*.so
