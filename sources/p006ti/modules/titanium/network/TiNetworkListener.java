package p006ti.modules.titanium.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import org.appcelerator.kroll.common.Log;

/* renamed from: ti.modules.titanium.network.TiNetworkListener */
public class TiNetworkListener {
    public static final String EXTRA_CONNECTED = "connected";
    public static final String EXTRA_FAILOVER = "failover";
    public static final String EXTRA_NETWORK_TYPE = "networkType";
    public static final String EXTRA_NETWORK_TYPE_NAME = "networkTypeName";
    public static final String EXTRA_REASON = "reason";
    private static final String TAG = "TiNetListener";
    private IntentFilter connectivityIntentFilter;
    private Context context;
    private boolean listening;
    /* access modifiers changed from: private */
    public Handler messageHandler;
    private ConnectivityBroadcastReceiver receiver = new ConnectivityBroadcastReceiver();

    /* renamed from: ti.modules.titanium.network.TiNetworkListener$ConnectivityBroadcastReceiver */
    private class ConnectivityBroadcastReceiver extends BroadcastReceiver {
        private ConnectivityBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            boolean z;
            if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                if (TiNetworkListener.this.messageHandler == null) {
                    Log.m44w(TiNetworkListener.TAG, "Network receiver is active but no handler has been set.");
                    return;
                }
                boolean noConnectivity = intent.getBooleanExtra("noConnectivity", false);
                NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra("otherNetwork");
                String reason = intent.getStringExtra("reason");
                boolean failover = intent.getBooleanExtra("isFailover", false);
                Log.m29d(TiNetworkListener.TAG, "onReceive(): mNetworkInfo=" + networkInfo + " mOtherNetworkInfo = " + (otherNetworkInfo == null ? "[none]" : otherNetworkInfo + " noConn=" + noConnectivity), Log.DEBUG_MODE);
                Message message = Message.obtain(TiNetworkListener.this.messageHandler);
                Bundle b = message.getData();
                String str = TiNetworkListener.EXTRA_CONNECTED;
                if (!noConnectivity) {
                    z = true;
                } else {
                    z = false;
                }
                b.putBoolean(str, z);
                b.putInt(TiNetworkListener.EXTRA_NETWORK_TYPE, networkInfo.getType());
                if (noConnectivity) {
                    b.putString(TiNetworkListener.EXTRA_NETWORK_TYPE_NAME, "NONE");
                } else {
                    b.putString(TiNetworkListener.EXTRA_NETWORK_TYPE_NAME, networkInfo.getTypeName());
                }
                b.putBoolean(TiNetworkListener.EXTRA_FAILOVER, failover);
                b.putString("reason", reason);
                message.sendToTarget();
            }
        }
    }

    public TiNetworkListener(Handler messageHandler2) {
        this.messageHandler = messageHandler2;
        this.connectivityIntentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    }

    public void attach(Context context2) {
        if (this.listening) {
            Log.m44w(TAG, "Connectivity listener is already attached");
        } else if (this.context == null) {
            this.context = context2;
            context2.registerReceiver(this.receiver, this.connectivityIntentFilter);
            this.listening = true;
        } else {
            throw new IllegalStateException("Context was not cleaned up from last release.");
        }
    }

    public void detach() {
        if (this.listening) {
            this.context.unregisterReceiver(this.receiver);
            this.context = null;
            this.listening = false;
        }
    }
}
