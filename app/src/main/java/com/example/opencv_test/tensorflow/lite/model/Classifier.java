package com.example.opencv_test.tensorflow.lite.model;
import android.graphics.Bitmap;

import java.util.List;

public interface Classifier {

    class Recognition {
        /**
         * A unique identifier for what has been recognized. Specific to the class, not the instance of
         * the object.
         */
        private final String id;

        /**
         * Display name for the recognition.
         */
        private final String title;

        /**
         * Whether or not the model features quantized or float weights.
         */
        private final boolean quant;

        /**
         * A sortable score for how good the recognition is relative to others. Higher should be better.
         */
        private final Float confidence;


        public Recognition(
                final String id, final String title, final Float confidence, final boolean quant, int input_size) {
            this.id = id;
            this.title = title;
            this.confidence = confidence;
            this.quant = quant;
        }

        public String getId() {
            return id;
        }

        public List<Recognition> recognizeImageSorted(Bitmap bitmap) {
            return null;
        }

        public String getTitle() {
            return title;
        }

        public Float getConfidence() {
            return confidence;
        }

        public boolean getQuant() {
            return quant;
        }

        @Override
        public String toString() {
            String resultString = "";
            if (id != null) {
                resultString += "[" + id + "] ";
            }

            if (title != null) {
                resultString += title + " ";
            }

            if (confidence != null) {
                resultString += String.format("(%.1f%%) ", confidence * 100.0f);
            }

            return resultString.trim();
        }
    }

    List<Recognition> recognizeImageSorted(Bitmap bitmap);

    List<Recognition> recognizeImage(Bitmap bitmap);

    int getInputSize();

    void close();
}

