package com.example.opencv_test.tensorflow.lite.handler;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class Dog {
    //original image
    public Bitmap oriBitmap;

    //for bbs
    public int[] pred_bb = new int[4];
    public int[] ori_bb = new int[4];
    public int[] newBox = new int[4];
    public int boxTop, boxBottom, boxLeft, boxRight;
    public int faceSize;
    public float centerX, centerY;
    public Bitmap newBoxBitmap;
    public Bitmap resizedBoxBitmap;

    //for lmks
    public int[] pred_lmks = new int[12];
    public int[] ori_lmks = new int[12];
    public int lmksTop, lmksBottom, lmksLeft, lmksRight;
    float lmksRatio;
    public Bitmap newLandmarkBitmap;
    public Bitmap resizedLandmarkBitmap;



    //constant for scaled img
    public float BOXRATIO = 224/1920.0f;

    public void newSizeBoxBitmap(){
        int delta_width = 224 - resizedBoxBitmap.getWidth();
        int delta_height = 224 - resizedBoxBitmap.getHeight();
        boxTop = delta_height/2;
        boxBottom = delta_height - boxTop;
        boxLeft = delta_width/2;
        boxRight = delta_width - boxLeft;

        newBoxBitmap = copyResizeBitmap(resizedBoxBitmap, boxTop, boxLeft);
//
//        this.newBitmap = copyResizeBitmap(bm,top,left);
    }

    public void createNewBox(){
        centerX = (ori_bb[0] + ori_bb[2])/2.0f;
        centerY = (ori_bb[1] + ori_bb[3])/2.0f;
        faceSize = Math.max(
                Math.abs(ori_bb[2] - ori_bb[0]),
                Math.abs(ori_bb[3] - ori_bb[1])
        );
        Log.d("faceSize : ", Float.toString(faceSize));
        newBox[0] = Math.round(centerX - (faceSize * 0.6f));
        newBox[1] = Math.round(centerY - (faceSize * 0.6f));
        newBox[2] = Math.round(centerX + (faceSize * 0.6f));
        newBox[2] = newBox[2] > oriBitmap.getWidth() ? oriBitmap.getWidth() - 1 : newBox[2];
        newBox[3] = Math.round(centerY + (faceSize * 0.6f));
        newBox[3] = newBox[3] > oriBitmap.getHeight() ? oriBitmap.getHeight() - 1 : newBox[3];
        for(int i = 0 ; i < 4 ; i++){
//            if(i%2==0)
//                newBox[i] = Math.round(centerX - (faceSize * 0.6f));
//            else
//                newBox[i] = Math.round(centerY + (faceSize * 0.6f));

            if(newBox[i] <= 0) newBox[i] = 1;
            else if(newBox[i] > 99999) newBox[i] = 99999;
            Log.d("newBox ", i + " : " + newBox[i] );
        }
    }

    public void newLandmarkBox(){
//        resizedLandmarkBitmap = Bitmap.createBitmap(
//                oriBitmap);

        int newLandmarkBoxSize = Math.max((newBox[2]-newBox[0]),(newBox[3]-newBox[1]));
        lmksRatio = 224.0f/newLandmarkBoxSize;
//        resizedLandmarkBitmap = Bitmap.createScaledBitmap(
//                resizedLandmarkBitmap,
//                resizedBoxBitmap
//        )

        resizedLandmarkBitmap = oriBitmap.createBitmap(
                oriBitmap,
                newBox[0],
                newBox[1],
                (newBox[2]-newBox[0]),
                (newBox[3]-newBox[1])
        );

        resizedLandmarkBitmap = Bitmap.createScaledBitmap(resizedLandmarkBitmap,
                Math.round(newLandmarkBoxSize * lmksRatio),
                Math.round(newLandmarkBoxSize * lmksRatio),
                false
        );
        int delta_width = 224 - resizedLandmarkBitmap.getWidth();
        int delta_height = 224 - resizedLandmarkBitmap.getHeight();

        lmksTop = delta_height/2;
        lmksBottom = delta_height - lmksTop;
        lmksLeft = delta_width/2;
        lmksRight = delta_width - lmksLeft;
        newLandmarkBitmap = copyResizeBitmap(resizedLandmarkBitmap,lmksTop,lmksLeft);
    }

    public void createOriBox(){
        for(int i = 0 ; i < ori_bb.length ; i++) {
            if(i%2==0) {
                ori_bb[i] = Math.round((pred_bb[i]
                        - boxLeft) / BOXRATIO);
                ori_bb[i] = ori_bb[i] < oriBitmap.getWidth() ? ori_bb[i] : oriBitmap.getWidth() - 1;
            }
            else {
                ori_bb[i] = Math.round((pred_bb[i]
                        - boxTop) / BOXRATIO);
                ori_bb[i] = ori_bb[i] < oriBitmap.getHeight() ? ori_bb[i] : oriBitmap.getHeight() - 1;
            }
            if(ori_bb[i]<=0) ori_bb[i]=1;
            Log.d("ori_bb",i + " : " + ori_bb[i]);
        }
    }

    public void createOriLmks(){
        for(int i = 0 ; i < pred_lmks.length ; i++){
            if(i%2==0) {
                ori_lmks[i] = Math.round((pred_lmks[i]
                        - lmksLeft) / lmksRatio);
                ori_lmks[i] += newBox[0];
            }
            else {
                ori_lmks[i] = Math.round((pred_lmks[i]
                        - lmksTop) / lmksRatio);
                ori_lmks[i] += newBox[1];
            }
            if(ori_lmks[i] < 0) ori_lmks[i]=0;
        }
    }

    public Bitmap copyResizeBitmap(Bitmap bitMap, int t,int l){
        Bitmap newbm = Bitmap.createBitmap(224,224,Bitmap.Config.ARGB_8888);

        if(t==0){
            for(int i=0;i<224;i++){
                for(int j=0;j<l;j++){
                    newbm.setPixel(j,i,Color.BLACK);
                    newbm.setPixel(224-j-1,i,Color.BLACK);
                }
            }
            for(int i=0;i<224;i++){
                for(int j=l;j<224-l;j++){
                    newbm.setPixel(j,i,bitMap.getPixel(j-l,i));
                }
            }
        }
        else{
            for(int i=0;i<224;i++) {
                for (int j = 0; j < t; j++) {
                    newbm.setPixel(i,j,Color.BLACK);
                    newbm.setPixel(i,224-j-1,Color.BLACK);
                }
            }
            for(int i=0;i<224;i++){
                for(int j=t;j<224-t;j++){
                    newbm.setPixel(i,j,bitMap.getPixel(i,j-t));
                }
            }
        }
        return newbm;
    }
}
