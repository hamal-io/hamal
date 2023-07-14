cmake_minimum_required(VERSION 3.25)
project(kua C)

set(CMAKE_C_STANDARD 23)

find_package(JNI)

if (JNI_FOUND)
    message (STATUS "JNI_INCLUDE_DIRS=${JNI_INCLUDE_DIRS}")
    message (STATUS "JNI_LIBRARIES=${JNI_LIBRARIES}")
endif()

include_directories(${JNI_INCLUDE_DIRS})

set(KUA_LIB_SRCS
    src/kua.c
    src/kua_info.c
)


add_library(kua SHARED ${KUA_LIB_SRCS})
target_include_directories(kua PUBLIC include)
target_link_libraries(kua PUBLIC lua)