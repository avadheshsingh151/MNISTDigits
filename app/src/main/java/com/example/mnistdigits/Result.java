package com.example.mnistdigits;

public class Result {

    private final int mNumber;
    private final float mProbability;
    private final long mTimeTaken;

    public Result(float[] probs, long timeTaken) {
        mNumber =argmax(probs);
        mProbability= probs[mNumber];
        mTimeTaken= timeTaken;
    }

    public int getmNumber() {
        return mNumber;
    }

    public float getmProbability() {
        return mProbability;
    }

    public long getmTimeTaken() {
        return mTimeTaken;
    }

    private static int argmax(float[] probs){
        int maxIdx = -1;
        float maxProb = 0.0f;
        for (int i = 0; i < probs.length; i++) {
            if(probs[i] > maxProb){
                maxProb=probs[i];
                maxIdx=i;
            }
        }
        return maxIdx;
    }
}
