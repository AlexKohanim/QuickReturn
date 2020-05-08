package org.appcelerator.titanium.proxy;

import android.graphics.drawable.Drawable;
import android.os.Message;
import android.support.p003v7.app.ActionBar;
import android.support.p003v7.app.AppCompatActivity;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiFileHelper;
import org.appcelerator.titanium.util.TiUrl;

public class ActionBarProxy extends KrollProxy {
    private static final String BACKGROUND_IMAGE = "backgroundImage";
    private static final String HOME_BUTTON_ENABLED = "homeButtonEnabled";
    private static final String ICON = "icon";
    private static final String LOGO = "logo";
    private static final int MSG_DISPLAY_HOME_AS_UP = 312;
    private static final int MSG_FIRST_ID = 212;
    private static final int MSG_HIDE = 316;
    private static final int MSG_SET_BACKGROUND_IMAGE = 313;
    private static final int MSG_SET_DISPLAY_SHOW_HOME = 322;
    private static final int MSG_SET_DISPLAY_SHOW_TITLE = 323;
    private static final int MSG_SET_HOME_BUTTON_ENABLED = 319;
    private static final int MSG_SET_ICON = 318;
    private static final int MSG_SET_LOGO = 317;
    private static final int MSG_SET_NAVIGATION_MODE = 320;
    private static final int MSG_SET_SUBTITLE = 321;
    private static final int MSG_SET_TITLE = 314;
    private static final int MSG_SHOW = 315;
    private static final String NAVIGATION_MODE = "navigationMode";
    private static final String SHOW_HOME_AS_UP = "showHomeAsUp";
    private static final String TAG = "ActionBarProxy";
    private static final String TITLE = "title";
    private ActionBar actionBar;
    private boolean showTitleEnabled = true;

    public ActionBarProxy(AppCompatActivity activity) {
        this.actionBar = activity.getSupportActionBar();
    }

    public void setDisplayHomeAsUp(boolean showHomeAsUp) {
        if (TiApplication.isUIThread()) {
            handlesetDisplayHomeAsUp(showHomeAsUp);
            return;
        }
        Message message = getMainHandler().obtainMessage(MSG_DISPLAY_HOME_AS_UP, Boolean.valueOf(showHomeAsUp));
        message.getData().putBoolean(SHOW_HOME_AS_UP, showHomeAsUp);
        message.sendToTarget();
    }

    public void setHomeButtonEnabled(boolean homeButtonEnabled) {
        if (TiApplication.isUIThread()) {
            handlesetHomeButtonEnabled(homeButtonEnabled);
            return;
        }
        Message message = getMainHandler().obtainMessage(MSG_SET_HOME_BUTTON_ENABLED, Boolean.valueOf(homeButtonEnabled));
        message.getData().putBoolean(HOME_BUTTON_ENABLED, homeButtonEnabled);
        message.sendToTarget();
    }

    public void setNavigationMode(int navigationMode) {
        if (TiApplication.isUIThread()) {
            handlesetNavigationMode(navigationMode);
            return;
        }
        Message message = getMainHandler().obtainMessage(MSG_SET_NAVIGATION_MODE, Integer.valueOf(navigationMode));
        message.getData().putInt(NAVIGATION_MODE, navigationMode);
        message.sendToTarget();
    }

    public void setBackgroundImage(String url) {
        if (TiApplication.isUIThread()) {
            handleSetBackgroundImage(url);
            return;
        }
        Message message = getMainHandler().obtainMessage(MSG_SET_BACKGROUND_IMAGE, url);
        message.getData().putString("backgroundImage", url);
        message.sendToTarget();
    }

    public void setTitle(String title) {
        if (TiApplication.isUIThread()) {
            handleSetTitle(title);
            return;
        }
        Message message = getMainHandler().obtainMessage(MSG_SET_TITLE, title);
        message.getData().putString("title", title);
        message.sendToTarget();
    }

