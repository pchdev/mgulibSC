cmake_minimum_required (VERSION 2.8)
project (MySaw)

include_directories(${/Users/pchdev/Downloads/supercollider-Version-3.7.0-alpha1}/include/plugin_interface)
include_directories(${/Users/pchdev/Downloads/supercollider-Version-3.7.0-alpha1}/include/common)
include_directories(${/Users/pchdev/Downloads/supercollider-Version-3.7.0-alpha1}/external_libraries/libsndfile/)

set(CMAKE_SHARED_MODULE_PREFIX "")
if(APPLE OR WIN32)
set(CMAKE_SHARED_MODULE_SUFFIX ".scx")
endif()

add_library(MySaw MODULE MySaw.cpp)