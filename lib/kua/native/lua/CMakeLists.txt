cmake_minimum_required(VERSION 3.25)
project(lua C)

set(CMAKE_C_STANDARD 23)
find_library(MATH_LIBRARY m)

set(LUA_LIB_SRCS
        "src/lapi.c"
        "src/lcode.c"
        "src/lctype.c"
        "src/ldebug.c"
        "src/ldo.c"
        "src/ldump.c"
        "src/lfunc.c"
        "src/lgc.c"
        "src/llex.c"
        "src/lmem.c"
        "src/lobject.c"
        "src/lopcodes.c"
        "src/lparser.c"
        "src/lstate.c"
        "src/lstring.c"
        "src/ltable.c"
        "src/ltm.c"
        "src/lundump.c"
        "src/lvm.c"
        "src/lzio.c"
        "src/lauxlib.c"
        "src/lbaselib.c"
        "src/lcorolib.c"
        "src/ldblib.c"
        "src/liolib.c"
        "src/lmathlib.c"
        "src/loadlib.c"
        "src/loslib.c"
        "src/lstrlib.c"
        "src/ltablib.c"
        "src/lutf8lib.c"
        "src/linit.c"
        )

add_library(lua SHARED ${LUA_LIB_SRCS})
target_include_directories(lua PUBLIC include)

if(MATH_LIBRARY)
    target_link_libraries(lua PUBLIC ${MATH_LIBRARY})
endif()