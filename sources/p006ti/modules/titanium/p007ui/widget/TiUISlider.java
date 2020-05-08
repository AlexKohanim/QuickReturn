package p006ti.modules.titanium.p007ui.widget;

import android.graphics.Rect;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiFileHelper;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.view.TiUIView;

/* renamed from: ti.modules.titanium.ui.widget.TiUISlider */
public class TiUISlider extends TiUIView implements OnSeekBarChangeListener {
    private static final String TAG = "TiUISlider";
    private int max = 1;
    private int maxRange;
    private int min = 0;
    private int minRange;
    private int offset;
    private float pos = 0.0f;
    private ClipDrawable rightClipDrawable;
    private int scaleFactor;
    private SoftReference<Drawable> thumbDrawable;

    public TiUISlider(final TiViewProxy proxy) {
        super(proxy);
        Log.m29d(TAG, "Creating a seekBar", Log.DEBUG_MODE);
        SeekBar seekBar = new SeekBar(proxy.getActivity()) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                TiUIHelper.firePostLayoutEvent(proxy);
            }
        };
        seekBar.setOnSeekBarChangeListener(this);
        setNativeView(seekBar);
    }

    public void processProperties(KrollDict d) {
        super.processProperties(d);
        SeekBar seekBar = (SeekBar) getNativeView();
        if (d.containsKey(TiC.PROPERTY_VALUE)) {
            this.pos = TiConvert.toFloat(d, TiC.PROPERTY_VALUE, 0.0f);
        }
        if (d.containsKey(TiC.PROPERTY_MIN)) {
            this.min = TiConvert.toInt(d.get(TiC.PROPERTY_MIN), 0);
        }
        if (d.containsKey(TiC.PROPERTY_MAX)) {
            this.max = TiConvert.toInt(d.get(TiC.PROPERTY_MAX), 0);
        }
        if (d.containsKey("minRange")) {
            this.minRange = TiConvert.toInt(d.get("minRange"), 0);
        } else {
            this.minRange = this.min;
        }
        if (d.containsKey("maxRange")) {
            this.maxRange = TiConvert.toInt(d.get("maxRange"), 0);
        } else {
            this.maxRange = this.max;
        }
        if (d.containsKey("thumbImage")) {
            updateThumb(seekBar, d);
        }
        if (d.containsKey("leftTrackImage") || d.containsKey("rightTrackImage")) {
            updateTrackingImages(seekBar, d);
        }
        updateRange();
        updateControl();
        updateRightDrawable();
    }

    private void updateRightDrawable() {
        if (this.rightClipDrawable != null) {
            SeekBar seekBar = (SeekBar) getNativeView();
            this.rightClipDrawable.setLevel(10000 - ((int) Math.floor(10000.0d * (((double) seekBar.getProgress()) / ((double) seekBar.getMax())))));
        }
    }

    private void updateRange() {
        this.minRange = Math.max(this.minRange, this.min);
        this.minRange = Math.min(this.minRange, this.max);
        this.proxy.setProperty("minRange", Integer.valueOf(this.minRange));
        this.maxRange = Math.min(this.maxRange, this.max);
        this.maxRange = Math.max(this.maxRange, this.minRange);
        this.proxy.setProperty("maxRange", Integer.valueOf(this.maxRange));
    }

    private void updateControl() {
        this.offset = -this.min;
        this.scaleFactor = 100;
        int length = (int) Math.floor(Math.sqrt(Math.pow((double) (this.max - this.min), 2.0d)));
        if (length > 0 && ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED / length < this.scaleFactor) {
            this.scaleFactor = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED / length;
            this.scaleFactor = this.scaleFactor == 0 ? 1 : this.scaleFactor;
        }
        SeekBar seekBar = (SeekBar) getNativeView();
        int curPos = (int) Math.floor((double) (((float) this.scaleFactor) * (this.pos + ((float) this.offset))));
        seekBar.setMax(length * this.scaleFactor);
        seekBar.setProgress(curPos);
    }

    private void updateThumb(SeekBar seekBar, KrollDict d) {
        TiFileHelper tfh = null;
        String thumbImage = TiConvert.toString((HashMap<String, Object>) d, "thumbImage");
        if (thumbImage != null) {
            if (0 == 0) {
                tfh = new TiFileHelper(seekBar.getContext());
            }
            String url = this.proxy.resolveUrl(null, thumbImage);
            Drawable thumb = tfh.loadDrawable(url, false);
            if (thumb != null) {
                this.thumbDrawable = new SoftReference<>(thumb);
                seekBar.setThumb(thumb);
                return;
            }
            Log.m32e(TAG, "Unable to locate thumb image for progress bar: " + url);
            return;
        }
        seekBar.setThumb(null);
    }

    private void updateTrackingImages(SeekBar seekBar, KrollDict d) {
        LayerDrawable ld;
        String leftImage = TiConvert.toString((HashMap<String, Object>) d, "leftTrackImage");
        String rightImage = TiConvert.toString((HashMap<String, Object>) d, "rightTrackImage");
        Drawable leftDrawable = null;
        Drawable rightDrawable = null;
        TiFileHelper tfh = new TiFileHelper(seekBar.getContext());
        if (leftImage != null) {
            String leftUrl = this.proxy.resolveUrl(null, leftImage);
            if (leftUrl != null) {
                leftDrawable = tfh.loadDrawable(leftUrl, false, true);
                if (leftDrawable == null) {
                    Log.m32e(TAG, "Unable to locate left image for progress bar: " + leftUrl);
                }
            }
        }
        if (rightImage != null) {
            String rightUrl = this.proxy.resolveUrl(null, rightImage);
            if (rightUrl != null) {
                rightDrawable = tfh.loadDrawable(rightUrl, false, true);
                if (rightDrawable == null) {
                    Log.m32e(TAG, "Unable to locate right image for progress bar: " + rightUrl);
                }
            }
        }
        if (leftDrawable == null && rightDrawable == null) {
            Log.m44w(TAG, "Custom tracking images could not be loaded.");
            return;
        }
        if (rightDrawable == null) {
            ld = new LayerDrawable(new Drawable[]{new ClipDrawable(leftDrawable, 3, 1)});
            ld.setId(0, 16908301);
        } else if (leftDrawable == null) {
            this.rightClipDrawable = new ClipDrawable(rightDrawable, 5, 1);
            ld = new LayerDrawable(new Drawable[]{this.rightClipDrawable});
            ld.setId(0, 16908303);
        } else {
            ld = new LayerDrawable(new Drawable[]{rightDrawable, new ClipDrawable(leftDrawable, 3, 1)});
            ld.setId(0, 16908288);
            ld.setId(1, 16908301);
        }
        seekBar.setProgressDrawable(ld);
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        if (Log.isDebugModeEnabled()) {
            Log.m29d(TAG, "Property: " + key + " old: " + oldValue + " new: " + newValue, Log.DEBUG_MODE);
        }
        SeekBar seekBar = (SeekBar) getNativeView();
        if (key.equals(TiC.PROPERTY_VALUE)) {
            this.pos = TiConvert.toFloat(newValue);
            int curPos = (int) Math.floor((double) (((float) this.scaleFactor) * (this.pos + ((float) this.offset))));
            seekBar.setProgress(curPos);
            onProgressChanged(seekBar, curPos, true);
        } else if (key.equals(TiC.PROPERTY_MIN)) {
            this.min = TiConvert.toInt(newValue);
            this.minRange = this.min;
            updateRange();
            if (this.pos < ((float) this.minRange)) {
                this.pos = (float) this.minRange;
            }
            updateControl();
            onProgressChanged(seekBar, (int) Math.floor((double) (((float) this.scaleFactor) * (this.pos + ((float) this.offset)))), true);
        } else if (key.equals("minRange")) {
            this.minRange = TiConvert.toInt(newValue);
            updateRange();
            if (this.pos < ((float) this.minRange)) {
                this.pos = (float) this.minRange;
            }
            updateControl();
            onProgressChanged(seekBar, (int) Math.floor((double) (((float) this.scaleFactor) * (this.pos + ((float) this.offset)))), true);
        } else if (key.equals(TiC.PROPERTY_MAX)) {
            this.max = TiConvert.toInt(newValue);
            this.maxRange = this.max;
            updateRange();
            if (this.pos > ((float) this.maxRange)) {
                this.pos = (float) this.maxRange;
            }
            updateControl();
            onProgressChanged(seekBar, (int) Math.floor((double) (((float) this.scaleFactor) * (this.pos + ((float) this.offset)))), true);
        } else if (key.equals("maxRange")) {
            this.maxRange = TiConvert.toInt(newValue);
            updateRange();
            if (this.pos > ((float) this.maxRange)) {
                this.pos = (float) this.maxRange;
            }
            updateControl();
            onProgressChanged(seekBar, (int) Math.floor((double) (((float) this.scaleFactor) * (this.pos + ((float) this.offset)))), true);
        } else if (key.equals("thumbImage")) {
            Log.m36i(TAG, "Dynamically changing thumbImage is not yet supported. Native control doesn't draw");
        } else if (key.equals("leftTrackImage") || key.equals("rightTrackImage")) {
            Log.m36i(TAG, "Dynamically changing leftTrackImage or rightTrackImage is not yet supported. Native control doesn't draw");
        } else {
            super.propertyChanged(key, oldValue, newValue, proxy);
        }
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        this.pos = (((float) seekBar.getProgress()) * 1.0f) / ((float) this.scaleFactor);
        int actualMinRange = this.minRange + this.offset;
        int actualMaxRange = this.maxRange + this.offset;
        if (this.pos < ((float) actualMinRange)) {
            seekBar.setProgress(this.scaleFactor * actualMinRange);
            this.pos = (float) this.minRange;
        } else if (this.pos > ((float) actualMaxRange)) {
            seekBar.setProgress(this.scaleFactor * actualMaxRange);
            this.pos = (float) this.maxRange;
        }
        updateRightDrawable();
        Drawable thumb = this.thumbDrawable != null ? (Drawable) this.thumbDrawable.get() : null;
        KrollDict offset2 = new KrollDict();
        offset2.put("x", Integer.valueOf(0));
        offset2.put("y", Integer.valueOf(0));
        KrollDict size = new KrollDict();
        size.put(TiC.PROPERTY_WIDTH, Integer.valueOf(0));
        size.put(TiC.PROPERTY_HEIGHT, Integer.valueOf(0));
        if (thumb != null) {
            Rect thumbBounds = thumb.getBounds();
            if (thumbBounds != null) {
                offset2.put("x", Integer.valueOf(thumbBounds.left - seekBar.getThumbOffset()));
                offset2.put("y", Integer.valueOf(thumbBounds.top));
                size.put(TiC.PROPERTY_WIDTH, Integer.valueOf(thumbBounds.width()));
                size.put(TiC.PROPERTY_HEIGHT, Integer.valueOf(thumbBounds.height()));
            }
        }
        KrollDict data = new KrollDict();
        float scaledValue = scaledValue();
        Log.m29d(TAG, "Progress " + seekBar.getProgress() + " ScaleFactor " + this.scaleFactor + " Calculated Position " + this.pos + " ScaledValue " + scaledValue + " Min " + this.min + " Max" + this.max + " MinRange" + this.minRange + " MaxRange" + this.maxRange, Log.DEBUG_MODE);
        data.put(TiC.PROPERTY_VALUE, Float.valueOf(scaledValue));
        data.put(TiC.EVENT_PROPERTY_THUMB_OFFSET, offset2);
        data.put(TiC.EVENT_PROPERTY_THUMB_SIZE, size);
        this.proxy.setProperty(TiC.PROPERTY_VALUE, Float.valueOf(scaledValue));
        fireEvent("change", data);
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        KrollDict data = new KrollDict();
        data.put(TiC.PROPERTY_VALUE, Float.valueOf(scaledValue()));
        fireEvent("start", data, false);
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        KrollDict data = new KrollDict();
        data.put(TiC.PROPERTY_VALUE, Float.valueOf(scaledValue()));
        fireEvent("stop", data, false);
    }

    private float scaledValue() {
        return this.pos + ((float) this.min);
    }
}
