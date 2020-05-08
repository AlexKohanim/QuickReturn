package p006ti.modules.titanium.network.socket;

import org.appcelerator.kroll.KrollModule;

/* renamed from: ti.modules.titanium.network.socket.SocketModule */
public class SocketModule extends KrollModule {
    public static final int CLOSED = 4;
    public static final int CONNECTED = 2;
    public static final int ERROR = 5;
    public static final int INITIALIZED = 1;
    public static final int LISTENING = 3;

    public String getApiName() {
        return "Ti.Network.Socket";
    }
}
