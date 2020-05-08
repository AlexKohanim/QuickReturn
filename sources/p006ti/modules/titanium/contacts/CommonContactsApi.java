package p006ti.modules.titanium.contacts;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;

/* renamed from: ti.modules.titanium.contacts.CommonContactsApi */
public abstract class CommonContactsApi {
    private static final String TAG = "TiCommonContactsApi";
    private static final boolean TRY_NEWER_API = (VERSION.SDK_INT > 4);

    /* renamed from: ti.modules.titanium.contacts.CommonContactsApi$LightPerson */
    protected static class LightPerson {
        Map<String, ArrayList<String>> addresses = new HashMap();
        String birthday;
        Map<String, ArrayList<String>> dates = new HashMap();
        String department;
        Map<String, ArrayList<String>> emails = new HashMap();
        String fname;
        String fphonetic;
        boolean hasImage = false;

        /* renamed from: id */
        long f51id;
        String instantMessage;
        Map<String, ArrayList<String>> instantMessages = new HashMap();
        String jobTitle;
        String lname;
        String lphonetic;
        String mname;
        String mphonetic;
        String name;
        String nickname;
        String notes;
        String organization;
        Map<String, ArrayList<String>> phones = new HashMap();
        String pname;
        String relatedName;
        Map<String, ArrayList<String>> relatedNames = new HashMap();
        String sname;
        Map<String, ArrayList<String>> websites = new HashMap();

        protected LightPerson() {
        }

        /* access modifiers changed from: 0000 */
        public void addPersonInfoFromL5DataRow(Cursor cursor) {
            this.f51id = cursor.getLong(ContactsApiLevel5.DATA_COLUMN_CONTACT_ID);
            this.name = cursor.getString(ContactsApiLevel5.DATA_COLUMN_DISPLAY_NAME);
            this.hasImage = cursor.getInt(ContactsApiLevel5.DATA_COLUMN_PHOTO_ID) > 0;
        }

        /* access modifiers changed from: 0000 */
        public void addPersonInfoFromL5PersonRow(Cursor cursor) {
            this.f51id = cursor.getLong(ContactsApiLevel5.PEOPLE_COL_ID);
            this.name = cursor.getString(ContactsApiLevel5.PEOPLE_COL_NAME);
            this.hasImage = cursor.getInt(ContactsApiLevel5.PEOPLE_COL_PHOTO_ID) > 0;
        }

        /* access modifiers changed from: 0000 */
        public void addDataFromL5Cursor(Cursor cursor) {
            String kind = cursor.getString(ContactsApiLevel5.DATA_COLUMN_MIMETYPE);
            if (kind.equals(ContactsApiLevel5.KIND_ADDRESS)) {
                loadAddressFromL5DataRow(cursor);
            } else if (kind.equals(ContactsApiLevel5.KIND_EMAIL)) {
                loadEmailFromL5DataRow(cursor);
            } else if (kind.equals(ContactsApiLevel5.KIND_EVENT)) {
                loadBirthdayFromL5DataRow(cursor);
            } else if (kind.equals(ContactsApiLevel5.KIND_NAME)) {
                loadNameFromL5DataRow(cursor);
            } else if (kind.equals(ContactsApiLevel5.KIND_NOTE)) {
                loadNoteFromL5DataRow(cursor);
            } else if (kind.equals(ContactsApiLevel5.KIND_PHONE)) {
                loadPhoneFromL5DataRow(cursor);
            } else if (kind.equals(ContactsApiLevel5.KIND_PHONE)) {
                loadPhoneFromL5DataRow(cursor);
            } else if (kind.equals(ContactsApiLevel5.KIND_NICKNAME)) {
                loadPhonNickL5DataRow(cursor);
            } else if (kind.equals(ContactsApiLevel5.KIND_ORGANIZE)) {
                loadOrganizationL5DataRow(cursor);
            } else if (kind.equals(ContactsApiLevel5.KIND_IM)) {
                loadImL5DataRow(cursor);
            } else if (kind.equals(ContactsApiLevel5.KIND_RELATED_NAME)) {
                loadRelatedNamesL5DataRow(cursor);
            } else if (kind.equals(ContactsApiLevel5.KIND_WEBSITE)) {
                loadWebSiteL5DataRow(cursor);
            }
        }

