package com.nineoldandroids.animation;

import android.view.animation.Interpolator;
import java.util.ArrayList;
import java.util.Arrays;

class KeyframeSet {
    TypeEvaluator mEvaluator;
    Keyframe mFirstKeyframe;
    Interpolator mInterpolator;
    ArrayList<Keyframe> mKeyframes = new ArrayList<>();
    Keyframe mLastKeyframe;
    int mNumKeyframes;

    public KeyframeSet(Keyframe... keyframes) {
        this.mNumKeyframes = keyframes.length;
        this.mKeyframes.addAll(Arrays.asList(keyframes));
        this.mFirstKeyframe = (Keyframe) this.mKeyframes.get(0);
        this.mLastKeyframe = (Keyframe) this.mKeyframes.get(this.mNumKeyframes - 1);
        this.mInterpolator = this.mLastKeyframe.getInterpolator();
    }

    public static KeyframeSet ofInt(int... values) {
        int numKeyframes = values.length;
        IntKeyframe[] keyframes = new IntKeyframe[Math.max(numKeyframes, 2)];
        if (numKeyframes == 1) {
            keyframes[0] = (IntKeyframe) Keyframe.ofInt(0.0f);
            keyframes[1] = (IntKeyframe) Keyframe.ofInt(1.0f, values[0]);
        } else {
            keyframes[0] = (IntKeyframe) Keyframe.ofInt(0.0f, values[0]);
            for (int i = 1; i < numKeyframes; i++) {
                keyframes[i] = (IntKeyframe) Keyframe.ofInt(((float) i) / ((float) (numKeyframes - 1)), values[i]);
            }
        }
        return new IntKeyframeSet(keyframes);
    }

    public static KeyframeSet ofFloat(float... values) {
        int numKeyframes = values.length;
        FloatKeyframe[] keyframes = new FloatKeyframe[Math.max(numKeyframes, 2)];
        if (numKeyframes == 1) {
            keyframes[0] = (FloatKeyframe) Keyframe.ofFloat(0.0f);
            keyframes[1] = (FloatKeyframe) Keyframe.ofFloat(1.0f, values[0]);
        } else {
            keyframes[0] = (FloatKeyframe) Keyframe.ofFloat(0.0f, values[0]);
            for (int i = 1; i < numKeyframes; i++) {
                keyframes[i] = (FloatKeyframe) Keyframe.ofFloat(((float) i) / ((float) (numKeyframes - 1)), values[i]);
            }
        }
        return new FloatKeyframeSet(keyframes);
    }

    public static KeyframeSet ofKeyframe(Keyframe... keyframes) {
        int numKeyframes = keyframes.length;
        boolean hasFloat = false;
        boolean hasInt = false;
        boolean hasOther = false;
        for (int i = 0; i < numKeyframes; i++) {
            if (keyframes[i] instanceof FloatKeyframe) {
                hasFloat = true;
            } else if (keyframes[i] instanceof IntKeyframe) {
                hasInt = true;
            } else {
                hasOther = true;
            }
        }
        if (hasFloat && !hasInt && !hasOther) {
            FloatKeyframe[] floatKeyframes = new FloatKeyframe[numKeyframes];
            for (int i2 = 0; i2 < numKeyframes; i2++) {
                floatKeyframes[i2] = keyframes[i2];
            }
            return new FloatKeyframeSet(floatKeyframes);
        } else if (!hasInt || hasFloat || hasOther) {
            return new KeyframeSet(keyframes);
        } else {
            IntKeyframe[] intKeyframes = new IntKeyframe[numKeyframes];
            for (int i3 = 0; i3 < numKeyframes; i3++) {
                intKeyframes[i3] = keyframes[i3];
            }
            return new IntKeyframeSet(intKeyframes);
        }
    }

