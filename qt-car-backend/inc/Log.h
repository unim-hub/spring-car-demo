#ifndef LOG_H
#define LOG_H

#include <iostream>
#include <sstream>
#include <thread>
#include <chrono>
#include <iomanip>

inline std::string getTimeStamp()
{
    // Get the current time
    auto now = std::chrono::system_clock::now();
    // Convert to time_t (calendar time)
    std::time_t now_time_t = std::chrono::system_clock::to_time_t(now);
    // Convert to local time
    std::tm* local_time = std::localtime(&now_time_t);
    // Get the milliseconds
    auto milliseconds = std::chrono::duration_cast<std::chrono::milliseconds>(now.time_since_epoch()) % 1000;
    std::stringstream ss;
    ss << std::put_time(local_time, "%H:%M:%S") << "." << std::setfill('0') << std::setw(3) << milliseconds.count();
    return ss.str();
}

#define LOGI std::cerr << getTimeStamp() <<  "  " << std::hex << std::this_thread::get_id() << "  INFO: " << __FUNCTION__ << std::dec << ": "

#define LOGW std::cerr << getTimeStamp() <<  "  " << std::hex << std::this_thread::get_id() << "  WARN: " << __FUNCTION__ << std::dec << ": "

#define LOGE std::cerr << getTimeStamp() <<  "  " << std::hex << std::this_thread::get_id() << "  ERR : " << __FUNCTION__ << std::dec << ": "

#endif // LOG_H