        /* access modifiers changed from: 0000 */
        public void loadImL5DataRow(Cursor imCursor) {
            ArrayList<String> collection;
            this.instantMessage = imCursor.getString(ContactsApiLevel5.DATA_COLUMN_IM);
            String key = CommonContactsApi.getImTextType(imCursor.getInt(ContactsApiLevel5.DATA_COLUMN_IM_TYPE));
            if (this.instantMessages.containsKey(key)) {
                collection = (ArrayList) this.instantMessages.get(key);
            } else {
                collection = new ArrayList<>();
                this.instantMessages.put(key, collection);
            }
            collection.add(this.instantMessage);
        }

        /* access modifiers changed from: 0000 */
        public void loadRelatedNamesL5DataRow(Cursor rnCursor) {
            ArrayList<String> collection;
            this.relatedName = rnCursor.getString(ContactsApiLevel5.DATA_COLUMN_RELATED_NAME);
            String key = CommonContactsApi.getRelatedNamesType(rnCursor.getInt(ContactsApiLevel5.DATA_COLUMN_RELATED_NAME_TYPE));
            if (this.relatedNames.containsKey(key)) {
                collection = (ArrayList) this.relatedNames.get(key);
            } else {
                collection = new ArrayList<>();
                this.relatedNames.put(key, collection);
            }
            collection.add(this.relatedName);
        }

        /* access modifiers changed from: 0000 */
        public void loadOrganizationL5DataRow(Cursor cursor) {
            this.organization = cursor.getString(ContactsApiLevel5.DATA_COLUMN_ORGANIZATION);
            this.jobTitle = cursor.getString(ContactsApiLevel5.DATA_COLUMN_JOB_TITLE);
            this.department = cursor.getString(ContactsApiLevel5.DATA_COLUMN_DEPARTMENT);
        }

        /* access modifiers changed from: 0000 */
        public void loadPhonNickL5DataRow(Cursor cursor) {
            this.nickname = cursor.getString(ContactsApiLevel5.DATA_COLUMN_NICK_NAME);
        }

        /* access modifiers changed from: 0000 */
        public void loadPhoneFromL5DataRow(Cursor phonesCursor) {
            ArrayList<String> collection;
            String phoneNumber = phonesCursor.getString(ContactsApiLevel5.DATA_COLUMN_PHONE_NUMBER);
            String key = CommonContactsApi.getPhoneTextType(phonesCursor.getInt(ContactsApiLevel5.DATA_COLUMN_PHONE_TYPE));
            if (this.phones.containsKey(key)) {
                collection = (ArrayList) this.phones.get(key);
            } else {
                collection = new ArrayList<>();
                this.phones.put(key, collection);
            }
            collection.add(phoneNumber);
        }

        /* access modifiers changed from: 0000 */
        public void loadNoteFromL5DataRow(Cursor cursor) {
            this.notes = cursor.getString(ContactsApiLevel5.DATA_COLUMN_NOTE);
        }

        /* access modifiers changed from: 0000 */
        public void loadBirthdayFromL5DataRow(Cursor cursor) {
            if (cursor.getInt(ContactsApiLevel5.DATA_COLUMN_EVENT_TYPE) == 3) {
                this.birthday = cursor.getString(ContactsApiLevel5.DATA_COLUMN_EVENT_DATE);
            }
            loadDatesL5DataRow(cursor);
        }

