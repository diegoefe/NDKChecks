/*
 *  This code is part of "Asynchronous Android Programming".
 *
 *  Copyright (C) 2016 Helder Vasconcelos (heldervasc@bearstouch.com)
 *  Copyright (C) 2016 Packt Publishing
 *
 *  Permission is hereby granted, free of charge, to any person obtaining
 *  a copy of this software and associated documentation files (the
 *  "Software"), to deal in the Software without restriction, including
 *  without limitation the rights to use, copy, modify, merge, publish,
 *  distribute, sublicense, and/or sell copies of the Software, and to
 *  permit persons to whom the Software is furnished to do so, subject to
 *  the following conditions:
 *
 */
#include "thread.h"
#include "mylog.h"
#include <iostream>
#include <sstream>
#include <ctime>
#include <ratio>
#include <chrono>

#ifdef ENABLE_CPU_STAT_CLASS
// Java VM interface pointer
static jclass cpuStatClass;

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    // Cache the JavaVM interface pointer
    JNIEnv *env;
    vm->GetEnv((void **) &env, JNI_VERSION_1_4);
    JavaThread::vm() = vm;
    cpuStatClass = (jclass) env->NewGlobalRef(env->FindClass("com/packpublishing/asynchronousandroid/chapter9/CPUStat"));
    return JNI_VERSION_1_4;
}

jclass getCPUStatClass() {
    return cpuStatClass;
}
#endif

class JLocalRef {
    JNIEnv *env;
    jobject ref;
    JLocalRef(JNIEnv *env_, jobject &ref_)
        : env(env_),  ref(env->NewLocalRef(ref_)) { }
    ~JLocalRef() {
        env->DeleteLocalRef(ref);
    }
    inline jobject getRef() { return ref; };
};

JavaThread::JavaThread() {}

void JavaThread::stop() {
    std::unique_lock<std::mutex> lck(stopMutex);
    this->shouldStop = true;
    stopCond.notify_all();
}

void JavaThread::entryPoint() {
    // Attach current thread to Java virtual machine
    // and obrain JNIEnv interface pointer
    {
        std::unique_lock<std::mutex> lck(startMutex);
        DLG("Attaching the thread to JVM ");
        if (JavaThread::vm()->AttachCurrentThread(&threadEnv, NULL) != 0) {
            jclass clazz = threadEnv->FindClass("java/lang/RuntimeException");
            threadEnv->ThrowNew(clazz, "Failed to attach the thread to JVM");
        }
        isStarted = true;
        startCond.notify_all();
    }
    onAttach();
    try {
        // Run the subclass method
        run();
    } catch (...) {
        // Detach current thread when an exception happens
        onDetach();
        JavaThread::vm()->DetachCurrentThread();
        throw;
    }
    DLG("Detaching the thread from JVM ");
    // Detach current thread from Java virtual machine
    onDetach();
    JavaThread::vm()->DetachCurrentThread();
}

void JavaThread::start(){
    //execution = std::thread(&JavaThread::entryPoint, this);
    thread_ = std::thread(&JavaThread::entryPoint, this);
    std::unique_lock<std::mutex> lck(startMutex);
    while (!isStarted) startCond.wait(lck);
}

JavaVM* JavaThread::vm(JavaVM* _vm) { gVm_ = _vm; return gVm_; };
JavaVM* JavaThread::vm() { return gVm_; };
JavaVM* JavaThread::JavaThread::gVm_=NULL;
