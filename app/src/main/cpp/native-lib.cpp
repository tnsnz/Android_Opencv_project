#include <jni.h>
#include <opencv2/opencv.hpp>

using namespace cv;

extern "C"
JNIEXPORT void JNICALL
Java_com_example_opencv_1test_MainActivity_ConvertRGBtoGray(JNIEnv *env, jobject thiz,
                                                            jlong mat_addr_input,
                                                            jlong mat_addr_result) {
    Mat &matInput = *(Mat *)mat_addr_input;
    Mat &matResult = *(Mat *)mat_addr_result;
    cvtColor(matInput,matResult,0);


    //    cvtColor(matInput,matResult,COLOR_YUV2BGRA_NV21);
//    cvtColor(matInput, matResult, COLOR_RGBA2GRAY);
}