package org.appcelerator.titanium.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import java.io.Serializable;
import p006ti.modules.titanium.android.AndroidModule;

public class TiIntentWrapper implements Serializable {
    protected static final String ACTIVITY_PREFIX = "TA-";
    public static final String EXTRA_ACTIVITY_TYPE = "activityType";
    public static final String EXTRA_BACKGROUND_COLOR = "backgroundColor";
    public static final String EXTRA_BACKGROUND_IMAGE = "backgroundImage";
    public static final String EXTRA_ICON_URL = "iconUrl";
    public static final String EXTRA_IS_FULLSCREEN = "isFullscreen";
    public static final String EXTRA_ORIENTATION = "orientation";
    public static final String EXTRA_SHOW_ACTIVITY_ON_LOAD = "showActivityOnLoad";
    public static final String EXTRA_WINDOW_ID = "windowId";
    private static final long serialVersionUID = 1;
    private Intent intent;

    public TiIntentWrapper(Intent intent2) {
        if (intent2 == null) {
        }
        this.intent = intent2;
    }

    public static TiIntentWrapper createUsing(Intent prototype) {
        return createUsing(new TiIntentWrapper(prototype));
    }

    public static TiIntentWrapper createUsing(TiIntentWrapper prototype) {
        TiIntentWrapper result = new TiIntentWrapper(new Intent());
        result.setFullscreen(false);
        result.setActivityType("single");
        result.setShowActivityOnLoad(true);
        return result;
    }

    public static String createActivityName(String name) {
        return ACTIVITY_PREFIX + name;
    }

    public Intent getIntent() {
        return this.intent;
    }

    public String getWindowId() {
        return this.intent.getExtras().getString("windowId");
    }

    public void setWindowId(String id) {
        this.intent.putExtra("windowId", id);
    }

    public boolean isFullscreen() {
        Bundle b = this.intent.getExtras();
        if (b == null || b.get(EXTRA_IS_FULLSCREEN) == null) {
            return false;
        }
        return b.getBoolean(EXTRA_IS_FULLSCREEN);
    }

    public void setFullscreen(boolean fullscreen) {
        this.intent.putExtra(EXTRA_IS_FULLSCREEN, fullscreen);
    }

    public boolean isShowActivityOnLoad() {
        Bundle b = this.intent.getExtras();
        if (b == null || b.get(EXTRA_SHOW_ACTIVITY_ON_LOAD) == null) {
            return true;
        }
        return b.getBoolean(EXTRA_SHOW_ACTIVITY_ON_LOAD);
    }

    public void setShowActivityOnLoad(boolean showActivityOnLoad) {
        this.intent.putExtra(EXTRA_SHOW_ACTIVITY_ON_LOAD, showActivityOnLoad);
    }

    public String getIconUrl() {
        return this.intent.getExtras().getString(EXTRA_ICON_URL);
    }

    public void setIconUrl(String iconUrl) {
        this.intent.putExtra(EXTRA_ICON_URL, iconUrl);
    }

    public String getActivityType() {
        return this.intent.getExtras().getString(EXTRA_ACTIVITY_TYPE);
    }

    public void setActivityType(String activityType) {
        this.intent.putExtra(EXTRA_ACTIVITY_TYPE, activityType);
    }

    public String getTitle() {
        return this.intent.getExtras().getString(AndroidModule.EXTRA_TITLE);
    }

    public void setTitle(String title) {
        this.intent.putExtra(AndroidModule.EXTRA_TITLE, title);
    }

    public boolean hasBackgroundColor() {
        return this.intent.getExtras().containsKey("backgroundColor");
    }

    public int getBackgroundColor() {
        return this.intent.getExtras().getInt("backgroundColor");
    }

    public void setBackgroundColor(int color) {
        this.intent.putExtra("backgroundColor", color);
    }

    public void setBackgroundColor(String colorCode) {
        this.intent.putExtra("backgroundColor", TiColorHelper.parseColor(colorCode));
    }

    public String getOrientation() {
        return this.intent.getExtras().getString(EXTRA_ORIENTATION);
    }

    public void setOrientation(String orientation) {
        this.intent.putExtra(EXTRA_ORIENTATION, orientation);
    }

    public boolean hasBackgroundImage() {
        return this.intent.getExtras().containsKey("backgroundImage");
    }

    public String getBackgroundImage() {
        return this.intent.getExtras().getString("backgroundImage");
    }

    public void setBackgroundImage(String backgroundImage) {
        this.intent.putExtra("backgroundImage", backgroundImage);
    }

    public Uri getData() {
        return this.intent.getData();
    }

    public void setData(String url) {
        this.intent.setData(Uri.parse(url));
    }

    public boolean isAutoNamed() {
        if (getWindowId() != null) {
            return getWindowId().startsWith(ACTIVITY_PREFIX);
        }
        return true;
    }
}
