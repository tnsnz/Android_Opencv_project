package com.example.opencv_test.tensorflow.lite.handler;

import com.example.opencv_test.tensorflow.lite.model.Classifier;

public class TFModel {
    private Classifier model;
    private String title;
    private String description;


    public TFModel(Classifier model, String title, String description) {
        this.model = model;
        this.title = title;
        this.description = description;
    }


    public Classifier getModel() {
        return model;
    }

    public void setModel(Classifier model) {
        this.model = model;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
