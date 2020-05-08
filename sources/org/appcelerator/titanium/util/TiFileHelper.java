package org.appcelerator.titanium.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.webkit.URLUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;

public class TiFileHelper implements Callback {
    private static final String MACOSX_PREFIX = "__MACOSX";
    private static final int MSG_NETWORK_URL = 100;
    public static final String RESOURCE_ROOT_ASSETS = "file:///android_asset/Resources";
    public static final String SD_CARD_PREFIX = "/sdcard/Ti.debug";
    private static final String TAG = "TiFileHelper";
    public static final String TI_DIR = "tiapp";
    public static final String TI_DIR_JS = "tijs";
    private static final String TI_RESOURCE_PREFIX = "ti:";
    private static TiFileHelper _instance = null;
    private static HashSet<String> foundResourcePathCache;
    private static HashSet<String> notFoundResourcePathCache;
    private static HashSet<String> resourcePathCache;
    static HashMap<String, Integer> systemIcons;
    private TiNinePatchHelper nph;
    protected Handler runtimeHandler = null;
    private SoftReference<Context> softContext;
    private ArrayList<File> tempFiles = new ArrayList<>();

    public TiFileHelper(Context context) {
        this.softContext = new SoftReference<>(context);
        this.nph = new TiNinePatchHelper();
        if (resourcePathCache == null) {
            resourcePathCache = new HashSet<>();
            foundResourcePathCache = new HashSet<>();
            notFoundResourcePathCache = new HashSet<>();
        }
        if (resourcePathCache == null) {
            resourcePathCache = new HashSet<>();
            foundResourcePathCache = new HashSet<>();
            notFoundResourcePathCache = new HashSet<>();
        }
        synchronized (TI_DIR) {
            if (systemIcons == null) {
                systemIcons = new HashMap<>();
                systemIcons.put("ic_menu_camera", Integer.valueOf(17301559));
                systemIcons.put("ic_menu_search", Integer.valueOf(17301583));
                systemIcons.put("ic_menu_add", Integer.valueOf(17301555));
                systemIcons.put("ic_menu_delete", Integer.valueOf(17301564));
                systemIcons.put("ic_media_play", Integer.valueOf(17301540));
                systemIcons.put("ic_media_ff", Integer.valueOf(17301537));
                systemIcons.put("ic_media_pause", Integer.valueOf(17301539));
                systemIcons.put("ic_media_rew", Integer.valueOf(17301542));
                systemIcons.put("ic_menu_edit", Integer.valueOf(17301566));
                systemIcons.put("ic_menu_close_clear_cancel", Integer.valueOf(17301560));
                systemIcons.put("ic_menu_save", Integer.valueOf(17301582));
                systemIcons.put("ic_menu_help", Integer.valueOf(17301568));
                systemIcons.put("ic_media_next", Integer.valueOf(17301538));
                systemIcons.put("ic_menu_preferences", Integer.valueOf(17301577));
                systemIcons.put("ic_media_previous", Integer.valueOf(17301541));
                systemIcons.put("ic_menu_revert", Integer.valueOf(17301580));
                systemIcons.put("ic_menu_send", Integer.valueOf(17301584));
                systemIcons.put("ic_menu_share", Integer.valueOf(17301586));
                systemIcons.put("ic_menu_view", Integer.valueOf(17301591));
                systemIcons.put("ic_menu_zoom", Integer.valueOf(17301593));
            }
        }
    }

    private Handler getRuntimeHandler() {
        if (this.runtimeHandler == null) {
            this.runtimeHandler = new Handler(TiMessenger.getRuntimeMessenger().getLooper(), this);
        }
        return this.runtimeHandler;
    }

    public static TiFileHelper getInstance() {
        if (_instance == null) {
            _instance = new TiFileHelper(TiApplication.getInstance());
        }
        return _instance;
    }