    public void setSubtitle(String subTitle) {
        if (TiApplication.isUIThread()) {
            handleSetSubTitle(subTitle);
            return;
        }
        Message message = getMainHandler().obtainMessage(MSG_SET_SUBTITLE, subTitle);
        message.getData().putString(TiC.PROPERTY_SUBTITLE, subTitle);
        message.sendToTarget();
    }

    public void setDisplayShowHomeEnabled(boolean show) {
        if (this.actionBar != null) {
            if (TiApplication.isUIThread()) {
                this.actionBar.setDisplayShowHomeEnabled(show);
            } else {
                getMainHandler().obtainMessage(MSG_SET_DISPLAY_SHOW_HOME, Boolean.valueOf(show)).sendToTarget();
            }
        }
    }

    public void setDisplayShowTitleEnabled(boolean show) {
        if (this.actionBar != null) {
            if (TiApplication.isUIThread()) {
                this.actionBar.setDisplayShowTitleEnabled(show);
                this.showTitleEnabled = show;
                return;
            }
            getMainHandler().obtainMessage(MSG_SET_DISPLAY_SHOW_TITLE, Boolean.valueOf(show)).sendToTarget();
        }
    }

    public String getSubtitle() {
        if (this.actionBar == null) {
            return null;
        }
        return (String) this.actionBar.getSubtitle();
    }

    public String getTitle() {
        if (this.actionBar == null) {
            return null;
        }
        return (String) this.actionBar.getTitle();
    }

    public int getNavigationMode() {
        if (this.actionBar == null) {
            return 0;
        }
        return this.actionBar.getNavigationMode();
    }

    public void show() {
        if (TiApplication.isUIThread()) {
            handleShow();
        } else {
            getMainHandler().obtainMessage(MSG_SHOW).sendToTarget();
        }
    }

    public void hide() {
        if (TiApplication.isUIThread()) {
            handleHide();
        } else {
            getMainHandler().obtainMessage(MSG_HIDE).sendToTarget();
        }
    }

    public void setLogo(String url) {
        if (TiApplication.isUIThread()) {
            handleSetLogo(url);
            return;
        }
        Message message = getMainHandler().obtainMessage(MSG_SET_LOGO, url);
        message.getData().putString(LOGO, url);
        message.sendToTarget();
    }

    public void setIcon(String url) {
        if (TiApplication.isUIThread()) {
            handleSetIcon(url);
            return;
        }
        Message message = getMainHandler().obtainMessage(MSG_SET_ICON, url);
        message.getData().putString("icon", url);
        message.sendToTarget();
    }

    private void handleSetIcon(String url) {
        if (this.actionBar == null) {
            Log.m44w(TAG, "ActionBar is not enabled");
            return;
        }
        Drawable icon = getDrawableFromUrl(url);
        if (icon != null) {
            this.actionBar.setIcon(icon);
        }
    }

    private void handleSetTitle(String title) {
        if (this.actionBar != null) {
            this.actionBar.setTitle((CharSequence) title);
        } else {
            Log.m44w(TAG, "ActionBar is not enabled");
        }
    }

    private void handleSetSubTitle(String subTitle) {
        if (this.actionBar != null) {
            this.actionBar.setDisplayShowTitleEnabled(true);
            this.actionBar.setSubtitle((CharSequence) subTitle);
            return;
        }
        Log.m44w(TAG, "ActionBar is not enabled");
    }

    private void handleShow() {
        if (this.actionBar != null) {
            this.actionBar.show();
        } else {
            Log.m44w(TAG, "ActionBar is not enabled");
        }
    }

    private void handleHide() {
        if (this.actionBar != null) {
            this.actionBar.hide();
        } else {
            Log.m44w(TAG, "ActionBar is not enabled");
        }
    }

    private void handleSetBackgroundImage(String url) {
        if (this.actionBar == null) {
            Log.m44w(TAG, "ActionBar is not enabled");
            return;
        }
        Drawable backgroundImage = getDrawableFromUrl(url);
        if (backgroundImage != null) {
            this.actionBar.setDisplayShowTitleEnabled(!this.showTitleEnabled);
            this.actionBar.setDisplayShowTitleEnabled(this.showTitleEnabled);
            this.actionBar.setBackgroundDrawable(backgroundImage);
        }
    }

