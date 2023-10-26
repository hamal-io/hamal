cmake_minimum_required(VERSION 3.24)
project(mpdecimal_100 C)

set(CMAKE_C_STANDARD 23)

set(SRCS
        src/basearith.c
        src/constants.c
        src/context.c
        src/convolute.c
        src/crt.c
        src/difradix2.c
        src/fnt.c
        src/fourstep.c
        src/io.c
        src/lmpdecimal.c
        src/mpalloc.c
        src/mpdecimal.c
        src/mpsignal.c
        src/numbertheory.c
        src/sixstep.c
        src/transpose.c
)

add_compile_definitions(CONFIG_64)
add_compile_definitions(ANSI)

add_library(mpdecimal_100 SHARED ${SRCS})
target_include_directories(mpdecimal_100 PUBLIC include)
target_link_libraries(mpdecimal_100 PUBLIC lua)