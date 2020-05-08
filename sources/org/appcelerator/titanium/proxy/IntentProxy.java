package org.appcelerator.titanium.proxy;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiUrl;
import p006ti.modules.titanium.android.AndroidModule;
import p006ti.modules.titanium.network.httpurlconnection.HttpUrlConnectionUtils;

public class IntentProxy extends KrollProxy {
    private static final String TAG = "TiIntent";
    public static final int TYPE_ACTIVITY = 0;
    public static final int TYPE_BROADCAST = 2;
    public static final int TYPE_SERVICE = 1;
    protected static char[] escapeChars = {'\\', '/', ' ', '.', '$', '&', '@'};
    protected Intent intent;
    protected int type = 0;

    public IntentProxy() {
    }

    public IntentProxy(Intent intent2) {
        this.intent = intent2;
    }

    public String getPackageName() {
        if (this.intent == null) {
            return null;
        }
        ComponentName componentName = this.intent.getComponent();
        if (componentName != null) {
            return componentName.getPackageName();
        }
        return null;
    }

    public String getClassName() {
        if (this.intent == null) {
            return null;
        }
        ComponentName componentName = this.intent.getComponent();
        if (componentName != null) {
            return componentName.getClassName();
        }
        return null;
    }

    protected static String getURLClassName(String url, int type2) {
        switch (type2) {
            case 0:
                return getURLClassName(url, "Activity");
            case 1:
                return getURLClassName(url, "Service");
            case 2:
                return getURLClassName(url, "Broadcast");
            default:
                return null;
        }
    }

    protected static String getURLClassName(String url, String appendage) {
        String className;
        List<String> parts = Arrays.asList(url.split(TiUrl.PATH_SEPARATOR));
        if (parts.size() == 0) {
            return null;
        }
        int start = 0;
        if (((String) parts.get(0)).equals("app:") && parts.size() >= 3) {
            start = 2;
        }
        String className2 = TextUtils.join("_", parts.subList(start, parts.size()));
        if (className2.endsWith(".js")) {
            className2 = className2.substring(0, className2.length() - 3);
        }
        if (className2.length() > 1) {
            className = className2.substring(0, 1).toUpperCase() + className2.substring(1);
        } else {
            className = className2.toUpperCase();
        }
        for (char escapeChar : escapeChars) {
            className = className.replace(escapeChar, '_');
        }
        return className + appendage;
    }

    public void handleCreationDict(KrollDict dict) {
        super.handleCreationDict(dict);
        this.intent = new Intent();
        String action = dict.getString(TiC.PROPERTY_ACTION);
        String url = dict.getString("url");
        String data = dict.getString(TiC.PROPERTY_DATA);
        String className = dict.getString(TiC.PROPERTY_CLASS_NAME);
        String packageName = dict.getString(TiC.PROPERTY_PACKAGE_NAME);
        String type2 = dict.getString("type");
        if (dict.containsKey(TiC.PROPERTY_FLAGS)) {
            int flags = TiConvert.toInt((HashMap<String, Object>) dict, TiC.PROPERTY_FLAGS);
            Log.m29d(TAG, "Setting flags: " + Integer.toString(flags), Log.DEBUG_MODE);
            this.intent.setFlags(flags);
        } else {
            setProperty(TiC.PROPERTY_FLAGS, Integer.valueOf(this.intent.getFlags()));
        }
        if (action != null) {
            Log.m29d(TAG, "Setting action: " + action, Log.DEBUG_MODE);
            this.intent.setAction(action);
        }
        if (packageName != null) {
            Log.m29d(TAG, "Setting package: " + packageName, Log.DEBUG_MODE);
            this.intent.setPackage(packageName);
        }
        if (url != null) {
            Log.m29d(TAG, "Creating intent for JS Activity/Service @ " + url, Log.DEBUG_MODE);
            packageName = TiApplication.getInstance().getPackageName();
            className = packageName + TiUrl.CURRENT_PATH + getURLClassName(url, this.type);
        }
        if (className != null) {
            if (packageName != null) {
                Log.m29d(TAG, "Both className and packageName set, using intent.setClassName(packageName, className", Log.DEBUG_MODE);
                this.intent.setClassName(packageName, className);
            } else {
                try {
                    this.intent.setClass(TiApplication.getInstance().getApplicationContext(), getClass().getClassLoader().loadClass(className));
                } catch (ClassNotFoundException e) {
                    Log.m32e(TAG, "Unable to locate class for name: " + className);
                    throw new IllegalStateException("Missing class for name: " + className, e);
                }
            }
        }
        if (type2 == null && action != null && action.equals(AndroidModule.ACTION_SEND)) {
            type2 = HttpUrlConnectionUtils.PLAIN_TEXT_TYPE;
        }
        if (type2 != null) {
            Log.m29d(TAG, "Setting type: " + type2, Log.DEBUG_MODE);
            if (data != null) {
                this.intent.setDataAndType(Uri.parse(data), type2);
            } else {
                this.intent.setType(type2);
            }
        } else if (data != null) {
            this.intent.setData(Uri.parse(data));
        }
    }

