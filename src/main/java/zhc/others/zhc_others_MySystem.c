#include "jni.h"
//#include "jni_util.h"
//#include "jvm.h"
//#include "java_props.h"

#include "zhc_others_MySystem.h"
#include <stdio.h>

JNIEXPORT void JNICALL Java_zhc_others_MySystem_setOut
  (JNIEnv *env, jclass cla, jobject stream) 
{
    jfieldID fid =
        (*env)->GetStaticFieldID(env,cla,"out2","Ljava/io/PrintStream;");
    if (fid == 0)
        return;
    (*env)->SetStaticObjectField(env,cla,fid,stream);
}

//编译语句
//tcc.exe -I"C:\Program Files\Java\jdk1.8.0_161\include" -I"C:\Program Files\java\jdk1.8.0_161\include\win32" -shared -o zhc_others_MySystem.dll zhc_others_MySystem.c