    public static KeyframeSet ofObject(Object... values) {
        int numKeyframes = values.length;
        ObjectKeyframe[] keyframes = new ObjectKeyframe[Math.max(numKeyframes, 2)];
        if (numKeyframes == 1) {
            keyframes[0] = (ObjectKeyframe) Keyframe.ofObject(0.0f);
            keyframes[1] = (ObjectKeyframe) Keyframe.ofObject(1.0f, values[0]);
        } else {
            keyframes[0] = (ObjectKeyframe) Keyframe.ofObject(0.0f, values[0]);
            for (int i = 1; i < numKeyframes; i++) {
                keyframes[i] = (ObjectKeyframe) Keyframe.ofObject(((float) i) / ((float) (numKeyframes - 1)), values[i]);
            }
        }
        return new KeyframeSet(keyframes);
    }

    public void setEvaluator(TypeEvaluator evaluator) {
        this.mEvaluator = evaluator;
    }

    public KeyframeSet clone() {
        ArrayList<Keyframe> keyframes = this.mKeyframes;
        int numKeyframes = this.mKeyframes.size();
        Keyframe[] newKeyframes = new Keyframe[numKeyframes];
        for (int i = 0; i < numKeyframes; i++) {
            newKeyframes[i] = ((Keyframe) keyframes.get(i)).clone();
        }
        return new KeyframeSet(newKeyframes);
    }

    public Object getValue(float fraction) {
        if (this.mNumKeyframes == 2) {
            if (this.mInterpolator != null) {
                fraction = this.mInterpolator.getInterpolation(fraction);
            }
            return this.mEvaluator.evaluate(fraction, this.mFirstKeyframe.getValue(), this.mLastKeyframe.getValue());
        } else if (fraction <= 0.0f) {
            Keyframe nextKeyframe = (Keyframe) this.mKeyframes.get(1);
            Interpolator interpolator = nextKeyframe.getInterpolator();
            if (interpolator != null) {
                fraction = interpolator.getInterpolation(fraction);
            }
            float prevFraction = this.mFirstKeyframe.getFraction();
            return this.mEvaluator.evaluate((fraction - prevFraction) / (nextKeyframe.getFraction() - prevFraction), this.mFirstKeyframe.getValue(), nextKeyframe.getValue());
        } else if (fraction >= 1.0f) {
            Keyframe prevKeyframe = (Keyframe) this.mKeyframes.get(this.mNumKeyframes - 2);
            Interpolator interpolator2 = this.mLastKeyframe.getInterpolator();
            if (interpolator2 != null) {
                fraction = interpolator2.getInterpolation(fraction);
            }
            float prevFraction2 = prevKeyframe.getFraction();
            return this.mEvaluator.evaluate((fraction - prevFraction2) / (this.mLastKeyframe.getFraction() - prevFraction2), prevKeyframe.getValue(), this.mLastKeyframe.getValue());
        } else {
            Keyframe prevKeyframe2 = this.mFirstKeyframe;
            for (int i = 1; i < this.mNumKeyframes; i++) {
                Keyframe nextKeyframe2 = (Keyframe) this.mKeyframes.get(i);
                if (fraction < nextKeyframe2.getFraction()) {
                    Interpolator interpolator3 = nextKeyframe2.getInterpolator();
                    if (interpolator3 != null) {
                        fraction = interpolator3.getInterpolation(fraction);
                    }
                    float prevFraction3 = prevKeyframe2.getFraction();
                    return this.mEvaluator.evaluate((fraction - prevFraction3) / (nextKeyframe2.getFraction() - prevFraction3), prevKeyframe2.getValue(), nextKeyframe2.getValue());
                }
                prevKeyframe2 = nextKeyframe2;
            }
            return this.mLastKeyframe.getValue();
        }
    }

    public String toString() {
        String returnVal = " ";
        for (int i = 0; i < this.mNumKeyframes; i++) {
            returnVal = new StringBuilder(String.valueOf(returnVal)).append(((Keyframe) this.mKeyframes.get(i)).getValue()).append("  ").toString();
        }
        return returnVal;
    }
}
