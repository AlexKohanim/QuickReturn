package org.appcelerator.kroll.runtime.p004v8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.appcelerator.kroll.common.TiMessenger;

/* renamed from: org.appcelerator.kroll.runtime.v8.JSDebugger */
public final class JSDebugger {
    private static final String DISCONNECT_MESSAGE = "{\"seq\":0,\"type\":\"request\",\"command\":\"disconnect\"}";
    private static final String HANDSHAKE_MESSAGE = "Type: connect\r\nV8-Version: 5.1.281.59\r\nProtocol-Version: 1\r\nEmbedding-Host: Titanium v%s\r\nContent-Length: 0\r\n\r\n";
    private static final String LINE_ENDING = "\r\n";
    /* access modifiers changed from: private */
    public static final byte[] LINE_END_BYTES = LINE_ENDING.getBytes();
    private static final String TAG = "JSDebugger";
    private DebugAgentThread agentThread;
    /* access modifiers changed from: private */
    public final int port;
    private final Runnable processDebugMessagesRunnable = new Runnable() {
        public void run() {
            JSDebugger.this.nativeProcessDebugMessages();
        }
    };
    /* access modifiers changed from: private */
    public final String sdkVersion;
    /* access modifiers changed from: private */
    public LinkedBlockingQueue<String> v8Messages = new LinkedBlockingQueue<>();

    /* renamed from: org.appcelerator.kroll.runtime.v8.JSDebugger$DebugAgentThread */
    private class DebugAgentThread extends Thread {
        private DebuggerMessageHandler debuggerMessageHandler;
        private ServerSocket serverSocket;
        private V8MessageHandler v8MessageHandler;

