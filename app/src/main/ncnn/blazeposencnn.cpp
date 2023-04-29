// Tencent is pleased to support the open source community by making ncnn available.
//
// Copyright (C) 2021 THL A29 Limited, a Tencent company. All rights reserved.
//
// Licensed under the BSD 3-Clause License (the "License"); you may not use this file except
// in compliance with the License. You may obtain a copy of the License at
//
// https://opensource.org/licenses/BSD-3-Clause
//
// Unless required by applicable law or agreed to in writing, software distributed
// under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
// CONDITIONS OF ANY KIND, either express or implied. See the License for the
// specific language governing permissions and limitations under the License.

#include <android/asset_manager_jni.h>
#include <android/native_window_jni.h>
#include <android/native_window.h>

#include <android/log.h>

#include <jni.h>

#include <string>
#include <vector>

#include <platform.h>
#include <benchmark.h>

#include "blazepose.h"

#include "ndkcamera.h"

#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>

#if __ARM_NEON
#include <arm_neon.h>
#endif // __ARM_NEON

static int draw_unsupported(cv::Mat& rgb)
{
    const char text[] = "unsupported";

    int baseLine = 0;
    cv::Size label_size = cv::getTextSize(text, cv::FONT_HERSHEY_SIMPLEX, 1.0, 1, &baseLine);

    int y = (rgb.rows - label_size.height) / 2;
    int x = (rgb.cols - label_size.width) / 2;

    cv::rectangle(rgb, cv::Rect(cv::Point(x, y), cv::Size(label_size.width, label_size.height + baseLine)),
                    cv::Scalar(255, 255, 255), -1);

    cv::putText(rgb, text, cv::Point(x, y + label_size.height),
                cv::FONT_HERSHEY_SIMPLEX, 1.0, cv::Scalar(0, 0, 0));

    return 0;
}

JavaVM* g_vm;
jobject g_callbackClass = NULL;

/**
 * 将keypoints输出到java回调中，cbw
 * @param objects
 * @return
 */
static int out_kpts(std::vector<Object> objects){
    // 获取JNI环境
    JNIEnv* env;
    jint attachResult = g_vm->AttachCurrentThread(&env, NULL);
    if (attachResult != JNI_OK) {
        std::puts("wtf");
        return -1;
    }

    // 调用Java回调方法
    jmethodID callbackMethod = env->GetMethodID(env->GetObjectClass(g_callbackClass), "onKptsCallback", "([[[F)V");

    // 构造参数
    jobjectArray jArray = env->NewObjectArray(objects.size(), env->FindClass("[[F"), NULL);
    for (int i = 0; i < objects.size(); i++) {
        Object obj = objects[i];
        int dim2 = obj.skeleton.size();
        jobjectArray jArray2d = env->NewObjectArray(dim2, env->FindClass("[F"), NULL);
        for(int j = 0; j< dim2; j++){
            jfloatArray jArray1d = env->NewFloatArray(3);
            jfloat buffer[] = {obj.skeleton[j].x, obj.skeleton[j].y, obj.skeleton[j].visibility ? 1.f: 0.f};
            env->SetFloatArrayRegion(jArray1d,0,3,buffer);

            env->SetObjectArrayElement(jArray2d, j, jArray1d);
            env->DeleteLocalRef(jArray1d);
        }

        env->SetObjectArrayElement(jArray, i, jArray2d);
        env->DeleteLocalRef(jArray2d);
    }

    // 调用回调方法并传递参数
    env->CallVoidMethod(g_callbackClass, callbackMethod, jArray);

    // 释放JNI环境
    g_vm->DetachCurrentThread();
    return 0;
}

static int draw_fps(cv::Mat& rgb)
{
    // resolve moving average
    float avg_fps = 0.f;
    {
        static double t0 = 0.f;
        static float fps_history[10] = {0.f};

        double t1 = ncnn::get_current_time();
        if (t0 == 0.f)
        {
            t0 = t1;
            return 0;
        }

        float fps = 1000.f / (t1 - t0);
        t0 = t1;

        for (int i = 9; i >= 1; i--)
        {
            fps_history[i] = fps_history[i - 1];
        }
        fps_history[0] = fps;

        if (fps_history[9] == 0.f)
        {
            return 0;
        }

        for (int i = 0; i < 10; i++)
        {
            avg_fps += fps_history[i];
        }
        avg_fps /= 10.f;
    }

    char text[32];
    sprintf(text, "FPS=%.2f", avg_fps);

    int baseLine = 0;
    cv::Size label_size = cv::getTextSize(text, cv::FONT_HERSHEY_SIMPLEX, 0.5, 1, &baseLine);

    int y = 0;
    int x = rgb.cols - label_size.width;

    cv::rectangle(rgb, cv::Rect(cv::Point(x, y), cv::Size(label_size.width, label_size.height + baseLine)),
                    cv::Scalar(255, 255, 255), -1);

    cv::putText(rgb, text, cv::Point(x, y + label_size.height),
                cv::FONT_HERSHEY_SIMPLEX, 0.5, cv::Scalar(0, 0, 0));

    return 0;
}

