package p006ti.modules.titanium.network;

import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.util.Base64;
import android.util.Base64OutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiFileProxy;
import org.appcelerator.titanium.p005io.TiBaseFile;
import org.appcelerator.titanium.p005io.TiFile;
import org.appcelerator.titanium.p005io.TiFileFactory;
import org.appcelerator.titanium.p005io.TiResourceFile;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiMimeTypeHelper;
import org.appcelerator.titanium.util.TiPlatformHelper;
import org.appcelerator.titanium.util.TiUrl;
import p006ti.modules.titanium.network.httpurlconnection.ContentBody;
import p006ti.modules.titanium.network.httpurlconnection.FileBody;
import p006ti.modules.titanium.network.httpurlconnection.FileEntity;
import p006ti.modules.titanium.network.httpurlconnection.HttpUrlConnectionUtils;
import p006ti.modules.titanium.network.httpurlconnection.JsonBody;
import p006ti.modules.titanium.network.httpurlconnection.NameValuePair;
import p006ti.modules.titanium.network.httpurlconnection.NullHostNameVerifier;
import p006ti.modules.titanium.network.httpurlconnection.StringBody;
import p006ti.modules.titanium.network.httpurlconnection.UrlEncodedFormEntity;
import p006ti.modules.titanium.xml.DocumentProxy;
import p006ti.modules.titanium.xml.XMLModule;

/* renamed from: ti.modules.titanium.network.TiHTTPClient */
public class TiHTTPClient {
    private static final int DEFAULT_MAX_BUFFER_SIZE = 524288;
    private static final String[] FALLBACK_CHARSETS = {"UTF_8", "ISO_8859_1"};
    private static final String HTML_META_TAG_REGEX = "charset=([^\"']*)";
    private static final String PROPERTY_MAX_BUFFER_SIZE = "ti.android.httpclient.maxbuffersize";
    private static final int PROTOCOL_DEFAULT_PORT = -1;
    public static final int READY_STATE_DONE = 4;
    public static final int READY_STATE_HEADERS_RECEIVED = 2;
    public static final int READY_STATE_LOADING = 3;
    public static final int READY_STATE_OPENED = 1;
    public static final int READY_STATE_UNSENT = 0;
    public static final int REDIRECTS = 5;
    private static final String TAG = "TiHTTPClient";
    private static final String TITANIUM_ID_HEADER = "X-Titanium-Id";
    private static final String TITANIUM_USER_AGENT = ("Appcelerator Titanium/" + TiApplication.getInstance().getTiBuildVersion() + " (" + Build.MODEL + "; Android API Level: " + Integer.toString(VERSION.SDK_INT) + "; " + TiPlatformHelper.getInstance().getLocale() + ";)");
    private static final String XML_DECLARATION_TAG_REGEX = "encoding=[\"']([^\"']*)[\"']";
    private static CookieManager cookieManager = NetworkModule.getCookieManagerInstance();
    private static AtomicInteger httpClientThreadCounter;
    /* access modifiers changed from: private */
    public boolean aborted;
    private boolean autoEncodeUrl = true;
    /* access modifiers changed from: private */
    public boolean autoRedirect = true;
    private String charset;
    /* access modifiers changed from: private */
    public HttpURLConnection client;
    /* access modifiers changed from: private */
    public Thread clientThread;
    /* access modifiers changed from: private */
    public boolean connected;
    private String contentEncoding;
    private String contentType;
    /* access modifiers changed from: private */
    public Object data;
    /* access modifiers changed from: private */
    public boolean hasAuthentication = false;
    private ArrayList<X509KeyManager> keyManagers = new ArrayList<>();
    /* access modifiers changed from: private */
    public URL mURL;
    private long maxBufferSize;
    /* access modifiers changed from: private */
    public String method;
    /* access modifiers changed from: private */
    public boolean needMultipart;
    /* access modifiers changed from: private */
    public ArrayList<NameValuePair> nvPairs;
    /* access modifiers changed from: private */
    public HashMap<String, ContentBody> parts;
    /* access modifiers changed from: private */
    public String password;
    /* access modifiers changed from: private */
    public HTTPClientProxy proxy;
    private int readyState;
    /* access modifiers changed from: private */
    public String redirectedLocation;
    protected HashMap<String, String> requestHeaders = new HashMap<>();
    /* access modifiers changed from: private */
    public boolean requestPending = false;
    private TiBlob responseData;
    private TiFile responseFile;
    protected Map<String, List<String>> responseHeaders;
    private OutputStream responseOut;
    private String responseText;
    private DocumentProxy responseXml;
    protected SecurityManagerProtocol securityManager;
    private int status;
    private String statusText;
    /* access modifiers changed from: private */
    public int timeout = -1;
    private int tlsVersion = 0;
    private ArrayList<File> tmpFiles = new ArrayList<>();
    private ArrayList<X509TrustManager> trustManagers = new ArrayList<>();
    private Uri uri;
    /* access modifiers changed from: private */
    public String url;
    /* access modifiers changed from: private */
    public String username;

    /* renamed from: ti.modules.titanium.network.TiHTTPClient$ClientRunnable */
    private class ClientRunnable implements Runnable {
        private static final String LINE_FEED = "\r\n";
        private String boundary;
        private int contentLength = 0;
        private OutputStream outputStream;
        private PrintWriter printWriter;
        /* access modifiers changed from: private */
        public final int totalLength;

        public ClientRunnable(int totalLength2) {
            this.totalLength = totalLength2;
        }

