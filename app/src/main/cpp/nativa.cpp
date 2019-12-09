#include <jni.h>
#include <iostream>
#include <sstream>
#include <ctime>
#include <ratio>
#include <chrono>
#include "mylog.h"
#include "thread.h"

jstring logTag(JNIEnv* env, jclass clazz) {
    return env->NewStringUTF(LOG_TAG);
}

jstring info(JNIEnv* env, jclass clazz) {
    return env->NewStringUTF(__DATE__ " at " __TIME__);
}


const int HEALTHECK_MESSAGE = 0;

class HealthCheckThread : public JavaThread {
	jobject handlerObj;
public:
	HealthCheckThread(JNIEnv *env_, jobject handlerObj_)
			: JavaThread(), handlerObj(env_->NewGlobalRef(handlerObj_)) { }
    ~HealthCheckThread() {
	    DLG("~HealthCheckThread (%ld)", std::this_thread::get_id());
	}
	virtual void onDetach() {
        DLG("onDetach (%ld)", std::this_thread::get_id());
		jniEnv()->DeleteGlobalRef(handlerObj);
	}
	void sendHealthMessage() {
		// Get the Handler.obtainMessage and call it
		jclass handlerClass = jniEnv()->FindClass("android/os/Handler");
		jmethodID obtainMId = jniEnv()->GetMethodID(handlerClass,
													"obtainMessage",
													"(ILjava/lang/Object;)Landroid/os/Message;");
		// Get the current time
		std::ostringstream oss;
		using std::chrono::system_clock;
		system_clock::time_point today = system_clock::now();
		std::time_t tt;
		tt = system_clock::to_time_t(today);

		// Build up the alive message
		oss << "Thread[" << std::this_thread::get_id() << "] is alive at " << ctime(&tt) << std::endl;;

		// Build up a Handler Message
		jobject messagObj = jniEnv()->CallObjectMethod(handlerObj, obtainMId, 0, jniEnv()->NewStringUTF( oss.str().c_str()));
		// Send the Message to the main UI Thread
		jmethodID sendMsgMId = jniEnv()->GetMethodID(handlerClass, "sendMessage", "(Landroid/os/Message;)Z");

		bool sent = jniEnv()->CallBooleanMethod(handlerObj, sendMsgMId, messagObj);

		jniEnv()->DeleteLocalRef(messagObj);
		jniEnv()->DeleteLocalRef(handlerClass);
	}

	virtual void run() {

		while (!shouldStop) {
			std::unique_lock<std::mutex> lck(stopMutex);
			// Do Work
			// ...
			sendHealthMessage();
			// Wait until a stop signal is sent
			stopCond.wait_for(lck, std::chrono::seconds(1));
		}
	}

};

namespace {

//const int num_threads = 10;
const int num_threads = 3;
JavaThread *threads[num_threads];

} //?

void startNativeThreads(JNIEnv *jEnv, jobject activity, jobject handler) {

	DLG("Starting  %d Native Threads", num_threads);
	//Launch a group of threads
	for (int i = 0; i < num_threads; ++i) {
		threads[i] = new HealthCheckThread(jEnv, handler);
		threads[i]->start();
	}
}

void stopNativeThreads(JNIEnv *env, jobject activity) {
	DLG("Stopping %d Native Threads", num_threads);
	//Launch a group of threads
	for (int i = 0; i < num_threads; ++i) {
		threads[i]->stop();
		threads[i]->join();
		delete threads[i];
	}
}


jint JNI_OnLoad(JavaVM* pVm, void* reserved) {
    JavaThread::vm(pVm);
    JNIEnv* env;
	if(pVm->GetEnv((void **)&env, JNI_VERSION_1_6) != JNI_OK) {
		DLG("JNI_OnLoad() error GetEnv");
		return -1;
	}
	const int numMethods = 4;
	JNINativeMethod nm[numMethods];

	nm[0].name = "logTag";
	nm[0].signature = "()Ljava/lang/String;";
	nm[0].fnPtr = (void*)logTag;

    nm[1].name = "info";
    nm[1].signature = "()Ljava/lang/String;";
    nm[1].fnPtr = (void*)info;

    nm[2].name = "startNativeThreads";
    nm[2].signature = "(Landroid/os/Handler;)V";
    nm[2].fnPtr = (void*)startNativeThreads;

    nm[3].name = "stopNativeThreads";
    nm[3].signature = "()V";
    nm[3].fnPtr = (void*)stopNativeThreads;

	jclass cls = env->FindClass("org/diegoefe/ndkchecks/Nativa");
	env->RegisterNatives(cls, nm, numMethods);
	return JNI_VERSION_1_6;
}