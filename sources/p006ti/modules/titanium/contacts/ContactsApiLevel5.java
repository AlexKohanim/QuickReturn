package p006ti.modules.titanium.contacts;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;
import p006ti.modules.titanium.android.AndroidModule;

/* renamed from: ti.modules.titanium.contacts.ContactsApiLevel5 */
public class ContactsApiLevel5 extends CommonContactsApi {
    protected static String BASE_SELECTION = "raw_contact_id=? AND mimetype=?";
    private static Class<?> Contacts = null;
    private static Uri ContactsUri = null;
    protected static int DATA_COLUMN_ADDRESS_CITY = DATA_COLUMN_DATA7;
    protected static int DATA_COLUMN_ADDRESS_COUNTRY = DATA_COLUMN_DATA10;
    protected static int DATA_COLUMN_ADDRESS_FULL = DATA_COLUMN_DATA1;
    protected static int DATA_COLUMN_ADDRESS_NEIGHBORHOOD = DATA_COLUMN_DATA6;
    protected static int DATA_COLUMN_ADDRESS_POBOX = DATA_COLUMN_DATA5;
    protected static int DATA_COLUMN_ADDRESS_POSTCODE = DATA_COLUMN_DATA9;
    protected static int DATA_COLUMN_ADDRESS_STATE = DATA_COLUMN_DATA8;
    protected static int DATA_COLUMN_ADDRESS_STREET = DATA_COLUMN_DATA4;
    protected static int DATA_COLUMN_ADDRESS_TYPE = DATA_COLUMN_DATA2;
    protected static int DATA_COLUMN_CONTACT_ID = 0;
    protected static int DATA_COLUMN_DATA1 = 4;
    protected static int DATA_COLUMN_DATA10 = 13;
    protected static int DATA_COLUMN_DATA2 = 5;
    protected static int DATA_COLUMN_DATA3 = 6;
    protected static int DATA_COLUMN_DATA4 = 7;
    protected static int DATA_COLUMN_DATA5 = 8;
    protected static int DATA_COLUMN_DATA6 = 9;
    protected static int DATA_COLUMN_DATA7 = 10;
    protected static int DATA_COLUMN_DATA8 = 11;
    protected static int DATA_COLUMN_DATA9 = 12;
    protected static int DATA_COLUMN_DATE_ADDR = DATA_COLUMN_DATA1;
    protected static int DATA_COLUMN_DATE_TYPE = DATA_COLUMN_DATA2;
    protected static int DATA_COLUMN_DEPARTMENT = DATA_COLUMN_DATA2;
    protected static int DATA_COLUMN_DISPLAY_NAME = 3;
    protected static int DATA_COLUMN_EMAIL_ADDR = DATA_COLUMN_DATA1;
    protected static int DATA_COLUMN_EMAIL_TYPE = DATA_COLUMN_DATA2;
    protected static int DATA_COLUMN_EVENT_DATE = DATA_COLUMN_DATA1;
    protected static int DATA_COLUMN_EVENT_TYPE = DATA_COLUMN_DATA2;
    protected static int DATA_COLUMN_IM = DATA_COLUMN_DATA1;
    protected static int DATA_COLUMN_IM_TYPE = DATA_COLUMN_DATA5;
    protected static int DATA_COLUMN_JOB_TITLE = DATA_COLUMN_DATA4;
    protected static int DATA_COLUMN_MIMETYPE = 1;
    protected static int DATA_COLUMN_NAME_FIRST = DATA_COLUMN_DATA2;
    protected static int DATA_COLUMN_NAME_LAST = DATA_COLUMN_DATA3;
    protected static int DATA_COLUMN_NAME_MIDDLE = DATA_COLUMN_DATA5;
    protected static int DATA_COLUMN_NAME_PREFIX = DATA_COLUMN_DATA4;
    protected static int DATA_COLUMN_NAME_SUFFIX = DATA_COLUMN_DATA6;
    protected static int DATA_COLUMN_NICK_NAME = DATA_COLUMN_DATA1;
    protected static int DATA_COLUMN_NOTE = DATA_COLUMN_DATA1;
    protected static int DATA_COLUMN_ORGANIZATION = DATA_COLUMN_DATA1;
    protected static int DATA_COLUMN_PHONE_NUMBER = DATA_COLUMN_DATA1;
    protected static int DATA_COLUMN_PHONE_TYPE = DATA_COLUMN_DATA2;
    protected static int DATA_COLUMN_PHOTO_ID = 2;
    protected static int DATA_COLUMN_RELATED_NAME = DATA_COLUMN_DATA1;
    protected static int DATA_COLUMN_RELATED_NAME_TYPE = DATA_COLUMN_DATA2;
    protected static int DATA_COLUMN_WEBSITE_ADDR = DATA_COLUMN_DATA1;
    protected static int DATA_COLUMN_WEBSITE_TYPE = DATA_COLUMN_DATA2;
    private static String[] DATA_PROJECTION = {"contact_id", "mimetype", "photo_id", "display_name", "data1", "data2", "data3", "data4", "data5", "data6", "data7", "data8", "data9", "data10"};
    private static Uri DataUri = null;
    private static String INConditionForKinds = ("('" + KIND_ADDRESS + "','" + KIND_EMAIL + "','" + KIND_EVENT + "','" + KIND_NAME + "','" + KIND_NOTE + "','" + KIND_PHONE + "','" + KIND_NICKNAME + "','" + KIND_ORGANIZE + "','" + KIND_IM + "','" + KIND_RELATED_NAME + "','" + KIND_WEBSITE + "')");
    protected static String KIND_ADDRESS = "vnd.android.cursor.item/postal-address_v2";
    protected static String KIND_EMAIL = "vnd.android.cursor.item/email_v2";
    protected static String KIND_EVENT = "vnd.android.cursor.item/contact_event";
    protected static String KIND_IM = "vnd.android.cursor.item/im";
    protected static String KIND_NAME = "vnd.android.cursor.item/name";
    protected static String KIND_NICKNAME = "vnd.android.cursor.item/nickname";
    protected static String KIND_NOTE = "vnd.android.cursor.item/note";
    protected static String KIND_ORGANIZE = "vnd.android.cursor.item/organization";
    protected static String KIND_PHONE = "vnd.android.cursor.item/phone_v2";
    protected static String KIND_RELATED_NAME = "vnd.android.cursor.item/relation";
    protected static String KIND_WEBSITE = "vnd.android.cursor.item/website";
    protected static int PEOPLE_COL_ID = 0;
    protected static int PEOPLE_COL_NAME = 1;
    protected static int PEOPLE_COL_PHOTO_ID = 2;
    private static String[] PEOPLE_PROJECTION = {"_id", "display_name", "photo_id"};
    protected static String[] RELATED_NAMES_TYPE = {TiC.PROPERTY_ASSISTANT, TiC.PROPERTY_BROTHER, TiC.PROPERTY_CHILD, TiC.PROPERTY_DOMESTIC_PARTNER, TiC.PROPERTY_FATHER, TiC.PROPERTY_FRIEND, TiC.PROPERTY_MANAGER, TiC.PROPERTY_MOTHER, TiC.PROPERTY_PARENT, TiC.PROPERTY_PARTNER, TiC.PROPERTY_REFERRED_BY, TiC.PROPERTY_OTHER, TiC.PROPERTY_SISTER};
    private static final String TAG = "TiContacts5";
    protected boolean loadedOk = true;
    private Method openContactPhotoInputStream;

