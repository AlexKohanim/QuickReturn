package android.support.p000v4.view;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.View;

/* renamed from: android.support.v4.view.ViewPropertyAnimatorCompatKK */
class ViewPropertyAnimatorCompatKK {
    ViewPropertyAnimatorCompatKK() {
    }

    public static void setUpdateListener(final View view, final ViewPropertyAnimatorUpdateListener listener) {
        AnimatorUpdateListener wrapped = null;
        if (listener != null) {
            wrapped = new AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    listener.onAnimationUpdate(view);
                }
            };
        }
        view.animate().setUpdateListener(wrapped);
    }
}