package com.example.mnistdigits;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nex3z.fingerpaintview.FingerPaintView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int PIXEL_WIDTH = 28;
    private Classifier mClassifier;
    // ui elements
    private Button clearBtn, classBtn;
    private TextView mTvPrediction,mTvProbability,mTvTimeCost;
    FingerPaintView mFpvPaint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            mClassifier = new Classifier(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        clearBtn = (Button) findViewById(R.id.btn_clear);
        clearBtn.setOnClickListener(this);
        classBtn = (Button) findViewById(R.id.btn_detect);
        classBtn.setOnClickListener(this);

        mFpvPaint = findViewById(R.id.fpv_paint);
        mTvPrediction = findViewById(R.id.prediction);
        mTvProbability = findViewById(R.id.probability);
        mTvTimeCost = findViewById(R.id.timecost);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_clear) {
            mFpvPaint.clear();
            mTvPrediction.setText("");
            mTvProbability.setText("");
            mTvTimeCost.setText("");
        }
        else if (view.getId() == R.id.btn_detect) {
            //if the user clicks the classify button get the pixel data and store it in an array
            Bitmap bitmap = mFpvPaint.exportToBitmap(Classifier.IMG_WIDTH,Classifier.IMG_HEIGHT);
            Result res = mClassifier.classify(bitmap);
            mTvProbability.setText("Probability: "+res.getmProbability()+"");
            mTvPrediction.setText("Prediction: "+res.getmNumber()+"");
            mTvTimeCost.setText("TimeCost: "+res.getmTimeTaken()+"");
        }
    }
}