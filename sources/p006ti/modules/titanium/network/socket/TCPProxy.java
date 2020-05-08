package p006ti.modules.titanium.network.socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.p005io.TiStream;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiStreamHelper;
import p006ti.modules.titanium.BufferProxy;
import p006ti.modules.titanium.network.TiNetworkListener;

/* renamed from: ti.modules.titanium.network.socket.TCPProxy */
public class TCPProxy extends KrollProxy implements TiStream {
    private static final String TAG = "TCPProxy";
    /* access modifiers changed from: private */
    public KrollDict acceptOptions;
    /* access modifiers changed from: private */
    public boolean accepting;
    /* access modifiers changed from: private */
    public Socket clientSocket;
    private InputStream inputStream;
    /* access modifiers changed from: private */
    public ServerSocket serverSocket;
    /* access modifiers changed from: private */
    public int state;

    /* renamed from: ti.modules.titanium.network.socket.TCPProxy$ConnectedSocketThread */
    private class ConnectedSocketThread extends Thread {
        public ConnectedSocketThread() {
            super("ConnectedSocketThread");
        }

        public void run() {
            String host = TiConvert.toString(TCPProxy.this.getProperty("host"));
            Object timeoutProperty = TCPProxy.this.getProperty(TiC.PROPERTY_TIMEOUT);
            if (timeoutProperty != null) {
                try {
                    int timeout = TiConvert.toInt(timeoutProperty, 0);
                    TCPProxy.this.clientSocket = new Socket();
                    TCPProxy.this.clientSocket.connect(new InetSocketAddress(host, TiConvert.toInt(TCPProxy.this.getProperty("port"))), timeout);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    TCPProxy.this.updateState(5, "error", TCPProxy.this.buildErrorCallbackArgs("Unable to connect, unknown host <" + host + ">", 0));
                    return;
                } catch (IOException e2) {
                    e2.printStackTrace();
                    TCPProxy.this.updateState(5, "error", TCPProxy.this.buildErrorCallbackArgs("Unable to connect, IO error", 0));
                    return;
                }
            } else {
                TCPProxy.this.clientSocket = new Socket(host, TiConvert.toInt(TCPProxy.this.getProperty("port")));
            }
            TCPProxy.this.updateState(2, TiNetworkListener.EXTRA_CONNECTED, TCPProxy.this.buildConnectedCallbackArgs());
        }
    }

    /* renamed from: ti.modules.titanium.network.socket.TCPProxy$ListeningSocketThread */
    private class ListeningSocketThread extends Thread {
        public ListeningSocketThread() {
            super("ListeningSocketThread");
        }

        public void run() {
            while (true) {
                if (TCPProxy.this.accepting) {
                    try {
                        if (TCPProxy.this.serverSocket != null) {
                            Socket acceptedSocket = TCPProxy.this.serverSocket.accept();
                            TCPProxy acceptedTcpProxy = new TCPProxy();
                            acceptedTcpProxy.clientSocket = acceptedSocket;
                            acceptedTcpProxy.setProperty("host", acceptedTcpProxy.clientSocket.getInetAddress().getHostAddress());
                            acceptedTcpProxy.setProperty("port", Integer.valueOf(acceptedTcpProxy.clientSocket.getPort()));
                            Object optionValue = TCPProxy.this.acceptOptions.get(TiC.PROPERTY_TIMEOUT);
                            if (optionValue != null) {
                                acceptedTcpProxy.setProperty(TiC.PROPERTY_TIMEOUT, Integer.valueOf(TiConvert.toInt(optionValue, 0)));
                            }
                            Object optionValue2 = TCPProxy.this.acceptOptions.get("error");
                            if (optionValue2 != null && (optionValue2 instanceof KrollFunction)) {
                                acceptedTcpProxy.setProperty("error", (KrollFunction) optionValue2);
                            }
                            acceptedTcpProxy.state = 2;
                            Object callback = TCPProxy.this.getProperty("accepted");
                            if (callback instanceof KrollFunction) {
                                ((KrollFunction) callback).callAsync(TCPProxy.this.getKrollObject(), (HashMap) TCPProxy.this.buildAcceptedCallbackArgs(acceptedTcpProxy));
                            }
                            TCPProxy.this.accepting = false;
                        } else {
                            return;
                        }
                    } catch (IOException e) {
                        if (TCPProxy.this.state == 3) {
                            e.printStackTrace();
                            TCPProxy.this.updateState(5, "error", TCPProxy.this.buildErrorCallbackArgs("Unable to accept new connection, IO error", 0));
                            return;
                        }
                        return;
                    }
                } else {
                    try {
                        sleep(500);
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                        Log.m32e(TCPProxy.TAG, "Listening thread interrupted");
                    }
                }
            }
        }
    }

