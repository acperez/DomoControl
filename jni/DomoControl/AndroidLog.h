#ifndef ANDROID_LOG_INCLUDE
#define ANDROID_LOG_INCLUDE

#define LOG_TAG	"DomoControl_JNI"
#include <android/log.h>

#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#endif