        /* access modifiers changed from: 0000 */
        public void loadEmailFromL5DataRow(Cursor emailsCursor) {
            ArrayList<String> collection;
            String emailAddress = emailsCursor.getString(ContactsApiLevel5.DATA_COLUMN_EMAIL_ADDR);
            String key = CommonContactsApi.getEmailTextType(emailsCursor.getInt(ContactsApiLevel5.DATA_COLUMN_EMAIL_TYPE));
            if (this.emails.containsKey(key)) {
                collection = (ArrayList) this.emails.get(key);
            } else {
                collection = new ArrayList<>();
                this.emails.put(key, collection);
            }
            collection.add(emailAddress);
        }

        /* access modifiers changed from: 0000 */
        public void loadWebSiteL5DataRow(Cursor websitesCursor) {
            ArrayList<String> collection;
            String website = websitesCursor.getString(ContactsApiLevel5.DATA_COLUMN_WEBSITE_ADDR);
            String key = "website";
            if (this.websites.containsKey(key)) {
                collection = (ArrayList) this.websites.get(key);
            } else {
                collection = new ArrayList<>();
                this.websites.put(key, collection);
            }
            collection.add(website);
        }

        /* access modifiers changed from: 0000 */
        public void loadDatesL5DataRow(Cursor datesCursor) {
            ArrayList<String> collection;
            String date = datesCursor.getString(ContactsApiLevel5.DATA_COLUMN_DATE_ADDR);
            String key = CommonContactsApi.getDateTextType(datesCursor.getInt(ContactsApiLevel5.DATA_COLUMN_DATE_TYPE));
            if (this.dates.containsKey(key)) {
                collection = (ArrayList) this.dates.get(key);
            } else {
                collection = new ArrayList<>();
                this.dates.put(key, collection);
            }
            collection.add(date);
        }

        /* access modifiers changed from: 0000 */
        public void loadNameFromL5DataRow(Cursor nameCursor) {
            this.fname = nameCursor.getString(ContactsApiLevel5.DATA_COLUMN_NAME_FIRST);
            this.lname = nameCursor.getString(ContactsApiLevel5.DATA_COLUMN_NAME_LAST);
            this.pname = nameCursor.getString(ContactsApiLevel5.DATA_COLUMN_NAME_PREFIX);
            this.mname = nameCursor.getString(ContactsApiLevel5.DATA_COLUMN_NAME_MIDDLE);
            this.sname = nameCursor.getString(ContactsApiLevel5.DATA_COLUMN_NAME_SUFFIX);
            this.fphonetic = nameCursor.getString(ContactsApiLevel5.DATA_COLUMN_DATA9);
            this.mphonetic = nameCursor.getString(ContactsApiLevel5.DATA_COLUMN_DATA8);
            this.lphonetic = nameCursor.getString(ContactsApiLevel5.DATA_COLUMN_DATA7);
        }

        /* access modifiers changed from: 0000 */
        public void loadAddressFromL5DataRow(Cursor cursor) {
            ArrayList<String> collection;
            String fullAddress = cursor.getString(ContactsApiLevel5.DATA_COLUMN_ADDRESS_FULL);
            String key = CommonContactsApi.getPostalAddressTextType(cursor.getInt(ContactsApiLevel5.DATA_COLUMN_ADDRESS_TYPE));
            if (this.addresses.containsKey(key)) {
                collection = (ArrayList) this.addresses.get(key);
            } else {
                collection = new ArrayList<>();
                this.addresses.put(key, collection);
            }
            collection.add(fullAddress);
        }

