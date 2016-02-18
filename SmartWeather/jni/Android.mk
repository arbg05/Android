LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := convertTemp
LOCAL_SRC_FILES := convertTemp.c

include $(BUILD_SHARED_LIBRARY)