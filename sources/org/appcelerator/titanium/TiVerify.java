package org.appcelerator.titanium;

import android.app.Activity;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollModuleInfo;
import org.appcelerator.kroll.common.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import p006ti.modules.titanium.network.httpurlconnection.HttpUrlConnectionUtils;

public class TiVerify extends TimerTask {

    /* renamed from: b */
    private static byte[] f29b = {97, 72, 82, 48, 99, 72, 77, 54, 76, 121, 57, 104, 99, 71, 107, 117, 89, 88, 66, 119, 89, 50, 86, 115, 90, 88, 74, 104, 100, 71, 57, 121, 76, 109, 53, 108, 100, 67, 57, 119, 76, 51, 89, 120, 76, 50, 49, 118, 90, 72, 86, 115, 90, 83, 49, 50, 90, 88, 74, 112, 90, 110, 107, 61};

    /* renamed from: a */
    protected TiApplication f30a;

    /* renamed from: c */
    private Activity f31c;

    /* renamed from: d */
    private JSONArray f32d;

    /* renamed from: e */
    private String f33e;

    /* renamed from: f */
    private File f34f;

    static {
        try {
            System.loadLibrary("tiverify");
        } catch (Throwable th) {
            Log.m34e("TiVerify", "Failed to load library.", th);
        }
    }

    public TiVerify(Activity activity, TiApplication tiApplication) {
        this.f31c = activity;
        this.f30a = tiApplication;
    }