    private void handlesetDisplayHomeAsUp(boolean showHomeAsUp) {
        if (this.actionBar != null) {
            this.actionBar.setDisplayHomeAsUpEnabled(showHomeAsUp);
        } else {
            Log.m44w(TAG, "ActionBar is not enabled");
        }
    }

    private void handlesetHomeButtonEnabled(boolean homeButtonEnabled) {
        if (this.actionBar != null) {
            this.actionBar.setHomeButtonEnabled(homeButtonEnabled);
        } else {
            Log.m44w(TAG, "ActionBar is not enabled");
        }
    }

    private void handlesetNavigationMode(int navigationMode) {
        this.actionBar.setNavigationMode(navigationMode);
    }

    private void handleSetLogo(String url) {
        if (this.actionBar == null) {
            Log.m44w(TAG, "ActionBar is not enabled");
            return;
        }
        Drawable logo = getDrawableFromUrl(url);
        if (logo != null) {
            this.actionBar.setLogo(logo);
        }
    }

    private Drawable getDrawableFromUrl(String url) {
        return new TiFileHelper(TiApplication.getInstance()).loadDrawable(new TiUrl(url).resolve(), false);
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_DISPLAY_HOME_AS_UP /*312*/:
                handlesetDisplayHomeAsUp(msg.getData().getBoolean(SHOW_HOME_AS_UP));
                return true;
            case MSG_SET_BACKGROUND_IMAGE /*313*/:
                handleSetBackgroundImage(msg.getData().getString("backgroundImage"));
                return true;
            case MSG_SET_TITLE /*314*/:
                handleSetTitle(msg.getData().getString("title"));
                return true;
            case MSG_SHOW /*315*/:
                handleShow();
                return true;
            case MSG_HIDE /*316*/:
                handleHide();
                return true;
            case MSG_SET_LOGO /*317*/:
                handleSetLogo(msg.getData().getString(LOGO));
                return true;
            case MSG_SET_ICON /*318*/:
                handleSetIcon(msg.getData().getString("icon"));
                return true;
            case MSG_SET_HOME_BUTTON_ENABLED /*319*/:
                handlesetHomeButtonEnabled(msg.getData().getBoolean(HOME_BUTTON_ENABLED));
                return true;
            case MSG_SET_NAVIGATION_MODE /*320*/:
                handlesetNavigationMode(msg.getData().getInt(NAVIGATION_MODE));
                return true;
            case MSG_SET_SUBTITLE /*321*/:
                handleSetSubTitle(msg.getData().getString(TiC.PROPERTY_SUBTITLE));
                return true;
            case MSG_SET_DISPLAY_SHOW_HOME /*322*/:
                boolean show = TiConvert.toBoolean(msg.obj, true);
                if (this.actionBar == null) {
                    return true;
                }
                this.actionBar.setDisplayShowHomeEnabled(show);
                return true;
            case MSG_SET_DISPLAY_SHOW_TITLE /*323*/:
                boolean show2 = TiConvert.toBoolean(msg.obj, true);
                if (this.actionBar == null) {
                    return true;
                }
                this.actionBar.setDisplayShowTitleEnabled(show2);
                this.showTitleEnabled = show2;
                return true;
            default:
                return super.handleMessage(msg);
        }
    }

    public void onPropertyChanged(String name, Object value) {
        if (TiC.PROPERTY_ON_HOME_ICON_ITEM_SELECTED.equals(name)) {
            if (TiApplication.isUIThread()) {
                this.actionBar.setHomeButtonEnabled(true);
            } else {
                Message message = getMainHandler().obtainMessage(MSG_SET_HOME_BUTTON_ENABLED, Boolean.valueOf(true));
                message.getData().putBoolean(HOME_BUTTON_ENABLED, true);
                message.sendToTarget();
            }
        }
        super.onPropertyChanged(name, value);
    }

    public String getApiName() {
        return "Ti.Android.ActionBar";
    }
}