    public TCPProxy() {
        this.clientSocket = null;
        this.serverSocket = null;
        this.accepting = false;
        this.acceptOptions = null;
        this.state = 0;
        this.inputStream = null;
        this.state = 1;
    }

    public void connect() throws Exception {
        if (this.state == 3 || this.state == 2) {
            throw new Exception("Unable to call connect on socket in <" + this.state + "> state");
        }
        Object host = getProperty("host");
        Object port = getProperty("port");
        if (host == null || port == null || TiConvert.toInt(port) <= 0) {
            throw new IllegalArgumentException("Unable to call connect, socket must have a valid host and port");
        }
        new ConnectedSocketThread().start();
    }

    public void listen() throws Exception {
        if (this.state == 3 || this.state == 2) {
            throw new Exception("Unable to call listen on socket in <" + this.state + "> state");
        }
        Object port = getProperty("port");
        Object listenQueueSize = getProperty("listenQueueSize");
        if (port != null && listenQueueSize != null) {
            try {
                this.serverSocket = new ServerSocket(TiConvert.toInt(port), TiConvert.toInt(listenQueueSize));
            } catch (IOException e) {
                e.printStackTrace();
                this.state = 5;
                throw new Exception("Unable to listen, IO error");
            }
        } else if (port != null) {
            this.serverSocket = new ServerSocket(TiConvert.toInt(port));
        } else {
            this.serverSocket = new ServerSocket();
        }
        new ListeningSocketThread().start();
        this.state = 3;
    }

    public void accept(KrollDict acceptOptions2) throws Exception {
        if (this.state != 3) {
            throw new Exception("Socket is not listening, unable to call accept");
        }
        this.acceptOptions = acceptOptions2;
        this.accepting = true;
    }

    private void closeSocket() throws IOException {
        if (this.clientSocket != null) {
            this.clientSocket.close();
            this.clientSocket = null;
        }
        if (this.serverSocket != null) {
            this.serverSocket.close();
            this.serverSocket = null;
        }
    }

    public void setHost(String host) {
        setSocketProperty("host", host);
    }

    public void setPort(int port) {
        setSocketProperty("port", Integer.valueOf(port));
    }

    public void setTimeout(int timeout) {
        setSocketProperty(TiC.PROPERTY_TIMEOUT, Integer.valueOf(timeout));
    }

    public void setOptions(KrollDict options) {
        Log.m36i(TAG, "setting options on socket is not supported yet");
    }

    public void setListenQueueSize(int listenQueueSize) {
        setSocketProperty("listenQueueSize", Integer.valueOf(listenQueueSize));
    }

    public void setConnected(KrollFunction connected) {
        setSocketProperty(TiNetworkListener.EXTRA_CONNECTED, connected);
    }

    public void setError(KrollFunction error) {
        setSocketProperty("error", error);
    }

    public void setAccepted(KrollFunction accepted) {
        setSocketProperty("accepted", accepted);
    }

    private void setSocketProperty(String propertyName, Object propertyValue) {
        if (this.state == 3 || this.state == 2) {
            Log.m32e(TAG, "Unable to set property <" + propertyName + "> on socket in <" + this.state + "> state");
        } else {
            setProperty(propertyName, propertyValue);
        }
    }

    public int getState() {
        return this.state;
    }

    /* access modifiers changed from: private */
    public KrollDict buildConnectedCallbackArgs() {
        KrollDict callbackArgs = new KrollDict();
        callbackArgs.put("socket", this);
        return callbackArgs;
    }

    /* access modifiers changed from: private */
    public KrollDict buildErrorCallbackArgs(String error, int errorCode) {
        KrollDict callbackArgs = new KrollDict();
        callbackArgs.put("socket", this);
        callbackArgs.putCodeAndMessage(errorCode, error);
        callbackArgs.put("errorCode", Integer.valueOf(errorCode));
        return callbackArgs;
    }

    /* access modifiers changed from: private */
    public KrollDict buildAcceptedCallbackArgs(TCPProxy acceptedTcpProxy) {
        KrollDict callbackArgs = new KrollDict();
        callbackArgs.put("socket", this);
        callbackArgs.put("inbound", acceptedTcpProxy);
        return callbackArgs;
    }

