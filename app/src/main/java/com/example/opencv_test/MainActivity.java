package com.example.opencv_test;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.opencv_test.tensorflow.lite.handler.Dog;
import com.example.opencv_test.tensorflow.lite.handler.TFModelHandler;
import com.example.opencv_test.ui.LoadingActivity;
import com.example.opencv_test.ui.dialog.TestDialog;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.Collections;
import java.util.List;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.INTERNET;


public class MainActivity extends AppCompatActivity
        implements CameraBridgeViewBase.CvCameraViewListener2 {

    private Handler mainHandler;

    Mat glasses;

    Mat mustache;

    private static final String TAG = "opencv";
    private Mat matInput;
    private Mat matResult;

    private static int WIDTH = 1080;
    private static int HEIGHT = 1920;

    private TFModelHandler tfModelHandler;
    private Dog dog = new Dog();

    private Button show_bnt;

    private CameraBridgeViewBase mOpenCvCameraView;

    static {
        System.loadLibrary("dlib");
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        glasses = Draw.readImage(MainActivity.this, "glasses");
        mustache = Draw.readImage(MainActivity.this, "mustache01");

//        Intent intent = new Intent(MainActivity.this,
//                LoadingActivity.class);
//        startActivity(intent);

        mainHandler = new Handler(Looper.getMainLooper());
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        tfModelHandler = new TFModelHandler(getAssets(), this);

        tfModelHandler.addModel("tflite/bbs_1104.tflite",
                "tflite/labels_bbs.txt",
                224,
                false,
                "dog bbs",
                "find dog face");

        tfModelHandler.addModel("tflite/lmks_1104.tflite",
                "tflite/labels_lmks.txt",
                224,
                false,
                "dog landmarks",
                "find dog landmarks");


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        show_bnt = findViewById(R.id.show_bnt);

        show_bnt.setOnClickListener(view -> {
                    if (dog.oriBitmap == null) {
                        Toast.makeText(MainActivity.this,
                                "null image",
                                Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
            (new TestDialog(
                    MainActivity.this,
                    dog.oriBitmap
//                    dog.ori_lmks
            )).show();
        });


        mOpenCvCameraView = (CameraBridgeViewBase)findViewById(R.id.activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setCameraIndex(0); // front-camera(1),  back-camera(0)
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume :: Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "onResum :: OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }


    public void onDestroy() {
        super.onDestroy();

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        int tfModelSize = tfModelHandler.getModelsSize();
        Log.d("tModelSize",Integer.toString(tfModelSize));
        if (tfModelSize < 2)
            return null;
        Mat matInputFrame = inputFrame.rgba();

        if (matInput == null) matInput = new Mat();
        Core.rotate(matInputFrame, matInput, Core.ROTATE_90_CLOCKWISE);

        if ( matResult == null )
            matResult = new Mat();

        matToBitmap(matInput);

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        matToBitmap(matInput);
//        dog.oriBitmap = Bitmap.createBitmap(dog.oriBitmap,
//                0, 0, dog.oriBitmap.getWidth(),dog.oriBitmap.getHeight(),
//                matrix, true);

        dog.resizedBoxBitmap = Bitmap.createScaledBitmap(dog.oriBitmap,
                Math.round((float)dog.oriBitmap.getWidth()*dog.BOXRATIO),
                Math.round((float)dog.oriBitmap.getHeight()*dog.BOXRATIO),
                false);

        Log.d("oriBitWidth()",Integer.toString(dog.oriBitmap.getWidth()));
        Log.d("oriBitHeight()",Integer.toString(dog.oriBitmap.getHeight()));
        dog.newSizeBoxBitmap();
//
        dog.pred_bb = TFModelHandler.roundsValue(
                tfModelHandler.runModelForRegression(0, dog.newBoxBitmap)

        );
//
        dog.createOriBox();
        dog.createNewBox();

        if(dog.newBox[2]-dog.newBox[0] >= 1080 || dog.newBox[3]-dog.newBox[1] >= 1920) return matInput;

        dog.newLandmarkBox();

        Log.d(TAG, "oribitmap: " + dog.oriBitmap.getWidth() + dog.oriBitmap.getHeight());

        dog.pred_lmks = TFModelHandler.roundsValue(
                tfModelHandler.runModelForRegression(
                        1,
                        dog.newLandmarkBitmap
                )
        );
        dog.createOriLmks();

        drawBox(dog.oriBitmap,dog.ori_bb,Color.RED);
        drawDots(dog.oriBitmap,dog.ori_lmks,Color.RED);

//        matrix.postRotate(180);
//        dog.oriBitmap = Bitmap.createBitmap(dog.oriBitmap,
//                0, 0, dog.oriBitmap.getWidth(),dog.oriBitmap.getHeight(),
//                matrix, true);


        Utils.bitmapToMat(dog.oriBitmap, matInput);
        Core.rotate(matInput, matInput, Core.ROTATE_90_COUNTERCLOCKWISE);


        Draw.fittingGlasses(matInput.getNativeObjAddr(), matResult.getNativeObjAddr(),
                glasses.getNativeObjAddr(), WIDTH - dog.ori_lmks[10], dog.ori_lmks[11],
                WIDTH - dog.ori_lmks[4], dog.ori_lmks[5]);

        Log.d(TAG, "left eye: (" + (WIDTH - dog.ori_lmks[10]) + dog.ori_lmks[11] + ") right eye: (" +
                (WIDTH - dog.ori_lmks[4]) + dog.ori_lmks[5] +") ");

//        Draw.fittingMustache(matInput.getNativeObjAddr(), matResult.getNativeObjAddr(),
//                mustache.getNativeObjAddr(), 1080 - dog.ori_lmks[4], 1920 - dog.ori_lmks[5],
//                1080 - dog.ori_lmks[10], 1920 - dog.ori_lmks[11],
//                1080 - dog.ori_lmks[6], 1920 - dog.ori_lmks[7]);


//        int centerX = WIDTH - (dog.ori_lmks[4] + dog.ori_lmks[10]) / 2;
//        int centerY = (dog.ori_lmks[5] + dog.ori_lmks[11]) / 2;
//
//        matResult = Draw.overlayTransparentByMatrix(matInput, glasses,
//                centerX, centerY, 0, 0);

        return matResult;
    }


    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvCameraView);
    }


    //여기서부턴 퍼미션 관련 메소드
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;


    protected void onCameraPermissionGranted() {
        List<? extends CameraBridgeViewBase> cameraViews = getCameraViewList();
        if (cameraViews == null) {
            return;
        }
        for (CameraBridgeViewBase cameraBridgeViewBase: cameraViews) {
            if (cameraBridgeViewBase != null) {
                cameraBridgeViewBase.setCameraPermissionGranted();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean havePermission = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                havePermission = false;
            }
        }
        if (havePermission) {
            onCameraPermissionGranted();
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onCameraPermissionGranted();
        }else{
            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder( MainActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id){
                requestPermissions(new String[]{CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }

    private void matToBitmap(Mat mat){
//        Mat tmp = new Mat (1920, 1080, CvType.CV_8U, new Scalar(4));
        try {
            dog.oriBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mat, dog.oriBitmap);
        }
        catch (CvException e){Log.d("Exception",e.getMessage());}
    }

    public void drawBox(Bitmap bitmap, int[] bbs, int color) {

        for(int i=bbs[0];i<bbs[2];i++) {
            bitmap.setPixel(i,bbs[1], color);
            bitmap.setPixel(i,bbs[3], color);
        }
        for(int i=bbs[1];i<bbs[3];i++){
            bitmap.setPixel(bbs[0],i, color);
            bitmap.setPixel(bbs[2],i, color);
        }
    }

    public void drawDots(Bitmap bitmap, int[] lmks, int color) {
        for (int i=0;i<12;i+=2)
            for(int j=0;j<6;j++)
                for(int k=0;k<6;k++)
                    bitmap.setPixel(lmks[i]+j, lmks[i+1]+k, color);
    }
}