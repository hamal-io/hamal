cmake_minimum_required(VERSION 3.24)
project(native C)

set(CMAKE_C_STANDARD 23)

add_subdirectory(lua)
add_subdirectory(mpdecimal-100)
add_subdirectory(kua)
