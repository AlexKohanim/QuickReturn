package p006ti.modules.titanium.contacts;

import android.graphics.Bitmap;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.TiC;

/* renamed from: ti.modules.titanium.contacts.PersonProxy */
public class PersonProxy extends KrollProxy {
    private static final String TAG = "Person";
    private String fullName = "";
    protected boolean hasImage = false;
    private TiBlob image = null;
    private boolean imageFetched;
    private HashMap<String, Boolean> modified = new HashMap<>();

    private boolean isPhotoFetchable() {
        return ((Long) getProperty(TiC.PROPERTY_ID)).longValue() > 0 && this.hasImage;
    }

    public void finishModification() {
        this.modified.clear();
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fname) {
        this.fullName = fname;
    }

    public long getId() {
        return ((Long) getProperty(TiC.PROPERTY_ID)).longValue();
    }

    public boolean isFieldModified(String field) {
        return this.modified.containsKey(field) && ((Boolean) this.modified.get(field)).booleanValue();
    }

    public TiBlob getImage() {
        if (this.image != null) {
            return this.image;
        }
        if (!this.imageFetched && isPhotoFetchable()) {
            Bitmap photo = CommonContactsApi.getContactImage(((Long) getProperty(TiC.PROPERTY_ID)).longValue());
            if (photo != null) {
                this.image = TiBlob.blobFromImage(photo);
            }
            this.imageFetched = true;
        }
        return this.image;
    }

    public void setImage(TiBlob blob) {
        this.image = blob;
        this.hasImage = true;
        this.imageFetched = true;
        this.modified.put(TiC.PROPERTY_IMAGE, Boolean.valueOf(true));
    }

    private KrollDict contactMethodMapToDict(Map<String, ArrayList<String>> map) {
        KrollDict result = new KrollDict();
        for (String key : map.keySet()) {
            result.put(key, ((ArrayList) map.get(key)).toArray());
        }
        return result;
    }

    /* access modifiers changed from: protected */
    public void setEmailFromMap(Map<String, ArrayList<String>> map) {
        setProperty("email", contactMethodMapToDict(map));
    }

    /* access modifiers changed from: protected */
    public void setDateFromMap(Map<String, ArrayList<String>> map) {
        setProperty(TiC.PROPERTY_DATE, contactMethodMapToDict(map));
    }

    /* access modifiers changed from: protected */
    public void setIMFromMap(Map<String, ArrayList<String>> map) {
        setProperty(TiC.PROPERTY_INSTANTMSG, contactMethodMapToDict(map));
    }

    /* access modifiers changed from: protected */
    public void setRelatedNameFromMap(Map<String, ArrayList<String>> map) {
        setProperty(TiC.PROPERTY_RELATED_NAMES, contactMethodMapToDict(map));
    }

    /* access modifiers changed from: protected */
    public void setWebSiteFromMap(Map<String, ArrayList<String>> map) {
        setProperty("url", contactMethodMapToDict(map));
    }

    /* access modifiers changed from: protected */
    public void setPhoneFromMap(Map<String, ArrayList<String>> map) {
        setProperty(TiC.PROPERTY_PHONE, contactMethodMapToDict(map));
    }

    /* access modifiers changed from: protected */
    public void setAddressFromMap(Map<String, ArrayList<String>> map) {
        KrollDict address = new KrollDict();
        for (String key : map.keySet()) {
            ArrayList<String> values = (ArrayList) map.get(key);
            KrollDict[] dictValues = new KrollDict[values.size()];
            for (int i = 0; i < dictValues.length; i++) {
                dictValues[i] = new KrollDict();
                dictValues[i].put("Street", values.get(i));
            }
            address.put(key, dictValues);
        }
        setProperty(TiC.PROPERTY_ADDRESS, address);
    }

    public void onPropertyChanged(String name, Object value) {
        if (name == null) {
            Log.w(TAG, "Property is null. Unable to process change");
            return;
        }
        if (name.equals(TiC.PROPERTY_FIRSTNAME) || name.equals(TiC.PROPERTY_MIDDLENAME) || name.equals(TiC.PROPERTY_LASTNAME)) {
            this.modified.put(TiC.PROPERTY_NAME, Boolean.valueOf(true));
        } else if (name.equals(TiC.PROPERTY_BIRTHDAY) || name.equals(TiC.PROPERTY_ORGANIZATION) || name.equals(TiC.PROPERTY_NOTE) || name.equals(TiC.PROPERTY_NICKNAME) || name.equals(TiC.PROPERTY_PHONE) || name.equals(TiC.PROPERTY_ADDRESS) || name.equals(TiC.PROPERTY_INSTANTMSG) || name.equals("url") || name.equals("email") || name.equals(TiC.PROPERTY_RELATED_NAMES) || name.equals(TiC.PROPERTY_DATE) || name.equals(TiC.PROPERTY_KIND) || name.equals(TiC.PROPERTY_PREFIX) || name.equals(TiC.PROPERTY_SUFFIX) || name.equals(TiC.PROPERTY_FIRSTPHONETIC) || name.equals(TiC.PROPERTY_MIDDLEPHONETIC) || name.equals(TiC.PROPERTY_LASTPHONETIC) || name.equals(TiC.PROPERTY_JOBTITLE) || name.equals(TiC.PROPERTY_DEPARTMENT)) {
            this.modified.put(name, Boolean.valueOf(true));
        }
        super.onPropertyChanged(name, value);
    }

    public String getApiName() {
        return "Ti.Contacts.Person";
    }
}
