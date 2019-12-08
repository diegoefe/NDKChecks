#include <jni.h>
#include "mylog.h"

jstring logTag(JNIEnv* env, jclass clazz) {
    return env->NewStringUTF(LOG_TAG);
}

jstring info(JNIEnv* env, jclass clazz) {
    return env->NewStringUTF(__DATE__ " at " __TIME__);
}

jint JNI_OnLoad(JavaVM* pVm, void* reserved) {
    JNIEnv* env;
	if(pVm->GetEnv((void **)&env, JNI_VERSION_1_6) != JNI_OK) {
		DLG("JNI_OnLoad() error GetEnv");
		return -1;
	}
	const int numMethods = 2;
	JNINativeMethod nm[numMethods];

	nm[0].name = "logTag";
	nm[0].signature = "()Ljava/lang/String;";
	nm[0].fnPtr = (void*)logTag;

    nm[1].name = "info";
    nm[1].signature = "()Ljava/lang/String;";
    nm[1].fnPtr = (void*)info;

	jclass cls = env->FindClass("org/diegoefe/ndkchecks/Nativa");
	env->RegisterNatives(cls, nm, numMethods);
	return JNI_VERSION_1_6;
}