cmake_minimum_required(VERSION 3.4.1)
add_library(nativa SHARED nativa.cpp)
find_library(log-lib log)
target_link_libraries(nativa ${log-lib})