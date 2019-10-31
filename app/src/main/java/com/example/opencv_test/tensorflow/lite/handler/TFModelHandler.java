package com.example.opencv_test.tensorflow.lite.handler;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.util.Log;

import com.example.opencv_test.tensorflow.lite.model.Classifier;
import com.example.opencv_test.tensorflow.lite.model.TensorFlowInterpreterGpu;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class TFModelHandler {
    private static final String TAG = TFModelHandler.class.getSimpleName();
    private List<TFModel> models;
    private AssetManager assetManager;
    private Context context;

    private Executor executor  =  Executors.newSingleThreadExecutor();

    public int getModelsSize() {
        return models.size();
    }

    /**
     *
     * @param assetManager
     */
    public TFModelHandler(AssetManager assetManager, Context context) {
        this.assetManager = assetManager;
        this.context = context;
        models = new ArrayList<TFModel>();
    }

    public String runModelForClassification(
            int index, Image img) {
        Bitmap bitmap = imgToBitmap(img,
                models.get(index).getModel().getInputSize());
        return models.get(index)
                .getModel()
                .recognizeImageSorted(bitmap)
                .get(0)
                .getTitle();
    }

    public String runModelForClassification(
            int index, Bitmap bitmap) {
        return models.get(index)
                .getModel()
                .recognizeImageSorted(bitmap).get(0).getTitle();
    }

    public List<Classifier.Recognition> runModelForRegression(
            int index, Image img) {
        Bitmap bitmap = imgToBitmap(img,
                models.get(index).getModel().getInputSize());
        return models.get(index)
                .getModel()
                .recognizeImage(bitmap);
    }

    public List<Classifier.Recognition> runModelForRegression(
            int index, Bitmap bitmap) {
        return models.get(index)
                .getModel()
                .recognizeImage(bitmap);
    }


    public boolean isModelCheck(int index) {
        int size = models.size();
        return size > index;
    }

    public static Dog imgToBitmap(Context context, Image img) {
        byte[] nv21;
        ContextWrapper cw = new ContextWrapper(context);
        String fileName = "test.jpg";
        File dir = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File file = new File(dir, fileName);
        FileOutputStream outputStream;
        Bitmap scaledBitmap = null;
        Dog dog = new Dog();
        try {
            outputStream = new FileOutputStream(file);
            ByteBuffer yBuffer = img.getPlanes()[0].getBuffer();
            ByteBuffer uBuffer = img.getPlanes()[1].getBuffer();
            ByteBuffer vBuffer = img.getPlanes()[2].getBuffer();

            int ySize = yBuffer.remaining();
            int uSize = uBuffer.remaining();
            int vSize = vBuffer.remaining();

            nv21 = new byte[ySize + uSize + vSize];

            yBuffer.get(nv21, 0, ySize);
            vBuffer.get(nv21, ySize, vSize);
            uBuffer.get(nv21, ySize + vSize, uSize);

            int width = img.getWidth();
            int height = img.getHeight();

            img.close();


            ByteArrayOutputStream out = new ByteArrayOutputStream();
            YuvImage yuv = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
            yuv.compressToJpeg(new Rect(0, 0, width, height), 100, out);
            byte[] byteArray = out.toByteArray();

            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            if (bitmap != null) {
                Log.i("bitmap ", "contains data");
            }
//            Bitmap portraitBitmap = Bitmap.createBitmap(bitmap,
//                    0, 0, width,height,
//                    matrix, true);
            dog.oriBitmap = Bitmap.createBitmap(bitmap,
                    0, 0, width,height,
                    matrix, true);
            dog.BOXRATIO = 224/Math.max(dog.oriBitmap.getWidth(),dog.oriBitmap.getHeight());
            Log.d("oriBitmapWidth()",Integer.toString(dog.oriBitmap.getWidth()));
            Log.d("oriBitmapHeight()",Integer.toString(dog.oriBitmap.getHeight()));

            Log.d("dog.oriBitmap.width ",Integer.toString(dog.oriBitmap.getWidth()));
            Log.d("dog.oriBitmap.height ",Integer.toString(dog.oriBitmap.getHeight()));
            Log.d("dog.IMG_RATIO ",Float.toString(dog.BOXRATIO));
//            bbs.oriBitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);

//            scaledBitmap = Bitmap.createScaledBitmap(portraitBitmap,
//                    width,height, false);
//            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

//            bbs.resizedBitmap = Bitmap.createScaledBitmap(bbs.oriBitmap,
//                    width,height, false);
            dog.resizedBoxBitmap = Bitmap.createScaledBitmap(dog.oriBitmap,
                    Math.round((float)dog.oriBitmap.getWidth()*dog.BOXRATIO),
                    Math.round((float)dog.oriBitmap.getHeight()*dog.BOXRATIO),
                    false);

            outputStream.flush();
            outputStream.close();
            return dog;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return dog;
    }

    public static int[] originBbs(int[] bbs, int ratio_X, int ratio_Y){
        int ret[] = new int[bbs.length];
        for(int i=0;i<bbs.length;i++){
            if(i%2==0) ret[i] = Math.round(bbs[i] * (ratio_X / 224.0f));
            else ret[i] = Math.round(bbs[i] * (ratio_Y / 224.0f));
        }
        return ret;
    }

    private Bitmap imgToBitmap(Image img, int inputSize) {
        byte[] nv21;
        ContextWrapper cw = new ContextWrapper(context);
//        String fileName = "test.jpg";
//        File dir = cw.getDir("imageDir", Context.MODE_PRIVATE);
//        File file = new File(dir, fileName);
//        FileOutputStream outputStream;
        Bitmap scaledBitmap = null;
        try {
//            outputStream = new FileOutputStream(file);
            ByteBuffer yBuffer = img.getPlanes()[0].getBuffer();
            ByteBuffer uBuffer = img.getPlanes()[1].getBuffer();
            ByteBuffer vBuffer = img.getPlanes()[2].getBuffer();

            int ySize = yBuffer.remaining();
            int uSize = uBuffer.remaining();
            int vSize = vBuffer.remaining();


            nv21 = new byte[ySize + uSize + vSize];

            yBuffer.get(nv21, 0, ySize);
            vBuffer.get(nv21, ySize, vSize);
            uBuffer.get(nv21, ySize + vSize, uSize);

            int width = img.getWidth();
            int height = img.getHeight();

            img.close();


//            ByteArrayOutputStream out = new ByteArrayOutputStream();
            YuvImage yuv = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
//            yuv.compressToJpeg(new Rect(0, 0, width, height), 100, out);
            byte[] byteArray = nv21;

            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//            Matrix matrix = new Matrix();
//            matrix.postRotate(90);
            if (bitmap != null) {
                Log.i("bitmap ", "contains data");
            }
//            Bitmap portraitBitmap = Bitmap.createBitmap(bitmap,
//                    0, 0, bitmap.getWidth(), bitmap.getHeight(),
//                    matrix, true);


            scaledBitmap = Bitmap.createScaledBitmap(bitmap,
                    inputSize, inputSize, false);
//            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//            outputStream.flush();
//            outputStream.close();
            return scaledBitmap;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return scaledBitmap;
    }

    /**
     * Img -> Bitmap
     *
     * @param img
     * @return
     */
    public static Bitmap imgToBitmap(Context context, Image img, int inputSize) {
        byte[] nv21;
        ContextWrapper cw = new ContextWrapper(context);
        String fileName = "test.jpg";
        File dir = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File file = new File(dir, fileName);
        FileOutputStream outputStream;
        Bitmap scaledBitmap = null;
        try {
            outputStream = new FileOutputStream(file);
            ByteBuffer yBuffer = img.getPlanes()[0].getBuffer();
            ByteBuffer uBuffer = img.getPlanes()[1].getBuffer();
            ByteBuffer vBuffer = img.getPlanes()[2].getBuffer();

            int ySize = yBuffer.remaining();
            int uSize = uBuffer.remaining();
            int vSize = vBuffer.remaining();

            nv21 = new byte[ySize + uSize + vSize];

            yBuffer.get(nv21, 0, ySize);
            vBuffer.get(nv21, ySize, vSize);
            uBuffer.get(nv21, ySize + vSize, uSize);

            int width = img.getWidth();
            int height = img.getHeight();

            img.close();


            ByteArrayOutputStream out = new ByteArrayOutputStream();
            YuvImage yuv = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
            yuv.compressToJpeg(new Rect(0, 0, width, height), 100, out);
            byte[] byteArray = out.toByteArray();

            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            if (bitmap != null) {
                Log.i("bitmap ", "contains data");
            }
            Bitmap portraitBitmap = Bitmap.createBitmap(bitmap,
                    0, 0, bitmap.getWidth(), bitmap.getHeight(),
                    matrix, true);


            scaledBitmap = Bitmap.createScaledBitmap(portraitBitmap,
                    inputSize, inputSize, false);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            return scaledBitmap;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return scaledBitmap;
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int inputSize) {
        return Bitmap.createScaledBitmap(bitmap,
                inputSize, inputSize, false);
    }


    /**
     *
     * add model to List
     * @param model_path
     * @param label_path
     * @param input_size
     * @param quant
     */

    public void addModel(String model_path,
                         String label_path,
                         int input_size,
                         boolean quant,
                         String title,
                         String description) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Classifier model = TensorFlowInterpreterGpu.create(assetManager,
                            model_path,
                            label_path,
                            input_size,
                            quant);
                    models.add(new TFModel(model, title, description));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static int[] roundsValue(List<Classifier.Recognition> list) {
        int[] result = new int[list.size()];
        int index = 0;
        for (Classifier.Recognition recognition : list) {
            result[index++] = Math.round(recognition.getConfidence());
        }
        return result;
    }
}
