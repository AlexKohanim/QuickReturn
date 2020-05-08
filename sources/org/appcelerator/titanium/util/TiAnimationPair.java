package org.appcelerator.titanium.util;

import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ViewAnimator;

public class TiAnimationPair {

    /* renamed from: in */
    Animation f39in;
    Animation out;

    public void apply(ViewAnimator layout) {
        layout.setInAnimation(this.f39in);
        layout.setOutAnimation(this.out);
    }

    public void setAnimationListener(AnimationListener listener) {
        this.f39in.setAnimationListener(listener);
    }
}