    protected ContactsApiLevel5() {
        try {
            DataUri = (Uri) Class.forName("android.provider.ContactsContract$Data").getField("CONTENT_URI").get(null);
            Contacts = Class.forName("android.provider.ContactsContract$Contacts");
            ContactsUri = (Uri) Contacts.getField("CONTENT_URI").get(null);
            this.openContactPhotoInputStream = Contacts.getMethod("openContactPhotoInputStream", new Class[]{ContentResolver.class, Uri.class});
        } catch (Throwable t) {
            Log.m35e(TAG, "Failed to load android.provider.ContactsContract$Contacts " + t.getMessage(), t, Log.DEBUG_MODE);
            this.loadedOk = false;
        }
    }

    /* access modifiers changed from: protected */
    public PersonProxy[] getAllPeople(int limit) {
        return getPeople(limit, null, null);
    }

    private PersonProxy[] getPeople(int limit, String additionalCondition, String[] additionalSelectionArgs) {
        LightPerson person;
        if (!hasContactsPermissions()) {
            Log.m32e(TAG, "Contacts permissions missing");
            return null;
        } else if (TiApplication.getInstance() == null) {
            Log.m33e(TAG, "Failed to call getPeople(), application is null", Log.DEBUG_MODE);
            return null;
        } else {
            Activity activity = TiApplication.getInstance().getRootOrCurrentActivity();
            if (activity == null) {
                Log.m33e(TAG, "Failed to call getPeople(), activity is null", Log.DEBUG_MODE);
                return null;
            }
            LinkedHashMap<Long, LightPerson> persons = new LinkedHashMap<>();
            String condition = "mimetype IN " + INConditionForKinds;
            if (additionalCondition != null) {
                condition = condition + " AND " + additionalCondition;
            }
            Cursor cursor = activity.getContentResolver().query(DataUri, DATA_PROJECTION, condition, additionalSelectionArgs, "display_name COLLATE LOCALIZED asc, contact_id asc, mimetype asc, is_super_primary desc, is_primary desc");
            while (cursor.moveToNext() && persons.size() < limit) {
                long id = cursor.getLong(DATA_COLUMN_CONTACT_ID);
                if (persons.containsKey(Long.valueOf(id))) {
                    person = (LightPerson) persons.get(Long.valueOf(id));
                } else {
                    person = new LightPerson();
                    person.addPersonInfoFromL5DataRow(cursor);
                    persons.put(Long.valueOf(id), person);
                }
                person.addDataFromL5Cursor(cursor);
            }
            cursor.close();
            return proxifyPeople(persons);
        }
    }

    /* access modifiers changed from: protected */
    public Intent getIntentForContactsPicker() {
        return new Intent(AndroidModule.ACTION_PICK, ContactsUri);
    }

    /* access modifiers changed from: protected */
    public PersonProxy[] getPeopleWithName(String name) {
        return getPeople(ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED, "display_name like ? or display_name like ?", new String[]{name + '%', "% " + name + '%'});
    }

    /* access modifiers changed from: protected */
    public void updateContactField(ArrayList<ContentProviderOperation> ops, String mimeType, String idKey, Object idValue, String typeKey, int typeValue, long rawContactId) {
        Builder builder = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue("mimetype", mimeType).withValue(idKey, idValue);
        if (rawContactId == -1) {
            builder.withValueBackReference("raw_contact_id", 0);
        } else {
            builder.withValue("raw_contact_id", Long.valueOf(rawContactId));
        }
        if (typeKey != null) {
            builder.withValue(typeKey, Integer.valueOf(typeValue));
        }
        ops.add(builder.build());
    }

