#include <jni.h>
#include <iostream>
#include <dlib/image_io.h>
#include <dlib/opencv.h>
#include <opencv2/opencv.hpp>


using namespace cv;
using namespace std;
using namespace dlib;

// check this

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

extern "C"
JNIEXPORT void JNICALL
Java_com_example_opencv_1test_Draw_fittingGlasses(JNIEnv *env, jclass clazz, jlong input_mat,
                                                  jlong output_mat, jlong input_glasses,
                                                  jint leye_x, jint leye_y, jint reye_x,
                                                  jint reye_y) {
    Mat &matInput = *(Mat *)input_mat;
    Mat &matResult = *(Mat *)output_mat;
    Mat &matGlasses = *(Mat *)input_glasses;
    point leye = point(leye_x, leye_y);
    point reye = point(reye_x, reye_y);

    matrix<rgb_alpha_pixel> img; matrix<rgb_alpha_pixel> glasses;

    cv_image<rgb_alpha_pixel> tmp1(matInput);
    assign_image(img, tmp1);
    cv_image<rgb_alpha_pixel> tmp2(matGlasses);
    assign_image(glasses, tmp2);

//    std::vector<point> from = { point(176,36), point(59,35) }, to = { leye, reye };
    std::vector<point> from = { point(176,36), point(59,35) }, to = { leye, reye };

    auto tform = find_similarity_transform(from, to);
    for (long r = 0; r < glasses.nr(); ++r) {
        for (long c = 0; c < glasses.nc(); ++c) {
            point p = tform(point(c, r));
            if (get_rect(img).contains(p))
                assign_pixel((rgb_pixel &) img(p.y(), p.x()), glasses(r, c));
        }
    }

    cvtColor(toMat(img), matResult, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_opencv_1test_Draw_fittingMustache(JNIEnv *env, jclass clazz, jlong input_mat,
                                                   jlong output_mat, jlong input_mustache,
                                                   jint leye_x, jint leye_y, jint reye_x,
                                                   jint reye_y, jint nose_x, jint nose_y) {
    Mat &matInput = *(Mat *)input_mat;
    Mat &matResult = *(Mat *)output_mat;
    Mat &matMustache = *(Mat *)input_mustache;
    point leye = point(leye_x, leye_y);
    point reye = point(reye_x, reye_y);
    point nose = point(nose_x, nose_y);

    matrix<rgb_alpha_pixel> img; matrix<rgb_alpha_pixel> mustache;

    cv_image<rgb_alpha_pixel> tmp1(matInput);
    assign_image(img, tmp1);
    cv_image<rgb_alpha_pixel> tmp2(matMustache);
    assign_image(mustache, tmp2);

    auto lmustache = 1.3 * (leye - reye) / 2 + nose;
    auto rmustache = 1.3 * (reye - leye) / 2 + nose;

    auto mrect = get_rect(mustache);

    std::vector<point> from = {mrect.tl_corner(), mrect.tr_corner()};
    std::vector<point> to = {rmustache, lmustache};

    auto tform = find_similarity_transform(from, to);
    for (long r = 0; r < mustache.nr(); ++r) {
        for (long c = 0; c < mustache.nc(); ++c) {
            point p = tform(point(c, r));
            if (get_rect(img).contains(p))
                assign_pixel((rgb_pixel &) img(p.y(), p.x()), mustache(r, c));
        }
    }

    cvtColor(toMat(img), matResult, 0);
}