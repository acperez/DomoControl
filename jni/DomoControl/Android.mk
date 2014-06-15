LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := DomoControl
### Add all source file names to be included in lib separated by a whitespace
LOCAL_SRC_FILES := DomoControl.cpp

LOCAL_CFLAGS :=

LOCAL_C_INCLUDES	:= $(TOP_PATH)/openssl/include

LOCAL_LDLIBS := -llog -L$(TOP_PATH)/openssl -lcrypto

include $(BUILD_SHARED_LIBRARY)
