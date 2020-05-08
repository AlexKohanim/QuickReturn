package p006ti.modules.titanium.p007ui;

import android.app.Activity;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.widget.TiUIImageView;

/* renamed from: ti.modules.titanium.ui.ImageViewProxy */
public class ImageViewProxy extends ViewProxy {
    public TiUIView createView(Activity activity) {
        return new TiUIImageView(this);
    }

    private TiUIImageView getImageView() {
        return (TiUIImageView) getOrCreateView();
    }

    public void start() {
        getImageView().start();
    }

    public void stop() {
        getImageView().stop();
    }

    public void pause() {
        getImageView().pause();
    }

    public void resume() {
        getImageView().resume();
    }

    public boolean getAnimating() {
        return getImageView().isAnimating();
    }

    public boolean getPaused() {
        return getImageView().isPaused();
    }

    public boolean getReverse() {
        return getImageView().isReverse();
    }

    public void setReverse(boolean reverse) {
        getImageView().setReverse(reverse);
    }

    public TiBlob toBlob() {
        return getImageView().toBlob();
    }

    public void setTintColor(String color) {
        getImageView().setTintColor(color);
    }

    public int getTintColor() {
        return getImageView().getTintColor();
    }

    public String getApiName() {
        return "Ti.UI.ImageView";
    }
}
