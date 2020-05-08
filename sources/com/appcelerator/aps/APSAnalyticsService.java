package com.appcelerator.aps;

import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

public class APSAnalyticsService extends Service {
    private static final String ANALYTICS_URL = "https://api.appcelerator.com/p/v3/mobile-track/";
    private static final int BUCKET_SIZE_FAST_NETWORK = 10;
    private static final int BUCKET_SIZE_SLOW_NETWORK = 5;
    private static final String KEY_DATA = "data";
    private static final String KEY_VALUE = "value";
    private static final String TAG = "APSAnalyticsService";
    /* access modifiers changed from: private */
    public static String analyticsURL;
    /* access modifiers changed from: private */
    public ConnectivityManager connectivityManager;
    /* access modifiers changed from: private */
    public AtomicBoolean sending;

    public APSAnalyticsService() {
        if (this.sending == null) {
            this.sending = new AtomicBoolean(false);
        }
    }

    public void onCreate() {
        super.onCreate();
        if (analyticsURL == null) {
            analyticsURL = ANALYTICS_URL;
        }
        this.connectivityManager = (ConnectivityManager) getSystemService("connectivity");
    }

    public void onDestroy() {
        super.onDestroy();
        this.connectivityManager = null;
    }

    protected static void setAnalyticsUrl(URL url) {
        if (url != null) {
            analyticsURL = url.toString();
        }
    }