        public void run() {
            String result;
            try {
                Thread.sleep(10);
                Log.m29d(TiHTTPClient.TAG, "send()", Log.DEBUG_MODE);
                Log.m29d(TiHTTPClient.TAG, "Preparing to execute request", Log.DEBUG_MODE);
                result = null;
                TiHTTPClient.this.mURL = new URL(TiHTTPClient.this.url);
                TiHTTPClient.this.client = (HttpURLConnection) TiHTTPClient.this.mURL.openConnection();
                boolean isPostOrPutOrPatch = TiHTTPClient.this.method.equals("POST") || TiHTTPClient.this.method.equals("PUT") || TiHTTPClient.this.method.equals("PATCH");
                setUpClient(TiHTTPClient.this.client, Boolean.valueOf(isPostOrPutOrPatch));
                if (isPostOrPutOrPatch) {
                    UrlEncodedFormEntity form = null;
                    if (TiHTTPClient.this.nvPairs.size() > 0) {
                        try {
                            form = new UrlEncodedFormEntity((List<? extends NameValuePair>) TiHTTPClient.this.nvPairs, HttpUrlConnectionUtils.UTF_8);
                        } catch (UnsupportedEncodingException e) {
                            Log.m34e(TiHTTPClient.TAG, "Unsupported encoding: ", (Throwable) e);
                        }
                        TiHTTPClient.this.nvPairs.clear();
                    }
                    if (TiHTTPClient.this.parts.size() > 0 && TiHTTPClient.this.needMultipart) {
                        for (String name : TiHTTPClient.this.parts.keySet()) {
                            this.contentLength = constructFilePart(name, (ContentBody) TiHTTPClient.this.parts.get(name)).length() + this.contentLength;
                            this.contentLength = (int) (((long) this.contentLength) + ((ContentBody) TiHTTPClient.this.parts.get(name)).getContentLength() + 2);
                        }
                        if (form != null) {
                            this.contentLength += (int) form.getContentLength();
                            try {
                                ByteArrayOutputStream bos = new ByteArrayOutputStream((int) form.getContentLength());
                                form.writeTo(bos);
                                this.contentLength += constructFilePart("form", new StringBody(bos.toString(), HttpUrlConnectionUtils.CONTENT_TYPE_X_WWW_FORM_URLENCODED, Charset.forName(HttpUrlConnectionUtils.UTF_8))).length();
                                this.contentLength = (int) (((long) this.contentLength) + form.getContentLength() + 2);
                            } catch (UnsupportedEncodingException e2) {
                                Log.m34e(TiHTTPClient.TAG, "Unsupported encoding: ", (Throwable) e2);
                            } catch (IOException e3) {
                                Log.m34e(TiHTTPClient.TAG, "Error converting form to string: ", (Throwable) e3);
                            }
                        }
                        this.contentLength += this.boundary.length() + 6;
                    } else if (TiHTTPClient.this.data instanceof String) {
                        this.contentLength = ((String) TiHTTPClient.this.data).getBytes().length + this.contentLength;
                    } else if (TiHTTPClient.this.data instanceof FileEntity) {
                        this.contentLength = (int) (((FileEntity) TiHTTPClient.this.data).getContentLength() + ((long) this.contentLength));
                    } else if (form != null) {
                        this.contentLength += (int) form.getContentLength();
                    }
                    TiHTTPClient.this.client.setFixedLengthStreamingMode(this.contentLength);
                    TiHTTPClient tiHTTPClient = TiHTTPClient.this;
                    OutputStream outputStream2 = TiHTTPClient.this.client.getOutputStream();
                    C03791 r0 = new ProgressListener() {
                        public void progress(int progress) {
                            KrollDict data = new KrollDict();
                            double currentProgress = ((double) progress) / ((double) ClientRunnable.this.totalLength);
                            if (currentProgress > 1.0d) {
                                currentProgress = 1.0d;
                            }
                            data.put("progress", Double.valueOf(currentProgress));
                            TiHTTPClient.this.dispatchCallback(TiC.PROPERTY_ONSENDSTREAM, data);
                        }
                    };
                    this.outputStream = new ProgressOutputStream(outputStream2, r0);
                    this.printWriter = new PrintWriter(this.outputStream, true);
                    if (TiHTTPClient.this.parts.size() <= 0 || !TiHTTPClient.this.needMultipart) {
                        handleURLEncodedData(form);
                    } else {
                        for (String name2 : TiHTTPClient.this.parts.keySet()) {
                            Log.m29d(TiHTTPClient.TAG, "adding part " + name2 + ", part type: " + ((ContentBody) TiHTTPClient.this.parts.get(name2)).getMimeType() + ", len: " + ((ContentBody) TiHTTPClient.this.parts.get(name2)).getContentLength(), Log.DEBUG_MODE);
                            addFilePart(name2, (ContentBody) TiHTTPClient.this.parts.get(name2));
                        }
                        TiHTTPClient.this.parts.clear();
                        if (form != null) {
                            try {
                                ByteArrayOutputStream bos2 = new ByteArrayOutputStream((int) form.getContentLength());
                                form.writeTo(bos2);
                                addFilePart("form", new StringBody(bos2.toString(), HttpUrlConnectionUtils.CONTENT_TYPE_X_WWW_FORM_URLENCODED, Charset.forName(HttpUrlConnectionUtils.UTF_8)));
                            } catch (UnsupportedEncodingException e4) {
                                Log.m34e(TiHTTPClient.TAG, "Unsupported encoding: ", (Throwable) e4);
                            } catch (IOException e5) {
                                Log.m34e(TiHTTPClient.TAG, "Error converting form to string: ", (Throwable) e5);
                            }
                        }
                        completeSendingMultipart();
                    }
                }
                if (TiHTTPClient.this.autoRedirect) {
                    for (int i = 0; i < 5; i++) {
                        int status = TiHTTPClient.this.client.getResponseCode();
                        if (status == 200 || (status != 302 && status != 301 && status != 303)) {
                            break;
                        }
                        TiHTTPClient.this.redirectedLocation = TiHTTPClient.this.client.getHeaderField("Location");
                        if (TiHTTPClient.this.redirectedLocation == null) {
                            break;
                        }
                        TiHTTPClient.this.client.disconnect();
                        TiHTTPClient.this.client = (HttpURLConnection) new URL(TiHTTPClient.this.redirectedLocation).openConnection();
                        setUpClient(TiHTTPClient.this.client, Boolean.valueOf(isPostOrPutOrPatch));
                    }
                }
                TiHTTPClient.this.handleResponse(TiHTTPClient.this.client);
                if (TiHTTPClient.this.client != null) {
                    TiHTTPClient.this.client.disconnect();
                }
            } catch (IOException e6) {
                if (!TiHTTPClient.this.aborted) {
                    throw e6;
                } else if (TiHTTPClient.this.client != null) {
                    TiHTTPClient.this.client.disconnect();
                }
            } catch (Throwable t) {
                try {
                    if (TiHTTPClient.this.client != null) {
                        Log.m29d(TiHTTPClient.TAG, "clearing the expired and idle connections", Log.DEBUG_MODE);
                        TiHTTPClient.this.client.disconnect();
                    } else {
                        Log.m28d(TiHTTPClient.TAG, "client is not valid, unable to clear expired and idle connections");
                    }
                    String msg = t.getMessage();
                    if (msg == null && t.getCause() != null) {
                        msg = t.getCause().getMessage();
                    }
                    if (msg == null) {
                        msg = t.getClass().getName();
                    }
                    Log.m34e(TiHTTPClient.TAG, "HTTP Error (" + t.getClass().getName() + "): " + msg, t);
                    KrollDict data = new KrollDict();
                    data.putCodeAndMessage(TiHTTPClient.this.getStatus() >= 400 ? TiHTTPClient.this.getStatus() : -1, msg);
                    TiHTTPClient.this.dispatchCallback(TiC.PROPERTY_ONERROR, data);
                    return;
                } finally {
                    TiHTTPClient.this.deleteTmpFiles();
                    TiHTTPClient.this.client = null;
                    TiHTTPClient.this.clientThread = null;
                    TiHTTPClient.this.requestPending = false;
                    TiHTTPClient.this.proxy.fireEvent(TiC.EVENT_DISPOSE_HANDLE, null);
                }
            } finally {
                if (TiHTTPClient.this.client != null) {
                    TiHTTPClient.this.client.disconnect();
                }
            }
            if (result != null) {
                Log.m29d(TiHTTPClient.TAG, "Have result back from request len=" + result.length(), Log.DEBUG_MODE);
            }
            TiHTTPClient.this.connected = false;
            TiHTTPClient.this.setResponseText(result);
            if (TiHTTPClient.this.getStatus() >= 400) {
                throw new IOException(TiHTTPClient.this.getStatus() + " : " + TiHTTPClient.this.getStatusText());
            }
            if (!TiHTTPClient.this.aborted) {
                TiHTTPClient.this.setReadyState(4);
            }
            TiHTTPClient.this.deleteTmpFiles();
            TiHTTPClient.this.client = null;
            TiHTTPClient.this.clientThread = null;
            TiHTTPClient.this.requestPending = false;
            TiHTTPClient.this.proxy.fireEvent(TiC.EVENT_DISPOSE_HANDLE, null);
        }

