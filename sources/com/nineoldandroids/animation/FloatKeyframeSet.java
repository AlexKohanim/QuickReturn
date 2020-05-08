package com.nineoldandroids.animation;

import android.view.animation.Interpolator;
import java.util.ArrayList;

class FloatKeyframeSet extends KeyframeSet {
    private float deltaValue;
    private boolean firstTime = true;
    private float firstValue;
    private float lastValue;

    public FloatKeyframeSet(FloatKeyframe... keyframes) {
        super(keyframes);
    }

    public Object getValue(float fraction) {
        return Float.valueOf(getFloatValue(fraction));
    }

    public FloatKeyframeSet clone() {
        ArrayList<Keyframe> keyframes = this.mKeyframes;
        int numKeyframes = this.mKeyframes.size();
        FloatKeyframe[] newKeyframes = new FloatKeyframe[numKeyframes];
        for (int i = 0; i < numKeyframes; i++) {
            newKeyframes[i] = (FloatKeyframe) ((Keyframe) keyframes.get(i)).clone();
        }
        return new FloatKeyframeSet(newKeyframes);
    }

    public float getFloatValue(float fraction) {
        if (this.mNumKeyframes == 2) {
            if (this.firstTime) {
                this.firstTime = false;
                this.firstValue = ((FloatKeyframe) this.mKeyframes.get(0)).getFloatValue();
                this.lastValue = ((FloatKeyframe) this.mKeyframes.get(1)).getFloatValue();
                this.deltaValue = this.lastValue - this.firstValue;
            }
            if (this.mInterpolator != null) {
                fraction = this.mInterpolator.getInterpolation(fraction);
            }
            if (this.mEvaluator == null) {
                return this.firstValue + (this.deltaValue * fraction);
            }
            return ((Number) this.mEvaluator.evaluate(fraction, Float.valueOf(this.firstValue), Float.valueOf(this.lastValue))).floatValue();
        } else if (fraction <= 0.0f) {
            FloatKeyframe prevKeyframe = (FloatKeyframe) this.mKeyframes.get(0);
            FloatKeyframe nextKeyframe = (FloatKeyframe) this.mKeyframes.get(1);
            float prevValue = prevKeyframe.getFloatValue();
            float nextValue = nextKeyframe.getFloatValue();
            float prevFraction = prevKeyframe.getFraction();
            float nextFraction = nextKeyframe.getFraction();
            Interpolator interpolator = nextKeyframe.getInterpolator();
            if (interpolator != null) {
                fraction = interpolator.getInterpolation(fraction);
            }
            float intervalFraction = (fraction - prevFraction) / (nextFraction - prevFraction);
            if (this.mEvaluator == null) {
                return ((nextValue - prevValue) * intervalFraction) + prevValue;
            }
            return ((Number) this.mEvaluator.evaluate(intervalFraction, Float.valueOf(prevValue), Float.valueOf(nextValue))).floatValue();
        } else if (fraction >= 1.0f) {
            FloatKeyframe prevKeyframe2 = (FloatKeyframe) this.mKeyframes.get(this.mNumKeyframes - 2);
            FloatKeyframe nextKeyframe2 = (FloatKeyframe) this.mKeyframes.get(this.mNumKeyframes - 1);
            float prevValue2 = prevKeyframe2.getFloatValue();
            float nextValue2 = nextKeyframe2.getFloatValue();
            float prevFraction2 = prevKeyframe2.getFraction();
            float nextFraction2 = nextKeyframe2.getFraction();
            Interpolator interpolator2 = nextKeyframe2.getInterpolator();
            if (interpolator2 != null) {
                fraction = interpolator2.getInterpolation(fraction);
            }
            float intervalFraction2 = (fraction - prevFraction2) / (nextFraction2 - prevFraction2);
            if (this.mEvaluator == null) {
                return ((nextValue2 - prevValue2) * intervalFraction2) + prevValue2;
            }
            return ((Number) this.mEvaluator.evaluate(intervalFraction2, Float.valueOf(prevValue2), Float.valueOf(nextValue2))).floatValue();
        } else {
            FloatKeyframe prevKeyframe3 = (FloatKeyframe) this.mKeyframes.get(0);
            for (int i = 1; i < this.mNumKeyframes; i++) {
                FloatKeyframe nextKeyframe3 = (FloatKeyframe) this.mKeyframes.get(i);
                if (fraction < nextKeyframe3.getFraction()) {
                    Interpolator interpolator3 = nextKeyframe3.getInterpolator();
                    if (interpolator3 != null) {
                        fraction = interpolator3.getInterpolation(fraction);
                    }
                    float intervalFraction3 = (fraction - prevKeyframe3.getFraction()) / (nextKeyframe3.getFraction() - prevKeyframe3.getFraction());
                    float prevValue3 = prevKeyframe3.getFloatValue();
                    float nextValue3 = nextKeyframe3.getFloatValue();
                    if (this.mEvaluator == null) {
                        return ((nextValue3 - prevValue3) * intervalFraction3) + prevValue3;
                    }
                    return ((Number) this.mEvaluator.evaluate(intervalFraction3, Float.valueOf(prevValue3), Float.valueOf(nextValue3))).floatValue();
                }
                prevKeyframe3 = nextKeyframe3;
            }
            return ((Number) ((Keyframe) this.mKeyframes.get(this.mNumKeyframes - 1)).getValue()).floatValue();
        }
    }
}
