package com.example.opencv_test;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Draw {
    private static final String TAG = Draw.class.getSimpleName();

    private static int height = 1920;
    private static int width = 1080;

    public static native void fittingGlasses(long inputMat, long outputMat, long inputGlasses,
                                                 int leyeX, int leyeY, int reyeX, int reyeY);

    public static native void fittingMustache(long inputMat, long outputMat, long inputMustache,
                                              int leyeX, int leyeY, int reyeX, int reyeY,
                                              int noseX, int noseY);



    public static void overlayTransparentByBitmap(Bitmap background_img, Bitmap imgToOverlay,
                                          int x, int y, int overlaySizeW, int overlaySizeH) {
        Bitmap bgImg = background_img.copy(background_img.getConfig(), true);
        // convert 3 channels to 4 channels

    }

    public static Mat overlayTransparentByMatrix(Mat background_img, Mat imgToOverlay,
                                                  int x, int y, int overlaySizeW, int overlaySizeH){
        Log.d(TAG, "overlayTransparentByMatrix: " + background_img.channels());
        Mat bgImg = new Mat();
        background_img.copyTo(bgImg);

        Log.d(TAG, "overlayTransparentByMatrix: " + background_img.channels());
        Log.d(TAG, "overlayTransparentByMatrix: " + bgImg.channels());

        int h = imgToOverlay.height();
        int w = imgToOverlay.width();

        Log.d(TAG, "x: " + x + " y: " + y +
                " w: " + w + " h: " + h);

        // b g r a
        List<Mat> mask = new ArrayList<>();
        Core.split(imgToOverlay, mask);

        Imgproc.medianBlur(mask.get(3), mask.get(3), 5);

        int dtop = y-h/2 >=0 ? y-h/2 : 0;
        int dbottom = y+h/2 <= height ? y+h/2 : height;
        int dleft = x-w/2 >=0 ? x-w/2 : 0;
        int dright = x+w/2 <= width ? x+w/2 : width;


//        Mat roi = bgImg.rowRange(dtop, dbottom)
//                .colRange(dleft, dright);
        Mat roi = bgImg.rowRange(dtop, dbottom)
                .colRange(dleft, dright);

        Mat tmp = new Mat();
        Mat img1Bg = new Mat();
        Mat img2Fg = new Mat();
        Mat maskNot = new Mat();
//        Core.bitwise_not(mask.get(3), maskNot);
//
//        tmp.copyTo(roi);
//
//        Core.bitwise_and(tmp, tmp, img1Bg, maskNot);
//        Core.bitwise_and(imgToOverlay, imgToOverlay, mask.get(3), img2Fg);
//
//        Core.add(img1Bg, img2Fg, roi);


        Core.add(roi, imgToOverlay, roi);

        return bgImg;
    }

    public static Mat readImage(Context context, String fileName) {
        InputStream stream = null;
        Uri uri = Uri.parse("android.resource://com.example.opencv_test/drawable/" + fileName);
        try {
            stream = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bmp = BitmapFactory.decodeStream(stream, null, bmpFactoryOptions);
        Mat ImageMat = new Mat();
        Utils.bitmapToMat(bmp, ImageMat);

        Mat result = new Mat();

        Core.rotate(ImageMat, result, Core.ROTATE_90_COUNTERCLOCKWISE);

        return result;
    }
}