        /* access modifiers changed from: 0000 */
        public PersonProxy proxify() {
            PersonProxy proxy = new PersonProxy();
            proxy.setFullName(this.name);
            proxy.setProperty(TiC.PROPERTY_FIRSTNAME, this.fname);
            proxy.setProperty(TiC.PROPERTY_LASTNAME, this.lname);
            proxy.setProperty(TiC.PROPERTY_MIDDLENAME, this.mname);
            proxy.setProperty(TiC.PROPERTY_PREFIX, this.pname);
            proxy.setProperty(TiC.PROPERTY_SUFFIX, this.sname);
            proxy.setProperty(TiC.PROPERTY_FIRSTPHONETIC, this.fphonetic);
            proxy.setProperty(TiC.PROPERTY_MIDDLEPHONETIC, this.mphonetic);
            proxy.setProperty(TiC.PROPERTY_LASTPHONETIC, this.lphonetic);
            proxy.setProperty(TiC.PROPERTY_BIRTHDAY, this.birthday);
            proxy.setProperty(TiC.PROPERTY_ORGANIZATION, this.organization);
            proxy.setProperty(TiC.PROPERTY_JOBTITLE, this.jobTitle);
            proxy.setProperty(TiC.PROPERTY_DEPARTMENT, this.department);
            proxy.setProperty(TiC.PROPERTY_NICKNAME, this.nickname);
            proxy.setIMFromMap(this.instantMessages);
            proxy.setRelatedNameFromMap(this.relatedNames);
            proxy.setWebSiteFromMap(this.websites);
            proxy.setProperty(TiC.PROPERTY_NOTE, this.notes);
            proxy.setProperty(TiC.PROPERTY_BIRTHDAY, this.birthday);
            proxy.setEmailFromMap(this.emails);
            proxy.setDateFromMap(this.dates);
            proxy.setPhoneFromMap(this.phones);
            proxy.setAddressFromMap(this.addresses);
            proxy.setProperty(TiC.PROPERTY_KIND, Integer.valueOf(1));
            proxy.setProperty(TiC.PROPERTY_ID, Long.valueOf(this.f51id));
            proxy.hasImage = this.hasImage;
            return proxy;
        }
    }

    /* access modifiers changed from: protected */
    public abstract PersonProxy addContact(KrollDict krollDict);

    /* access modifiers changed from: protected */
    public abstract PersonProxy[] getAllPeople(int i);

    /* access modifiers changed from: protected */
    public abstract Intent getIntentForContactsPicker();

    /* access modifiers changed from: protected */
    public abstract Bitmap getInternalContactImage(long j);

    /* access modifiers changed from: protected */
    public abstract PersonProxy[] getPeopleWithName(String str);

    /* access modifiers changed from: protected */
    public abstract PersonProxy getPersonById(long j);

    /* access modifiers changed from: protected */
    public abstract PersonProxy getPersonByUri(Uri uri);

    /* access modifiers changed from: protected */
    public abstract void removePerson(PersonProxy personProxy);

    /* access modifiers changed from: protected */
    public abstract void save(Object obj);

    protected static CommonContactsApi getInstance() {
        boolean useNew = false;
        if (TRY_NEWER_API) {
            try {
                Class.forName("android.provider.ContactsContract");
                useNew = true;
            } catch (ClassNotFoundException e) {
                Log.m34e(TAG, "Unable to load contacts api: " + e.getMessage(), (Throwable) e);
                useNew = false;
            }
        } else {
            Log.m32e(TAG, "Contacts API 4 is not supported");
        }
        if (!useNew) {
            return null;
        }
        ContactsApiLevel5 c = new ContactsApiLevel5();
        if (c.loadedOk) {
            return c;
        }
        Log.m32e(TAG, "ContactsApiLevel5 did not load successfully.");
        return null;
    }

    public boolean hasContactsPermissions() {
        if (VERSION.SDK_INT < 23) {
            return true;
        }
        Context context = TiApplication.getInstance().getApplicationContext();
        if (context != null && context.checkSelfPermission("android.permission.READ_CONTACTS") == 0) {
            return true;
        }
        Log.m44w(TAG, "Contact permissions are missing");
        return false;
    }

    protected static Bitmap getContactImage(long contact_id) {
        return getInstance().getInternalContactImage(contact_id);
    }

