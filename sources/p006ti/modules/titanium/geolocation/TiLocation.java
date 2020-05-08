package p006ti.modules.titanium.geolocation;

import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import com.appcelerator.aps.APSAnalytics;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.analytics.TiAnalyticsEventFactory;
import org.appcelerator.titanium.util.TiPlatformHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* renamed from: ti.modules.titanium.geolocation.TiLocation */
public class TiLocation implements Callback {
    private static final String BASE_GEO_URL = "http://api.appcelerator.com/p/v1/geo?";
    public static final int ERR_POSITION_UNAVAILABLE = 6;
    public static final int MSG_FIRST_ID = 100;
    public static final int MSG_LAST_ID = 102;
    public static final int MSG_LOOKUP = 101;
    private static final String TAG = "TiLocation";
    private String appGuid = TiApplication.getInstance().getAppInfo().getGUID();
    private String countryCode = Locale.getDefault().getCountry();
    private List<String> knownProviders = this.locationManager.getAllProviders();
    private long lastAnalyticsTimestamp = 0;
    public LocationManager locationManager = ((LocationManager) TiApplication.getInstance().getSystemService("location"));
    private String mobileId = TiPlatformHelper.getInstance().getMobileId();
    private Handler runtimeHandler = new Handler(TiMessenger.getRuntimeMessenger().getLooper(), this);
    private String sessionId = TiPlatformHelper.getInstance().getSessionId();

    /* renamed from: ti.modules.titanium.geolocation.TiLocation$GeocodeResponseHandler */
    public interface GeocodeResponseHandler {
        void handleGeocodeResponse(KrollDict krollDict);
    }

    public boolean handleMessage(Message msg) {
        if (msg.what != 101) {
            return false;
        }
        String urlValue = msg.getData().getString("url");
        String directionValue = msg.getData().getString("direction");
        getLookUpTask().execute(new Object[]{urlValue, directionValue, msg.obj});
        return true;
    }

    public boolean isProvider(String name) {
        return this.knownProviders.contains(name);
    }

    public boolean getLocationServicesEnabled() {
        List<String> providerNames = this.locationManager.getProviders(true);
        if (Log.isDebugModeEnabled()) {
            Log.m36i(TAG, "Enabled location provider count: " + providerNames.size());
            for (String providerName : providerNames) {
                Log.m36i(TAG, providerName + " service available");
            }
        }
        for (String name : providerNames) {
            if (name.equals("network")) {
                return true;
            }
            if (name.equals("gps")) {
                return true;
            }
        }
        return false;
    }

    public Location getLastKnownLocation() {
        Location latestKnownLocation = null;
        for (String provider : this.knownProviders) {
            Location lastKnownLocation = null;
            try {
                lastKnownLocation = this.locationManager.getLastKnownLocation(provider);
            } catch (IllegalArgumentException e) {
                Log.m32e(TAG, "Unable to get last know location for [" + provider + "], provider is null");
            } catch (SecurityException e2) {
                Log.m32e(TAG, "Unable to get last know location for [" + provider + "], permission denied");
            }
            if (lastKnownLocation != null && (latestKnownLocation == null || lastKnownLocation.getTime() > latestKnownLocation.getTime())) {
                latestKnownLocation = lastKnownLocation;
            }
        }
        return latestKnownLocation;
    }

    public void doAnalytics(Location location) {
        long locationTime = location.getTime();
        TiApplication application = TiApplication.getInstance();
        if (locationTime - this.lastAnalyticsTimestamp > TiAnalyticsEventFactory.MAX_GEO_ANALYTICS_FREQUENCY && application.isAnalyticsEnabled() && !application.isAnalyticsFiltered("ti.geo")) {
            APSAnalytics.getInstance().sendAppGeoEvent(location);
        }
    }

    public void forwardGeocode(String address, GeocodeResponseHandler responseHandler) {
        if (address != null) {
            String geocoderUrl = buildGeocoderURL(TiC.PROPERTY_FORWARD, this.mobileId, this.appGuid, this.sessionId, address, this.countryCode);
            if (geocoderUrl != null) {
                Message message = this.runtimeHandler.obtainMessage(101);
                message.getData().putString("direction", TiC.PROPERTY_FORWARD);
                message.getData().putString("url", geocoderUrl);
                message.obj = responseHandler;
                message.sendToTarget();
                return;
            }
            return;
        }
        Log.m32e(TAG, "Unable to forward geocode, address is null");
    }