    /* access modifiers changed from: protected */
    public int processIMProtocol(String serviceName) {
        if (serviceName == null) {
            return -2;
        }
        if (serviceName.equals("AIM")) {
            return 0;
        }
        if (serviceName.equals("MSN")) {
            return 1;
        }
        if (serviceName.equals("ICQ")) {
            return 6;
        }
        if (serviceName.equals("Facebook")) {
            return -1;
        }
        if (serviceName.equals("GaduGadu")) {
            return -1;
        }
        if (serviceName.equals("GoogleTalk")) {
            return 5;
        }
        if (serviceName.equals("QQ")) {
            return 4;
        }
        if (serviceName.equals("Skype")) {
            return 3;
        }
        if (serviceName.equals("Yahoo")) {
            return 2;
        }
        if (serviceName.equals("Jabber")) {
            return 7;
        }
        return -2;
    }

    /* access modifiers changed from: protected */
    public void parseIm(ArrayList<ContentProviderOperation> ops, HashMap instantHashMap, long rawContactId) {
        if (instantHashMap.containsKey(TiC.PROPERTY_WORK)) {
            processInstantMsg(instantHashMap, TiC.PROPERTY_WORK, ops, 2, rawContactId);
        }
        if (instantHashMap.containsKey(TiC.PROPERTY_HOME)) {
            processInstantMsg(instantHashMap, TiC.PROPERTY_HOME, ops, 1, rawContactId);
        }
        if (instantHashMap.containsKey(TiC.PROPERTY_OTHER)) {
            processInstantMsg(instantHashMap, TiC.PROPERTY_OTHER, ops, 3, rawContactId);
        }
    }

