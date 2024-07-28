cmake_minimum_required(VERSION 3.0)

project(bridge C)

message(WARNING "CMAKE_SOURCE_DIR: ${CMAKE_SOURCE_DIR}")

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/../jniLibs/${CMAKE_ANDROID_ARCH_ABI})
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/../../../../corespace/ffi)

link_directories(${CMAKE_CURRENT_SOURCE_DIR}/../jniLibs/${CMAKE_ANDROID_ARCH_ABI})

add_library(bridge SHARED main.c trace.c)

target_link_libraries(bridge android log core)