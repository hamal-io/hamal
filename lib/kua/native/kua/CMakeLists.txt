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
    src/kua_call.c
    src/kua_check.c
    src/kua_info.c
    src/kua_jni.c
    src/kua_jni_error.c
    src/kua_memory.c
    src/kua_memory_arena.c
    src/kua_stack.c
    src/kua_state.c
    src/kua_table.c
)


add_library(kua SHARED ${KUA_LIB_SRCS})
target_include_directories(kua PUBLIC include)
target_link_libraries(kua PUBLIC lua)

add_executable(playground src/playground.c )
target_include_directories(playground PUBLIC include)
target_link_libraries(playground PUBLIC m kua )