    public InputStream openInputStream(String path, boolean report) throws IOException {
        InputStream is = null;
        Context context = (Context) this.softContext.get();
        if (context != null) {
            if (isTitaniumResource(path)) {
                String[] parts = path.split(":");
                if (parts.length != 3) {
                    Log.m44w(TAG, "Malformed titanium resource url, resource not loaded: " + path);
                    return null;
                }
                String str = parts[0];
                String section = parts[1];
                String resid = parts[2];
                if (TI_RESOURCE_PREFIX.equals(section)) {
                    is = TiFileHelper.class.getResourceAsStream("/org/appcelerator/titanium/res/drawable/" + resid + ".png");
                } else if ("Sys".equals(section)) {
                    Log.m32e(TAG, "Accessing Android system icons is deprecated. Instead copy to res folder.");
                    Integer id = (Integer) systemIcons.get(resid);
                    if (id != null) {
                        is = Resources.getSystem().openRawResource(id.intValue());
                    } else {
                        Log.m44w(TAG, "Drawable not found for system id: " + path);
                    }
                } else {
                    Log.m32e(TAG, "Unknown section identifier: " + section);
                }
            } else if (URLUtil.isNetworkUrl(path)) {
                is = TiApplication.isUIThread() ? (InputStream) TiMessenger.sendBlockingRuntimeMessage(getRuntimeHandler().obtainMessage(100), path) : handleNetworkURL(path);
            } else if (path.startsWith(RESOURCE_ROOT_ASSETS)) {
                String path2 = path.substring(TiConvert.ASSET_URL.length());
                boolean found = false;
                if (foundResourcePathCache.contains(path2)) {
                    found = true;
                } else if (!notFoundResourcePathCache.contains(path2)) {
                    String base = path2.substring(0, path2.lastIndexOf(TiUrl.PATH_SEPARATOR));
                    synchronized (resourcePathCache) {
                        if (!resourcePathCache.contains(base)) {
                            String[] paths = context.getAssets().list(base);
                            for (int i = 0; i < paths.length; i++) {
                                foundResourcePathCache.add(base + '/' + paths[i]);
                            }
                            resourcePathCache.add(base);
                            if (foundResourcePathCache.contains(path2)) {
                                found = true;
                            }
                        }
                        if (!found) {
                            notFoundResourcePathCache.add(path2);
                        }
                    }
                }
                if (found) {
                    is = context.getAssets().open(path2);
                }
            } else if (path.startsWith(SD_CARD_PREFIX)) {
                File file = new File(path);
                is = new FileInputStream(file);
            } else if (URLUtil.isFileUrl(path)) {
                URL url = new URL(path);
                is = url.openStream();
            } else {
                is = context.getAssets().open(joinPaths("Resources", path));
            }
        }
        InputStream inputStream = is;
        return is;
    }

    /* JADX WARNING: Removed duplicated region for block: B:25:0x0055 A[SYNTHETIC, Splitter:B:25:0x0055] */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x005b A[SYNTHETIC, Splitter:B:29:0x005b] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.io.InputStream handleNetworkURL(java.lang.String r15) throws java.io.IOException {
        /*
            r14 = this;
            r5 = 0
            java.net.URI r10 = new java.net.URI     // Catch:{ URISyntaxException -> 0x0014 }
            r10.<init>(r15)     // Catch:{ URISyntaxException -> 0x0014 }
            boolean r11 = org.appcelerator.titanium.util.TiResponseCache.peek(r10)     // Catch:{ URISyntaxException -> 0x0014 }
            if (r11 == 0) goto L_0x0015
            java.io.InputStream r8 = org.appcelerator.titanium.util.TiResponseCache.openCachedStream(r10)     // Catch:{ URISyntaxException -> 0x0014 }
            if (r8 == 0) goto L_0x0015
            r6 = r5
        L_0x0013:
            return r8
        L_0x0014:
            r11 = move-exception
        L_0x0015:
            java.net.URL r9 = new java.net.URL
            r9.<init>(r15)
            java.io.InputStream r7 = r9.openStream()
            r0 = 0
            java.io.ByteArrayOutputStream r1 = new java.io.ByteArrayOutputStream     // Catch:{ IOException -> 0x0084 }
            r11 = 8192(0x2000, float:1.14794E-41)
            r1.<init>(r11)     // Catch:{ IOException -> 0x0084 }
            r3 = 0
            r11 = 8192(0x2000, float:1.14794E-41)
            byte[] r2 = new byte[r11]     // Catch:{ IOException -> 0x0037, all -> 0x0081 }
        L_0x002b:
            int r3 = r7.read(r2)     // Catch:{ IOException -> 0x0037, all -> 0x0081 }
            r11 = -1
            if (r3 == r11) goto L_0x0060
            r11 = 0
            r1.write(r2, r11, r3)     // Catch:{ IOException -> 0x0037, all -> 0x0081 }
            goto L_0x002b
        L_0x0037:
            r4 = move-exception
            r0 = r1
        L_0x0039:
            java.lang.String r11 = "TiFileHelper"
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x0052 }
            r12.<init>()     // Catch:{ all -> 0x0052 }
            java.lang.String r13 = "Problem pulling image data from "
            java.lang.StringBuilder r12 = r12.append(r13)     // Catch:{ all -> 0x0052 }
            java.lang.StringBuilder r12 = r12.append(r15)     // Catch:{ all -> 0x0052 }
            java.lang.String r12 = r12.toString()     // Catch:{ all -> 0x0052 }
            org.appcelerator.kroll.common.Log.m34e(r11, r12, r4)     // Catch:{ all -> 0x0052 }
            throw r4     // Catch:{ all -> 0x0052 }
        L_0x0052:
            r11 = move-exception
        L_0x0053:
            if (r7 == 0) goto L_0x0059
            r7.close()     // Catch:{ Exception -> 0x007d }
            r7 = 0
        L_0x0059:
            if (r0 == 0) goto L_0x005f
            r0.close()     // Catch:{ Exception -> 0x007f }
            r0 = 0
        L_0x005f:
            throw r11
        L_0x0060:
            java.io.ByteArrayInputStream r5 = new java.io.ByteArrayInputStream     // Catch:{ IOException -> 0x0037, all -> 0x0081 }
            byte[] r11 = r1.toByteArray()     // Catch:{ IOException -> 0x0037, all -> 0x0081 }
            r5.<init>(r11)     // Catch:{ IOException -> 0x0037, all -> 0x0081 }
            if (r7 == 0) goto L_0x006f
            r7.close()     // Catch:{ Exception -> 0x007b }
            r7 = 0
        L_0x006f:
            if (r1 == 0) goto L_0x0086
            r1.close()     // Catch:{ Exception -> 0x0078 }
            r0 = 0
        L_0x0075:
            r6 = r5
            r8 = r5
            goto L_0x0013
        L_0x0078:
            r11 = move-exception
            r0 = r1
            goto L_0x0075
        L_0x007b:
            r11 = move-exception
            goto L_0x006f
        L_0x007d:
            r12 = move-exception
            goto L_0x0059
        L_0x007f:
            r12 = move-exception
            goto L_0x005f
        L_0x0081:
            r11 = move-exception
            r0 = r1
            goto L_0x0053
        L_0x0084:
            r4 = move-exception
            goto L_0x0039
        L_0x0086:
            r0 = r1
            goto L_0x0075
        */
        throw new UnsupportedOperationException("Method not decompiled: org.appcelerator.titanium.util.TiFileHelper.handleNetworkURL(java.lang.String):java.io.InputStream");
    }

