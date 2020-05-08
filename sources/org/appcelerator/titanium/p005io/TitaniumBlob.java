package org.appcelerator.titanium.p005io;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore.Images.Media;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.appcelerator.titanium.TiApplication;

/* renamed from: org.appcelerator.titanium.io.TitaniumBlob */
public class TitaniumBlob extends TiBaseFile {
    protected String name;
    protected String path;
    protected String url;

    public TitaniumBlob(String url2) {
        super(3);
        this.url = url2;
        if (url2 != null) {
            init();
        }
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: protected */
    public void init() {
        String[] projection = {"_display_name", "_data"};
        Cursor c = null;
        if (this.url.startsWith("content://com.android.providers.media.documents")) {
            try {
                Cursor c2 = TiApplication.getInstance().getContentResolver().query(Uri.parse(this.url), null, null, null, null);
                c2.moveToFirst();
                String id = c2.getString(0);
                String id2 = id.substring(id.lastIndexOf(":") + 1);
                c2.close();
                String[] strArr = projection;
                Cursor c3 = TiApplication.getInstance().getContentResolver().query(Media.EXTERNAL_CONTENT_URI, strArr, "_id = ? ", new String[]{id2}, null);
                if (c3.moveToNext()) {
                    this.name = c3.getString(0);
                    this.path = c3.getString(1);
                }
                if (c3 != null) {
                    c3.close();
                }
            } catch (Throwable th) {
                if (c != null) {
                    c.close();
                }
                throw th;
            }
        } else if (this.url.startsWith("content://com.android.providers.downloads.documents")) {
            try {
                Cursor c4 = TiApplication.getInstance().getContentResolver().query(ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(DocumentsContract.getDocumentId(Uri.parse(this.url))).longValue()), projection, null, null, null);
                if (c4.moveToNext()) {
                    this.name = c4.getString(0);
                    this.path = c4.getString(1);
                }
                if (c4 != null) {
                    c4.close();
                }
            } catch (Throwable th2) {
                if (c != null) {
                    c.close();
                }
                throw th2;
            }
        } else {
            try {
                Cursor c5 = TiApplication.getInstance().getContentResolver().query(Uri.parse(this.url), projection, null, null, null);
                if (c5.moveToNext()) {
                    this.name = c5.getString(0);
                    this.path = c5.getString(1);
                }
                if (c5 != null) {
                    c5.close();
                }
            } catch (Throwable th3) {
                if (c != null) {
                    c.close();
                }
                throw th3;
            }
        }
    }

    public void setUrl(String url2) {
        this.url = url2;
        if (url2 != null) {
            init();
        }
    }

    public String nativePath() {
        return this.url;
    }

    public String toURL() {
        return this.url;
    }

    public String name() {
        return this.name;
    }

    public File getFile() {
        return new File(this.path);
    }

    public String getContentType() {
        return TiApplication.getInstance().getContentResolver().getType(Uri.parse(this.url));
    }

    public InputStream getInputStream() throws IOException {
        return TiApplication.getInstance().getContentResolver().openInputStream(Uri.parse(this.url));
    }

    public OutputStream getOutputStream() throws IOException {
        return null;
    }

    public File getNativeFile() {
        return new File(this.path);
    }

    public String getNativePath() {
        return this.path;
    }
}