    /* access modifiers changed from: protected */
    public void processInstantMsg(HashMap instantHashMap, String msgType, ArrayList<ContentProviderOperation> ops, int iType, long rawContactId) {
        Object instantObject = instantHashMap.get(msgType);
        if (instantObject instanceof Object[]) {
            Object[] instantArray = (Object[]) instantObject;
            for (Object typeIM : instantArray) {
                if (typeIM instanceof HashMap) {
                    HashMap typeHashMap = (HashMap) typeIM;
                    String userName = "";
                    String serviceName = "";
                    int serviceType = -2;
                    if (typeHashMap.containsKey("service")) {
                        serviceName = TiConvert.toString(typeHashMap, "service");
                        serviceType = processIMProtocol(serviceName);
                    }
                    if (typeHashMap.containsKey(TiC.PROPERTY_USERNAME)) {
                        userName = TiConvert.toString(typeHashMap, TiC.PROPERTY_USERNAME);
                    }
                    if (serviceType == -2) {
                        Log.m32e(TAG, "Unsupported IM Protocol detected when adding new contact");
                    } else if (userName.length() == 0) {
                        Log.m32e(TAG, "User name not provided when adding new contact");
                    } else {
                        Builder builder = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue("mimetype", "vnd.android.cursor.item/im").withValue("data1", userName).withValue("data2", Integer.valueOf(iType));
                        if (rawContactId == -1) {
                            builder.withValueBackReference("raw_contact_id", 0);
                        } else {
                            builder.withValue("raw_contact_id", Long.valueOf(rawContactId));
                        }
                        if (serviceType == -1) {
                            builder.withValue("data6", serviceName);
                        } else {
                            builder.withValue("data5", Integer.valueOf(serviceType));
                        }
                        ops.add(builder.build());
                    }
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void processData(HashMap dataHashMap, String dataType, ArrayList<ContentProviderOperation> ops, int dType, String mimeType, String idKey, String typeKey, long rawContactId) {
        Object dataObject = dataHashMap.get(dataType);
        if (dataObject instanceof Object[]) {
            Object[] dataArray = (Object[]) dataObject;
            for (Object obj : dataArray) {
                updateContactField(ops, mimeType, idKey, obj.toString(), typeKey, dType, rawContactId);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void parseURL(ArrayList<ContentProviderOperation> ops, HashMap urlHashMap, long rawContactId) {
        if (urlHashMap.containsKey(TiC.PROPERTY_HOMEPAGE)) {
            processURL(urlHashMap, TiC.PROPERTY_HOMEPAGE, ops, 1, rawContactId);
        }
        if (urlHashMap.containsKey(TiC.PROPERTY_WORK)) {
            processURL(urlHashMap, TiC.PROPERTY_WORK, ops, 5, rawContactId);
        }
        if (urlHashMap.containsKey(TiC.PROPERTY_HOME)) {
            processURL(urlHashMap, TiC.PROPERTY_HOME, ops, 4, rawContactId);
        }
        if (urlHashMap.containsKey(TiC.PROPERTY_OTHER)) {
            processURL(urlHashMap, TiC.PROPERTY_OTHER, ops, 7, rawContactId);
        }
    }

    /* access modifiers changed from: protected */
    public void processURL(HashMap urlHashMap, String urlType, ArrayList<ContentProviderOperation> ops, int uType, long rawContactId) {
        processData(urlHashMap, urlType, ops, uType, "vnd.android.cursor.item/website", "data1", "data2", rawContactId);
    }

    /* access modifiers changed from: protected */
    public void processRelation(HashMap relHashMap, String relType, ArrayList<ContentProviderOperation> ops, int rType, long rawContactId) {
        processData(relHashMap, relType, ops, rType, "vnd.android.cursor.item/relation", "data1", "data2", rawContactId);
    }

    /* access modifiers changed from: protected */
    public void parseDate(ArrayList<ContentProviderOperation> ops, HashMap dateHashMap, long rawContactId) {
        if (dateHashMap.containsKey(TiC.PROPERTY_ANNIVERSARY)) {
            processDate(dateHashMap, TiC.PROPERTY_ANNIVERSARY, ops, 1, rawContactId);
        }
        if (dateHashMap.containsKey(TiC.PROPERTY_OTHER)) {
            processDate(dateHashMap, TiC.PROPERTY_OTHER, ops, 2, rawContactId);
        }
    }

    /* access modifiers changed from: protected */
    public void processDate(HashMap dateHashMap, String dateType, ArrayList<ContentProviderOperation> ops, int dType, long rawContactId) {
        processData(dateHashMap, dateType, ops, dType, "vnd.android.cursor.item/contact_event", "data1", "data2", rawContactId);
    }

    /* access modifiers changed from: protected */
    public void parseEmail(ArrayList<ContentProviderOperation> ops, HashMap emailHashMap, long rawContactId) {
        if (emailHashMap.containsKey(TiC.PROPERTY_WORK)) {
            processEmail(emailHashMap, TiC.PROPERTY_WORK, ops, 2, rawContactId);
        }
        if (emailHashMap.containsKey(TiC.PROPERTY_HOME)) {
            processEmail(emailHashMap, TiC.PROPERTY_HOME, ops, 1, rawContactId);
        }
        if (emailHashMap.containsKey(TiC.PROPERTY_OTHER)) {
            processEmail(emailHashMap, TiC.PROPERTY_OTHER, ops, 3, rawContactId);
        }
    }

    /* access modifiers changed from: protected */
    public void processEmail(HashMap emailHashMap, String emailType, ArrayList<ContentProviderOperation> ops, int eType, long rawContactId) {
        processData(emailHashMap, emailType, ops, eType, "vnd.android.cursor.item/email_v2", "data1", "data2", rawContactId);
    }

    /* access modifiers changed from: protected */
    public void parsePhone(ArrayList<ContentProviderOperation> ops, HashMap phoneHashMap, long rawContactId) {
        if (phoneHashMap.containsKey(TiC.PROPERTY_HOME)) {
            processPhone(phoneHashMap, TiC.PROPERTY_HOME, ops, 1, rawContactId);
        }
        if (phoneHashMap.containsKey(TiC.PROPERTY_MOBILE)) {
            processPhone(phoneHashMap, TiC.PROPERTY_MOBILE, ops, 2, rawContactId);
        }
        if (phoneHashMap.containsKey(TiC.PROPERTY_WORK)) {
            processPhone(phoneHashMap, TiC.PROPERTY_WORK, ops, 3, rawContactId);
        }
        if (phoneHashMap.containsKey(TiC.PROPERTY_OTHER)) {
            processPhone(phoneHashMap, TiC.PROPERTY_OTHER, ops, 7, rawContactId);
        }
    }

    /* access modifiers changed from: protected */
    public void processPhone(HashMap phoneHashMap, String phoneType, ArrayList<ContentProviderOperation> ops, int pType, long rawContactId) {
        processData(phoneHashMap, phoneType, ops, pType, "vnd.android.cursor.item/phone_v2", "data1", "data2", rawContactId);
    }

    /* access modifiers changed from: protected */
    public void parseAddress(ArrayList<ContentProviderOperation> ops, HashMap addressHashMap, long rawContactId) {
        if (addressHashMap.containsKey(TiC.PROPERTY_WORK)) {
            processAddress(addressHashMap, TiC.PROPERTY_WORK, ops, 2, rawContactId);
        }
        if (addressHashMap.containsKey(TiC.PROPERTY_HOME)) {
            processAddress(addressHashMap, TiC.PROPERTY_HOME, ops, 1, rawContactId);
        }
        if (addressHashMap.containsKey(TiC.PROPERTY_OTHER)) {
            processAddress(addressHashMap, TiC.PROPERTY_OTHER, ops, 3, rawContactId);
        }
    }

    /* access modifiers changed from: protected */
    public void processAddress(HashMap addressHashMap, String addressType, ArrayList<ContentProviderOperation> ops, int aType, long rawContactId) {
        String country = "";
        String street = "";
        String city = "";
        String state = "";
        String zip = "";
        Object type = addressHashMap.get(addressType);
        if (type instanceof Object[]) {
            Object[] typeArray = (Object[]) type;
            for (Object typeAddress : typeArray) {
                if (typeAddress instanceof HashMap) {
                    HashMap typeHashMap = (HashMap) typeAddress;
                    if (typeHashMap.containsKey("Country")) {
                        country = TiConvert.toString(typeHashMap, "Country");
                    }
                    if (typeHashMap.containsKey("Street")) {
                        street = TiConvert.toString(typeHashMap, "Street");
                    }
                    if (typeHashMap.containsKey("City")) {
                        city = TiConvert.toString(typeHashMap, "City");
                    }
                    if (typeHashMap.containsKey("ZIP")) {
                        zip = TiConvert.toString(typeHashMap, "ZIP");
                    }
                    if (typeHashMap.containsKey("State")) {
                        state = TiConvert.toString(typeHashMap, "State");
                    }
                    Builder builder = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue("mimetype", "vnd.android.cursor.item/postal-address_v2").withValue("data7", city).withValue("data8", state).withValue("data10", country).withValue("data4", street).withValue("data9", zip).withValue("data2", Integer.valueOf(aType));
                    if (rawContactId == -1) {
                        builder.withValueBackReference("raw_contact_id", 0);
                    } else {
                        builder.withValue("raw_contact_id", Long.valueOf(rawContactId));
                    }
                    ops.add(builder.build());
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public PersonProxy addContact(KrollDict options) {
        if (options == null || !hasContactsPermissions()) {
            return null;
        }
        String firstName = "";
        String lastName = "";
        String middleName = "";
        String str = "";
        String str2 = "";
        PersonProxy newContact = new PersonProxy();
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI).withValue("account_type", null).withValue("account_name", null).build());
        if (options.containsKey(TiC.PROPERTY_FIRSTNAME)) {
            firstName = TiConvert.toString((HashMap<String, Object>) options, TiC.PROPERTY_FIRSTNAME);
            newContact.setProperty(TiC.PROPERTY_FIRSTNAME, firstName);
        }
        if (options.containsKey(TiC.PROPERTY_LASTNAME)) {
            lastName = TiConvert.toString((HashMap<String, Object>) options, TiC.PROPERTY_LASTNAME);
            newContact.setProperty(TiC.PROPERTY_LASTNAME, lastName);
        }
        if (options.containsKey(TiC.PROPERTY_MIDDLENAME)) {
            middleName = TiConvert.toString((HashMap<String, Object>) options, TiC.PROPERTY_MIDDLENAME);
            newContact.setProperty(TiC.PROPERTY_MIDDLENAME, middleName);
        }
        String displayName = firstName + " " + middleName + " " + lastName;
        updateContactField(ops, "vnd.android.cursor.item/name", "data1", displayName, null, 0, -1);
        if (displayName.length() > 0) {
            newContact.setFullName(displayName);
        }
        if (options.containsKey(TiC.PROPERTY_PHONE)) {
            Object phoneNumbers = options.get(TiC.PROPERTY_PHONE);
            if (phoneNumbers instanceof HashMap) {
                HashMap phoneHashMap = (HashMap) phoneNumbers;
                newContact.setProperty(TiC.PROPERTY_PHONE, phoneHashMap);
                parsePhone(ops, phoneHashMap, -1);
            }
        }
        if (options.containsKey(TiC.PROPERTY_BIRTHDAY)) {
            String birthday = TiConvert.toString((HashMap<String, Object>) options, TiC.PROPERTY_BIRTHDAY);
            newContact.setProperty(TiC.PROPERTY_BIRTHDAY, birthday);
            updateContactField(ops, "vnd.android.cursor.item/contact_event", "data1", birthday, "data2", 3, -1);
        }
        if (options.containsKey(TiC.PROPERTY_ADDRESS)) {
            Object address = options.get(TiC.PROPERTY_ADDRESS);
            if (address instanceof HashMap) {
                HashMap addressHashMap = (HashMap) address;
                newContact.setProperty(TiC.PROPERTY_ADDRESS, addressHashMap);
                parseAddress(ops, addressHashMap, -1);
            }
        }
        if (options.containsKey(TiC.PROPERTY_INSTANTMSG)) {
            Object instantMsg = options.get(TiC.PROPERTY_INSTANTMSG);
            if (instantMsg instanceof HashMap) {
                HashMap instantHashMap = (HashMap) instantMsg;
                newContact.setProperty(TiC.PROPERTY_INSTANTMSG, instantHashMap);
                parseIm(ops, instantHashMap, -1);
            }
        }
        if (options.containsKey(TiC.PROPERTY_ORGANIZATION)) {
            String organization = TiConvert.toString((HashMap<String, Object>) options, TiC.PROPERTY_ORGANIZATION);
            newContact.setProperty(TiC.PROPERTY_ORGANIZATION, organization);
            updateContactField(ops, "vnd.android.cursor.item/organization", "data1", organization, null, 0, -1);
        }
        if (options.containsKey("url")) {
            Object urlObject = options.get("url");
            if (urlObject instanceof HashMap) {
                HashMap urlHashMap = (HashMap) urlObject;
                newContact.setProperty("url", urlHashMap);
                parseURL(ops, urlHashMap, -1);
            }
        }
        if (options.containsKey("email")) {
            Object emailObject = options.get("email");
            if (emailObject instanceof HashMap) {
                HashMap emailHashMap = (HashMap) emailObject;
                newContact.setProperty("email", emailHashMap);
                parseEmail(ops, emailHashMap, -1);
            }
        }
        if (options.containsKey(TiC.PROPERTY_RELATED_NAMES)) {
            Object namesObject = options.get(TiC.PROPERTY_RELATED_NAMES);
            if (namesObject instanceof HashMap) {
                HashMap namesHashMap = (HashMap) namesObject;
                newContact.setProperty(TiC.PROPERTY_RELATED_NAMES, namesHashMap);
                for (int i = 0; i < RELATED_NAMES_TYPE.length; i++) {
                    if (namesHashMap.containsKey(RELATED_NAMES_TYPE[i])) {
                        processRelation(namesHashMap, RELATED_NAMES_TYPE[i], ops, i + 1, -1);
                    }
                }
            }
        }
        if (options.containsKey(TiC.PROPERTY_NOTE)) {
            String note = TiConvert.toString((HashMap<String, Object>) options, TiC.PROPERTY_NOTE);
            newContact.setProperty(TiC.PROPERTY_NOTE, note);
            updateContactField(ops, "vnd.android.cursor.item/note", "data1", note, null, 0, -1);
        }
        if (options.containsKey(TiC.PROPERTY_NICKNAME)) {
            String nickname = TiConvert.toString((HashMap<String, Object>) options, TiC.PROPERTY_NICKNAME);
            newContact.setProperty(TiC.PROPERTY_NICKNAME, nickname);
            updateContactField(ops, "vnd.android.cursor.item/nickname", "data1", nickname, "data2", 1, -1);
        }
        if (options.containsKey(TiC.PROPERTY_IMAGE)) {
            Object imageObject = options.get(TiC.PROPERTY_IMAGE);
            if (imageObject instanceof TiBlob) {
                TiBlob imageBlob = (TiBlob) imageObject;
                newContact.setImage(imageBlob);
                updateContactField(ops, "vnd.android.cursor.item/photo", "data15", imageBlob.getData(), null, 0, -1);
            }
        }
        if (options.containsKey(TiC.PROPERTY_DATE)) {
            Object dateObject = options.get(TiC.PROPERTY_DATE);
            if (dateObject instanceof HashMap) {
                HashMap dateHashMap = (HashMap) dateObject;
                newContact.setProperty(TiC.PROPERTY_DATE, dateHashMap);
                parseDate(ops, dateHashMap, -1);
            }
        }
        try {
            PersonProxy personProxy = newContact;
            personProxy.setProperty(TiC.PROPERTY_ID, Long.valueOf(ContentUris.parseId(TiApplication.getAppRootOrCurrentActivity().getContentResolver().applyBatch("com.android.contacts", ops)[0].uri)));
            return newContact;
        } catch (RemoteException e) {
            Log.m32e(TAG, "RemoteException - Failed to add new contact into database");
            return null;
        } catch (OperationApplicationException e2) {
            Log.m32e(TAG, "OperationApplicationException - Failed to add new contact into database");
            return null;
        }
    }

    /* access modifiers changed from: protected */
    public void removePerson(PersonProxy person) {
        if (hasContactsPermissions()) {
            if (!(person instanceof PersonProxy)) {
                Log.m32e(TAG, "Invalid argument type. Expected [PersonProxy], but was: " + person);
                return;
            }
            Object idObj = person.getProperty(TiC.PROPERTY_ID);
            if (idObj instanceof Long) {
                Long id = (Long) idObj;
                ContentResolver cr = TiApplication.getAppRootOrCurrentActivity().getContentResolver();
                Cursor cur = cr.query(Contacts.CONTENT_URI, null, "_id=?", new String[]{String.valueOf(id)}, null);
                if (cur.moveToNext()) {
                    cr.delete(Uri.withAppendedPath(Contacts.CONTENT_LOOKUP_URI, cur.getString(cur.getColumnIndex("lookup"))), null, null);
                }
                cur.close();
            }
        }
    }

    /* access modifiers changed from: protected */
    public PersonProxy getPersonById(long id) {
        if (!hasContactsPermissions()) {
            return null;
        }
        if (TiApplication.getInstance() == null) {
            Log.m33e(TAG, "Failed to call getPersonById(), application is null", Log.DEBUG_MODE);
            return null;
        }
        Activity activity = TiApplication.getInstance().getRootOrCurrentActivity();
        if (activity == null) {
            Log.m33e(TAG, "Failed to call getPersonById(), activity is null", Log.DEBUG_MODE);
            return null;
        }
        LightPerson person = null;
        Cursor cursor = activity.getContentResolver().query(ContentUris.withAppendedId(ContactsUri, id), PEOPLE_PROJECTION, null, null, null);
        if (cursor.moveToFirst()) {
            person = new LightPerson();
            person.addPersonInfoFromL5PersonRow(cursor);
        }
        cursor.close();
        if (person == null) {
            return null;
        }
        String condition = "mimetype IN " + INConditionForKinds + " AND contact_id = ?";
        Cursor cursor2 = activity.getContentResolver().query(DataUri, DATA_PROJECTION, condition, new String[]{String.valueOf(id)}, "mimetype asc, is_super_primary desc, is_primary desc");
        while (cursor2.moveToNext()) {
            person.addDataFromL5Cursor(cursor2);
        }
        cursor2.close();
        return person.proxify();
    }

    /* access modifiers changed from: protected */
    public PersonProxy getPersonByUri(Uri uri) {
        return getPersonById(ContentUris.parseId(uri));
    }

    /* access modifiers changed from: protected */
    public Bitmap getInternalContactImage(long id) {
        Bitmap bitmap = null;
        if (hasContactsPermissions()) {
            if (TiApplication.getInstance() == null) {
                Log.m33e(TAG, "Failed to call getInternalContactImage(), application is null", Log.DEBUG_MODE);
            } else {
                Uri uri = ContentUris.withAppendedId(ContactsUri, id);
                ContentResolver cr = TiApplication.getInstance().getContentResolver();
                try {
                    InputStream stream = (InputStream) this.openContactPhotoInputStream.invoke(null, new Object[]{cr, uri});
                    if (stream != null) {
                        bitmap = BitmapFactory.decodeStream(stream);
                        try {
                            stream.close();
                        } catch (IOException e) {
                            Log.m35e(TAG, "Unable to close stream from openContactPhotoInputStream: " + e.getMessage(), e, Log.DEBUG_MODE);
                        }
                    }
                } catch (Throwable t) {
                    Log.m35e(TAG, "Could not invoke openContactPhotoInputStream: " + t.getMessage(), t, Log.DEBUG_MODE);
                }
            }
        }
        return bitmap;
    }

    /* access modifiers changed from: protected */
    public void deleteField(ArrayList<ContentProviderOperation> ops, String selection, String[] selectionArgs) {
        ops.add(ContentProviderOperation.newDelete(Data.CONTENT_URI).withSelection(selection, selectionArgs).build());
    }

    /* access modifiers changed from: protected */
    public void modifyName(ArrayList<ContentProviderOperation> ops, PersonProxy person, String id) {
        String firstName = "";
        String lastName = "";
        String middleName = "";
        String str = "";
        if (person.hasProperty(TiC.PROPERTY_FIRSTNAME)) {
            firstName = TiConvert.toString(person.getProperty(TiC.PROPERTY_FIRSTNAME));
        }
        if (person.hasProperty(TiC.PROPERTY_LASTNAME)) {
            lastName = TiConvert.toString(person.getProperty(TiC.PROPERTY_LASTNAME));
        }
        if (person.hasProperty(TiC.PROPERTY_MIDDLENAME)) {
            middleName = TiConvert.toString(person.getProperty(TiC.PROPERTY_MIDDLENAME));
        }
        String displayName = firstName + " " + middleName + " " + lastName;
        person.setFullName(displayName);
        deleteField(ops, BASE_SELECTION, new String[]{id, "vnd.android.cursor.item/name"});
        updateContactField(ops, "vnd.android.cursor.item/name", "data1", displayName, null, 0, person.getId());
    }

    /* access modifiers changed from: protected */
    public void modifyBirthday(ArrayList<ContentProviderOperation> ops, PersonProxy person, String id) {
        String birthday = TiConvert.toString(person.getProperty(TiC.PROPERTY_BIRTHDAY));
        deleteField(ops, BASE_SELECTION + " AND " + "data2" + "=?", new String[]{id, "vnd.android.cursor.item/contact_event", String.valueOf(3)});
        updateContactField(ops, "vnd.android.cursor.item/contact_event", "data1", birthday, "data2", 3, person.getId());
    }

    /* access modifiers changed from: protected */
    public void modifyOrganization(ArrayList<ContentProviderOperation> ops, PersonProxy person, String id) {
        String company = TiConvert.toString(person.getProperty(TiC.PROPERTY_ORGANIZATION));
        deleteField(ops, BASE_SELECTION, new String[]{id, "vnd.android.cursor.item/organization"});
        updateContactField(ops, "vnd.android.cursor.item/organization", "data1", company, null, 0, person.getId());
    }

    /* access modifiers changed from: protected */
    public void modifyNote(ArrayList<ContentProviderOperation> ops, PersonProxy person, String id) {
        String note = TiConvert.toString(person.getProperty(TiC.PROPERTY_NOTE));
        deleteField(ops, BASE_SELECTION, new String[]{id, "vnd.android.cursor.item/note"});
        updateContactField(ops, "vnd.android.cursor.item/note", "data1", note, null, 0, person.getId());
    }

    /* access modifiers changed from: protected */
    public void modifyNickName(ArrayList<ContentProviderOperation> ops, PersonProxy person, String id) {
        String nickname = TiConvert.toString(person.getProperty(TiC.PROPERTY_NICKNAME));
        deleteField(ops, BASE_SELECTION + " AND " + "data2" + "=?", new String[]{id, "vnd.android.cursor.item/nickname", String.valueOf(1)});
        updateContactField(ops, "vnd.android.cursor.item/nickname", "data1", nickname, "data2", 1, person.getId());
    }

    /* access modifiers changed from: protected */
    public void modifyImage(ArrayList<ContentProviderOperation> ops, PersonProxy person, String id) {
        TiBlob imageBlob = person.getImage();
        deleteField(ops, BASE_SELECTION, new String[]{id, "vnd.android.cursor.item/photo"});
        updateContactField(ops, "vnd.android.cursor.item/photo", "data15", imageBlob.getData(), null, 0, person.getId());
    }

    /* access modifiers changed from: protected */
    public void modifyField(ArrayList<ContentProviderOperation> ops, PersonProxy person, String id, long rawContactId, String field, String itemType) {
        Object fieldObject = person.getProperty(field);
        if (fieldObject instanceof HashMap) {
            HashMap fieldHashMap = (HashMap) fieldObject;
            ArrayList<ContentProviderOperation> arrayList = ops;
            deleteField(arrayList, BASE_SELECTION, new String[]{id, itemType});
            if (field.equals(TiC.PROPERTY_PHONE)) {
                parsePhone(ops, fieldHashMap, rawContactId);
                return;
            }
            if (field.equals(TiC.PROPERTY_ADDRESS)) {
                parseAddress(ops, fieldHashMap, rawContactId);
                return;
            }
            if (field.equals(TiC.PROPERTY_INSTANTMSG)) {
                parseIm(ops, fieldHashMap, rawContactId);
                return;
            }
            if (field.equals("url")) {
                parseURL(ops, fieldHashMap, rawContactId);
                return;
            }
            if (field.equals("email")) {
                parseEmail(ops, fieldHashMap, rawContactId);
                return;
            }
            if (field.equals(TiC.PROPERTY_RELATED_NAMES)) {
                for (int i = 0; i < RELATED_NAMES_TYPE.length; i++) {
                    if (fieldHashMap.containsKey(RELATED_NAMES_TYPE[i])) {
                        processRelation(fieldHashMap, RELATED_NAMES_TYPE[i], ops, i + 1, rawContactId);
                    }
                }
                return;
            }
            if (field.equals(TiC.PROPERTY_DATE)) {
                parseDate(ops, fieldHashMap, rawContactId);
                if (person.hasProperty(TiC.PROPERTY_BIRTHDAY)) {
                    String str = "vnd.android.cursor.item/contact_event";
                    String str2 = "data1";
                    ArrayList<ContentProviderOperation> arrayList2 = ops;
                    updateContactField(arrayList2, str, str2, TiConvert.toString(person.getProperty(TiC.PROPERTY_BIRTHDAY)), "data2", 3, rawContactId);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void modifyPhone(ArrayList<ContentProviderOperation> ops, PersonProxy person, String id) {
        modifyField(ops, person, id, person.getId(), TiC.PROPERTY_PHONE, "vnd.android.cursor.item/phone_v2");
    }

    /* access modifiers changed from: protected */
    public void modifyAddress(ArrayList<ContentProviderOperation> ops, PersonProxy person, String id) {
        modifyField(ops, person, id, person.getId(), TiC.PROPERTY_ADDRESS, "vnd.android.cursor.item/postal-address_v2");
    }

    /* access modifiers changed from: protected */
    public void modifyIm(ArrayList<ContentProviderOperation> ops, PersonProxy person, String id) {
        modifyField(ops, person, id, person.getId(), TiC.PROPERTY_INSTANTMSG, "vnd.android.cursor.item/im");
    }

    /* access modifiers changed from: protected */
    public void modifyUrl(ArrayList<ContentProviderOperation> ops, PersonProxy person, String id) {
        modifyField(ops, person, id, person.getId(), "url", "vnd.android.cursor.item/website");
    }

    /* access modifiers changed from: protected */
    public void modifyEmail(ArrayList<ContentProviderOperation> ops, PersonProxy person, String id) {
        modifyField(ops, person, id, person.getId(), "email", "vnd.android.cursor.item/email_v2");
    }

    /* access modifiers changed from: protected */
    public void modifyRelatedNames(ArrayList<ContentProviderOperation> ops, PersonProxy person, String id) {
        modifyField(ops, person, id, person.getId(), TiC.PROPERTY_RELATED_NAMES, "vnd.android.cursor.item/relation");
    }

    /* access modifiers changed from: protected */
    public void modifyDate(ArrayList<ContentProviderOperation> ops, PersonProxy person, String id) {
        modifyField(ops, person, id, person.getId(), TiC.PROPERTY_DATE, "vnd.android.cursor.item/contact_event");
    }

    /* access modifiers changed from: protected */
    public void modifyContact(PersonProxy person, String id) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        if (person.isFieldModified(TiC.PROPERTY_NAME)) {
            modifyName(ops, person, id);
        }
        if (person.isFieldModified(TiC.PROPERTY_BIRTHDAY)) {
            modifyBirthday(ops, person, id);
        }
        if (person.isFieldModified(TiC.PROPERTY_ORGANIZATION)) {
            modifyOrganization(ops, person, id);
        }
        if (person.isFieldModified(TiC.PROPERTY_NOTE)) {
            modifyNote(ops, person, id);
        }
        if (person.isFieldModified(TiC.PROPERTY_NICKNAME)) {
            modifyNickName(ops, person, id);
        }
        if (person.isFieldModified(TiC.PROPERTY_IMAGE)) {
            modifyImage(ops, person, id);
        }
        if (person.isFieldModified(TiC.PROPERTY_PHONE)) {
            modifyPhone(ops, person, id);
        }
        if (person.isFieldModified(TiC.PROPERTY_ADDRESS)) {
            modifyAddress(ops, person, id);
        }
        if (person.isFieldModified(TiC.PROPERTY_INSTANTMSG)) {
            modifyIm(ops, person, id);
        }
        if (person.isFieldModified("url")) {
            modifyUrl(ops, person, id);
        }
        if (person.isFieldModified("email")) {
            modifyEmail(ops, person, id);
        }
        if (person.isFieldModified(TiC.PROPERTY_RELATED_NAMES)) {
            modifyRelatedNames(ops, person, id);
        }
        if (person.isFieldModified(TiC.PROPERTY_DATE)) {
            modifyDate(ops, person, id);
        }
        try {
            TiApplication.getAppRootOrCurrentActivity().getContentResolver().applyBatch("com.android.contacts", ops);
            person.finishModification();
        } catch (RemoteException e) {
            Log.m32e(TAG, "RemoteException - unable to save changes to contact Database.");
        } catch (OperationApplicationException e2) {
            Log.m32e(TAG, "OperationApplicationException - unable to save changes to contact Database.");
        }
    }

    /* access modifiers changed from: protected */
    public void save(Object people) {
        if ((people instanceof Object[]) && hasContactsPermissions()) {
            Object[] contacts = (Object[]) people;
            for (Object contact : contacts) {
                if (contact instanceof PersonProxy) {
                    PersonProxy person = (PersonProxy) contact;
                    Object idObj = person.getProperty(TiC.PROPERTY_ID);
                    if (idObj instanceof Long) {
                        modifyContact(person, String.valueOf((Long) idObj));
                    }
                } else {
                    Log.m32e(TAG, "Invalid argument type to save");
                }
            }
        }
    }
}