    public Drawable loadDrawable(String path, boolean report) {
        return loadDrawable(path, report, false);
    }

    public Drawable loadDrawable(String path, boolean report, boolean checkForNinePatch) {
        return loadDrawable(path, report, checkForNinePatch, true);
    }

    /* JADX WARNING: Removed duplicated region for block: B:26:0x0061 A[SYNTHETIC, Splitter:B:26:0x0061] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.graphics.drawable.Drawable loadDrawable(java.lang.String r11, boolean r12, boolean r13, boolean r14) {
        /*
            r10 = this;
            r2 = 0
            r6 = 0
            android.graphics.drawable.Drawable r2 = org.appcelerator.titanium.util.TiUIHelper.getResourceDrawable(r11)
            if (r2 == 0) goto L_0x000b
            r3 = r2
            r4 = r2
        L_0x000a:
            return r4
        L_0x000b:
            if (r13 == 0) goto L_0x00a7
            if (r11 == 0) goto L_0x00a7
            boolean r7 = android.webkit.URLUtil.isNetworkUrl(r11)     // Catch:{ IOException -> 0x0081 }
            if (r7 != 0) goto L_0x00a7
            java.lang.String r7 = ".png"
            boolean r7 = r11.endsWith(r7)     // Catch:{ IOException -> 0x0081 }
            if (r7 == 0) goto L_0x004c
            java.lang.String r7 = ".9.png"
            boolean r7 = r11.endsWith(r7)     // Catch:{ IOException -> 0x0081 }
            if (r7 != 0) goto L_0x004c
            r0 = 0
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0081 }
            r7.<init>()     // Catch:{ IOException -> 0x0081 }
            r8 = 0
            java.lang.String r9 = "."
            int r9 = r11.lastIndexOf(r9)     // Catch:{ IOException -> 0x0081 }
            java.lang.String r8 = r11.substring(r8, r9)     // Catch:{ IOException -> 0x0081 }
            java.lang.StringBuilder r7 = r7.append(r8)     // Catch:{ IOException -> 0x0081 }
            java.lang.String r8 = ".9.png"
            java.lang.StringBuilder r7 = r7.append(r8)     // Catch:{ IOException -> 0x0081 }
            java.lang.String r0 = r7.toString()     // Catch:{ IOException -> 0x0081 }
            r7 = 0
            java.io.InputStream r6 = r10.openInputStream(r0, r7)     // Catch:{ IOException -> 0x0067 }
            if (r6 == 0) goto L_0x004c
            r11 = r0
        L_0x004c:
            if (r6 != 0) goto L_0x0052
            java.io.InputStream r6 = r10.openInputStream(r11, r12)     // Catch:{ IOException -> 0x0081 }
        L_0x0052:
            r1 = 0
            if (r14 == 0) goto L_0x00a2
            android.graphics.Bitmap r1 = org.appcelerator.titanium.util.TiUIHelper.createDensityScaledBitmap(r6)     // Catch:{ IOException -> 0x0081 }
        L_0x0059:
            org.appcelerator.titanium.util.TiNinePatchHelper r7 = r10.nph     // Catch:{ IOException -> 0x0081 }
            android.graphics.drawable.Drawable r2 = r7.process(r1)     // Catch:{ IOException -> 0x0081 }
        L_0x005f:
            if (r6 == 0) goto L_0x0064
            r6.close()     // Catch:{ IOException -> 0x00c7 }
        L_0x0064:
            r3 = r2
            r4 = r2
            goto L_0x000a
        L_0x0067:
            r5 = move-exception
            java.lang.String r7 = "TiFileHelper"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0081 }
            r8.<init>()     // Catch:{ IOException -> 0x0081 }
            java.lang.String r9 = "path not found: "
            java.lang.StringBuilder r8 = r8.append(r9)     // Catch:{ IOException -> 0x0081 }
            java.lang.StringBuilder r8 = r8.append(r0)     // Catch:{ IOException -> 0x0081 }
            java.lang.String r8 = r8.toString()     // Catch:{ IOException -> 0x0081 }
            org.appcelerator.kroll.common.Log.m28d(r7, r8)     // Catch:{ IOException -> 0x0081 }
            goto L_0x004c
        L_0x0081:
            r5 = move-exception
            java.lang.String r7 = "TiFileHelper"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x00c0 }
            r8.<init>()     // Catch:{ all -> 0x00c0 }
            java.lang.StringBuilder r8 = r8.append(r11)     // Catch:{ all -> 0x00c0 }
            java.lang.String r9 = " not found."
            java.lang.StringBuilder r8 = r8.append(r9)     // Catch:{ all -> 0x00c0 }
            java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x00c0 }
            org.appcelerator.kroll.common.Log.m34e(r7, r8, r5)     // Catch:{ all -> 0x00c0 }
            if (r6 == 0) goto L_0x0064
            r6.close()     // Catch:{ IOException -> 0x00a0 }
            goto L_0x0064
        L_0x00a0:
            r7 = move-exception
            goto L_0x0064
        L_0x00a2:
            android.graphics.Bitmap r1 = org.appcelerator.titanium.util.TiUIHelper.createBitmap(r6)     // Catch:{ IOException -> 0x0081 }
            goto L_0x0059
        L_0x00a7:
            java.io.InputStream r6 = r10.openInputStream(r11, r12)     // Catch:{ IOException -> 0x0081 }
            r1 = 0
            if (r14 == 0) goto L_0x00bb
            android.graphics.Bitmap r1 = org.appcelerator.titanium.util.TiUIHelper.createDensityScaledBitmap(r6)     // Catch:{ IOException -> 0x0081 }
        L_0x00b2:
            if (r1 == 0) goto L_0x005f
            android.graphics.drawable.BitmapDrawable r3 = new android.graphics.drawable.BitmapDrawable     // Catch:{ IOException -> 0x0081 }
            r3.<init>(r1)     // Catch:{ IOException -> 0x0081 }
            r2 = r3
            goto L_0x005f
        L_0x00bb:
            android.graphics.Bitmap r1 = org.appcelerator.titanium.util.TiUIHelper.createBitmap(r6)     // Catch:{ IOException -> 0x0081 }
            goto L_0x00b2
        L_0x00c0:
            r7 = move-exception
            if (r6 == 0) goto L_0x00c6
            r6.close()     // Catch:{ IOException -> 0x00c9 }
        L_0x00c6:
            throw r7
        L_0x00c7:
            r7 = move-exception
            goto L_0x0064
        L_0x00c9:
            r8 = move-exception
            goto L_0x00c6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.appcelerator.titanium.util.TiFileHelper.loadDrawable(java.lang.String, boolean, boolean, boolean):android.graphics.drawable.Drawable");
    }

    public boolean isTitaniumResource(String s) {
        if (s == null || !s.startsWith(TI_RESOURCE_PREFIX)) {
            return false;
        }
        return true;
    }

    public Drawable getTitaniumResource(Context context, String s) {
        Drawable d = null;
        if (isTitaniumResource(s)) {
            String[] parts = s.split(":");
            if (parts.length != 2) {
                Log.m44w(TAG, "Malformed titanium resource url, resource not loaded: " + s);
                return null;
            }
            String section = parts[0];
            String resid = parts[1];
            if (TI_RESOURCE_PREFIX.equals(section)) {
                InputStream is = null;
                try {
                    is = TiFileHelper.class.getResourceAsStream("/org/appcelerator/titanium/res/drawable/" + resid + ".png");
                    d = new BitmapDrawable(is);
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                        }
                    }
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e2) {
                        }
                    }
                }
            } else if ("Sys".equals(section)) {
                Log.m32e(TAG, "Accessing Android system icons is deprecated. Instead copy to res folder.");
                Integer id = (Integer) systemIcons.get(resid);
                if (id != null) {
                    d = Resources.getSystem().getDrawable(id.intValue());
                } else {
                    Log.m44w(TAG, "Drawable not found for system id: " + s);
                }
            } else {
                Log.m32e(TAG, "Unknown section identifier: " + section);
            }
        } else {
            Log.m44w(TAG, "Ignoring non titanium resource string id: " + s);
        }
        Drawable drawable = d;
        return d;
    }

    public String getResourceUrl(String path) {
        return joinPaths(RESOURCE_ROOT_ASSETS, path);
    }

    public String joinPaths(String pre, String post) {
        StringBuilder sb = new StringBuilder();
        sb.append(pre);
        if (pre.endsWith(TiUrl.PATH_SEPARATOR) && !post.startsWith(TiUrl.PATH_SEPARATOR)) {
            sb.append(post);
        } else if (!pre.endsWith(TiUrl.PATH_SEPARATOR) && post.startsWith(TiUrl.PATH_SEPARATOR)) {
            sb.append(post);
        } else if (pre.endsWith(TiUrl.PATH_SEPARATOR) || post.startsWith(TiUrl.PATH_SEPARATOR)) {
            sb.append(post.substring(1));
        } else {
            sb.append(TiUrl.PATH_SEPARATOR).append(post);
        }
        return sb.toString();
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x00a5 A[SYNTHETIC, Splitter:B:20:0x00a5] */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x00ab A[SYNTHETIC, Splitter:B:24:0x00ab] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void deployFromAssets(java.io.File r21) throws java.io.IOException {
        /*
            r20 = this;
            r0 = r20
            java.lang.ref.SoftReference<android.content.Context> r0 = r0.softContext
            r17 = r0
            java.lang.Object r6 = r17.get()
            android.content.Context r6 = (android.content.Context) r6
            if (r6 == 0) goto L_0x00f3
            java.util.ArrayList r15 = new java.util.ArrayList
            r15.<init>()
            android.content.res.AssetManager r2 = r6.getAssets()
            java.lang.String r17 = ""
            r0 = r20
            r1 = r17
            r0.walkAssets(r2, r1, r15)
            r20.wipeDirectoryTree(r21)
            r3 = 0
            r10 = 0
            r17 = 8096(0x1fa0, float:1.1345E-41)
            r0 = r17
            byte[] r5 = new byte[r0]
            int r13 = r15.size()     // Catch:{ all -> 0x00a2 }
            r12 = 0
            r11 = r10
            r4 = r3
        L_0x0032:
            if (r12 >= r13) goto L_0x00e7
            java.lang.Object r14 = r15.get(r12)     // Catch:{ all -> 0x00fc }
            java.lang.String r14 = (java.lang.String) r14     // Catch:{ all -> 0x00fc }
            java.io.File r9 = new java.io.File     // Catch:{ all -> 0x00fc }
            r9.<init>(r14)     // Catch:{ all -> 0x00fc }
            java.lang.String r17 = r9.getName()     // Catch:{ all -> 0x00fc }
            java.lang.String r18 = "."
            int r17 = r17.indexOf(r18)     // Catch:{ all -> 0x00fc }
            r18 = -1
            r0 = r17
            r1 = r18
            if (r0 <= r1) goto L_0x00be
            java.io.BufferedInputStream r3 = new java.io.BufferedInputStream     // Catch:{ all -> 0x00fc }
            java.io.InputStream r17 = r2.open(r14)     // Catch:{ all -> 0x00fc }
            r18 = 8096(0x1fa0, float:1.1345E-41)
            r0 = r17
            r1 = r18
            r3.<init>(r0, r1)     // Catch:{ all -> 0x00fc }
            java.io.File r8 = new java.io.File     // Catch:{ all -> 0x0100 }
            r0 = r21
            r8.<init>(r0, r14)     // Catch:{ all -> 0x0100 }
            java.lang.String r17 = "TiFileHelper"
            java.lang.StringBuilder r18 = new java.lang.StringBuilder     // Catch:{ all -> 0x0100 }
            r18.<init>()     // Catch:{ all -> 0x0100 }
            java.lang.String r19 = "Copying to: "
            java.lang.StringBuilder r18 = r18.append(r19)     // Catch:{ all -> 0x0100 }
            java.lang.String r19 = r8.getAbsolutePath()     // Catch:{ all -> 0x0100 }
            java.lang.StringBuilder r18 = r18.append(r19)     // Catch:{ all -> 0x0100 }
            java.lang.String r18 = r18.toString()     // Catch:{ all -> 0x0100 }
            java.lang.String r19 = "DEBUG_MODE"
            org.appcelerator.kroll.common.Log.m29d(r17, r18, r19)     // Catch:{ all -> 0x0100 }
            java.io.FileOutputStream r10 = new java.io.FileOutputStream     // Catch:{ all -> 0x0100 }
            r10.<init>(r8)     // Catch:{ all -> 0x0100 }
            r16 = 0
        L_0x008c:
            int r16 = r3.read(r5)     // Catch:{ all -> 0x00a2 }
            r17 = -1
            r0 = r16
            r1 = r17
            if (r0 == r1) goto L_0x00b0
            r17 = 0
            r0 = r17
            r1 = r16
            r10.write(r5, r0, r1)     // Catch:{ all -> 0x00a2 }
            goto L_0x008c
        L_0x00a2:
            r17 = move-exception
        L_0x00a3:
            if (r3 == 0) goto L_0x00a9
            r3.close()     // Catch:{ IOException -> 0x00f8 }
        L_0x00a8:
            r3 = 0
        L_0x00a9:
            if (r10 == 0) goto L_0x00af
            r10.close()     // Catch:{ IOException -> 0x00fa }
        L_0x00ae:
            r10 = 0
        L_0x00af:
            throw r17
        L_0x00b0:
            r3.close()     // Catch:{ all -> 0x00a2 }
            r3 = 0
            r10.close()     // Catch:{ all -> 0x00a2 }
            r10 = 0
        L_0x00b8:
            int r12 = r12 + 1
            r11 = r10
            r4 = r3
            goto L_0x0032
        L_0x00be:
            java.io.File r7 = new java.io.File     // Catch:{ all -> 0x00fc }
            r0 = r21
            r7.<init>(r0, r14)     // Catch:{ all -> 0x00fc }
            java.lang.String r17 = "TiFileHelper"
            java.lang.StringBuilder r18 = new java.lang.StringBuilder     // Catch:{ all -> 0x00fc }
            r18.<init>()     // Catch:{ all -> 0x00fc }
            java.lang.String r19 = "Creating directory: "
            java.lang.StringBuilder r18 = r18.append(r19)     // Catch:{ all -> 0x00fc }
            java.lang.String r19 = r7.getAbsolutePath()     // Catch:{ all -> 0x00fc }
            java.lang.StringBuilder r18 = r18.append(r19)     // Catch:{ all -> 0x00fc }
            java.lang.String r18 = r18.toString()     // Catch:{ all -> 0x00fc }
            org.appcelerator.kroll.common.Log.m28d(r17, r18)     // Catch:{ all -> 0x00fc }
            r7.mkdirs()     // Catch:{ all -> 0x00fc }
            r10 = r11
            r3 = r4
            goto L_0x00b8
        L_0x00e7:
            if (r4 == 0) goto L_0x0103
            r4.close()     // Catch:{ IOException -> 0x00f4 }
        L_0x00ec:
            r3 = 0
        L_0x00ed:
            if (r11 == 0) goto L_0x00f3
            r11.close()     // Catch:{ IOException -> 0x00f6 }
        L_0x00f2:
            r10 = 0
        L_0x00f3:
            return
        L_0x00f4:
            r17 = move-exception
            goto L_0x00ec
        L_0x00f6:
            r17 = move-exception
            goto L_0x00f2
        L_0x00f8:
            r18 = move-exception
            goto L_0x00a8
        L_0x00fa:
            r18 = move-exception
            goto L_0x00ae
        L_0x00fc:
            r17 = move-exception
            r10 = r11
            r3 = r4
            goto L_0x00a3
        L_0x0100:
            r17 = move-exception
            r10 = r11
            goto L_0x00a3
        L_0x0103:
            r3 = r4
            goto L_0x00ed
        */
        throw new UnsupportedOperationException("Method not decompiled: org.appcelerator.titanium.util.TiFileHelper.deployFromAssets(java.io.File):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x00cd A[SYNTHETIC, Splitter:B:31:0x00cd] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void deployFromZip(java.io.File r14, java.io.File r15) throws java.io.IOException {
        /*
            r13 = this;
            r13.wipeDirectoryTree(r15)
            r9 = 0
            r8 = 0
            r10 = 8096(0x1fa0, float:1.1345E-41)
            byte[] r0 = new byte[r10]
            java.io.FileInputStream r10 = new java.io.FileInputStream     // Catch:{ all -> 0x0056 }
            r10.<init>(r14)     // Catch:{ all -> 0x0056 }
            java.util.zip.ZipInputStream r9 = r13.getZipInputStream(r10)     // Catch:{ all -> 0x0056 }
            java.lang.String r6 = r13.getRootDir(r9)     // Catch:{ all -> 0x0056 }
            int r7 = r6.length()     // Catch:{ all -> 0x0056 }
            r9.close()     // Catch:{ all -> 0x0056 }
            java.lang.String r10 = "TiFileHelper"
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x0056 }
            r11.<init>()     // Catch:{ all -> 0x0056 }
            java.lang.String r12 = "Zip file root: "
            java.lang.StringBuilder r11 = r11.append(r12)     // Catch:{ all -> 0x0056 }
            java.lang.StringBuilder r11 = r11.append(r6)     // Catch:{ all -> 0x0056 }
            java.lang.String r11 = r11.toString()     // Catch:{ all -> 0x0056 }
            java.lang.String r12 = "DEBUG_MODE"
            org.appcelerator.kroll.common.Log.m29d(r10, r11, r12)     // Catch:{ all -> 0x0056 }
            java.io.FileInputStream r10 = new java.io.FileInputStream     // Catch:{ all -> 0x0056 }
            r10.<init>(r14)     // Catch:{ all -> 0x0056 }
            java.util.zip.ZipInputStream r9 = r13.getZipInputStream(r10)     // Catch:{ all -> 0x0056 }
        L_0x0040:
            java.util.zip.ZipEntry r8 = r9.getNextEntry()     // Catch:{ all -> 0x0056 }
            if (r8 == 0) goto L_0x00d9
            java.lang.String r4 = r8.getName()     // Catch:{ all -> 0x0056 }
            java.lang.String r10 = "__MACOSX"
            boolean r10 = r4.startsWith(r10)     // Catch:{ all -> 0x0056 }
            if (r10 == 0) goto L_0x005d
            r9.closeEntry()     // Catch:{ all -> 0x0056 }
            goto L_0x0040
        L_0x0056:
            r10 = move-exception
            if (r9 == 0) goto L_0x005c
            r9.close()     // Catch:{ Throwable -> 0x00e3 }
        L_0x005c:
            throw r10
        L_0x005d:
            java.lang.String r4 = r4.substring(r7)     // Catch:{ all -> 0x0056 }
            int r10 = r4.length()     // Catch:{ all -> 0x0056 }
            if (r10 <= 0) goto L_0x00ad
            java.lang.String r10 = "TiFileHelper"
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x0056 }
            r11.<init>()     // Catch:{ all -> 0x0056 }
            java.lang.String r12 = "Extracting "
            java.lang.StringBuilder r11 = r11.append(r12)     // Catch:{ all -> 0x0056 }
            java.lang.StringBuilder r11 = r11.append(r4)     // Catch:{ all -> 0x0056 }
            java.lang.String r11 = r11.toString()     // Catch:{ all -> 0x0056 }
            java.lang.String r12 = "DEBUG_MODE"
            org.appcelerator.kroll.common.Log.m29d(r10, r11, r12)     // Catch:{ all -> 0x0056 }
            boolean r10 = r8.isDirectory()     // Catch:{ all -> 0x0056 }
            if (r10 == 0) goto L_0x00b1
            java.io.File r1 = new java.io.File     // Catch:{ all -> 0x0056 }
            r1.<init>(r15, r4)     // Catch:{ all -> 0x0056 }
            r1.mkdirs()     // Catch:{ all -> 0x0056 }
            java.lang.String r10 = "TiFileHelper"
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x0056 }
            r11.<init>()     // Catch:{ all -> 0x0056 }
            java.lang.String r12 = "Created directory "
            java.lang.StringBuilder r11 = r11.append(r12)     // Catch:{ all -> 0x0056 }
            java.lang.String r12 = r1.toString()     // Catch:{ all -> 0x0056 }
            java.lang.StringBuilder r11 = r11.append(r12)     // Catch:{ all -> 0x0056 }
            java.lang.String r11 = r11.toString()     // Catch:{ all -> 0x0056 }
            java.lang.String r12 = "DEBUG_MODE"
            org.appcelerator.kroll.common.Log.m29d(r10, r11, r12)     // Catch:{ all -> 0x0056 }
        L_0x00ad:
            r9.closeEntry()     // Catch:{ all -> 0x0056 }
            goto L_0x0040
        L_0x00b1:
            r2 = 0
            java.io.FileOutputStream r3 = new java.io.FileOutputStream     // Catch:{ all -> 0x00e6 }
            java.io.File r10 = new java.io.File     // Catch:{ all -> 0x00e6 }
            r10.<init>(r15, r4)     // Catch:{ all -> 0x00e6 }
            r3.<init>(r10)     // Catch:{ all -> 0x00e6 }
            r5 = 0
        L_0x00bd:
            int r5 = r9.read(r0)     // Catch:{ all -> 0x00c9 }
            r10 = -1
            if (r5 == r10) goto L_0x00d1
            r10 = 0
            r3.write(r0, r10, r5)     // Catch:{ all -> 0x00c9 }
            goto L_0x00bd
        L_0x00c9:
            r10 = move-exception
            r2 = r3
        L_0x00cb:
            if (r2 == 0) goto L_0x00d0
            r2.close()     // Catch:{ Throwable -> 0x00df }
        L_0x00d0:
            throw r10     // Catch:{ all -> 0x0056 }
        L_0x00d1:
            if (r3 == 0) goto L_0x00ad
            r3.close()     // Catch:{ Throwable -> 0x00d7 }
            goto L_0x00ad
        L_0x00d7:
            r10 = move-exception
            goto L_0x00ad
        L_0x00d9:
            if (r9 == 0) goto L_0x00de
            r9.close()     // Catch:{ Throwable -> 0x00e1 }
        L_0x00de:
            return
        L_0x00df:
            r11 = move-exception
            goto L_0x00d0
        L_0x00e1:
            r10 = move-exception
            goto L_0x00de
        L_0x00e3:
            r11 = move-exception
            goto L_0x005c
        L_0x00e6:
            r10 = move-exception
            goto L_0x00cb
        */
        throw new UnsupportedOperationException("Method not decompiled: org.appcelerator.titanium.util.TiFileHelper.deployFromZip(java.io.File, java.io.File):void");
    }

    public void wipeDirectoryTree(File path) {
        TreeSet<String> dirs = new TreeSet<>(new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o1.compareTo(o2) * -1;
            }
        });
        wipeDirectoryTree(path, dirs);
        Iterator<String> d = dirs.iterator();
        while (d.hasNext()) {
            File f = new File((String) d.next());
            Log.m29d(TAG, "Deleting Dir: " + f.getAbsolutePath(), Log.DEBUG_MODE);
            f.delete();
        }
    }

    public File getTempFile(String suffix, boolean destroyOnExit) throws IOException {
        Context context = (Context) this.softContext.get();
        if (context != null) {
            return getTempFile(context.getCacheDir(), suffix, destroyOnExit);
        }
        return null;
    }

    public File getTempFile(File dir, String suffix, boolean destroyOnExit) throws IOException {
        File result = null;
        if (((Context) this.softContext.get()) != null) {
            if (!dir.exists()) {
                Log.m44w(TAG, "getTempFile: Directory '" + dir.getAbsolutePath() + "' does not exist. Call to File.createTempFile() will fail.");
            }
            result = File.createTempFile("tia", suffix, dir);
            if (destroyOnExit) {
                this.tempFiles.add(result);
            }
        }
        return result;
    }

    public File getTempFileFromInputStream(InputStream is, String suffix, boolean destroyOnExit) {
        try {
            File tempFile = getTempFile(suffix, destroyOnExit);
            if (!tempFile.exists()) {
                return tempFile;
            }
            byte[] bytes = new byte[1024];
            FileOutputStream os = new FileOutputStream(tempFile);
            while (true) {
                int length = is.read(bytes);
                if (length != -1) {
                    os.write(bytes, 0, length);
                } else {
                    os.close();
                    return tempFile;
                }
            }
        } catch (FileNotFoundException e) {
            Log.m44w(TAG, "Could not find temp file: " + suffix);
        } catch (IOException e2) {
            Log.m44w(TAG, "Error occurred while creating output stream from temp file: " + suffix);
        }
        return null;
    }

    public void destroyTempFiles() {
        Iterator it = this.tempFiles.iterator();
        while (it.hasNext()) {
            ((File) it.next()).delete();
        }
        this.tempFiles.clear();
    }

    public File getDataDirectory(boolean privateStorage) {
        Context context = (Context) this.softContext.get();
        if (context == null) {
            return null;
        }
        if (privateStorage) {
            return context.getDir("appdata", 0);
        }
        File f = new File(Environment.getExternalStorageDirectory(), context.getPackageName());
        if (f.exists()) {
            return f;
        }
        f.mkdirs();
        return f;
    }

    private void wipeDirectoryTree(File path, SortedSet<String> dirs) {
        File[] files = path.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    dirs.add(f.getAbsolutePath());
                    wipeDirectoryTree(f, dirs);
                } else {
                    Log.m29d(TAG, "Deleting File: " + f.getAbsolutePath(), Log.DEBUG_MODE);
                    f.delete();
                }
            }
        }
    }

    private void walkAssets(AssetManager am, String path, ArrayList<String> paths) throws IOException {
        String todo;
        if (titaniumPath(path)) {
            String[] files = am.list(path);
            if (files.length > 0) {
                for (String newPath : files) {
                    String todo2 = path;
                    if (path.length() > 0) {
                        todo = todo2 + TiUrl.PATH_SEPARATOR + newPath;
                    } else {
                        todo = newPath;
                    }
                    if (titaniumPath(todo)) {
                        paths.add(todo);
                        walkAssets(am, todo, paths);
                    }
                }
            }
        }
    }

    private boolean titaniumPath(String path) {
        return path == "" || path.equals("tiapp.xml") || path.startsWith("Resources");
    }

    private ZipInputStream getZipInputStream(InputStream is) throws FileNotFoundException, IOException {
        return new ZipInputStream(is);
    }

    private String getRootDir(ZipInputStream zis) throws FileNotFoundException, IOException {
        String root = "";
        while (true) {
            ZipEntry ze = zis.getNextEntry();
            if (ze == null) {
                return root;
            }
            String name = ze.getName();
            zis.closeEntry();
            if (!name.startsWith(MACOSX_PREFIX) && name.indexOf("tiapp.xml") > -1) {
                String[] segments = name.split("\\/");
                if (segments.length == 2) {
                    return segments[0] + TiUrl.PATH_SEPARATOR;
                }
                if (segments.length == 1) {
                    return root;
                }
            }
        }
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 100:
                AsyncResult result = (AsyncResult) msg.obj;
                try {
                    result.setResult(handleNetworkURL(TiConvert.toString(result.getArg())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return false;
        }
    }
}