    public void putExtra(String key, Object value) {
        if (value != null) {
            if (value instanceof String) {
                this.intent.putExtra(key, (String) value);
            } else if (value instanceof Boolean) {
                this.intent.putExtra(key, (Boolean) value);
            } else if (value instanceof Double) {
                this.intent.putExtra(key, (Double) value);
            } else if (value instanceof Integer) {
                this.intent.putExtra(key, (Integer) value);
            } else if (value instanceof Long) {
                this.intent.putExtra(key, (Long) value);
            } else if (value instanceof IntentProxy) {
                this.intent.putExtra(key, ((IntentProxy) value).getIntent());
            } else if (value instanceof TiBlob) {
                this.intent.putExtra(key, ((TiBlob) value).getImage());
            } else if (value instanceof Object[]) {
                try {
                    Object[] objVal = (Object[]) value;
                    this.intent.putExtra(key, (String[]) Arrays.copyOf(objVal, objVal.length, String[].class));
                } catch (Exception ex) {
                    Log.m33e(TAG, "Error unimplemented put conversion ", ex.getMessage());
                }
            } else {
                Log.m44w(TAG, "Warning unimplemented put conversion for " + value.getClass().getCanonicalName() + " trying String");
                this.intent.putExtra(key, TiConvert.toString(value));
            }
        }
    }

    public void addFlags(int flags) {
        this.intent.addFlags(flags);
    }

    public void setFlags(int flags) {
        this.intent.setFlags(flags);
    }

    public int getFlags() {
        return this.intent.getFlags();
    }

    public void putExtraUri(String key, Object value) {
        if (value != null) {
            if (value instanceof String) {
                this.intent.putExtra(key, Uri.parse((String) value));
            } else if (value instanceof Object[]) {
                try {
                    Object[] objVal = (Object[]) value;
                    String[] stringArray = (String[]) Arrays.copyOf(objVal, objVal.length, String[].class);
                    ArrayList<Uri> imageUris = new ArrayList<>();
                    for (String s : stringArray) {
                        imageUris.add(Uri.parse(s));
                    }
                    this.intent.putParcelableArrayListExtra(key, imageUris);
                } catch (Exception ex) {
                    Log.m33e(TAG, "Error unimplemented put conversion ", ex.getMessage());
                }
            }
        }
    }

    public void addCategory(String category) {
        if (category != null) {
            Log.m29d(TAG, "Adding category: " + category, Log.DEBUG_MODE);
            this.intent.addCategory(category);
        }
    }

    public String getStringExtra(String name) {
        if (!this.intent.hasExtra(name)) {
            return null;
        }
        String result = this.intent.getStringExtra(name);
        if (result != null) {
            return result;
        }
        Parcelable parcelable = this.intent.getParcelableExtra(name);
        if (parcelable != null) {
            return parcelable.toString();
        }
        return result;
    }

    public boolean getBooleanExtra(String name, boolean defaultValue) {
        return this.intent.getBooleanExtra(name, defaultValue);
    }

    public int getIntExtra(String name, int defaultValue) {
        return this.intent.getIntExtra(name, defaultValue);
    }

    public long getLongExtra(String name, long defaultValue) {
        return this.intent.getLongExtra(name, defaultValue);
    }

    public double getDoubleExtra(String name, double defaultValue) {
        return this.intent.getDoubleExtra(name, defaultValue);
    }