    /* renamed from: a */
    private static JSONObject m48a(KrollModuleInfo krollModuleInfo) {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("author", krollModuleInfo.getAuthor());
            jSONObject.put("copyright", krollModuleInfo.getCopyright());
            jSONObject.put("description", krollModuleInfo.getDescription());
            jSONObject.put("guid", krollModuleInfo.getGuid());
            jSONObject.put(TiC.PROPERTY_ID, krollModuleInfo.getId());
            jSONObject.put("license", krollModuleInfo.getLicense());
            jSONObject.put("licenseKey", krollModuleInfo.getLicenseKey());
            jSONObject.put(TiC.PROPERTY_NAME, krollModuleInfo.getName());
            jSONObject.put(TiC.PROPERTY_VERSION, krollModuleInfo.getVersion());
            return jSONObject;
        } catch (JSONException e) {
            Log.m46w("TiVerify", "Error Generating Module Info JSON", (Throwable) e);
            return null;
        }
    }

    /* renamed from: a */
    private void m49a(HttpResponse httpResponse) {
        HttpEntity entity = httpResponse.getEntity();
        if (entity != null) {
            try {
                InputStream content = entity.getContent();
                StringBuilder sb = new StringBuilder();
                byte[] bArr = new byte[1024];
                while (true) {
                    int read = content.read(bArr);
                    if (read == -1) {
                        break;
                    }
                    sb.append(new String(bArr, 0, read, HttpUrlConnectionUtils.UTF_8));
                }
                entity.consumeContent();
                JSONObject jSONObject = new JSONObject(sb.toString());
                if (jSONObject.getBoolean("valid")) {
                    Log.m36i("TiVerify", "Succesfully verified module licenses");
                    FileWriter fileWriter = new FileWriter(this.f34f);
                    fileWriter.write(this.f33e);
                    fileWriter.close();
                    return;
                }
                try {
                    String string = jSONObject.getString("error");
                    Log.m32e("TiVerify", "License violation detected. " + string + ". Please contact Appcelerator Support.");
                    this.f31c.runOnUiThread(new C0298a(this, this.f31c, string));
                } catch (JSONException e) {
                    Log.m46w("TiVerify", "Error Parsing License Response Message", (Throwable) e);
                }
            } catch (Exception e2) {
                Log.m46w("TiVerify", "Error Verifying License Response", (Throwable) e2);
            }
        } else {
            Log.m44w("TiVerify", "Received empty response from Verify Service with Status: " + httpResponse.getStatusLine().getStatusCode());
        }
    }

    /* renamed from: a */
    private boolean m50a() {
        if (this.f34f.exists()) {
            try {
                int length = (int) this.f34f.length();
                byte[] bArr = new byte[length];
                FileInputStream fileInputStream = new FileInputStream(this.f34f);
                int read = fileInputStream.read(bArr, 0, length);
                fileInputStream.close();
                if (read == length && new String(bArr, HttpUrlConnectionUtils.UTF_8).equals(this.f33e) && System.currentTimeMillis() - this.f34f.lastModified() < 86400000) {
                    return false;
                }
            } catch (Exception e) {
                Log.m46w("TiVerify", "Error Checking Module License File", (Throwable) e);
            }
        }
        return true;
    }

    public static native byte[] filterDataInRange(byte[] bArr, int i, int i2);

    /* JADX WARNING: type inference failed for: r2v5, types: [ti.modules.titanium.network.NonValidatingSSLSocketFactory, org.apache.http.conn.scheme.SocketFactory] */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r2v5, types: [ti.modules.titanium.network.NonValidatingSSLSocketFactory, org.apache.http.conn.scheme.SocketFactory]
      assigns: [ti.modules.titanium.network.NonValidatingSSLSocketFactory]
      uses: [org.apache.http.conn.scheme.SocketFactory]
      mth insns count: 93
    	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
    	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
    	at jadx.core.ProcessClass.process(ProcessClass.java:30)
    	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:49)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:49)
    	at jadx.core.ProcessClass.process(ProcessClass.java:35)
    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
     */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void run() {
        /*
            r8 = this;
            r3 = 0
            org.json.JSONArray r1 = r8.f32d
            java.lang.String r1 = r1.toString()
            java.lang.String r1 = org.apache.commons.codec.digest.DigestUtils.shaHex(r1)
            r8.f33e = r1
            org.appcelerator.titanium.TiApplication r1 = r8.f30a
            android.content.Context r1 = r1.getApplicationContext()
            java.lang.String r2 = "appdata"
            java.io.File r1 = r1.getDir(r2, r3)
            java.io.File r2 = new java.io.File
            java.lang.String r3 = ".tilicense"
            r2.<init>(r1, r3)
            r8.f34f = r2
            boolean r1 = r8.m50a()
            if (r1 == 0) goto L_0x0101
            java.lang.String r1 = "TiVerify"
            java.lang.String r2 = "Verifying module licenses..."
            org.appcelerator.kroll.common.Log.m44w(r1, r2)     // Catch:{ Exception -> 0x0120 }
            org.apache.http.conn.scheme.SchemeRegistry r1 = new org.apache.http.conn.scheme.SchemeRegistry     // Catch:{ Exception -> 0x0120 }
            r1.<init>()     // Catch:{ Exception -> 0x0120 }
            org.apache.http.conn.scheme.Scheme r2 = new org.apache.http.conn.scheme.Scheme     // Catch:{ Exception -> 0x0120 }
            java.lang.String r3 = "http"
            org.apache.http.conn.scheme.PlainSocketFactory r4 = org.apache.http.conn.scheme.PlainSocketFactory.getSocketFactory()     // Catch:{ Exception -> 0x0120 }
            r5 = 80
            r2.<init>(r3, r4, r5)     // Catch:{ Exception -> 0x0120 }
            r1.register(r2)     // Catch:{ Exception -> 0x0120 }
            ti.modules.titanium.network.NonValidatingSSLSocketFactory r2 = new ti.modules.titanium.network.NonValidatingSSLSocketFactory     // Catch:{ Exception -> 0x0120 }
            r2.<init>()     // Catch:{ Exception -> 0x0120 }
            org.apache.http.conn.scheme.Scheme r3 = new org.apache.http.conn.scheme.Scheme     // Catch:{ Exception -> 0x0120 }
            java.lang.String r4 = "https"
            r5 = 443(0x1bb, float:6.21E-43)
            r3.<init>(r4, r2, r5)     // Catch:{ Exception -> 0x0120 }
            r1.register(r3)     // Catch:{ Exception -> 0x0120 }
            org.apache.http.params.BasicHttpParams r2 = new org.apache.http.params.BasicHttpParams     // Catch:{ Exception -> 0x0120 }
            r2.<init>()     // Catch:{ Exception -> 0x0120 }
            r3 = 15000(0x3a98, float:2.102E-41)
            org.apache.http.params.HttpConnectionParams.setConnectionTimeout(r2, r3)     // Catch:{ Exception -> 0x0120 }
            r3 = 15000(0x3a98, float:2.102E-41)
            org.apache.http.params.HttpConnectionParams.setSoTimeout(r2, r3)     // Catch:{ Exception -> 0x0120 }
            org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager r3 = new org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager     // Catch:{ Exception -> 0x0120 }
            r3.<init>(r2, r1)     // Catch:{ Exception -> 0x0120 }
            org.apache.http.impl.client.DefaultHttpClient r4 = new org.apache.http.impl.client.DefaultHttpClient     // Catch:{ Exception -> 0x0120 }
            r4.<init>(r3, r2)     // Catch:{ Exception -> 0x0120 }
            org.apache.http.params.HttpParams r1 = r4.getParams()     // Catch:{ Exception -> 0x0120 }
            r2 = 0
            org.apache.http.params.HttpProtocolParams.setUseExpectContinue(r1, r2)     // Catch:{ Exception -> 0x0120 }
            org.apache.http.params.HttpParams r1 = r4.getParams()     // Catch:{ Exception -> 0x0120 }
            org.apache.http.HttpVersion r2 = org.apache.http.HttpVersion.HTTP_1_1     // Catch:{ Exception -> 0x0120 }
            org.apache.http.params.HttpProtocolParams.setVersion(r1, r2)     // Catch:{ Exception -> 0x0120 }
            java.lang.String r1 = new java.lang.String     // Catch:{ Exception -> 0x0120 }
            byte[] r2 = f29b     // Catch:{ Exception -> 0x0120 }
            byte[] r2 = org.apache.commons.codec.binary.Base64.decodeBase64(r2)     // Catch:{ Exception -> 0x0120 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x0120 }
            android.net.Uri r1 = android.net.Uri.parse(r1)     // Catch:{ Exception -> 0x0120 }
            org.json.JSONObject r2 = new org.json.JSONObject     // Catch:{ Exception -> 0x0120 }
            r2.<init>()     // Catch:{ Exception -> 0x0120 }
            org.appcelerator.titanium.TiApplication r3 = r8.f30a     // Catch:{ Exception -> 0x0120 }
            org.appcelerator.titanium.ITiAppInfo r3 = r3.getAppInfo()     // Catch:{ Exception -> 0x0120 }
            java.lang.String r5 = "platform"
            java.lang.String r6 = "android"
            r2.put(r5, r6)     // Catch:{ Exception -> 0x0120 }
            java.lang.String r5 = "guid"
            java.lang.String r6 = r3.getGUID()     // Catch:{ Exception -> 0x0120 }
            r2.put(r5, r6)     // Catch:{ Exception -> 0x0120 }
            java.lang.String r5 = "appid"
            java.lang.String r6 = r3.getId()     // Catch:{ Exception -> 0x0120 }
            r2.put(r5, r6)     // Catch:{ Exception -> 0x0120 }
            java.lang.String r5 = "name"
            java.lang.String r6 = r3.getName()     // Catch:{ Exception -> 0x0120 }
            r2.put(r5, r6)     // Catch:{ Exception -> 0x0120 }
            java.lang.String r5 = "version"
            java.lang.String r3 = r3.getVersion()     // Catch:{ Exception -> 0x0120 }
            r2.put(r5, r3)     // Catch:{ Exception -> 0x0120 }
            java.lang.String r3 = "deploytype"
            org.appcelerator.titanium.TiApplication r5 = r8.f30a     // Catch:{ Exception -> 0x0120 }
            java.lang.String r5 = r5.getDeployType()     // Catch:{ Exception -> 0x0120 }
            r2.put(r3, r5)     // Catch:{ Exception -> 0x0120 }
            java.lang.String r3 = "modules"
            org.json.JSONArray r5 = r8.f32d     // Catch:{ Exception -> 0x0120 }
            r2.put(r3, r5)     // Catch:{ Exception -> 0x0120 }
            java.lang.String r3 = r2.toString()     // Catch:{ Exception -> 0x0120 }
            org.apache.http.HttpHost r5 = new org.apache.http.HttpHost     // Catch:{ Exception -> 0x0120 }
            java.lang.String r2 = r1.getHost()     // Catch:{ Exception -> 0x0120 }
            int r6 = r1.getPort()     // Catch:{ Exception -> 0x0120 }
            r5.<init>(r2, r6)     // Catch:{ Exception -> 0x0120 }
            org.apache.http.impl.DefaultHttpRequestFactory r2 = new org.apache.http.impl.DefaultHttpRequestFactory     // Catch:{ Exception -> 0x0120 }
            r2.<init>()     // Catch:{ Exception -> 0x0120 }
            java.lang.String r6 = "POST"
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0120 }
            org.apache.http.HttpRequest r2 = r2.newHttpRequest(r6, r1)     // Catch:{ Exception -> 0x0120 }
            boolean r1 = r2 instanceof org.apache.http.HttpEntityEnclosingRequest     // Catch:{ Exception -> 0x0120 }
            if (r1 != 0) goto L_0x0105
            java.lang.String r1 = "TiVerify"
            java.lang.String r2 = "Error creating entity request"
            org.appcelerator.kroll.common.Log.m32e(r1, r2)     // Catch:{ Exception -> 0x0120 }
        L_0x0101:
            r1 = 0
            r8.f31c = r1
            return
        L_0x0105:
            r0 = r2
            org.apache.http.HttpEntityEnclosingRequest r0 = (org.apache.http.HttpEntityEnclosingRequest) r0     // Catch:{ Exception -> 0x0120 }
            r1 = r0
            org.apache.http.entity.StringEntity r6 = new org.apache.http.entity.StringEntity     // Catch:{ Exception -> 0x0120 }
            java.lang.String r7 = "UTF-8"
            r6.<init>(r3, r7)     // Catch:{ Exception -> 0x0120 }
            java.lang.String r3 = "application/x-www-form-urlencoded"
            r6.setContentType(r3)     // Catch:{ Exception -> 0x0120 }
            r1.setEntity(r6)     // Catch:{ Exception -> 0x0120 }
            org.apache.http.HttpResponse r1 = r4.execute(r5, r2)     // Catch:{ Exception -> 0x0120 }
            r8.m49a(r1)     // Catch:{ Exception -> 0x0120 }
            goto L_0x0101
        L_0x0120:
            r1 = move-exception
            java.lang.String r2 = "TiVerify"
            java.lang.String r3 = "Error Verifying License"
            org.appcelerator.kroll.common.Log.m46w(r2, r3, r1)
            goto L_0x0101
        */
        throw new UnsupportedOperationException("Method not decompiled: org.appcelerator.titanium.TiVerify.run():void");
    }

    public void verify() {
        if (!this.f30a.getDeployType().equals(TiApplication.DEPLOY_TYPE_PRODUCTION)) {
            this.f32d = new JSONArray();
            Iterator it = KrollModule.getCustomModuleInfoList().iterator();
            while (it.hasNext()) {
                KrollModuleInfo krollModuleInfo = (KrollModuleInfo) it.next();
                if (krollModuleInfo != null) {
                    JSONObject a = m48a(krollModuleInfo);
                    if (a != null) {
                        this.f32d.put(a);
                    }
                }
            }
            if (this.f32d.length() != 0) {
                new Timer().schedule(this, 5000);
            }
        }
    }
}