    /* access modifiers changed from: protected */
    public PersonProxy[] getAllPeople() {
        return getAllPeople(ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
    }

    /* access modifiers changed from: protected */
    public PersonProxy[] proxifyPeople(Map<Long, LightPerson> persons) {
        PersonProxy[] proxies = new PersonProxy[persons.size()];
        int index = 0;
        for (LightPerson person : persons.values()) {
            proxies[index] = person.proxify();
            index++;
        }
        return proxies;
    }

    protected static String getEmailTextType(int type) {
        String key = TiC.PROPERTY_OTHER;
        if (type == 1) {
            return TiC.PROPERTY_HOME;
        }
        if (type == 2) {
            return TiC.PROPERTY_WORK;
        }
        return key;
    }

    protected static String getDateTextType(int type) {
        String key = TiC.PROPERTY_OTHER;
        if (type == 1) {
            return TiC.PROPERTY_ANNIVERSARY;
        }
        if (type == 3) {
            return TiC.PROPERTY_BIRTHDAY;
        }
        if (type == 0) {
            return TiC.PROPERTY_CUSTOM;
        }
        return key;
    }

    protected static String getImTextType(int type) {
        String key = TiC.PROPERTY_OTHER;
        if (type == 0) {
            return "aim";
        }
        if (type == -1) {
            return TiC.PROPERTY_CUSTOM;
        }
        if (type == 1) {
            return "msn";
        }
        if (type == 2) {
            return "yahoo";
        }
        if (type == 3) {
            return "skype";
        }
        if (type == 4) {
            return "qq";
        }
        if (type == 5) {
            return "googleTalk";
        }
        if (type == 6) {
            return "icq";
        }
        if (type == 7) {
            return "jabber";
        }
        if (type == 8) {
            return "netMeeting";
        }
        return key;
    }

    protected static String getRelatedNamesType(int type) {
        String key = TiC.PROPERTY_OTHER;
        if (type == 1) {
            return TiC.PROPERTY_ASSISTANT;
        }
        if (type == 2) {
            return TiC.PROPERTY_BROTHER;
        }
        if (type == 3) {
            return TiC.PROPERTY_CHILD;
        }
        if (type == 4) {
            return TiC.PROPERTY_DOMESTIC_PARTNER;
        }
        if (type == 5) {
            return TiC.PROPERTY_FATHER;
        }
        if (type == 6) {
            return TiC.PROPERTY_FRIEND;
        }
        if (type == 7) {
            return TiC.PROPERTY_MANAGER;
        }
        if (type == 8) {
            return TiC.PROPERTY_MOTHER;
        }
        if (type == 9) {
            return TiC.PROPERTY_PARENT;
        }
        if (type == 10) {
            return TiC.PROPERTY_PARTNER;
        }
        if (type == 11) {
            return TiC.PROPERTY_REFERRED_BY;
        }
        if (type == 12) {
            return "relative";
        }
        if (type == 13) {
            return TiC.PROPERTY_SISTER;
        }
        if (type == 14) {
            return "spose";
        }
        if (type == 0) {
            return TiC.PROPERTY_CUSTOM;
        }
        return key;
    }

    protected static String getPhoneTextType(int type) {
        String key = TiC.PROPERTY_OTHER;
        if (type == 5) {
            key = "homeFax";
        }
        if (type == 4) {
            key = "workFax";
        }
        if (type == 1) {
            key = TiC.PROPERTY_HOME;
        }
        if (type == 2) {
            key = TiC.PROPERTY_MOBILE;
        }
        if (type == 6) {
            key = "pager";
        }
        if (type == 3) {
            return TiC.PROPERTY_WORK;
        }
        return key;
    }

    protected static String getPostalAddressTextType(int type) {
        String key = TiC.PROPERTY_OTHER;
        if (type == 1) {
            return TiC.PROPERTY_HOME;
        }
        if (type == 2) {
            return TiC.PROPERTY_WORK;
        }
        return key;
    }
}