    public int onStartCommand(Intent intent, int flags, final int startId) {
        super.onStartCommand(intent, flags, startId);
        if (!this.sending.compareAndSet(false, true)) {
            Log.i(TAG, "Send already in progress, skipping intent");
        } else {
            Thread t = new Thread(new Runnable() {
                /* JADX WARNING: Code restructure failed: missing block: B:67:?, code lost:
                    android.util.Log.e(com.appcelerator.aps.APSAnalyticsService.TAG, "Failed to send analytics events after 5 attempts");
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:91:?, code lost:
                    android.util.Log.w(com.appcelerator.aps.APSAnalyticsService.TAG, "Network unavailable, can't send analytics");
                 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void run() {
                    /*
                        r28 = this;
                        java.lang.String r24 = "APSAnalyticsService"
                        java.lang.String r25 = "Analytics Service Started"
                        android.util.Log.i(r24, r25)
                        r20 = 0
                        r0 = r28
                        com.appcelerator.aps.APSAnalyticsService r0 = com.appcelerator.aps.APSAnalyticsService.this     // Catch:{ Throwable -> 0x0325 }
                        r24 = r0
                        android.net.ConnectivityManager r24 = r24.connectivityManager     // Catch:{ Throwable -> 0x0325 }
                        if (r24 != 0) goto L_0x0051
                        java.lang.String r24 = "APSAnalyticsService"
                        java.lang.String r25 = "Connectivity manager not available."
                        android.util.Log.w(r24, r25)     // Catch:{ Throwable -> 0x0325 }
                        r0 = r28
                        com.appcelerator.aps.APSAnalyticsService r0 = com.appcelerator.aps.APSAnalyticsService.this     // Catch:{ Throwable -> 0x0325 }
                        r24 = r0
                        r0 = r28
                        int r0 = r8     // Catch:{ Throwable -> 0x0325 }
                        r25 = r0
                        r24.stopSelf(r25)     // Catch:{ Throwable -> 0x0325 }
                        r0 = r28
                        com.appcelerator.aps.APSAnalyticsService r0 = com.appcelerator.aps.APSAnalyticsService.this
                        r24 = r0
                        java.util.concurrent.atomic.AtomicBoolean r24 = r24.sending
                        r25 = 1
                        r26 = 0
                        boolean r24 = r24.compareAndSet(r25, r26)
                        if (r24 != 0) goto L_0x0050
                        java.lang.String r24 = "APSAnalyticsService"
                        r25 = 3
                        boolean r24 = android.util.Log.isLoggable(r24, r25)
                        if (r24 == 0) goto L_0x0050
                        java.lang.String r24 = "APSAnalyticsService"
                        java.lang.String r25 = "Expected to be in a sending state. Sending was already false."
                        android.util.Log.w(r24, r25)
                    L_0x0050:
                        return
                    L_0x0051:
                        com.appcelerator.aps.APSAnalyticsModel r15 = new com.appcelerator.aps.APSAnalyticsModel     // Catch:{ Throwable -> 0x0325 }
                        r0 = r28
                        com.appcelerator.aps.APSAnalyticsService r0 = r0     // Catch:{ Throwable -> 0x0325 }
                        r24 = r0
                        r0 = r24
                        r15.<init>(r0)     // Catch:{ Throwable -> 0x0325 }
                        boolean r24 = r15.hasEvents()     // Catch:{ Throwable -> 0x0325 }
                        if (r24 != 0) goto L_0x00c8
                        java.lang.String r24 = "APSAnalyticsService"
                        r25 = 3
                        boolean r24 = android.util.Log.isLoggable(r24, r25)     // Catch:{ Throwable -> 0x0325 }
                        if (r24 == 0) goto L_0x0075
                        java.lang.String r24 = "APSAnalyticsService"
                        java.lang.String r25 = "No events to send."
                        android.util.Log.d(r24, r25)     // Catch:{ Throwable -> 0x0325 }
                    L_0x0075:
                        java.lang.String r24 = "APSAnalyticsService"
                        java.lang.String r25 = "Stopping Analytics Service"
                        android.util.Log.i(r24, r25)     // Catch:{ Throwable -> 0x0325 }
                        r0 = r28
                        com.appcelerator.aps.APSAnalyticsService r0 = com.appcelerator.aps.APSAnalyticsService.this     // Catch:{ Throwable -> 0x0325 }
                        r24 = r0
                        r0 = r28
                        int r0 = r8     // Catch:{ Throwable -> 0x0325 }
                        r25 = r0
                        r24.stopSelf(r25)     // Catch:{ Throwable -> 0x0325 }
                        r0 = r28
                        com.appcelerator.aps.APSAnalyticsService r0 = com.appcelerator.aps.APSAnalyticsService.this
                        r24 = r0
                        java.util.concurrent.atomic.AtomicBoolean r24 = r24.sending
                        r25 = 1
                        r26 = 0
                        boolean r24 = r24.compareAndSet(r25, r26)
                        if (r24 != 0) goto L_0x0050
                        java.lang.String r24 = "APSAnalyticsService"
                        r25 = 3
                        boolean r24 = android.util.Log.isLoggable(r24, r25)
                        if (r24 == 0) goto L_0x0050
                        java.lang.String r24 = "APSAnalyticsService"
                        java.lang.String r25 = "Expected to be in a sending state. Sending was already false."
                        android.util.Log.w(r24, r25)
                        goto L_0x0050
                    L_0x00b1:
                        if (r20 <= 0) goto L_0x00c0
                        java.lang.String r24 = "APSAnalyticsService"
                        java.lang.String r25 = "Failed to send anayltics events. Retrying in 15 seconds"
                        android.util.Log.d(r24, r25)     // Catch:{ Throwable -> 0x0325 }
                        r24 = 15000(0x3a98, double:7.411E-320)
                        java.lang.Thread.sleep(r24)     // Catch:{ Throwable -> 0x0325 }
                        r6 = 0
                    L_0x00c0:
                        if (r6 == 0) goto L_0x00c5
                        r15.deleteEvents(r8)     // Catch:{ Throwable -> 0x0325 }
                    L_0x00c5:
                        r9.clear()     // Catch:{ Throwable -> 0x0325 }
                    L_0x00c8:
                        boolean r24 = r15.hasEvents()     // Catch:{ Throwable -> 0x0325 }
                        if (r24 == 0) goto L_0x02a4
                        r0 = r28
                        com.appcelerator.aps.APSAnalyticsService r0 = com.appcelerator.aps.APSAnalyticsService.this     // Catch:{ Throwable -> 0x0325 }
                        r24 = r0
                        boolean r24 = r24.canSend()     // Catch:{ Throwable -> 0x0325 }
                        if (r24 == 0) goto L_0x0369
                        r24 = 10
                        r0 = r24
                        java.util.LinkedHashMap r9 = r15.getEventsAsJSON(r0)     // Catch:{ Throwable -> 0x0325 }
                        int r14 = r9.size()     // Catch:{ Throwable -> 0x0325 }
                        int[] r8 = new int[r14]     // Catch:{ Throwable -> 0x0325 }
                        java.util.Set r24 = r9.keySet()     // Catch:{ Throwable -> 0x0325 }
                        java.util.Iterator r13 = r24.iterator()     // Catch:{ Throwable -> 0x0325 }
                        org.json.JSONArray r18 = new org.json.JSONArray     // Catch:{ Throwable -> 0x0325 }
                        r18.<init>()     // Catch:{ Throwable -> 0x0325 }
                        r10 = 0
                    L_0x00f6:
                        if (r10 >= r14) goto L_0x01ba
                        java.lang.Object r24 = r13.next()     // Catch:{ Throwable -> 0x0325 }
                        java.lang.Integer r24 = (java.lang.Integer) r24     // Catch:{ Throwable -> 0x0325 }
                        int r11 = r24.intValue()     // Catch:{ Throwable -> 0x0325 }
                        r8[r10] = r11     // Catch:{ Throwable -> 0x0325 }
                        java.lang.Integer r24 = java.lang.Integer.valueOf(r11)     // Catch:{ Throwable -> 0x0325 }
                        r0 = r24
                        java.lang.Object r7 = r9.get(r0)     // Catch:{ Throwable -> 0x0325 }
                        org.json.JSONObject r7 = (org.json.JSONObject) r7     // Catch:{ Throwable -> 0x0325 }
                        java.lang.String r24 = "data"
                        r0 = r24
                        boolean r24 = r7.has(r0)     // Catch:{ Throwable -> 0x0325 }
                        if (r24 == 0) goto L_0x0165
                        java.lang.String r24 = "data"
                        r0 = r24
                        org.json.JSONObject r24 = r7.getJSONObject(r0)     // Catch:{ Throwable -> 0x0325 }
                        java.lang.String r25 = "value"
                        boolean r24 = r24.has(r25)     // Catch:{ Throwable -> 0x0325 }
                        if (r24 == 0) goto L_0x0165
                        java.lang.String r24 = "data"
                        r0 = r24
                        org.json.JSONObject r5 = r7.getJSONObject(r0)     // Catch:{ Throwable -> 0x0325 }
                        java.lang.String r24 = "data"
                        r0 = r24
                        org.json.JSONObject r24 = r7.getJSONObject(r0)     // Catch:{ Throwable -> 0x0325 }
                        java.lang.String r25 = "value"
                        java.lang.Object r23 = r24.get(r25)     // Catch:{ Throwable -> 0x0325 }
                        r0 = r23
                        boolean r0 = r0 instanceof java.lang.String     // Catch:{ Throwable -> 0x0325 }
                        r24 = r0
                        if (r24 == 0) goto L_0x0165
                        java.lang.String r23 = (java.lang.String) r23     // Catch:{ Throwable -> 0x0325 }
                        int r24 = r23.length()     // Catch:{ Throwable -> 0x0325 }
                        if (r24 != 0) goto L_0x0165
                        java.lang.String r24 = "value"
                        r0 = r24
                        r5.remove(r0)     // Catch:{ Throwable -> 0x0325 }
                        java.lang.String r24 = "data"
                        r0 = r24
                        r7.remove(r0)     // Catch:{ Throwable -> 0x0325 }
                        java.lang.String r24 = "data"
                        r0 = r24
                        r7.put(r0, r5)     // Catch:{ Throwable -> 0x0325 }
                    L_0x0165:
                        r0 = r18
                        r0.put(r7)     // Catch:{ Throwable -> 0x0325 }
                        java.lang.String r24 = "APSAnalyticsService"
                        r25 = 3
                        boolean r24 = android.util.Log.isLoggable(r24, r25)     // Catch:{ Throwable -> 0x0325 }
                        if (r24 == 0) goto L_0x01b6
                        java.lang.Integer r24 = java.lang.Integer.valueOf(r11)     // Catch:{ Throwable -> 0x0325 }
                        r0 = r24
                        java.lang.Object r16 = r9.get(r0)     // Catch:{ Throwable -> 0x0325 }
                        org.json.JSONObject r16 = (org.json.JSONObject) r16     // Catch:{ Throwable -> 0x0325 }
                        java.lang.String r24 = "APSAnalyticsService"
                        java.lang.StringBuilder r25 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0325 }
                        r25.<init>()     // Catch:{ Throwable -> 0x0325 }
                        java.lang.String r26 = "Sending event: type = "
                        java.lang.StringBuilder r25 = r25.append(r26)     // Catch:{ Throwable -> 0x0325 }
                        java.lang.String r26 = "type"
                        r0 = r16
                        r1 = r26
                        java.lang.String r26 = r0.getString(r1)     // Catch:{ Throwable -> 0x0325 }
                        java.lang.StringBuilder r25 = r25.append(r26)     // Catch:{ Throwable -> 0x0325 }
                        java.lang.String r26 = ", timestamp = "
                        java.lang.StringBuilder r25 = r25.append(r26)     // Catch:{ Throwable -> 0x0325 }
                        java.lang.String r26 = "ts"
                        r0 = r16
                        r1 = r26
                        java.lang.String r26 = r0.getString(r1)     // Catch:{ Throwable -> 0x0325 }
                        java.lang.StringBuilder r25 = r25.append(r26)     // Catch:{ Throwable -> 0x0325 }
                        java.lang.String r25 = r25.toString()     // Catch:{ Throwable -> 0x0325 }
                        android.util.Log.d(r24, r25)     // Catch:{ Throwable -> 0x0325 }
                    L_0x01b6:
                        int r10 = r10 + 1
                        goto L_0x00f6
                    L_0x01ba:
                        r6 = 1
                        int r24 = r18.length()     // Catch:{ Throwable -> 0x0325 }
                        if (r24 <= 0) goto L_0x0293
                        java.lang.String r24 = "APSAnalyticsService"
                        r25 = 3
                        boolean r24 = android.util.Log.isLoggable(r24, r25)     // Catch:{ Throwable -> 0x0325 }
                        if (r24 == 0) goto L_0x01ed
                        java.lang.String r24 = "APSAnalyticsService"
                        java.lang.StringBuilder r25 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0325 }
                        r25.<init>()     // Catch:{ Throwable -> 0x0325 }
                        java.lang.String r26 = "Sending "
                        java.lang.StringBuilder r25 = r25.append(r26)     // Catch:{ Throwable -> 0x0325 }
                        int r26 = r18.length()     // Catch:{ Throwable -> 0x0325 }
                        java.lang.StringBuilder r25 = r25.append(r26)     // Catch:{ Throwable -> 0x0325 }
                        java.lang.String r26 = " analytics events."
                        java.lang.StringBuilder r25 = r25.append(r26)     // Catch:{ Throwable -> 0x0325 }
                        java.lang.String r25 = r25.toString()     // Catch:{ Throwable -> 0x0325 }
                        android.util.Log.d(r24, r25)     // Catch:{ Throwable -> 0x0325 }
                    L_0x01ed:
                        java.lang.StringBuilder r24 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0300 }
                        r24.<init>()     // Catch:{ Throwable -> 0x0300 }
                        java.lang.String r25 = r18.toString()     // Catch:{ Throwable -> 0x0300 }
                        java.lang.StringBuilder r24 = r24.append(r25)     // Catch:{ Throwable -> 0x0300 }
                        java.lang.String r25 = "\n"
                        java.lang.StringBuilder r24 = r24.append(r25)     // Catch:{ Throwable -> 0x0300 }
                        java.lang.String r12 = r24.toString()     // Catch:{ Throwable -> 0x0300 }
                        com.appcelerator.aps.APSAnalyticsHelper r24 = com.appcelerator.aps.APSAnalyticsHelper.getInstance()     // Catch:{ Throwable -> 0x0300 }
                        java.lang.String r24 = r24.getAppGuid()     // Catch:{ Throwable -> 0x0300 }
                        if (r24 != 0) goto L_0x02e1
                        java.lang.String r17 = com.appcelerator.aps.APSAnalyticsService.analyticsURL     // Catch:{ Throwable -> 0x0300 }
                    L_0x0212:
                        java.net.URL r22 = new java.net.URL     // Catch:{ Throwable -> 0x0300 }
                        r0 = r22
                        r1 = r17
                        r0.<init>(r1)     // Catch:{ Throwable -> 0x0300 }
                        java.net.URLConnection r4 = r22.openConnection()     // Catch:{ Throwable -> 0x0300 }
                        java.net.HttpURLConnection r4 = (java.net.HttpURLConnection) r4     // Catch:{ Throwable -> 0x0300 }
                        java.lang.String r24 = "POST"
                        r0 = r24
                        r4.setRequestMethod(r0)     // Catch:{ Throwable -> 0x0300 }
                        java.lang.String r24 = "Content-Type"
                        java.lang.String r25 = "application/json"
                        r0 = r24
                        r1 = r25
                        r4.setRequestProperty(r0, r1)     // Catch:{ Throwable -> 0x0300 }
                        java.lang.String r24 = "Content-Length"
                        int r25 = r12.length()     // Catch:{ Throwable -> 0x0300 }
                        java.lang.String r25 = java.lang.String.valueOf(r25)     // Catch:{ Throwable -> 0x0300 }
                        r0 = r24
                        r1 = r25
                        r4.setRequestProperty(r0, r1)     // Catch:{ Throwable -> 0x0300 }
                        r24 = 5000(0x1388, float:7.006E-42)
                        r0 = r24
                        r4.setConnectTimeout(r0)     // Catch:{ Throwable -> 0x0300 }
                        r24 = 1
                        r0 = r24
                        r4.setDoOutput(r0)     // Catch:{ Throwable -> 0x0300 }
                        java.io.OutputStream r24 = r4.getOutputStream()     // Catch:{ Throwable -> 0x0300 }
                        java.lang.String r25 = "UTF-8"
                        r0 = r25
                        byte[] r25 = r12.getBytes(r0)     // Catch:{ Throwable -> 0x0300 }
                        r24.write(r25)     // Catch:{ Throwable -> 0x0300 }
                        int r19 = r4.getResponseCode()     // Catch:{ Throwable -> 0x0300 }
                        r24 = 200(0xc8, float:2.8E-43)
                        r0 = r19
                        r1 = r24
                        if (r0 < r1) goto L_0x0275
                        r24 = 299(0x12b, float:4.19E-43)
                        r0 = r19
                        r1 = r24
                        if (r0 <= r1) goto L_0x0293
                    L_0x0275:
                        java.lang.String r24 = "APSAnalyticsService"
                        java.lang.StringBuilder r25 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0300 }
                        r25.<init>()     // Catch:{ Throwable -> 0x0300 }
                        java.lang.String r26 = "Error posting events with errorCode: "
                        java.lang.StringBuilder r25 = r25.append(r26)     // Catch:{ Throwable -> 0x0300 }
                        r0 = r25
                        r1 = r19
                        java.lang.StringBuilder r25 = r0.append(r1)     // Catch:{ Throwable -> 0x0300 }
                        java.lang.String r25 = r25.toString()     // Catch:{ Throwable -> 0x0300 }
                        android.util.Log.e(r24, r25)     // Catch:{ Throwable -> 0x0300 }
                        int r20 = r20 + 1
                    L_0x0293:
                        r18 = 0
                        r24 = 5
                        r0 = r20
                        r1 = r24
                        if (r0 < r1) goto L_0x00b1
                        java.lang.String r24 = "APSAnalyticsService"
                        java.lang.String r25 = "Failed to send analytics events after 5 attempts"
                        android.util.Log.e(r24, r25)     // Catch:{ Throwable -> 0x0325 }
                    L_0x02a4:
                        java.lang.String r24 = "APSAnalyticsService"
                        java.lang.String r25 = "Stopping Analytics Service"
                        android.util.Log.i(r24, r25)     // Catch:{ Throwable -> 0x0325 }
                        r0 = r28
                        com.appcelerator.aps.APSAnalyticsService r0 = com.appcelerator.aps.APSAnalyticsService.this     // Catch:{ Throwable -> 0x0325 }
                        r24 = r0
                        r0 = r28
                        int r0 = r8     // Catch:{ Throwable -> 0x0325 }
                        r25 = r0
                        r24.stopSelf(r25)     // Catch:{ Throwable -> 0x0325 }
                        r0 = r28
                        com.appcelerator.aps.APSAnalyticsService r0 = com.appcelerator.aps.APSAnalyticsService.this
                        r24 = r0
                        java.util.concurrent.atomic.AtomicBoolean r24 = r24.sending
                        r25 = 1
                        r26 = 0
                        boolean r24 = r24.compareAndSet(r25, r26)
                        if (r24 != 0) goto L_0x0050
                        java.lang.String r24 = "APSAnalyticsService"
                        r25 = 3
                        boolean r24 = android.util.Log.isLoggable(r24, r25)
                        if (r24 == 0) goto L_0x0050
                        java.lang.String r24 = "APSAnalyticsService"
                        java.lang.String r25 = "Expected to be in a sending state. Sending was already false."
                        android.util.Log.w(r24, r25)
                        goto L_0x0050
                    L_0x02e1:
                        java.lang.StringBuilder r24 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0300 }
                        r24.<init>()     // Catch:{ Throwable -> 0x0300 }
                        java.lang.String r25 = com.appcelerator.aps.APSAnalyticsService.analyticsURL     // Catch:{ Throwable -> 0x0300 }
                        java.lang.StringBuilder r24 = r24.append(r25)     // Catch:{ Throwable -> 0x0300 }
                        com.appcelerator.aps.APSAnalyticsHelper r25 = com.appcelerator.aps.APSAnalyticsHelper.getInstance()     // Catch:{ Throwable -> 0x0300 }
                        java.lang.String r25 = r25.getAppGuid()     // Catch:{ Throwable -> 0x0300 }
                        java.lang.StringBuilder r24 = r24.append(r25)     // Catch:{ Throwable -> 0x0300 }
                        java.lang.String r17 = r24.toString()     // Catch:{ Throwable -> 0x0300 }
                        goto L_0x0212
                    L_0x0300:
                        r21 = move-exception
                        java.lang.String r24 = "APSAnalyticsService"
                        java.lang.StringBuilder r25 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0325 }
                        r25.<init>()     // Catch:{ Throwable -> 0x0325 }
                        java.lang.String r26 = "Error posting events: "
                        java.lang.StringBuilder r25 = r25.append(r26)     // Catch:{ Throwable -> 0x0325 }
                        java.lang.String r26 = r21.getMessage()     // Catch:{ Throwable -> 0x0325 }
                        java.lang.StringBuilder r25 = r25.append(r26)     // Catch:{ Throwable -> 0x0325 }
                        java.lang.String r25 = r25.toString()     // Catch:{ Throwable -> 0x0325 }
                        r0 = r24
                        r1 = r25
                        r2 = r21
                        android.util.Log.e(r0, r1, r2)     // Catch:{ Throwable -> 0x0325 }
                        goto L_0x0293
                    L_0x0325:
                        r21 = move-exception
                        java.lang.String r24 = "APSAnalyticsService"
                        java.lang.String r25 = "Unhandled exception in analytics thread: "
                        r0 = r24
                        r1 = r25
                        r2 = r21
                        android.util.Log.e(r0, r1, r2)     // Catch:{ all -> 0x0372 }
                        r0 = r28
                        com.appcelerator.aps.APSAnalyticsService r0 = com.appcelerator.aps.APSAnalyticsService.this     // Catch:{ all -> 0x0372 }
                        r24 = r0
                        r0 = r28
                        int r0 = r8     // Catch:{ all -> 0x0372 }
                        r25 = r0
                        r24.stopSelf(r25)     // Catch:{ all -> 0x0372 }
                        r0 = r28
                        com.appcelerator.aps.APSAnalyticsService r0 = com.appcelerator.aps.APSAnalyticsService.this
                        r24 = r0
                        java.util.concurrent.atomic.AtomicBoolean r24 = r24.sending
                        r25 = 1
                        r26 = 0
                        boolean r24 = r24.compareAndSet(r25, r26)
                        if (r24 != 0) goto L_0x0050
                        java.lang.String r24 = "APSAnalyticsService"
                        r25 = 3
                        boolean r24 = android.util.Log.isLoggable(r24, r25)
                        if (r24 == 0) goto L_0x0050
                        java.lang.String r24 = "APSAnalyticsService"
                        java.lang.String r25 = "Expected to be in a sending state. Sending was already false."
                        android.util.Log.w(r24, r25)
                        goto L_0x0050
                    L_0x0369:
                        java.lang.String r24 = "APSAnalyticsService"
                        java.lang.String r25 = "Network unavailable, can't send analytics"
                        android.util.Log.w(r24, r25)     // Catch:{ Throwable -> 0x0325 }
                        goto L_0x02a4
                    L_0x0372:
                        r24 = move-exception
                        r0 = r28
                        com.appcelerator.aps.APSAnalyticsService r0 = com.appcelerator.aps.APSAnalyticsService.this
                        r25 = r0
                        java.util.concurrent.atomic.AtomicBoolean r25 = r25.sending
                        r26 = 1
                        r27 = 0
                        boolean r25 = r25.compareAndSet(r26, r27)
                        if (r25 != 0) goto L_0x0398
                        java.lang.String r25 = "APSAnalyticsService"
                        r26 = 3
                        boolean r25 = android.util.Log.isLoggable(r25, r26)
                        if (r25 == 0) goto L_0x0398
                        java.lang.String r25 = "APSAnalyticsService"
                        java.lang.String r26 = "Expected to be in a sending state. Sending was already false."
                        android.util.Log.w(r25, r26)
                    L_0x0398:
                        throw r24
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.appcelerator.aps.APSAnalyticsService.C02561.run():void");
                }
            });
            t.setPriority(1);
            t.start();
        }
        return 1;
    }

    /* access modifiers changed from: private */
    public boolean canSend() {
        NetworkInfo netInfo = null;
        try {
            netInfo = this.connectivityManager.getActiveNetworkInfo();
        } catch (SecurityException e) {
            Log.w(TAG, "Connectivity permissions have been removed from AndroidManifest.xml: " + e.getMessage());
        }
        if (netInfo == null || !netInfo.isConnected() || netInfo.isRoaming()) {
            return false;
        }
        return true;
    }

    public IBinder onBind(Intent intent) {
        return null;
    }
}
