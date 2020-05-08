package org.appcelerator.titanium.util;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class TiAnimationFactory {
    public static TiAnimationPair getAnimationFor(String style, int duration) {
        TiAnimationPair a = new TiAnimationPair();
        boolean needsDuration = true;
        if (style.equals("fade-in")) {
            a.f39in = new AlphaAnimation(0.0f, 1.0f);
            a.out = new AlphaAnimation(1.0f, 0.0f);
        } else if (style.equals("fade-out")) {
            a.f39in = new AlphaAnimation(1.0f, 0.0f);
            a.out = new AlphaAnimation(0.0f, 1.0f);
        } else if (style.equals("slide-from-left")) {
            a.f39in = new TranslateAnimation(1, -1.0f, 1, 0.0f, 1, 0.0f, 1, 0.0f);
            a.out = new TranslateAnimation(1, 0.0f, 1, 1.0f, 1, 0.0f, 1, 0.0f);
        } else if (style.equals("slide-from-top")) {
            a.f39in = new TranslateAnimation(1, 0.0f, 1, 0.0f, 2, -1.0f, 2, 0.0f);
            a.out = new TranslateAnimation(1, 0.0f, 1, 0.0f, 2, 0.0f, 2, 1.0f);
        } else if (style.equals("slide-from-right")) {
            a.f39in = new TranslateAnimation(1, 1.0f, 1, 0.0f, 1, 0.0f, 1, 0.0f);
            a.out = new TranslateAnimation(1, 0.0f, 1, -1.0f, 1, 0.0f, 1, 0.0f);
        } else if (style.equals("slide-from-bottom")) {
            a.f39in = new TranslateAnimation(1, 0.0f, 1, 0.0f, 2, 1.0f, 2, 0.0f);
            a.out = new TranslateAnimation(1, 0.0f, 1, 0.0f, 2, 0.0f, 2, -1.0f);
        } else if (style.equals("scale-in")) {
            a.f39in = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, 2, 0.5f, 2, 0.5f);
            a.out = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, 2, 0.5f, 2, 0.5f);
        } else if (style.equals("wink-in")) {
            needsDuration = false;
            int half = duration / 2;
            a.f39in = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, 2, 0.5f, 2, 0.5f);
            a.f39in.setStartOffset((long) ((half / 5) + half));
            a.f39in.setDuration((long) half);
            a.out = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, 2, 0.5f, 2, 0.5f);
            a.out.setDuration((long) half);
        } else if (style.equals("headlines")) {
            needsDuration = false;
            int half2 = duration / 2;
            int pause = half2 / 5;
            AnimationSet as = new AnimationSet(true);
            Animation t = new AlphaAnimation(0.0f, 1.0f);
            t.setDuration((long) half2);
            as.addAnimation(t);
            Animation t2 = new RotateAnimation(0.0f, -720.0f, 1, 0.5f, 1, 0.5f);
            t2.setDuration((long) half2);
            as.addAnimation(t2);
            Animation t3 = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, 2, 0.5f, 2, 0.5f);
            t3.setDuration((long) half2);
            as.addAnimation(t3);
            a.f39in = as;
            a.f39in.setStartOffset((long) (half2 + pause));
            AnimationSet as2 = new AnimationSet(true);
            Animation t4 = new AlphaAnimation(1.0f, 0.0f);
            t4.setDuration((long) half2);
            as2.addAnimation(t4);
            Animation t5 = new RotateAnimation(0.0f, 720.0f, 1, 0.5f, 1, 0.5f);
            t5.setDuration((long) half2);
            as2.addAnimation(t5);
            Animation t6 = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, 2, 0.5f, 2, 0.5f);
            t6.setDuration((long) half2);
            as2.addAnimation(t6);
            a.out = as2;
        }
        if (a != null) {
            if (a.f39in != null) {
                if (needsDuration) {
                    a.f39in.setDuration((long) duration);
                }
                a.f39in.setInterpolator(new AccelerateDecelerateInterpolator());
            }
            if (a.out != null) {
                if (needsDuration) {
                    a.out.setDuration((long) duration);
                }
                a.out.setInterpolator(new AccelerateDecelerateInterpolator());
            }
        }
        return a;
    }
}
