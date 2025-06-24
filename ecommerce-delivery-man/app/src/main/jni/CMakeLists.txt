cmake_minimum_required(VERSION 3.31.5)

project("eccorce-keys")  # Unique project name

# Include Android NDK toolchain using FULL PATH
include(/home/abdullah/Android/Sdk/ndk/28.0.13004108/build/cmake/android.toolchain.cmake)

add_library(
        keys # Matches System.loadLibrary("keys")
        SHARED
        keys.cpp
)