    public void reverseGeocode(double latitude, double longitude, GeocodeResponseHandler responseHandler) {
        String geocoderUrl = buildGeocoderURL(TiC.PROPERTY_REVERSE, this.mobileId, this.appGuid, this.sessionId, latitude + "," + longitude, this.countryCode);
        if (geocoderUrl != null) {
            Message message = this.runtimeHandler.obtainMessage(101);
            message.getData().putString("direction", TiC.PROPERTY_REVERSE);
            message.getData().putString("url", geocoderUrl);
            message.obj = responseHandler;
            message.sendToTarget();
            return;
        }
        Log.m32e(TAG, "Unable to reverse geocode, geocoder url is null");
    }

    private String buildGeocoderURL(String direction, String mid, String aguid, String sid, String query, String countryCode2) {
        String url = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(BASE_GEO_URL).append("d=r").append("&mid=").append(mid).append("&aguid=").append(aguid).append("&sid=").append(sid).append("&q=").append(URLEncoder.encode(query, "utf-8"));
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            Log.m32e(TAG, "Unable to encode query to utf-8: " + e.getMessage());
            return url;
        }
    }

    private AsyncTask<Object, Void, Integer> getLookUpTask() {
        return new AsyncTask<Object, Void, Integer>() {
            /* access modifiers changed from: protected */
            /* JADX WARNING: Removed duplicated region for block: B:25:0x00f2  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public java.lang.Integer doInBackground(java.lang.Object... r25) {
                /*
                    r24 = this;
                    r10 = 0
                    r8 = 0
                    r21 = 0
                    r20 = r25[r21]     // Catch:{ Throwable -> 0x0118 }
                    java.lang.String r20 = (java.lang.String) r20     // Catch:{ Throwable -> 0x0118 }
                    r21 = 1
                    r5 = r25[r21]     // Catch:{ Throwable -> 0x0118 }
                    java.lang.String r5 = (java.lang.String) r5     // Catch:{ Throwable -> 0x0118 }
                    r21 = 2
                    r21 = r25[r21]     // Catch:{ Throwable -> 0x0118 }
                    r0 = r21
                    ti.modules.titanium.geolocation.TiLocation$GeocodeResponseHandler r0 = (p006ti.modules.titanium.geolocation.TiLocation.GeocodeResponseHandler) r0     // Catch:{ Throwable -> 0x0118 }
                    r10 = r0
                    java.lang.String r21 = "TiLocation"
                    java.lang.StringBuilder r22 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0118 }
                    r22.<init>()     // Catch:{ Throwable -> 0x0118 }
                    java.lang.String r23 = "GEO URL ["
                    java.lang.StringBuilder r22 = r22.append(r23)     // Catch:{ Throwable -> 0x0118 }
                    r0 = r22
                    r1 = r20
                    java.lang.StringBuilder r22 = r0.append(r1)     // Catch:{ Throwable -> 0x0118 }
                    java.lang.String r23 = "]"
                    java.lang.StringBuilder r22 = r22.append(r23)     // Catch:{ Throwable -> 0x0118 }
                    java.lang.String r22 = r22.toString()     // Catch:{ Throwable -> 0x0118 }
                    java.lang.String r23 = "DEBUG_MODE"
                    org.appcelerator.kroll.common.Log.m29d(r21, r22, r23)     // Catch:{ Throwable -> 0x0118 }
                    r4 = 0
                    java.lang.StringBuilder r18 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0118 }
                    r18.<init>()     // Catch:{ Throwable -> 0x0118 }
                    java.net.URL r14 = new java.net.URL     // Catch:{ Exception -> 0x0090, all -> 0x0149 }
                    r0 = r20
                    r14.<init>(r0)     // Catch:{ Exception -> 0x0090, all -> 0x0149 }
                    java.net.URLConnection r21 = r14.openConnection()     // Catch:{ Exception -> 0x0090, all -> 0x0149 }
                    r0 = r21
                    java.net.HttpURLConnection r0 = (java.net.HttpURLConnection) r0     // Catch:{ Exception -> 0x0090, all -> 0x0149 }
                    r4 = r0
                    java.lang.String r21 = "Expect"
                    java.lang.String r22 = "100-continue"
                    r0 = r21
                    r1 = r22
                    r4.setRequestProperty(r0, r1)     // Catch:{ Exception -> 0x0090, all -> 0x0149 }
                    r4.connect()     // Catch:{ Exception -> 0x0090, all -> 0x0149 }
                    int r17 = r4.getResponseCode()     // Catch:{ Exception -> 0x0090, all -> 0x0149 }
                    r21 = 200(0xc8, float:2.8E-43)
                    r0 = r17
                    r1 = r21
                    if (r0 != r1) goto L_0x0146
                    java.io.BufferedInputStream r11 = new java.io.BufferedInputStream     // Catch:{ Exception -> 0x0090, all -> 0x0149 }
                    java.io.InputStream r21 = r4.getInputStream()     // Catch:{ Exception -> 0x0090, all -> 0x0149 }
                    r0 = r21
                    r11.<init>(r0)     // Catch:{ Exception -> 0x0090, all -> 0x0149 }
                    java.io.BufferedReader r15 = new java.io.BufferedReader     // Catch:{ Exception -> 0x0090, all -> 0x0149 }
                    java.io.InputStreamReader r21 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x0090, all -> 0x0149 }
                    r0 = r21
                    r0.<init>(r11)     // Catch:{ Exception -> 0x0090, all -> 0x0149 }
                    r0 = r21
                    r15.<init>(r0)     // Catch:{ Exception -> 0x0090, all -> 0x0149 }
                L_0x0084:
                    java.lang.String r13 = r15.readLine()     // Catch:{ Exception -> 0x0090, all -> 0x0149 }
                    if (r13 == 0) goto L_0x010e
                    r0 = r18
                    r0.append(r13)     // Catch:{ Exception -> 0x0090, all -> 0x0149 }
                    goto L_0x0084
                L_0x0090:
                    r6 = move-exception
                    r16 = 0
                    if (r4 == 0) goto L_0x0098
                    r4.disconnect()     // Catch:{ Throwable -> 0x0118 }
                L_0x0098:
                    java.lang.String r21 = "TiLocation"
                    java.lang.StringBuilder r22 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0118 }
                    r22.<init>()     // Catch:{ Throwable -> 0x0118 }
                    java.lang.String r23 = "received Geo ["
                    java.lang.StringBuilder r22 = r22.append(r23)     // Catch:{ Throwable -> 0x0118 }
                    r0 = r22
                    r1 = r16
                    java.lang.StringBuilder r22 = r0.append(r1)     // Catch:{ Throwable -> 0x0118 }
                    java.lang.String r23 = "]"
                    java.lang.StringBuilder r22 = r22.append(r23)     // Catch:{ Throwable -> 0x0118 }
                    java.lang.String r22 = r22.toString()     // Catch:{ Throwable -> 0x0118 }
                    java.lang.String r23 = "DEBUG_MODE"
                    org.appcelerator.kroll.common.Log.m37i(r21, r22, r23)     // Catch:{ Throwable -> 0x0118 }
                    if (r16 == 0) goto L_0x00f0
                    org.json.JSONObject r12 = new org.json.JSONObject     // Catch:{ JSONException -> 0x018d }
                    r0 = r16
                    r12.<init>(r0)     // Catch:{ JSONException -> 0x018d }
                    java.lang.String r21 = "success"
                    r0 = r21
                    boolean r21 = r12.getBoolean(r0)     // Catch:{ JSONException -> 0x018d }
                    if (r21 == 0) goto L_0x015d
                    java.lang.String r21 = "forward"
                    r0 = r21
                    boolean r21 = r5.equals(r0)     // Catch:{ JSONException -> 0x018d }
                    if (r21 == 0) goto L_0x0150
                    r0 = r24
                    ti.modules.titanium.geolocation.TiLocation r0 = p006ti.modules.titanium.geolocation.TiLocation.this     // Catch:{ JSONException -> 0x018d }
                    r21 = r0
                    r0 = r21
                    org.appcelerator.kroll.KrollDict r8 = r0.buildForwardGeocodeResponse(r12)     // Catch:{ JSONException -> 0x018d }
                L_0x00e5:
                    r21 = 0
                    r22 = 0
                    r0 = r21
                    r1 = r22
                    r8.putCodeAndMessage(r0, r1)     // Catch:{ JSONException -> 0x018d }
                L_0x00f0:
                    if (r10 == 0) goto L_0x0107
                    if (r8 != 0) goto L_0x0104
                    org.appcelerator.kroll.KrollDict r8 = new org.appcelerator.kroll.KrollDict
                    r8.<init>()
                    r21 = -1
                    java.lang.String r22 = "Error obtaining geolocation"
                    r0 = r21
                    r1 = r22
                    r8.putCodeAndMessage(r0, r1)
                L_0x0104:
                    r10.handleGeocodeResponse(r8)
                L_0x0107:
                    r21 = -1
                    java.lang.Integer r21 = java.lang.Integer.valueOf(r21)
                    return r21
                L_0x010e:
                    java.lang.String r16 = r18.toString()     // Catch:{ Exception -> 0x0090, all -> 0x0149 }
                L_0x0112:
                    if (r4 == 0) goto L_0x0098
                    r4.disconnect()     // Catch:{ Throwable -> 0x0118 }
                    goto L_0x0098
                L_0x0118:
                    r19 = move-exception
                L_0x0119:
                    java.lang.String r21 = "TiLocation"
                    java.lang.StringBuilder r22 = new java.lang.StringBuilder
                    r22.<init>()
                    java.lang.String r23 = "Error retrieving geocode information ["
                    java.lang.StringBuilder r22 = r22.append(r23)
                    java.lang.String r23 = r19.getMessage()
                    java.lang.StringBuilder r22 = r22.append(r23)
                    java.lang.String r23 = "]"
                    java.lang.StringBuilder r22 = r22.append(r23)
                    java.lang.String r22 = r22.toString()
                    java.lang.String r23 = "DEBUG_MODE"
                    r0 = r21
                    r1 = r22
                    r2 = r19
                    r3 = r23
                    org.appcelerator.kroll.common.Log.m35e(r0, r1, r2, r3)
                    goto L_0x00f0
                L_0x0146:
                    r16 = 0
                    goto L_0x0112
                L_0x0149:
                    r21 = move-exception
                    if (r4 == 0) goto L_0x014f
                    r4.disconnect()     // Catch:{ Throwable -> 0x0118 }
                L_0x014f:
                    throw r21     // Catch:{ Throwable -> 0x0118 }
                L_0x0150:
                    r0 = r24
                    ti.modules.titanium.geolocation.TiLocation r0 = p006ti.modules.titanium.geolocation.TiLocation.this     // Catch:{ JSONException -> 0x018d }
                    r21 = r0
                    r0 = r21
                    org.appcelerator.kroll.KrollDict r8 = r0.buildReverseGeocodeResponse(r12)     // Catch:{ JSONException -> 0x018d }
                    goto L_0x00e5
                L_0x015d:
                    org.appcelerator.kroll.KrollDict r9 = new org.appcelerator.kroll.KrollDict     // Catch:{ JSONException -> 0x018d }
                    r9.<init>()     // Catch:{ JSONException -> 0x018d }
                    java.lang.StringBuilder r21 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x01be, Throwable -> 0x01ba }
                    r21.<init>()     // Catch:{ JSONException -> 0x01be, Throwable -> 0x01ba }
                    java.lang.String r22 = "Unable to resolve message: Code ("
                    java.lang.StringBuilder r21 = r21.append(r22)     // Catch:{ JSONException -> 0x01be, Throwable -> 0x01ba }
                    java.lang.String r22 = "errorcode"
                    r0 = r22
                    java.lang.String r22 = r12.getString(r0)     // Catch:{ JSONException -> 0x01be, Throwable -> 0x01ba }
                    java.lang.StringBuilder r21 = r21.append(r22)     // Catch:{ JSONException -> 0x01be, Throwable -> 0x01ba }
                    java.lang.String r22 = ")"
                    java.lang.StringBuilder r21 = r21.append(r22)     // Catch:{ JSONException -> 0x01be, Throwable -> 0x01ba }
                    java.lang.String r7 = r21.toString()     // Catch:{ JSONException -> 0x01be, Throwable -> 0x01ba }
                    r21 = -1
                    r0 = r21
                    r9.putCodeAndMessage(r0, r7)     // Catch:{ JSONException -> 0x01be, Throwable -> 0x01ba }
                    r8 = r9
                    goto L_0x00f0
                L_0x018d:
                    r6 = move-exception
                L_0x018e:
                    java.lang.String r21 = "TiLocation"
                    java.lang.StringBuilder r22 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x0118 }
                    r22.<init>()     // Catch:{ Throwable -> 0x0118 }
                    java.lang.String r23 = "Error converting geo response to JSONObject ["
                    java.lang.StringBuilder r22 = r22.append(r23)     // Catch:{ Throwable -> 0x0118 }
                    java.lang.String r23 = r6.getMessage()     // Catch:{ Throwable -> 0x0118 }
                    java.lang.StringBuilder r22 = r22.append(r23)     // Catch:{ Throwable -> 0x0118 }
                    java.lang.String r23 = "]"
                    java.lang.StringBuilder r22 = r22.append(r23)     // Catch:{ Throwable -> 0x0118 }
                    java.lang.String r22 = r22.toString()     // Catch:{ Throwable -> 0x0118 }
                    java.lang.String r23 = "DEBUG_MODE"
                    r0 = r21
                    r1 = r22
                    r2 = r23
                    org.appcelerator.kroll.common.Log.m35e(r0, r1, r6, r2)     // Catch:{ Throwable -> 0x0118 }
                    goto L_0x00f0
                L_0x01ba:
                    r19 = move-exception
                    r8 = r9
                    goto L_0x0119
                L_0x01be:
                    r6 = move-exception
                    r8 = r9
                    goto L_0x018e
                */
                throw new UnsupportedOperationException("Method not decompiled: p006ti.modules.titanium.geolocation.TiLocation.C03551.doInBackground(java.lang.Object[]):java.lang.Integer");
            }
        };
    }

    /* access modifiers changed from: private */
    public KrollDict buildForwardGeocodeResponse(JSONObject jsonResponse) throws JSONException {
        KrollDict address = new KrollDict();
        JSONArray places = jsonResponse.getJSONArray(TiC.PROPERTY_PLACES);
        if (places.length() > 0) {
            return buildAddress(places.getJSONObject(0));
        }
        return address;
    }

    /* access modifiers changed from: private */
    public KrollDict buildReverseGeocodeResponse(JSONObject jsonResponse) throws JSONException {
        JSONArray places = jsonResponse.getJSONArray(TiC.PROPERTY_PLACES);
        ArrayList<KrollDict> addresses = new ArrayList<>();
        int count = places.length();
        for (int i = 0; i < count; i++) {
            addresses.add(buildAddress(places.getJSONObject(i)));
        }
        KrollDict response = new KrollDict();
        response.put(TiC.PROPERTY_SUCCESS, Boolean.valueOf(true));
        response.put(TiC.PROPERTY_PLACES, addresses.toArray());
        return response;
    }

    private KrollDict buildAddress(JSONObject place) {
        KrollDict address = new KrollDict();
        address.put(TiC.PROPERTY_STREET1, place.optString(TiC.PROPERTY_STREET, ""));
        address.put(TiC.PROPERTY_STREET, place.optString(TiC.PROPERTY_STREET, ""));
        address.put(TiC.PROPERTY_CITY, place.optString(TiC.PROPERTY_CITY, ""));
        address.put(TiC.PROPERTY_REGION1, "");
        address.put(TiC.PROPERTY_REGION2, "");
        address.put(TiC.PROPERTY_POSTAL_CODE, place.optString("zipcode", ""));
        address.put(TiC.PROPERTY_COUNTRY, place.optString(TiC.PROPERTY_COUNTRY, ""));
        address.put("state", place.optString("state", ""));
        address.put("countryCode", place.optString(TiC.PROPERTY_COUNTRY_CODE, ""));
        address.put(TiC.PROPERTY_COUNTRY_CODE, place.optString(TiC.PROPERTY_COUNTRY_CODE, ""));
        address.put(TiC.PROPERTY_LONGITUDE, place.optString(TiC.PROPERTY_LONGITUDE, ""));
        address.put(TiC.PROPERTY_LATITUDE, place.optString(TiC.PROPERTY_LATITUDE, ""));
        address.put(TiC.PROPERTY_DISPLAY_ADDRESS, place.optString(TiC.PROPERTY_ADDRESS));
        address.put(TiC.PROPERTY_ADDRESS, place.optString(TiC.PROPERTY_ADDRESS));
        return address;
    }
}