    public void updateState(int state2, String callbackName, KrollDict callbackArgs) {
        this.state = state2;
        if (state2 == 5) {
            try {
                if (this.clientSocket != null) {
                    this.clientSocket.close();
                }
                if (this.serverSocket != null) {
                    this.serverSocket.close();
                }
            } catch (IOException e) {
                Log.m45w(TAG, "Unable to close socket in error state", Log.DEBUG_MODE);
            }
        }
        Object callback = getProperty(callbackName);
        if (callback instanceof KrollFunction) {
            ((KrollFunction) callback).callAsync(getKrollObject(), (HashMap) callbackArgs);
        }
    }

    public boolean isConnected() {
        if (this.state == 2) {
            return true;
        }
        return false;
    }

    public int read(Object[] args) throws IOException {
        if (!isConnected()) {
            throw new IOException("Unable to read from socket, not connected");
        }
        BufferProxy bufferProxy = null;
        int offset = 0;
        int length = 0;
        if (args.length == 1 || args.length == 3) {
            if (args.length > 0) {
                if (args[0] instanceof BufferProxy) {
                    bufferProxy = args[0];
                    length = bufferProxy.getLength();
                } else {
                    throw new IllegalArgumentException("Invalid buffer argument");
                }
            }
            if (args.length == 3) {
                if (args[1] instanceof Integer) {
                    offset = args[1].intValue();
                } else if (args[1] instanceof Double) {
                    offset = args[1].intValue();
                } else {
                    throw new IllegalArgumentException("Invalid offset argument");
                }
                if (args[2] instanceof Integer) {
                    length = args[2].intValue();
                } else if (args[2] instanceof Double) {
                    length = args[2].intValue();
                } else {
                    throw new IllegalArgumentException("Invalid length argument");
                }
            }
            if (this.inputStream == null) {
                this.inputStream = this.clientSocket.getInputStream();
            }
            try {
                return TiStreamHelper.read(this.inputStream, bufferProxy, offset, length);
            } catch (IOException e) {
                e.printStackTrace();
                if (this.state != 4) {
                    closeSocket();
                    updateState(5, "error", buildErrorCallbackArgs("Unable to read from socket, IO error", 0));
                }
                throw new IOException("Unable to read from socket, IO error");
            }
        } else {
            throw new IllegalArgumentException("Invalid number of arguments");
        }
    }

    public int write(Object[] args) throws IOException {
        if (!isConnected()) {
            throw new IOException("Unable to write to socket, not connected");
        }
        BufferProxy bufferProxy = null;
        int offset = 0;
        int length = 0;
        if (args.length == 1 || args.length == 3) {
            if (args.length > 0) {
                if (args[0] instanceof BufferProxy) {
                    bufferProxy = args[0];
                    length = bufferProxy.getLength();
                } else {
                    throw new IllegalArgumentException("Invalid buffer argument");
                }
            }
            if (args.length == 3) {
                if (args[1] instanceof Integer) {
                    offset = args[1].intValue();
                } else if (args[1] instanceof Double) {
                    offset = args[1].intValue();
                } else {
                    throw new IllegalArgumentException("Invalid offset argument");
                }
                if (args[2] instanceof Integer) {
                    length = args[2].intValue();
                } else if (args[2] instanceof Double) {
                    length = args[2].intValue();
                } else {
                    throw new IllegalArgumentException("Invalid length argument");
                }
            }
            try {
                return TiStreamHelper.write(this.clientSocket.getOutputStream(), bufferProxy, offset, length);
            } catch (IOException e) {
                e.printStackTrace();
                closeSocket();
                updateState(5, "error", buildErrorCallbackArgs("Unable to write to socket, IO error", 0));
                throw new IOException("Unable to write to socket, IO error");
            }
        } else {
            throw new IllegalArgumentException("Invalid number of arguments");
        }
    }

    public boolean isWritable() {
        return isConnected();
    }

    public boolean isReadable() {
        return isConnected();
    }

    public void close() throws IOException {
        if (this.state != 4) {
            if (this.state == 2 || this.state == 3) {
                try {
                    this.state = 0;
                    closeSocket();
                    this.state = 4;
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new IOException("Error occured when closing socket");
                }
            } else {
                throw new IOException("Socket is not connected or listening, unable to call close on socket in <" + this.state + "> state");
            }
        }
    }

    public String getApiName() {
        return "Ti.Network.Socket.TCP";
    }
}
