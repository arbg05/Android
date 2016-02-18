//
// Created by Arun on 2/17/2016.
//

#include <jni.h>
#include <math.h>

JNIEXPORT jfloatArray JNICALL Java_utd_smartweather_WeatherActivity_convertTemp(JNIEnv* env, jobject obj, jfloatArray oldArray, jint size, jboolean isShowingFahrenheit){
    jfloat *body = (*env)->GetFloatArrayElements(env, oldArray, 0);
    jfloatArray jArray;
    jArray = (*env)->NewFloatArray(env, size);
    jfloat newVals[size];
    if(isShowingFahrenheit){
        int i=0;
        for(i=0;i<size;i++){
            newVals[i] = body[i] + 32;
        }
    } else{
        int i=0;
        for(i=0;i<size;i++){
            newVals[i] = body[i] - 32;
        }
    }
    (*env)->ReleaseFloatArrayElements(env,oldArray,body,0);
    (*env)->SetFloatArrayRegion(env, jArray, 0, size, newVals);
    return jArray;
}
