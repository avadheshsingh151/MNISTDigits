package com.example.mnistdigits;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class Classifier {

    public static final int IMG_HEIGHT = 28;
    public static final int IMG_WIDTH = 28;

    public static final String  LOG_TAG = Classifier.class.getSimpleName();
    private static final String MODEL_NAME = "model.tflite";

    private static final int BATCH_SIZE = 1;     //IMG supplied
    private static final int NUM_CHANNEL = 1;    // 1:Grey IMAGE,  3: RGB Image
    private static final int NUM_CLASSS = 10;    // classificton output

    private final Interpreter.Options options= new Interpreter.Options();
    private final Interpreter mInterpreter;
    private  final ByteBuffer mImageData;
    private final int[] mImagePixels = new int[IMG_HEIGHT*IMG_WIDTH];
    private final float[][] mResult= new float[1][NUM_CLASSS];

    public Classifier(Activity activity) throws IOException{
        mInterpreter = new Interpreter(loadModelFile(activity),options);

        mImageData = ByteBuffer.allocateDirect(4*BATCH_SIZE*IMG_HEIGHT*IMG_WIDTH); //float data type image size
        mImageData.order(ByteOrder.nativeOrder());
    }

    public Result classify(Bitmap bitmap){

        convertBitmaptoByteBuffer(bitmap);
//        ImageProcessor imageProcessor =
//                new ImageProcessor.Builder()
//                        .add(new ResizeOp(IMG_HEIGHT, IMG_WIDTH, ResizeOp.ResizeMethod.BILINEAR))
//                        .add(new NormalizeOp(0,255))
//                        .build();
//
//        TensorImage tImage = new TensorImage(DataType.FLOAT32);
//        tImage.load(bitmap);
//        tImage = imageProcessor.process(tImage);

        

        long startTime= SystemClock.uptimeMillis();
        mInterpreter.run(mImageData,mResult);
        long endTime = SystemClock.uptimeMillis();

        long timeTaken = endTime-startTime;

        Log.v(LOG_TAG, "classify(): result = " + Arrays.toString(mResult[0])+ ", TimeTaken ="+ timeTaken);

        return new Result(mResult[0],timeTaken);

    }



    private MappedByteBuffer loadModelFile(Activity activity) throws IOException{

//       AssetFileDescriptor fileDescriptor=activity.getAssets().openFd(MODEL_NAME);
//       FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
//       FileChannel fileChannel=inputStream.getChannel();
//
//      long startOffset = fileDescriptor.getStartOffset();
//      long declaredLength = fileDescriptor.getDeclaredLength();
//
//      return  fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,declaredLength);
//
        MappedByteBuffer tfliteModel
                = FileUtil.loadMappedFile(activity,
                MODEL_NAME);

        return  tfliteModel;
    }

    private void convertBitmaptoByteBuffer(Bitmap bitmap) {

        if(mImageData==null){
            return;
        }

        mImageData.rewind();
        bitmap.getPixels(mImagePixels,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight());

        int pixel=0;
        for (int i=0; i<IMG_WIDTH;++i){
            for (int j = 0; j <IMG_HEIGHT ; j++) {
                int value = mImagePixels[pixel++];
                mImageData.putFloat(convertPixel(value));
            }
        }
    }

    private static float convertPixel(int color) {

        return (255 - (((color >> 16) & 0xFF) * 0.299f +
                ((color >> 8 )* 0.587f) +
                (color >> 8 )* 0.114f))/255.0f;
    }

}
