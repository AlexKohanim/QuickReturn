package p006ti.modules.titanium.media;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.p000v4.internal.view.SupportMenu;
import android.widget.TiVideoView8;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiLifecycle.OnLifecycleEvent;
import org.appcelerator.titanium.view.TiCompositeLayout;
import org.appcelerator.titanium.view.TiCompositeLayout.LayoutParams;

/* renamed from: ti.modules.titanium.media.TiVideoActivity */
public class TiVideoActivity extends Activity {
    private static final String TAG = "TiVideoActivity";
    protected TiCompositeLayout layout = null;
    private OnLifecycleEvent lifecycleListener = null;
    private Messenger proxyMessenger = null;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.m37i(TAG, "TiVideoActivity onCreate", Log.DEBUG_MODE);
        Intent intent = getIntent();
        this.proxyMessenger = (Messenger) intent.getParcelableExtra("messenger");
        if (intent.hasExtra("backgroundColor")) {
            getWindow().setBackgroundDrawable(new ColorDrawable(intent.getIntExtra("backgroundColor", SupportMenu.CATEGORY_MASK)));
        }
        this.layout = new TiCompositeLayout(this);
        this.layout.addView(new TiVideoView8(this), new LayoutParams());
        setContentView(this.layout);
        if (this.proxyMessenger != null) {
            Message msg = Message.obtain();
            msg.what = 101;
            msg.obj = this;
            try {
                this.proxyMessenger.send(msg);
            } catch (RemoteException e) {
                Log.m34e(TAG, "Failed to send 'activity available' message to proxy", (Throwable) e);
            }
        }
        Log.m37i(TAG, "exiting onCreate", Log.DEBUG_MODE);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        sendProxyMessage(102);
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        if (this.lifecycleListener != null) {
            this.lifecycleListener.onStart(this);
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        TiApplication.getInstance().setCurrentActivity(this, this);
        if (this.lifecycleListener != null) {
            this.lifecycleListener.onResume(this);
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        TiApplication.getInstance().setCurrentActivity(this, null);
        if (this.lifecycleListener != null) {
            this.lifecycleListener.onPause(this);
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        if (this.lifecycleListener != null) {
            this.lifecycleListener.onDestroy(this);
        }
    }

    private void sendProxyMessage(int messageId) {
        if (this.proxyMessenger != null) {
            Message msg = Message.obtain();
            msg.what = messageId;
            try {
                this.proxyMessenger.send(msg);
            } catch (RemoteException e) {
                Log.m44w(TAG, "VideoPlayerProxy no longer available: " + e.getMessage());
            }
        }
    }

    public void setOnLifecycleEventListener(OnLifecycleEvent listener) {
        this.lifecycleListener = listener;
    }
}
