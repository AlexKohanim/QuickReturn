package android.support.p000v4.graphics.drawable;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.support.annotation.Nullable;

/* renamed from: android.support.v4.graphics.drawable.DrawableWrapperEclair */
class DrawableWrapperEclair extends DrawableWrapperDonut {

    /* renamed from: android.support.v4.graphics.drawable.DrawableWrapperEclair$DrawableWrapperStateEclair */
    private static class DrawableWrapperStateEclair extends DrawableWrapperState {
        DrawableWrapperStateEclair(@Nullable DrawableWrapperState orig, @Nullable Resources res) {
            super(orig, res);
        }

        public Drawable newDrawable(@Nullable Resources res) {
            return new DrawableWrapperEclair(this, res);
        }
    }

    DrawableWrapperEclair(Drawable drawable) {
        super(drawable);
    }

    DrawableWrapperEclair(DrawableWrapperState state, Resources resources) {
        super(state, resources);
    }

    /* access modifiers changed from: 0000 */
    public DrawableWrapperState mutateConstantState() {
        return new DrawableWrapperStateEclair(this.mState, null);
    }

    /* access modifiers changed from: protected */
    public Drawable newDrawableFromState(ConstantState state, Resources res) {
        return state.newDrawable(res);
    }
}
