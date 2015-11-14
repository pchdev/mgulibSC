cmake_minimum_required (VERSION 2.8)
project (Noiser)

include_directories(/Users/pchdev/Downloads/supercollider-Version-3.6.6/include/plugin_interface)
include_directories(/Users/pchdev/Downloads/supercollider-Version-3.6.6/include/common)
include_directories(/Users/pchdev/Downloads/supercollider-Version-3.6.6/external_libraries/libsndfile/)

set(CMAKE_SHARED_MODULE_PREFIX "")
if(APPLE OR WIN32)
set(CMAKE_SHARED_MODULE_SUFFIX ".scx")
endif()

add_library(Noiser MODULE noiser.cpp)