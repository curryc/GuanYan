project(blazeposencnn)

cmake_minimum_required(VERSION 3.10)

#set(OpenCV_DIR ${CMAKE_SOURCE_DIR}/ncnn/opencv-mobile-4.6.0-android/sdk/native/jni)
set(OpenCV_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../../OpenCV/sdk/native/jni")
find_package(OpenCV REQUIRED core imgproc)

set(ncnn_DIR ${CMAKE_SOURCE_DIR}/ncnn/ncnn-20230223-android-vulkan/${ANDROID_ABI}/lib/cmake/ncnn)
find_package(ncnn REQUIRED)

file(GLOB filter_files filter/*.cpp)
include_directories(
        ${CMAKE_CURRENT_SOURCE_DIR}/filter
        ${CMAKE_CURRENT_SOURCE_DIR}
        )
add_library(blazeposencnn
        SHARED
        blazeposencnn.cpp
        blazepose.cpp
        landmark.cpp
        ndkcamera.cpp
        ${filter_files})

target_link_libraries(blazeposencnn
        ncnn
        ${OpenCV_LIBS}
        camera2ndk
        mediandk)