    /* JADX WARNING: Removed duplicated region for block: B:66:0x0105 A[SYNTHETIC, Splitter:B:66:0x0105] */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x010a A[SYNTHETIC, Splitter:B:69:0x010a] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.appcelerator.titanium.TiBlob getBlobExtra(java.lang.String r16) {
        /*
            r15 = this;
            r11 = 0
            r5 = 0
            r1 = 0
            android.content.Intent r12 = r15.intent     // Catch:{ Exception -> 0x012b }
            android.os.Bundle r12 = r12.getExtras()     // Catch:{ Exception -> 0x012b }
            r0 = r16
            android.os.Parcelable r8 = r12.getParcelable(r0)     // Catch:{ Exception -> 0x012b }
            boolean r12 = r8 instanceof android.net.Uri     // Catch:{ Exception -> 0x012b }
            if (r12 == 0) goto L_0x0090
            r0 = r8
            android.net.Uri r0 = (android.net.Uri) r0     // Catch:{ Exception -> 0x012b }
            r10 = r0
            org.appcelerator.titanium.TiApplication r12 = org.appcelerator.titanium.TiApplication.getInstance()     // Catch:{ Exception -> 0x012b }
            android.content.ContentResolver r12 = r12.getContentResolver()     // Catch:{ Exception -> 0x012b }
            java.io.InputStream r5 = r12.openInputStream(r10)     // Catch:{ Exception -> 0x012b }
            java.io.ByteArrayOutputStream r2 = new java.io.ByteArrayOutputStream     // Catch:{ Exception -> 0x012b }
            r2.<init>()     // Catch:{ Exception -> 0x012b }
            r9 = 4096(0x1000, float:5.74E-42)
            byte[] r3 = new byte[r9]     // Catch:{ Exception -> 0x0039, all -> 0x0128 }
        L_0x002c:
            r12 = 0
            int r6 = r5.read(r3, r12, r9)     // Catch:{ Exception -> 0x0039, all -> 0x0128 }
            r12 = -1
            if (r6 == r12) goto L_0x0062
            r12 = 0
            r2.write(r3, r12, r6)     // Catch:{ Exception -> 0x0039, all -> 0x0128 }
            goto L_0x002c
        L_0x0039:
            r4 = move-exception
            r1 = r2
        L_0x003b:
            java.lang.String r12 = "TiIntent"
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ all -> 0x0102 }
            r13.<init>()     // Catch:{ all -> 0x0102 }
            java.lang.String r14 = "Error getting blob extra: "
            java.lang.StringBuilder r13 = r13.append(r14)     // Catch:{ all -> 0x0102 }
            java.lang.String r14 = r4.getMessage()     // Catch:{ all -> 0x0102 }
            java.lang.StringBuilder r13 = r13.append(r14)     // Catch:{ all -> 0x0102 }
            java.lang.String r13 = r13.toString()     // Catch:{ all -> 0x0102 }
            org.appcelerator.kroll.common.Log.m34e(r12, r13, r4)     // Catch:{ all -> 0x0102 }
            if (r5 == 0) goto L_0x005c
            r5.close()     // Catch:{ IOException -> 0x00e6 }
        L_0x005c:
            if (r1 == 0) goto L_0x0061
            r1.close()     // Catch:{ IOException -> 0x00f4 }
        L_0x0061:
            return r11
        L_0x0062:
            byte[] r3 = r2.toByteArray()     // Catch:{ Exception -> 0x0039, all -> 0x0128 }
            org.appcelerator.titanium.TiBlob r11 = org.appcelerator.titanium.TiBlob.blobFromData(r3)     // Catch:{ Exception -> 0x0039, all -> 0x0128 }
            if (r5 == 0) goto L_0x006f
            r5.close()     // Catch:{ IOException -> 0x0076 }
        L_0x006f:
            if (r2 == 0) goto L_0x0074
            r2.close()     // Catch:{ IOException -> 0x0083 }
        L_0x0074:
            r1 = r2
            goto L_0x0061
        L_0x0076:
            r4 = move-exception
            java.lang.String r12 = "TiIntent"
            java.lang.String r13 = r4.getMessage()
            java.lang.String r14 = "DEBUG_MODE"
            org.appcelerator.kroll.common.Log.m33e(r12, r13, r14)
            goto L_0x006f
        L_0x0083:
            r4 = move-exception
            java.lang.String r12 = "TiIntent"
            java.lang.String r13 = r4.getMessage()
            java.lang.String r14 = "DEBUG_MODE"
            org.appcelerator.kroll.common.Log.m33e(r12, r13, r14)
            goto L_0x0074
        L_0x0090:
            boolean r12 = r8 instanceof android.graphics.Bitmap     // Catch:{ Exception -> 0x012b }
            if (r12 == 0) goto L_0x00c1
            r0 = r8
            android.graphics.Bitmap r0 = (android.graphics.Bitmap) r0     // Catch:{ Exception -> 0x012b }
            r7 = r0
            org.appcelerator.titanium.TiBlob r11 = org.appcelerator.titanium.TiBlob.blobFromImage(r7)     // Catch:{ Exception -> 0x012b }
            if (r5 == 0) goto L_0x00a1
            r5.close()     // Catch:{ IOException -> 0x00b4 }
        L_0x00a1:
            if (r1 == 0) goto L_0x0061
            r1.close()     // Catch:{ IOException -> 0x00a7 }
            goto L_0x0061
        L_0x00a7:
            r4 = move-exception
            java.lang.String r12 = "TiIntent"
            java.lang.String r13 = r4.getMessage()
            java.lang.String r14 = "DEBUG_MODE"
            org.appcelerator.kroll.common.Log.m33e(r12, r13, r14)
            goto L_0x0061
        L_0x00b4:
            r4 = move-exception
            java.lang.String r12 = "TiIntent"
            java.lang.String r13 = r4.getMessage()
            java.lang.String r14 = "DEBUG_MODE"
            org.appcelerator.kroll.common.Log.m33e(r12, r13, r14)
            goto L_0x00a1
        L_0x00c1:
            if (r5 == 0) goto L_0x00c6
            r5.close()     // Catch:{ IOException -> 0x00d9 }
        L_0x00c6:
            if (r1 == 0) goto L_0x0061
            r1.close()     // Catch:{ IOException -> 0x00cc }
            goto L_0x0061
        L_0x00cc:
            r4 = move-exception
            java.lang.String r12 = "TiIntent"
            java.lang.String r13 = r4.getMessage()
            java.lang.String r14 = "DEBUG_MODE"
            org.appcelerator.kroll.common.Log.m33e(r12, r13, r14)
            goto L_0x0061
        L_0x00d9:
            r4 = move-exception
            java.lang.String r12 = "TiIntent"
            java.lang.String r13 = r4.getMessage()
            java.lang.String r14 = "DEBUG_MODE"
            org.appcelerator.kroll.common.Log.m33e(r12, r13, r14)
            goto L_0x00c6
        L_0x00e6:
            r4 = move-exception
            java.lang.String r12 = "TiIntent"
            java.lang.String r13 = r4.getMessage()
            java.lang.String r14 = "DEBUG_MODE"
            org.appcelerator.kroll.common.Log.m33e(r12, r13, r14)
            goto L_0x005c
        L_0x00f4:
            r4 = move-exception
            java.lang.String r12 = "TiIntent"
            java.lang.String r13 = r4.getMessage()
            java.lang.String r14 = "DEBUG_MODE"
            org.appcelerator.kroll.common.Log.m33e(r12, r13, r14)
            goto L_0x0061
        L_0x0102:
            r11 = move-exception
        L_0x0103:
            if (r5 == 0) goto L_0x0108
            r5.close()     // Catch:{ IOException -> 0x010e }
        L_0x0108:
            if (r1 == 0) goto L_0x010d
            r1.close()     // Catch:{ IOException -> 0x011b }
        L_0x010d:
            throw r11
        L_0x010e:
            r4 = move-exception
            java.lang.String r12 = "TiIntent"
            java.lang.String r13 = r4.getMessage()
            java.lang.String r14 = "DEBUG_MODE"
            org.appcelerator.kroll.common.Log.m33e(r12, r13, r14)
            goto L_0x0108
        L_0x011b:
            r4 = move-exception
            java.lang.String r12 = "TiIntent"
            java.lang.String r13 = r4.getMessage()
            java.lang.String r14 = "DEBUG_MODE"
            org.appcelerator.kroll.common.Log.m33e(r12, r13, r14)
            goto L_0x010d
        L_0x0128:
            r11 = move-exception
            r1 = r2
            goto L_0x0103
        L_0x012b:
            r4 = move-exception
            goto L_0x003b
        */
        throw new UnsupportedOperationException("Method not decompiled: org.appcelerator.titanium.proxy.IntentProxy.getBlobExtra(java.lang.String):org.appcelerator.titanium.TiBlob");
    }

    public String getData() {
        return this.intent.getDataString();
    }

    public Intent getIntent() {
        return this.intent;
    }

    public String getType() {
        return this.intent.getType();
    }

    public void setType(String type2) {
        this.intent.setType(type2);
    }

    public String getAction() {
        return this.intent.getAction();
    }

    public void setAction(String action) {
        this.intent.setAction(action);
    }

    public int getInternalType() {
        return this.type;
    }

    public void setInternalType(int type2) {
        this.type = type2;
    }

    public boolean hasExtra(String name) {
        if (this.intent != null) {
            return this.intent.hasExtra(name);
        }
        return false;
    }

    public String getApiName() {
        return "Ti.Android.Intent";
    }
}