        /* access modifiers changed from: protected */
        public void setUpClient(HttpURLConnection client, Boolean isPostOrPutOrPatch) throws ProtocolException {
            client.setInstanceFollowRedirects(TiHTTPClient.this.autoRedirect);
            if (client instanceof HttpsURLConnection) {
                TiHTTPClient.this.setUpSSL(TiHTTPClient.this.validatesSecureCertificate(), (HttpsURLConnection) client);
            }
            if (TiHTTPClient.this.timeout != -1) {
                client.setReadTimeout(TiHTTPClient.this.timeout);
                client.setConnectTimeout(TiHTTPClient.this.timeout);
            }
            if (!TiHTTPClient.this.aborted) {
                client.setRequestMethod(TiHTTPClient.this.method);
                client.setDoInput(true);
                if (isPostOrPutOrPatch.booleanValue()) {
                    client.setDoOutput(true);
                }
                client.setUseCaches(false);
                if (TiHTTPClient.this.hasAuthentication) {
                    String domain = TiHTTPClient.this.proxy.getDomain();
                    if (domain != null) {
                        TiHTTPClient.this.username = domain + "\\" + TiHTTPClient.this.username;
                    }
                    client.setRequestProperty("Authorization", "Basic " + Base64.encodeToString((TiHTTPClient.this.username + ":" + TiHTTPClient.this.password).getBytes(), 2));
                }
                client.setRequestProperty("Accept-Encoding", "identity");
                client.setRequestProperty(TiHTTPClient.TITANIUM_ID_HEADER, TiApplication.getInstance().getAppGUID());
                if (TiHTTPClient.this.parts.size() > 0 && TiHTTPClient.this.needMultipart) {
                    this.boundary = HttpUrlConnectionUtils.generateBoundary();
                    client.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + this.boundary);
                } else if (isPostOrPutOrPatch.booleanValue()) {
                    client.setRequestProperty("Content-Type", HttpUrlConnectionUtils.CONTENT_TYPE_X_WWW_FORM_URLENCODED);
                }
                for (String header : TiHTTPClient.this.requestHeaders.keySet()) {
                    client.setRequestProperty(header, (String) TiHTTPClient.this.requestHeaders.get(header));
                }
            }
        }

        private String constructFilePart(String name, ContentBody contentBody) {
            String fileName = contentBody.getFilename();
            String part = ("" + "--" + this.boundary + LINE_FEED) + "Content-Disposition: form-data; name=\"" + name + "\"";
            if (fileName != null) {
                part = part + "; filename=\"" + fileName + "\"";
            }
            String part2 = part + LINE_FEED;
            String mimeType = contentBody.getMimeType();
            if (mimeType != null && !mimeType.isEmpty()) {
                String part3 = part2 + "Content-Type: " + contentBody.getMimeType();
                if (contentBody.getCharset() != null) {
                    part3 = part3 + HttpUrlConnectionUtils.CHARSET_PARAM + contentBody.getCharset();
                }
                part2 = part3 + LINE_FEED;
            }
            return part2 + "Content-Transfer-Encoding: " + contentBody.getTransferEncoding() + LINE_FEED + LINE_FEED;
        }

        private void addFilePart(String name, ContentBody contentBody) throws IOException {
            this.printWriter.append(constructFilePart(name, contentBody));
            this.printWriter.flush();
            contentBody.writeTo(this.outputStream);
            this.printWriter.append(LINE_FEED);
            this.printWriter.flush();
        }

        public void completeSendingMultipart() throws IOException {
            this.printWriter.append("--" + this.boundary + "--").append(LINE_FEED);
            this.printWriter.close();
        }

