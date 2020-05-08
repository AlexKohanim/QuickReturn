package android.support.p000v4.graphics.drawable;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/* renamed from: android.support.v4.graphics.drawable.DrawableWrapperHoneycomb */
class DrawableWrapperHoneycomb extends DrawableWrapperDonut {

    /* renamed from: android.support.v4.graphics.drawable.DrawableWrapperHoneycomb$DrawableWrapperStateHoneycomb */
    private static class DrawableWrapperStateHoneycomb extends DrawableWrapperState {
        DrawableWrapperStateHoneycomb(@Nullable DrawableWrapperState orig, @Nullable Resources res) {
            super(orig, res);
        }

        public Drawable newDrawable(@Nullable Resources res) {
            return new DrawableWrapperHoneycomb(this, res);
        }
    }

    DrawableWrapperHoneycomb(Drawable drawable) {
        super(drawable);
    }

    DrawableWrapperHoneycomb(DrawableWrapperState state, Resources resources) {
        super(state, resources);
    }

    public void jumpToCurrentState() {
        this.mDrawable.jumpToCurrentState();
    }

    /* access modifiers changed from: 0000 */
    @NonNull
    public DrawableWrapperState mutateConstantState() {
        return new DrawableWrapperStateHoneycomb(this.mState, null);
    }
}
