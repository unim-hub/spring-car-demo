cmake_minimum_required(VERSION 3.14)

project(qt-car-backend VERSION 0.1 LANGUAGES CXX)

set(CMAKE_AUTOUIC ON)
set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTORCC ON)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY "${CMAKE_SOURCE_DIR}/build")

find_package(QT NAMES Qt5 REQUIRED COMPONENTS Widgets)
find_package(Qt${QT_VERSION_MAJOR} REQUIRED COMPONENTS Widgets)

find_package(PkgConfig REQUIRED)
pkg_check_modules(RDKAFKA REQUIRED rdkafka++)

set(PROJECT_SOURCES
        inc/Log.h
        inc/MainWindow.h
        src/MainWindow.cpp
        src/main.cpp
)

add_executable(qt-car-backend ${PROJECT_SOURCES})

include_directories(${CMAKE_SOURCE_DIR}/inc)

include_directories(${RDKAFKA_INCLUDE_DIRS})
link_directories(${RDKAFKA_LIBRARY_DIRS})

target_link_libraries(qt-car-backend PRIVATE Qt${QT_VERSION_MAJOR}::Widgets)
target_link_libraries(qt-car-backend PRIVATE ${RDKAFKA_LIBRARIES})