        private DebugAgentThread(String name) {
            super(name);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:12:?, code lost:
            r3 = r6.serverSocket;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0062, code lost:
            r6.serverSocket.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:?, code lost:
            r1.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
            org.appcelerator.kroll.runtime.p004v8.JSDebugger.access$300(r6.this$0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x0074, code lost:
            r3 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:23:?, code lost:
            r4 = r6.serverSocket;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:25:0x0079, code lost:
            r6.serverSocket.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:27:0x007f, code lost:
            r3 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:28:0x0080, code lost:
            if (r1 != null) goto L_0x0082;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:30:?, code lost:
            r1.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:32:?, code lost:
            org.appcelerator.kroll.runtime.p004v8.JSDebugger.access$300(r6.this$0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:33:0x008a, code lost:
            throw r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:42:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:43:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:44:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Removed duplicated region for block: B:21:0x0074 A[ExcHandler: all (r3v0 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:0:0x0000] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r6 = this;
                java.net.ServerSocket r3 = new java.net.ServerSocket     // Catch:{ Throwable -> 0x005d, all -> 0x0074 }
                r3.<init>()     // Catch:{ Throwable -> 0x005d, all -> 0x0074 }
                r6.serverSocket = r3     // Catch:{ Throwable -> 0x005d, all -> 0x0074 }
                java.net.ServerSocket r3 = r6.serverSocket     // Catch:{ Throwable -> 0x005d, all -> 0x0074 }
                r4 = 1
                r3.setReuseAddress(r4)     // Catch:{ Throwable -> 0x005d, all -> 0x0074 }
                java.net.ServerSocket r3 = r6.serverSocket     // Catch:{ Throwable -> 0x005d, all -> 0x0074 }
                java.net.InetSocketAddress r4 = new java.net.InetSocketAddress     // Catch:{ Throwable -> 0x005d, all -> 0x0074 }
                org.appcelerator.kroll.runtime.v8.JSDebugger r5 = org.appcelerator.kroll.runtime.p004v8.JSDebugger.this     // Catch:{ Throwable -> 0x005d, all -> 0x0074 }
                int r5 = r5.port     // Catch:{ Throwable -> 0x005d, all -> 0x0074 }
                r4.<init>(r5)     // Catch:{ Throwable -> 0x005d, all -> 0x0074 }
                r3.bind(r4)     // Catch:{ Throwable -> 0x005d, all -> 0x0074 }
            L_0x001d:
                r1 = 0
                java.net.ServerSocket r3 = r6.serverSocket     // Catch:{ Throwable -> 0x0068, all -> 0x007f }
                java.net.Socket r1 = r3.accept()     // Catch:{ Throwable -> 0x0068, all -> 0x007f }
                org.appcelerator.kroll.runtime.v8.JSDebugger$V8MessageHandler r3 = new org.appcelerator.kroll.runtime.v8.JSDebugger$V8MessageHandler     // Catch:{ Throwable -> 0x0068, all -> 0x007f }
                org.appcelerator.kroll.runtime.v8.JSDebugger r4 = org.appcelerator.kroll.runtime.p004v8.JSDebugger.this     // Catch:{ Throwable -> 0x0068, all -> 0x007f }
                r3.<init>(r1)     // Catch:{ Throwable -> 0x0068, all -> 0x007f }
                r6.v8MessageHandler = r3     // Catch:{ Throwable -> 0x0068, all -> 0x007f }
                java.lang.Thread r2 = new java.lang.Thread     // Catch:{ Throwable -> 0x0068, all -> 0x007f }
                org.appcelerator.kroll.runtime.v8.JSDebugger$V8MessageHandler r3 = r6.v8MessageHandler     // Catch:{ Throwable -> 0x0068, all -> 0x007f }
                r2.<init>(r3)     // Catch:{ Throwable -> 0x0068, all -> 0x007f }
                r2.start()     // Catch:{ Throwable -> 0x0068, all -> 0x007f }
                org.appcelerator.kroll.runtime.v8.JSDebugger$DebuggerMessageHandler r3 = new org.appcelerator.kroll.runtime.v8.JSDebugger$DebuggerMessageHandler     // Catch:{ Throwable -> 0x0068, all -> 0x007f }
                org.appcelerator.kroll.runtime.v8.JSDebugger r4 = org.appcelerator.kroll.runtime.p004v8.JSDebugger.this     // Catch:{ Throwable -> 0x0068, all -> 0x007f }
                r3.<init>(r1)     // Catch:{ Throwable -> 0x0068, all -> 0x007f }
                r6.debuggerMessageHandler = r3     // Catch:{ Throwable -> 0x0068, all -> 0x007f }
                java.lang.Thread r0 = new java.lang.Thread     // Catch:{ Throwable -> 0x0068, all -> 0x007f }
                org.appcelerator.kroll.runtime.v8.JSDebugger$DebuggerMessageHandler r3 = r6.debuggerMessageHandler     // Catch:{ Throwable -> 0x0068, all -> 0x007f }
                r0.<init>(r3)     // Catch:{ Throwable -> 0x0068, all -> 0x007f }
                r0.start()     // Catch:{ Throwable -> 0x0068, all -> 0x007f }
                r0.join()     // Catch:{ Throwable -> 0x0068, all -> 0x007f }
                org.appcelerator.kroll.runtime.v8.JSDebugger$V8MessageHandler r3 = r6.v8MessageHandler     // Catch:{ Throwable -> 0x0068, all -> 0x007f }
                r3.stop()     // Catch:{ Throwable -> 0x0068, all -> 0x007f }
                if (r1 == 0) goto L_0x0057
                r1.close()     // Catch:{ Throwable -> 0x008b, all -> 0x0074 }
            L_0x0057:
                org.appcelerator.kroll.runtime.v8.JSDebugger r3 = org.appcelerator.kroll.runtime.p004v8.JSDebugger.this     // Catch:{ Throwable -> 0x005d, all -> 0x0074 }
                r3.clearMessages()     // Catch:{ Throwable -> 0x005d, all -> 0x0074 }
                goto L_0x001d
            L_0x005d:
                r3 = move-exception
                java.net.ServerSocket r3 = r6.serverSocket     // Catch:{ IOException -> 0x0093 }
                if (r3 == 0) goto L_0x0067
                java.net.ServerSocket r3 = r6.serverSocket     // Catch:{ IOException -> 0x0093 }
                r3.close()     // Catch:{ IOException -> 0x0093 }
            L_0x0067:
                return
            L_0x0068:
                r3 = move-exception
                if (r1 == 0) goto L_0x006e
                r1.close()     // Catch:{ Throwable -> 0x008d, all -> 0x0074 }
            L_0x006e:
                org.appcelerator.kroll.runtime.v8.JSDebugger r3 = org.appcelerator.kroll.runtime.p004v8.JSDebugger.this     // Catch:{ Throwable -> 0x005d, all -> 0x0074 }
                r3.clearMessages()     // Catch:{ Throwable -> 0x005d, all -> 0x0074 }
                goto L_0x001d
            L_0x0074:
                r3 = move-exception
                java.net.ServerSocket r4 = r6.serverSocket     // Catch:{ IOException -> 0x0091 }
                if (r4 == 0) goto L_0x007e
                java.net.ServerSocket r4 = r6.serverSocket     // Catch:{ IOException -> 0x0091 }
                r4.close()     // Catch:{ IOException -> 0x0091 }
            L_0x007e:
                throw r3
            L_0x007f:
                r3 = move-exception
                if (r1 == 0) goto L_0x0085
                r1.close()     // Catch:{ Throwable -> 0x008f, all -> 0x0074 }
            L_0x0085:
                org.appcelerator.kroll.runtime.v8.JSDebugger r4 = org.appcelerator.kroll.runtime.p004v8.JSDebugger.this     // Catch:{ Throwable -> 0x005d, all -> 0x0074 }
                r4.clearMessages()     // Catch:{ Throwable -> 0x005d, all -> 0x0074 }
                throw r3     // Catch:{ Throwable -> 0x005d, all -> 0x0074 }
            L_0x008b:
                r3 = move-exception
                goto L_0x0057
            L_0x008d:
                r3 = move-exception
                goto L_0x006e
            L_0x008f:
                r4 = move-exception
                goto L_0x0085
            L_0x0091:
                r4 = move-exception
                goto L_0x007e
            L_0x0093:
                r3 = move-exception
                goto L_0x0067
            */
            throw new UnsupportedOperationException("Method not decompiled: org.appcelerator.kroll.runtime.p004v8.JSDebugger.DebugAgentThread.run():void");
        }
    }

    /* renamed from: org.appcelerator.kroll.runtime.v8.JSDebugger$DebuggerMessageHandler */
    private class DebuggerMessageHandler implements Runnable {
        private BufferedReader input;
        private AtomicBoolean stop = new AtomicBoolean(false);

        public DebuggerMessageHandler(Socket socket) throws IOException {
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        public void stop() {
            this.stop.set(true);
            try {
                this.input.close();
            } catch (IOException e) {
            }
        }

        public void run() {
            while (!this.stop.get()) {
                try {
                    int length = readHeaders();
                    if (length != -1) {
                        String message = readMessage(length);
                        if (message == null) {
                            break;
                        }
                        JSDebugger.this.sendMessage(message);
                    } else {
                        break;
                    }
                } catch (IOException e) {
                    try {
                        this.input.close();
                        return;
                    } catch (IOException e2) {
                        return;
                    }
                } finally {
                    this.stop.set(true);
                    JSDebugger.this.sendMessage(JSDebugger.DISCONNECT_MESSAGE);
                    try {
                        this.input.close();
                    } catch (IOException e3) {
                    }
                }
            }
            try {
                this.input.close();
            } catch (IOException e4) {
            }
        }

        private int readHeaders() throws IOException {
            int messageLength = -1;
            while (!this.stop.get()) {
                String line = this.input.readLine();
                if (line == null || line.length() == 0) {
                    break;
                } else if (line.startsWith("Content-Length:")) {
                    messageLength = Integer.parseInt(line.substring(15).trim());
                }
            }
            return messageLength;
        }

        private String readMessage(int length) throws IOException {
            if (this.stop.get()) {
                return null;
            }
            char[] buf = new char[length];
            if (this.input.read(buf, 0, length) == length) {
                return new String(buf);
            }
            return null;
        }
    }

    /* renamed from: org.appcelerator.kroll.runtime.v8.JSDebugger$V8MessageHandler */
    private class V8MessageHandler implements Runnable {
        private static final String STOP_MESSAGE = "STOP_MESSAGE";
        private OutputStream output;
        private AtomicBoolean stop = new AtomicBoolean(false);

        public V8MessageHandler(Socket socket) throws IOException {
            this.output = socket.getOutputStream();
        }

        public void stop() {
            this.stop.set(true);
            JSDebugger.this.v8Messages.add(STOP_MESSAGE);
        }

        public void run() {
            sendHandshake();
            while (!this.stop.get()) {
                try {
                    String message = (String) JSDebugger.this.v8Messages.take();
                    if (!message.equals(STOP_MESSAGE)) {
                        sendMessageToDebugger(message);
                    }
                } catch (Throwable th) {
                }
            }
            try {
                this.output.close();
            } catch (IOException e) {
            }
        }

        private void sendHandshake() {
            try {
                this.output.write(String.format(JSDebugger.HANDSHAKE_MESSAGE, new Object[]{JSDebugger.this.sdkVersion}).getBytes("UTF8"));
                this.output.flush();
            } catch (IOException e) {
            }
        }

        private void sendMessageToDebugger(String msg) {
            try {
                byte[] utf8 = msg.getBytes("UTF8");
                try {
                    this.output.write(("Content-Length: " + utf8.length).getBytes("UTF8"));
                    this.output.write(JSDebugger.LINE_END_BYTES);
                    this.output.write(JSDebugger.LINE_END_BYTES);
                    this.output.write(utf8);
                    this.output.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e2) {
            }
        }
    }

    private native void nativeDebugBreak();

    private native void nativeDisable();

    private native void nativeEnable();

    private native boolean nativeIsDebuggerActive();

    /* access modifiers changed from: private */
    public native void nativeProcessDebugMessages();

    private native void nativeSendCommand(byte[] bArr, int i);

    public JSDebugger(int port2, String sdkVersion2) {
        this.port = port2;
        this.sdkVersion = sdkVersion2;
    }

    public void handleMessage(String message) {
        this.v8Messages.add(message);
    }

    public void sendMessage(String message) {
        byte[] cmdBytes = null;
        try {
            cmdBytes = message.getBytes("UTF-16LE");
        } catch (UnsupportedEncodingException e) {
        }
        nativeSendCommand(cmdBytes, cmdBytes.length);
        TiMessenger.postOnRuntime(this.processDebugMessagesRunnable);
    }

    public void start() {
        this.agentThread = new DebugAgentThread("titanium-debug");
        this.agentThread.start();
        nativeEnable();
        nativeDebugBreak();
    }

    /* access modifiers changed from: private */
    public void clearMessages() {
        this.v8Messages.clear();
    }
}