        /* JADX WARNING: Incorrect type for immutable var: ssa=ti.modules.titanium.network.httpurlconnection.UrlEncodedFormEntity, code=ti.modules.titanium.network.httpurlconnection.Entity, for r6v0, types: [ti.modules.titanium.network.httpurlconnection.Entity, ti.modules.titanium.network.httpurlconnection.UrlEncodedFormEntity] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void handleURLEncodedData(p006ti.modules.titanium.network.httpurlconnection.Entity r6) throws java.io.IOException {
            /*
                r5 = this;
                r0 = 0
                ti.modules.titanium.network.TiHTTPClient r3 = p006ti.modules.titanium.network.TiHTTPClient.this
                java.lang.Object r3 = r3.data
                boolean r3 = r3 instanceof java.lang.String
                if (r3 == 0) goto L_0x0031
                ti.modules.titanium.network.httpurlconnection.StringEntity r1 = new ti.modules.titanium.network.httpurlconnection.StringEntity     // Catch:{ Exception -> 0x0028 }
                ti.modules.titanium.network.TiHTTPClient r3 = p006ti.modules.titanium.network.TiHTTPClient.this     // Catch:{ Exception -> 0x0028 }
                java.lang.Object r3 = r3.data     // Catch:{ Exception -> 0x0028 }
                java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x0028 }
                java.lang.String r4 = "UTF-8"
                r1.<init>(r3, r4)     // Catch:{ Exception -> 0x0028 }
                r0 = r1
            L_0x001b:
                if (r0 == 0) goto L_0x0027
                java.io.OutputStream r3 = r5.outputStream
                r0.writeTo(r3)
                java.io.PrintWriter r3 = r5.printWriter
                r3.flush()
            L_0x0027:
                return
            L_0x0028:
                r2 = move-exception
                java.lang.String r3 = "TiHTTPClient"
                java.lang.String r4 = "Exception, implement recovery: "
                org.appcelerator.kroll.common.Log.m34e(r3, r4, r2)
                goto L_0x001b
            L_0x0031:
                ti.modules.titanium.network.TiHTTPClient r3 = p006ti.modules.titanium.network.TiHTTPClient.this
                java.lang.Object r3 = r3.data
                boolean r3 = r3 instanceof p006ti.modules.titanium.network.httpurlconnection.Entity
                if (r3 == 0) goto L_0x0044
                ti.modules.titanium.network.TiHTTPClient r3 = p006ti.modules.titanium.network.TiHTTPClient.this
                java.lang.Object r0 = r3.data
                ti.modules.titanium.network.httpurlconnection.Entity r0 = (p006ti.modules.titanium.network.httpurlconnection.Entity) r0
                goto L_0x001b
            L_0x0044:
                r0 = r6
                goto L_0x001b
            */
            throw new UnsupportedOperationException("Method not decompiled: p006ti.modules.titanium.network.TiHTTPClient.ClientRunnable.handleURLEncodedData(ti.modules.titanium.network.httpurlconnection.UrlEncodedFormEntity):void");
        }
    }

    /* renamed from: ti.modules.titanium.network.TiHTTPClient$ProgressListener */
    private interface ProgressListener {
        void progress(int i);
    }

    /* renamed from: ti.modules.titanium.network.TiHTTPClient$ProgressOutputStream */
    private class ProgressOutputStream extends FilterOutputStream {
        private int lastTransferred = 0;
        private ProgressListener listener;
        private int transferred = 0;

        public ProgressOutputStream(OutputStream delegate, ProgressListener listener2) {
            super(delegate);
            this.listener = listener2;
        }

        private void fireProgress() {
            if (this.transferred - this.lastTransferred >= 512) {
                this.lastTransferred = this.transferred;
                this.listener.progress(this.transferred);
            }
        }

        public void write(int b) throws IOException {
            if (!TiHTTPClient.this.aborted) {
                super.write(b);
                this.transferred++;
                fireProgress();
            }
        }
    }

    /* JADX WARNING: type inference failed for: r16v0, types: [java.io.InputStream] */
    /* JADX WARNING: type inference failed for: r16v1 */
    /* JADX WARNING: type inference failed for: r16v2 */
    /* JADX WARNING: type inference failed for: r1v0, types: [java.io.InputStream] */
    /* JADX WARNING: type inference failed for: r1v3, types: [java.io.InputStream] */
    /* JADX WARNING: type inference failed for: r16v3 */
    /* JADX WARNING: type inference failed for: r16v4, types: [java.io.InputStream] */
    /* JADX WARNING: type inference failed for: r16v5 */
    /* JADX WARNING: type inference failed for: r16v6 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 5 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleResponse(java.net.HttpURLConnection r26) throws java.io.IOException {
        /*
            r25 = this;
            r3 = 1
            r0 = r25
            r0.connected = r3
            if (r26 == 0) goto L_0x0188
            r10 = r25
            int r3 = r26.getContentLength()
            long r8 = (long) r3
            java.util.Map r3 = r26.getHeaderFields()
            r0 = r25
            r0.responseHeaders = r3
            int r3 = r26.getResponseCode()
            r0 = r25
            r0.setStatus(r3)
            r3 = 2
            r0 = r25
            r0.setReadyState(r3)
            java.lang.String r3 = r26.getResponseMessage()
            r0 = r25
            r0.setStatusText(r3)
            r3 = 3
            r0 = r25
            r0.setReadyState(r3)
            r0 = r25
            ti.modules.titanium.network.HTTPClientProxy r3 = r0.proxy
            java.lang.String r22 = "file"
            r0 = r22
            boolean r3 = r3.hasProperty(r0)
            if (r3 == 0) goto L_0x0077
            ti.modules.titanium.network.HTTPClientProxy r3 = r10.proxy
            java.lang.String r22 = "file"
            r0 = r22
            java.lang.Object r14 = r3.getProperty(r0)
            boolean r3 = r14 instanceof java.lang.String
            if (r3 == 0) goto L_0x0062
            r15 = r14
            java.lang.String r15 = (java.lang.String) r15
            r3 = 0
            org.appcelerator.titanium.io.TiBaseFile r2 = org.appcelerator.titanium.p005io.TiFileFactory.createTitaniumFile(r15, r3)
            boolean r3 = r2 instanceof org.appcelerator.titanium.p005io.TiFile
            if (r3 == 0) goto L_0x0062
            org.appcelerator.titanium.io.TiFile r2 = (org.appcelerator.titanium.p005io.TiFile) r2
            r0 = r25
            r0.responseFile = r2
        L_0x0062:
            r0 = r25
            org.appcelerator.titanium.io.TiFile r3 = r0.responseFile
            if (r3 != 0) goto L_0x0077
            boolean r3 = org.appcelerator.kroll.common.Log.isDebugModeEnabled()
            if (r3 == 0) goto L_0x0077
            java.lang.String r3 = "TiHTTPClient"
            java.lang.String r22 = "Ignore the provided response file because it is not valid / writable."
            r0 = r22
            org.appcelerator.kroll.common.Log.m44w(r3, r0)
        L_0x0077:
            java.net.URL r12 = r26.getURL()
            r0 = r25
            boolean r3 = r0.autoRedirect
            if (r3 == 0) goto L_0x0093
            r0 = r25
            java.net.URL r3 = r0.mURL
            boolean r3 = r3.sameFile(r12)
            if (r3 != 0) goto L_0x0093
            java.lang.String r3 = r12.toString()
            r0 = r25
            r0.redirectedLocation = r3
        L_0x0093:
            java.lang.String r3 = r26.getContentEncoding()
            r0 = r25
            r0.contentEncoding = r3
            java.lang.String r3 = r26.getContentType()
            r0 = r25
            r0.contentType = r3
            java.lang.String r11 = ""
            r0 = r25
            java.lang.String r3 = r0.contentType
            if (r3 == 0) goto L_0x00e4
            r0 = r25
            java.lang.String r3 = r0.contentType
            java.lang.String r22 = ";"
            r0 = r22
            java.lang.String[] r21 = r3.split(r0)
            r0 = r21
            int r0 = r0.length
            r22 = r0
            r3 = 0
        L_0x00bd:
            r0 = r22
            if (r3 >= r0) goto L_0x00e4
            r20 = r21[r3]
            java.lang.String r20 = r20.trim()
            java.lang.String r23 = r20.toLowerCase()
            java.lang.String r24 = "charset="
            boolean r23 = r23.startsWith(r24)
            if (r23 == 0) goto L_0x00e1
            java.lang.String r23 = "charset="
            int r23 = r23.length()
            r0 = r20
            r1 = r23
            java.lang.String r11 = r0.substring(r1)
        L_0x00e1:
            int r3 = r3 + 1
            goto L_0x00bd
        L_0x00e4:
            boolean r3 = r11.isEmpty()
            if (r3 == 0) goto L_0x00ec
            java.lang.String r11 = "UTF-8"
        L_0x00ec:
            r3 = 0
            r0 = r25
            r0.responseData = r3
            int r19 = r26.getResponseCode()
            r3 = 400(0x190, float:5.6E-43)
            r0 = r19
            if (r0 < r3) goto L_0x0189
            java.io.InputStream r16 = r26.getErrorStream()
        L_0x00ff:
            java.lang.String r3 = "gzip"
            r0 = r25
            java.lang.String r0 = r0.contentEncoding
            r22 = r0
            r0 = r22
            boolean r3 = r3.equalsIgnoreCase(r0)
            if (r3 == 0) goto L_0x011a
            java.util.zip.GZIPInputStream r17 = new java.util.zip.GZIPInputStream
            r0 = r17
            r1 = r16
            r0.<init>(r1)
            r16 = r17
        L_0x011a:
            java.io.BufferedInputStream r18 = new java.io.BufferedInputStream
            r0 = r18
            r1 = r16
            r0.<init>(r1)
            if (r18 == 0) goto L_0x0188
            java.lang.String r3 = "TiHTTPClient"
            java.lang.StringBuilder r22 = new java.lang.StringBuilder
            r22.<init>()
            java.lang.String r23 = "Content length: "
            java.lang.StringBuilder r22 = r22.append(r23)
            r0 = r22
            java.lang.StringBuilder r22 = r0.append(r8)
            java.lang.String r22 = r22.toString()
            java.lang.String r23 = "DEBUG_MODE"
            r0 = r22
            r1 = r23
            org.appcelerator.kroll.common.Log.m29d(r3, r0, r1)
            r5 = 0
            r6 = 0
            r3 = 4096(0x1000, float:5.74E-42)
            byte[] r4 = new byte[r3]
            java.lang.String r3 = "TiHTTPClient"
            java.lang.StringBuilder r22 = new java.lang.StringBuilder
            r22.<init>()
            java.lang.String r23 = "Available: "
            java.lang.StringBuilder r22 = r22.append(r23)
            int r23 = r18.available()
            java.lang.StringBuilder r22 = r22.append(r23)
            java.lang.String r22 = r22.toString()
            java.lang.String r23 = "DEBUG_MODE"
            r0 = r22
            r1 = r23
            org.appcelerator.kroll.common.Log.m29d(r3, r0, r1)
        L_0x016e:
            r0 = r18
            int r5 = r0.read(r4)
            r3 = -1
            if (r5 == r3) goto L_0x017d
            r0 = r25
            boolean r3 = r0.aborted
            if (r3 == 0) goto L_0x018f
        L_0x017d:
            r22 = 0
            int r3 = (r6 > r22 ? 1 : (r6 == r22 ? 0 : -1))
            if (r3 <= 0) goto L_0x0188
            r0 = r25
            r0.finishedReceivingEntityData(r6)
        L_0x0188:
            return
        L_0x0189:
            java.io.InputStream r16 = r26.getInputStream()
            goto L_0x00ff
        L_0x018f:
            long r0 = (long) r5
            r22 = r0
            long r6 = r6 + r22
            java.lang.String r3 = new java.lang.String     // Catch:{ IOException -> 0x01ad }
            r22 = 0
            r0 = r22
            byte[] r22 = java.util.Arrays.copyOfRange(r4, r0, r5)     // Catch:{ IOException -> 0x01ad }
            r0 = r22
            r3.<init>(r0)     // Catch:{ IOException -> 0x01ad }
            r0 = r25
            r0.responseText = r3     // Catch:{ IOException -> 0x01ad }
            r3 = r25
            r3.handleEntityData(r4, r5, r6, r8)     // Catch:{ IOException -> 0x01ad }
            goto L_0x016e
        L_0x01ad:
            r13 = move-exception
            java.lang.String r3 = "TiHTTPClient"
            java.lang.String r22 = "Error handling entity data"
            r0 = r22
            org.appcelerator.kroll.common.Log.m34e(r3, r0, r13)
            goto L_0x016e
        */
        throw new UnsupportedOperationException("Method not decompiled: p006ti.modules.titanium.network.TiHTTPClient.handleResponse(java.net.HttpURLConnection):void");
    }

    private TiFile createFileResponseData(boolean dumpResponseOut) throws IOException {
        TiFile tiFile = null;
        File outFile = null;
        if (this.responseFile != null) {
            tiFile = this.responseFile;
            outFile = tiFile.getFile();
            try {
                this.responseOut = new FileOutputStream(outFile, dumpResponseOut);
                TiApplication app = TiApplication.getInstance();
                if (app != null) {
                    app.getTempFileHelper().excludeFileOnCleanup(outFile);
                }
            } catch (FileNotFoundException e) {
                this.responseFile = null;
                tiFile = null;
                if (Log.isDebugModeEnabled()) {
                    Log.m32e(TAG, "Unable to create / write to the response file. Will write the response data to the internal data directory.");
                }
            }
        }
        if (tiFile == null) {
            outFile = TiFileFactory.createDataFile("tihttp", "tmp");
            tiFile = new TiFile(outFile, outFile.getAbsolutePath(), false);
        }
        if (dumpResponseOut) {
            tiFile.write(TiBlob.blobFromData(((ByteArrayOutputStream) this.responseOut).toByteArray()), false);
        }
        this.responseOut = new FileOutputStream(outFile, dumpResponseOut);
        this.responseData = TiBlob.blobFromFile(tiFile, this.contentType);
        return tiFile;
    }

    private void createFileFromBlob(TiBlob blob, File file) throws FileNotFoundException, IOException {
        BufferedInputStream bufferedInput = new BufferedInputStream(blob.getInputStream());
        BufferedOutputStream bufferedOutput = new BufferedOutputStream(new FileOutputStream(file));
        byte[] buffer = new byte[8388608];
        while (true) {
            int available = bufferedInput.read(buffer);
            if (available > 0) {
                bufferedOutput.write(buffer, 0, available);
            } else {
                bufferedOutput.flush();
                bufferedOutput.close();
                bufferedInput.close();
                return;
            }
        }
    }

    private void handleEntityData(byte[] data2, int size, long totalSize, long contentLength) throws IOException {
        if (this.responseOut == null) {
            if (this.responseFile != null) {
                createFileResponseData(false);
            } else if (contentLength > this.maxBufferSize) {
                createFileResponseData(false);
            } else {
                this.responseOut = new ByteArrayOutputStream((int) (contentLength > 0 ? contentLength : 512));
            }
        }
        if (totalSize > this.maxBufferSize && (this.responseOut instanceof ByteArrayOutputStream)) {
            createFileResponseData(true);
        }
        this.responseOut.write(data2, 0, size);
        KrollDict callbackData = new KrollDict();
        callbackData.put("totalCount", Long.valueOf(contentLength));
        callbackData.put("totalSize", Long.valueOf(totalSize));
        callbackData.put("size", Integer.valueOf(size));
        byte[] blobData = new byte[size];
        System.arraycopy(data2, 0, blobData, 0, size);
        callbackData.put("blob", TiBlob.blobFromData(blobData, this.contentType));
        double progress = ((double) totalSize) / ((double) contentLength);
        if (progress > 1.0d || progress < 0.0d) {
            progress = -1.0d;
        }
        callbackData.put("progress", Double.valueOf(progress));
        dispatchCallback(TiC.PROPERTY_ONDATASTREAM, callbackData);
    }

    private void finishedReceivingEntityData(long contentLength) throws IOException {
        if (this.responseOut instanceof ByteArrayOutputStream) {
            this.responseData = TiBlob.blobFromData(((ByteArrayOutputStream) this.responseOut).toByteArray(), this.contentType);
        }
        this.responseOut.close();
        this.responseOut = null;
    }

    public TiHTTPClient(HTTPClientProxy proxy2) {
        this.proxy = proxy2;
        if (httpClientThreadCounter == null) {
            httpClientThreadCounter = new AtomicInteger();
        }
        this.readyState = 0;
        this.responseText = "";
        this.connected = false;
        this.nvPairs = new ArrayList<>();
        this.parts = new HashMap<>();
        this.maxBufferSize = (long) TiApplication.getInstance().getAppProperties().getInt(PROPERTY_MAX_BUFFER_SIZE, 524288);
    }

    public int getReadyState() {
        synchronized (this) {
            notify();
        }
        return this.readyState;
    }

    public boolean validatesSecureCertificate() {
        if (this.proxy.hasProperty("validatesSecureCertificate")) {
            return TiConvert.toBoolean(this.proxy.getProperty("validatesSecureCertificate"));
        }
        if (TiApplication.getInstance().getDeployType().equals(TiApplication.DEPLOY_TYPE_PRODUCTION)) {
            return true;
        }
        return false;
    }

    public void setReadyState(int readyState2) {
        Log.m28d(TAG, "Setting ready state to " + readyState2);
        this.readyState = readyState2;
        KrollDict data2 = new KrollDict();
        data2.put("readyState", Integer.valueOf(readyState2));
        dispatchCallback(TiC.PROPERTY_ONREADYSTATECHANGE, data2);
        if (readyState2 == 4) {
            KrollDict data1 = new KrollDict();
            data1.putCodeAndMessage(0, null);
            dispatchCallback(TiC.PROPERTY_ONLOAD, data1);
        }
    }

    private String decodeResponseData(String charsetName) {
        String str = null;
        try {
            try {
                return Charset.forName(charsetName).newDecoder().decode(ByteBuffer.wrap(this.responseData.getBytes())).toString();
            } catch (CharacterCodingException e) {
                return str;
            } catch (OutOfMemoryError e2) {
                Log.m32e(TAG, "Not enough memory to decode response data.");
                return str;
            }
        } catch (IllegalArgumentException e3) {
            Log.m32e(TAG, "Could not find charset: " + e3.getMessage());
            return str;
        }
    }

    private String detectResponseDataEncoding() {
        String regex;
        if (this.contentType == null) {
            Log.m45w(TAG, "Could not detect charset, no content type specified.", Log.DEBUG_MODE);
            return null;
        }
        if (this.contentType.contains("xml")) {
            regex = XML_DECLARATION_TAG_REGEX;
        } else if (this.contentType.contains(TiC.PROPERTY_HTML)) {
            regex = HTML_META_TAG_REGEX;
        } else {
            Log.m45w(TAG, "Cannot detect charset, unknown content type: " + this.contentType, Log.DEBUG_MODE);
            return null;
        }
        Matcher matcher = Pattern.compile(regex).matcher(this.responseData.toString());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public String getResponseText() {
        if (this.responseText != null || this.responseData == null) {
            return this.responseText;
        }
        if (this.charset != null) {
            this.responseText = decodeResponseData(this.charset);
            if (this.responseText != null) {
                return this.responseText;
            }
        }
        String detectedCharset = detectResponseDataEncoding();
        if (detectedCharset != null) {
            Log.m29d(TAG, "detected charset: " + detectedCharset, Log.DEBUG_MODE);
            this.responseText = decodeResponseData(detectedCharset);
            if (this.responseText != null) {
                this.charset = detectedCharset;
                return this.responseText;
            }
        }
        for (String charset2 : FALLBACK_CHARSETS) {
            this.responseText = decodeResponseData(charset2);
            if (this.responseText != null) {
                return this.responseText;
            }
        }
        Log.m32e(TAG, "Could not decode response text.");
        return this.responseText;
    }

    public TiBlob getResponseData() {
        return this.responseData;
    }

    public DocumentProxy getResponseXML() {
        if (TiMimeTypeHelper.isBinaryMimeType(this.contentType)) {
            return null;
        }
        if (this.responseXml == null && !(this.responseData == null && this.responseText == null)) {
            try {
                String text = getResponseText();
                if (text == null || text.length() == 0) {
                    return null;
                }
                if (this.charset == null || this.charset.length() <= 0) {
                    this.responseXml = XMLModule.parse(text);
                } else {
                    this.responseXml = XMLModule.parse(text, this.charset);
                }
            } catch (Exception e) {
                Log.m34e(TAG, "Error parsing XML", (Throwable) e);
            }
        }
        return this.responseXml;
    }

    public void setResponseText(String responseText2) {
        this.responseText = responseText2;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status2) {
        this.status = status2;
    }

    public String getStatusText() {
        return this.statusText;
    }

    public void setStatusText(String statusText2) {
        this.statusText = statusText2;
    }

    public void abort() {
        if (this.readyState > 0 && this.readyState < 4) {
            this.aborted = true;
            if (this.client != null) {
                this.client.disconnect();
                this.client = null;
            }
            this.proxy.fireEvent(TiC.EVENT_DISPOSE_HANDLE, null);
        }
    }

    public String getAllResponseHeaders() {
        String result = "";
        if (this.responseHeaders == null || this.responseHeaders.isEmpty()) {
            return result;
        }
        StringBuilder sb = new StringBuilder(256);
        for (Entry<String, List<String>> entry : this.responseHeaders.entrySet()) {
            sb.append((String) entry.getKey()).append(":");
            for (String value : (List) entry.getValue()) {
                sb.append(value).append("\n");
            }
        }
        return sb.toString();
    }

    public void clearCookies(String url2) {
        URI uriDomain;
        List<HttpCookie> cookies = new ArrayList<>(cookieManager.getCookieStore().getCookies());
        cookieManager.getCookieStore().removeAll();
        String lower_url = url2.toLowerCase();
        for (HttpCookie cookie : cookies) {
            String cookieDomain = cookie.getDomain();
            if (!lower_url.contains(cookieDomain.toLowerCase())) {
                try {
                    uriDomain = new URI(cookieDomain);
                } catch (URISyntaxException e) {
                    uriDomain = null;
                }
                cookieManager.getCookieStore().add(uriDomain, cookie);
            }
        }
    }

    public void setRequestHeader(String header, String value) {
        if (this.readyState > 1) {
            throw new IllegalStateException("setRequestHeader can only be called before invoking send.");
        } else if (value == null) {
            this.requestHeaders.remove(header);
        } else if (this.requestHeaders.containsKey(header)) {
            String separator = "Cookie".equalsIgnoreCase(header) ? "; " : ", ";
            StringBuffer val = new StringBuffer((String) this.requestHeaders.get(header));
            val.append(separator + value);
            this.requestHeaders.put(header, val.toString());
        } else {
            this.requestHeaders.put(header, value);
        }
    }

    public String getResponseHeader(String getHeaderName) {
        String result = "";
        if (!this.responseHeaders.isEmpty()) {
            boolean firstPass = true;
            StringBuilder sb = new StringBuilder(256);
            for (Entry<String, List<String>> entry : this.responseHeaders.entrySet()) {
                String headerName = (String) entry.getKey();
                if (headerName != null && headerName.equalsIgnoreCase(getHeaderName)) {
                    for (String value : (List) entry.getValue()) {
                        if (!firstPass) {
                            sb.append(", ");
                        }
                        sb.append(value);
                        firstPass = false;
                    }
                }
            }
            result = sb.toString();
        }
        if (result.length() == 0) {
            Log.m45w(TAG, "No value for response header: " + getHeaderName, Log.DEBUG_MODE);
        }
        return result;
    }

    public void open(String method2, String url2) {
        if (this.requestPending) {
            Log.m44w(TAG, "open cancelled, a request is already pending for response.");
            return;
        }
        Log.m29d(TAG, "open request method=" + method2 + " url=" + url2, Log.DEBUG_MODE);
        if (url2 == null) {
            Log.m32e(TAG, "Unable to open a null URL");
            throw new IllegalArgumentException("URL cannot be null");
        }
        String lowerCaseUrl = url2.toLowerCase();
        if (!lowerCaseUrl.startsWith("http://") && !lowerCaseUrl.startsWith("https://")) {
            url2 = "http://" + url2;
        }
        if (this.autoEncodeUrl) {
            this.uri = TiUrl.getCleanUri(url2);
        } else {
            this.uri = Uri.parse(url2);
        }
        if (!this.autoEncodeUrl || url2.matches(".*\\?.*\\%\\d\\d.*$")) {
            this.url = url2;
        } else {
            this.url = this.uri.toString();
        }
        this.redirectedLocation = null;
        this.method = method2;
        String hostString = this.uri.getHost();
        int port = -1;
        if (this.uri.getUserInfo() == null || !this.uri.getUserInfo().contains("@")) {
            port = this.uri.getPort();
        } else {
            try {
                URL javaUrl = new URL(this.uri.toString());
                hostString = javaUrl.getHost();
                port = javaUrl.getPort();
            } catch (MalformedURLException e) {
                Log.m34e(TAG, "Error attempting to derive Java url from uri: " + e.getMessage(), (Throwable) e);
            }
        }
        Log.m29d(TAG, "Instantiating host with hostString='" + hostString + "', port='" + port + "', scheme='" + this.uri.getScheme() + "'", Log.DEBUG_MODE);
        this.username = this.proxy.getUsername();
        this.password = this.proxy.getPassword();
        if (!(this.username == null || this.password == null)) {
            this.hasAuthentication = true;
        }
        setReadyState(1);
        setRequestHeader("User-Agent", TITANIUM_USER_AGENT);
        if (!hostString.contains("twitter.com")) {
            setRequestHeader("X-Requested-With", "XMLHttpRequest");
        } else {
            Log.m37i(TAG, "Twitter: not sending X-Requested-With header", Log.DEBUG_MODE);
        }
    }

    public void setRawData(Object data2) {
        this.data = data2;
    }

    public void addPostData(String name, String value) throws UnsupportedEncodingException {
        if (value == null) {
            value = "";
        }
        if (this.needMultipart) {
            this.parts.put(name, new StringBody(value, "", null));
        } else {
            this.nvPairs.add(new NameValuePair(name, value.toString()));
        }
    }

    /* access modifiers changed from: private */
    public void dispatchCallback(String name, KrollDict data2) {
        if (data2 == null) {
            data2 = new KrollDict();
        }
        data2.put("source", this.proxy);
        this.proxy.callPropertyAsync(name, new Object[]{data2});
    }

    private int addTitaniumFileAsPostData(String name, Object value) {
        TiBlob blob;
        try {
            if ((value instanceof TiBaseFile) && !(value instanceof TiResourceFile)) {
                TiBaseFile baseFile = (TiBaseFile) value;
                this.parts.put(name, new FileBody(baseFile.getNativeFile(), TiMimeTypeHelper.getMimeType(baseFile.nativePath())));
                return (int) baseFile.getNativeFile().length();
            } else if ((value instanceof TiBlob) || (value instanceof TiResourceFile)) {
                if (value instanceof TiBlob) {
                    blob = (TiBlob) value;
                } else {
                    blob = ((TiResourceFile) value).read();
                }
                String mimeType = blob.getMimeType();
                File tmpFile = File.createTempFile("tixhr", TiUrl.CURRENT_PATH + TiMimeTypeHelper.getFileExtensionFromMimeType(mimeType, "txt"));
                if (blob.getType() == 4) {
                    FileOutputStream fos = new FileOutputStream(tmpFile);
                    TiBaseFile.copyStream(blob.getInputStream(), (OutputStream) new Base64OutputStream(fos, 0));
                    fos.close();
                } else {
                    createFileFromBlob(blob, tmpFile);
                }
                this.tmpFiles.add(tmpFile);
                this.parts.put(name, new FileBody(tmpFile, mimeType));
                return (int) tmpFile.length();
            } else if (value instanceof HashMap) {
                JsonBody jsonBody = new JsonBody(TiConvert.toJSON((HashMap) value), null);
                this.parts.put(name, jsonBody);
                return (int) jsonBody.getContentLength();
            } else if (value != null) {
                Log.m32e(TAG, name + " is a " + value.getClass().getSimpleName());
                return 0;
            } else {
                Log.m32e(TAG, name + " is null");
                return 0;
            }
        } catch (IOException e) {
            Log.m32e(TAG, "Error adding post data (" + name + "): " + e.getMessage());
            return 0;
        }
    }

    /* access modifiers changed from: private */
    public void setUpSSL(boolean validating, HttpsURLConnection securedConnection) {
        SSLSocketFactory sslSocketFactory = null;
        if (this.securityManager != null && this.securityManager.willHandleURL(this.uri)) {
            TrustManager[] trustManagerArray = this.securityManager.getTrustManagers(this.proxy);
            try {
                sslSocketFactory = new TiSocketFactory(this.securityManager.getKeyManagers(this.proxy), trustManagerArray, this.tlsVersion);
            } catch (Exception e) {
                Log.m32e(TAG, "Error creating SSLSocketFactory: " + e.getMessage());
                sslSocketFactory = null;
            }
        }
        if (sslSocketFactory == null) {
            if (this.trustManagers.size() > 0 || this.keyManagers.size() > 0) {
                TrustManager[] trustManagerArray2 = null;
                KeyManager[] keyManagerArray = null;
                if (this.trustManagers.size() > 0) {
                    trustManagerArray2 = (TrustManager[]) this.trustManagers.toArray(new X509TrustManager[this.trustManagers.size()]);
                }
                if (this.keyManagers.size() > 0) {
                    keyManagerArray = (KeyManager[]) this.keyManagers.toArray(new X509KeyManager[this.keyManagers.size()]);
                }
                try {
                    sslSocketFactory = new TiSocketFactory(keyManagerArray, trustManagerArray2, this.tlsVersion);
                } catch (Exception e2) {
                    Log.m32e(TAG, "Error creating SSLSocketFactory: " + e2.getMessage());
                    sslSocketFactory = null;
                }
            } else if (!validating) {
                try {
                    sslSocketFactory = new TiSocketFactory(null, new TrustManager[]{new NonValidatingTrustManager()}, this.tlsVersion);
                } catch (Exception e3) {
                    Log.m32e(TAG, "Error creating SSLSocketFactory: " + e3.getMessage());
                    sslSocketFactory = null;
                }
            } else {
                try {
                    sslSocketFactory = new TiSocketFactory(null, null, this.tlsVersion);
                } catch (Exception e4) {
                    Log.m32e(TAG, "Error creating SSLSocketFactory: " + e4.getMessage());
                    sslSocketFactory = null;
                }
            }
        }
        if (sslSocketFactory != null) {
            securedConnection.setSSLSocketFactory(sslSocketFactory);
        } else if (!validating) {
            securedConnection.setSSLSocketFactory(new NonValidatingSSLSocketFactory());
        }
        if (!validating) {
            securedConnection.setHostnameVerifier(new NullHostNameVerifier());
        }
    }

    private Object titaniumFileAsPutData(Object value) {
        TiBlob blob;
        if ((value instanceof TiBaseFile) && !(value instanceof TiResourceFile)) {
            TiBaseFile baseFile = (TiBaseFile) value;
            return new FileEntity(baseFile.getNativeFile(), TiMimeTypeHelper.getMimeType(baseFile.nativePath()));
        } else if (!(value instanceof TiBlob) && !(value instanceof TiResourceFile)) {
            return value;
        } else {
            try {
                if (value instanceof TiBlob) {
                    blob = (TiBlob) value;
                } else {
                    blob = ((TiResourceFile) value).read();
                }
                String mimeType = blob.getMimeType();
                File tmpFile = File.createTempFile("tixhr", TiUrl.CURRENT_PATH + TiMimeTypeHelper.getFileExtensionFromMimeType(mimeType, "txt"));
                createFileFromBlob(blob, tmpFile);
                this.tmpFiles.add(tmpFile);
                return new FileEntity(tmpFile, mimeType);
            } catch (IOException e) {
                Log.m32e(TAG, "Error adding put data: " + e.getMessage());
                return value;
            }
        }
    }

    public void send(Object userData) throws UnsupportedEncodingException {
        boolean isPostOrPutOrPatch;
        boolean isGet = false;
        if (this.requestPending) {
            Log.m44w(TAG, "send cancelled, a request is already pending for response.");
            return;
        }
        this.requestPending = true;
        this.aborted = false;
        int totalLength = 0;
        this.needMultipart = false;
        if (userData != null) {
            if (userData instanceof HashMap) {
                HashMap<String, Object> data2 = (HashMap) userData;
                if (this.method.equals("POST") || this.method.equals("PUT") || this.method.equals("PATCH")) {
                    isPostOrPutOrPatch = true;
                } else {
                    isPostOrPutOrPatch = false;
                }
                if (!isPostOrPutOrPatch && this.method.equals("GET")) {
                    isGet = true;
                }
                Iterator it = data2.keySet().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    Object value = data2.get((String) it.next());
                    if (value != null) {
                        if (value instanceof TiFileProxy) {
                            value = ((TiFileProxy) value).getBaseFile();
                        }
                        if ((value instanceof TiBaseFile) || (value instanceof TiBlob)) {
                            this.needMultipart = true;
                        }
                    }
                }
                boolean queryStringAltered = false;
                for (String key : data2.keySet()) {
                    Object value2 = data2.get(key);
                    if (isPostOrPutOrPatch && value2 != null) {
                        if (value2 instanceof TiFileProxy) {
                            value2 = ((TiFileProxy) value2).getBaseFile();
                        }
                        if ((value2 instanceof TiBaseFile) || (value2 instanceof TiBlob) || (value2 instanceof HashMap)) {
                            totalLength += addTitaniumFileAsPostData(key, value2);
                        } else {
                            String str = TiConvert.toString(value2);
                            addPostData(key, str);
                            totalLength += str.length();
                        }
                    } else if (isGet) {
                        this.uri = this.uri.buildUpon().appendQueryParameter(key, TiConvert.toString(value2)).build();
                        queryStringAltered = true;
                    }
                }
                if (queryStringAltered) {
                    this.url = this.uri.toString();
                }
            } else if ((userData instanceof TiFileProxy) || (userData instanceof TiBaseFile) || (userData instanceof TiBlob)) {
                Object value3 = userData;
                if (value3 instanceof TiFileProxy) {
                    value3 = ((TiFileProxy) value3).getBaseFile();
                }
                if ((value3 instanceof TiBaseFile) || (value3 instanceof TiBlob)) {
                    setRawData(titaniumFileAsPutData(value3));
                } else {
                    setRawData(TiConvert.toString(value3));
                }
            } else {
                setRawData(TiConvert.toString(userData));
            }
        }
        Log.m29d(TAG, "Instantiating http request with method='" + this.method + "' and this url:", Log.DEBUG_MODE);
        Log.m29d(TAG, this.url, Log.DEBUG_MODE);
        this.clientThread = new Thread(new ClientRunnable(totalLength), "TiHttpClient-" + httpClientThreadCounter.incrementAndGet());
        this.clientThread.setPriority(1);
        this.clientThread.start();
        Log.m29d(TAG, "Leaving send()", Log.DEBUG_MODE);
    }

    /* access modifiers changed from: private */
    public void deleteTmpFiles() {
        if (!this.tmpFiles.isEmpty()) {
            Iterator it = this.tmpFiles.iterator();
            while (it.hasNext()) {
                ((File) it.next()).delete();
            }
            this.tmpFiles.clear();
        }
    }

    public String getLocation() {
        if (this.redirectedLocation != null) {
            return this.redirectedLocation;
        }
        return this.url;
    }

    public String getConnectionType() {
        return this.method;
    }

    public boolean isConnected() {
        return this.connected;
    }

    public void setTimeout(int millis) {
        this.timeout = millis;
    }

    /* access modifiers changed from: protected */
    public void setAutoEncodeUrl(boolean value) {
        this.autoEncodeUrl = value;
    }

    /* access modifiers changed from: protected */
    public boolean getAutoEncodeUrl() {
        return this.autoEncodeUrl;
    }

    /* access modifiers changed from: protected */
    public void setAutoRedirect(boolean value) {
        this.autoRedirect = value;
    }

    /* access modifiers changed from: protected */
    public boolean getAutoRedirect() {
        return this.autoRedirect;
    }

    /* access modifiers changed from: protected */
    public void addKeyManager(X509KeyManager manager) {
        if (Log.isDebugModeEnabled()) {
            Log.m29d(TAG, "addKeyManager method is deprecated. Use the securityManager property on the HttpClient to define custom SSL Contexts", Log.DEBUG_MODE);
        }
        this.keyManagers.add(manager);
    }

    /* access modifiers changed from: protected */
    public void addTrustManager(X509TrustManager manager) {
        if (Log.isDebugModeEnabled()) {
            Log.m29d(TAG, "addTrustManager method is deprecated. Use the securityManager property on the HttpClient to define custom SSL Contexts", Log.DEBUG_MODE);
        }
        this.trustManagers.add(manager);
    }

    /* access modifiers changed from: protected */
    public void setTlsVersion(int value) {
        this.proxy.setProperty(TiC.PROPERTY_TLS_VERSION, Integer.valueOf(value));
        this.tlsVersion = value;
    }
}
