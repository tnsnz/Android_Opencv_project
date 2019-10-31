package com.example.opencv_test.ui;
import android.app.Activity;
import android.os.Bundle;
import android.os.HandlerThread;

import androidx.annotation.Nullable;

import com.example.opencv_test.R;

import java.util.logging.Handler;


public class LoadingActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        startLoading();
    }

    private void startLoading() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finish();
    }

}
