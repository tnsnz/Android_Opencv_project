package com.example.opencv_test.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.opencv_test.R;


public class TestDialog extends Activity{
    private final String TAG = TestDialog.class.getSimpleName();

    private Context context;
    private Bitmap bitmap;

    // UI
    private TextView textView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textView = findViewById(R.id.reulst_text);
    }

    public TestDialog(Context context, Bitmap bitmap, int bbs[]) {
        this.context = context;
        this.bitmap = bitmap;

        drawSquare(this.bitmap, bbs, Color.RED);

    }
    public TestDialog(Context context, Bitmap bitmap, int bbs[], int lmks[]) {

        this.context = context;
        this.bitmap = bitmap;

        drawSquare(this.bitmap, bbs, Color.RED);
        drawDots(this.bitmap, lmks, Color.RED);
    }

    public TestDialog(Context context, Bitmap bitmap) {

        this.context = context;
        this.bitmap = bitmap;
    }



    /**
     * draw square
     * @param bbs
     * bbs = left_top.x, left_top.y, right_bottom.x, right_bottom.y
     * @param color
     * color = Color. value
     */
    public void drawSquare(Bitmap bitmap, int[] bbs, int color) {
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
        for (int i=0;i<12;i+=2){
            for(int j=0;j<6;j++) {
                for(int k=0;k<6;k++){

                    bitmap.setPixel(lmks[i]+j, lmks[i+1]+k, color);
                }
//                bitmap.setPixel(lmks[i], lmks[i + 1], color);
//                bitmap.setPixel(lmks[i], lmks[i + 1] + j, color);
//                bitmap.setPixel(lmks[i] + j, lmks[i + 1], color);
//                bitmap.setPixel(lmks[i] + j, lmks[i + 1] + j, color);
            }
        }
    }


    public void show(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        final AlertDialog dialog = builder.create();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.dialog, null);
        textView = dialogLayout.findViewById(R.id.reulst_text);
        textView.setText(text);
        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                if (bitmap == null ){
                    Toast.makeText(context, "bitmap is null", Toast.LENGTH_SHORT).show();
                    return ;
                }

                ImageView imageView = (ImageView) dialog.findViewById(R.id.dialogImage);

                int height = bitmap.getHeight();
                int width = bitmap.getWidth();

                Bitmap resized = null;

                float dpi =  context.getResources().getDisplayMetrics().density;
                int px = (int)(350 * dpi);

                if(width > px) {
                    resized = Bitmap.createScaledBitmap(bitmap, (width * px) / height, px, true);//http://javalove.egloos.com/m/67828
                    height = resized.getHeight();
                    width = resized.getWidth();
                    imageView.setImageBitmap(resized);
                }
                else imageView.setImageBitmap(bitmap);



                Log.d("@@@", px + " " + width + " " + height );

            }
        });
        dialog.show();
    }

    public void show(String text, int[] lmks) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        final AlertDialog dialog = builder.create();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.dialog, null);
        textView = dialogLayout.findViewById(R.id.reulst_text);
        textView.setText(text);
        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                if (bitmap == null ){
                    Toast.makeText(context, "bitmap is null", Toast.LENGTH_SHORT).show();
                    return ;
                }

                ImageView imageView = (ImageView) dialog.findViewById(R.id.dialogImage);

                int height = bitmap.getHeight();
                int width = bitmap.getWidth();

                Bitmap resized = null;

                float dpi =  context.getResources().getDisplayMetrics().density;
                int px = (int)(350 * dpi);

                if(width < px) {
                    resized = Bitmap.createScaledBitmap(bitmap, (width * px) / height, px, true);//http://javalove.egloos.com/m/67828
                    height = resized.getHeight();
                    width = resized.getWidth();
                    imageView.setImageBitmap(resized);
                }
                else imageView.setImageBitmap(bitmap);



                Log.d("@@@", px + " " + width + " " + height );

            }
        });
        dialog.show();
    }


    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        final AlertDialog dialog = builder.create();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.dialog, null);
        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {

                if (bitmap == null ){
                    Toast.makeText(context, "bitmap is null", Toast.LENGTH_SHORT).show();
                    return ;
                }

                ImageView imageView = (ImageView) dialog.findViewById(R.id.dialogImage);

                int height = bitmap.getHeight();
                int width = bitmap.getWidth();

                Bitmap resized = null;

                float dpi =  context.getResources().getDisplayMetrics().density;
                int px = (int)(350 * dpi);

                if(width > px) {
                    resized = Bitmap.createScaledBitmap(bitmap, (width * px) / height, px, true);//http://javalove.egloos.com/m/67828
                    height = resized.getHeight();
                    width = resized.getWidth();
                    imageView.setImageBitmap(resized);
                }
                else imageView.setImageBitmap(bitmap);



                Log.d("@@@", px + " " + width + " " + height );

            }
        });
        dialog.show();
    }
}