static BlazePose* g_blazepose = 0;
static ncnn::Mutex lock;

class MyNdkCamera : public NdkCameraWindow
{
public:
    virtual void on_image_render(cv::Mat& rgb) const;
};

void MyNdkCamera::on_image_render(cv::Mat& rgb) const
{
    std::vector<Object> faceobjects;
    {
        ncnn::MutexLockGuard g(lock);

        if (g_blazepose)
        {
            g_blazepose->detect(rgb, faceobjects);

            g_blazepose->draw(rgb, faceobjects);
        }
        else
        {
            draw_unsupported(rgb);
        }
    }

    out_kpts(faceobjects);
    draw_fps(rgb);
}

static MyNdkCamera* g_camera = 0;

extern "C" {

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "JNI_OnLoad");

    g_camera = new MyNdkCamera;
    g_vm  = vm;

    return JNI_VERSION_1_4;
}

JNIEXPORT void JNI_OnUnload(JavaVM* vm, void* reserved)
{
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "JNI_OnUnload");

    {
        ncnn::MutexLockGuard g(lock);

        delete g_blazepose;
        g_blazepose = 0;
    }

    delete g_camera;
    g_camera = 0;
}

// public native boolean loadModel(AssetManager mgr, int modelid, int cpugpu);
JNIEXPORT jboolean JNICALL Java_com_scu_guanyan_utils_ncnn_BlazePoseNcnn_loadModel(JNIEnv* env, jobject thiz, jobject assetManager, jint modelid, jint cpugpu)
{
    if (modelid < 0 || modelid > 6 || cpugpu < 0 || cpugpu > 1)
    {
        return JNI_FALSE;
    }

    AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);

    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "loadModel %p", mgr);
    const char* modeltypes[] =
    {
        "lite",
        "full",
        "heavy"
    };

    const char* modeltype = modeltypes[(int)modelid];
    bool use_gpu = (int)cpugpu == 1;

    // reload
    {
        ncnn::MutexLockGuard g(lock);

        if (use_gpu && ncnn::get_gpu_count() == 0)
        {
            // no gpu
            delete g_blazepose;
            g_blazepose = 0;
        }
        else
        {
            if (!g_blazepose)
                g_blazepose = new BlazePose;
            g_blazepose->load(mgr, modeltype, use_gpu);
        }
    }


    g_callbackClass = env->NewGlobalRef(thiz); // 保存引用， cbw

    return JNI_TRUE;
}

// public native boolean openCamera(int facing);
JNIEXPORT jboolean JNICALL Java_com_scu_guanyan_utils_ncnn_BlazePoseNcnn_openCamera(JNIEnv* env, jobject thiz, jint facing)
{
    if (facing < 0 || facing > 1)
        return JNI_FALSE;

    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "openCamera %d", facing);

    g_camera->open((int)facing);

    return JNI_TRUE;
}

// public native boolean closeCamera();
JNIEXPORT jboolean JNICALL Java_com_scu_guanyan_utils_ncnn_BlazePoseNcnn_closeCamera(JNIEnv* env, jobject thiz)
{
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "closeCamera");

    g_camera->close();

    return JNI_TRUE;
}

// public native boolean setOutputWindow(Surface surface);
JNIEXPORT jboolean JNICALL Java_com_scu_guanyan_utils_ncnn_BlazePoseNcnn_setOutputWindow(JNIEnv* env, jobject thiz, jobject surface)
{
    ANativeWindow* win = ANativeWindow_fromSurface(env, surface);

    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "setOutputWindow %p", win);

    g_camera->set_window(win);

    return JNI_TRUE;
}
//JNIEXPORT void JNICALL Java_com_example_MyCallback_registerKptsCallback(JNIEnv* env, jobject obj) {
//    // 保存Java回调对象的引用
//    g_callbackKpts = env->NewGlobalRef(obj);
//
//}
}
