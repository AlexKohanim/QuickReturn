package p006ti.modules.titanium.contacts;

import android.app.Activity;
import android.content.Intent;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll.argument;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.ContextSpecific;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiActivityResultHandler;
import org.appcelerator.titanium.util.TiActivitySupport;

@ContextSpecific
/* renamed from: ti.modules.titanium.contacts.ContactsModule */
public class ContactsModule extends KrollModule implements TiActivityResultHandler {
    public static final int AUTHORIZATION_AUTHORIZED = 3;
    public static final int AUTHORIZATION_DENIED = 2;
    public static final int AUTHORIZATION_RESTRICTED = 1;
    public static final int AUTHORIZATION_UNKNOWN = 0;
    public static final int CONTACTS_KIND_ORGANIZATION = 0;
    public static final int CONTACTS_KIND_PERSON = 1;
    public static final int CONTACTS_SORT_FIRST_NAME = 0;
    public static final int CONTACTS_SORT_LAST_NAME = 1;
    private static final String TAG = "TiContacts";
    private final CommonContactsApi contactsApi = CommonContactsApi.getInstance();
    private final AtomicInteger requestCodeGen = new AtomicInteger();
    private Map<Integer, Map<String, KrollFunction>> requests;

    public int getContactsAuthorization() {
        return 3;
    }

    public boolean hasContactsPermissions() {
        return this.contactsApi.hasContactsPermissions();
    }

    public void requestContactsPermissions(@argument(optional = true) KrollFunction permissionCallback) {
        if (!hasContactsPermissions()) {
            TiBaseActivity.registerPermissionRequestCallback(Integer.valueOf(102), permissionCallback, getKrollObject());
            TiApplication.getInstance().getCurrentActivity().requestPermissions(new String[]{"android.permission.READ_CONTACTS"}, 102);
        }
    }

    public Object[] getAllPeople(@argument(optional = true) KrollDict options) {
        Calendar start = Calendar.getInstance();
        int length = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        if (options != null && options.containsKey(TiC.PROPERTY_MAX)) {
            length = ((Double) options.get(TiC.PROPERTY_MAX)).intValue();
        }
        Object[] persons = this.contactsApi.getAllPeople(length);
        Log.m29d(TAG, "getAllPersons elapsed: " + (Calendar.getInstance().getTimeInMillis() - start.getTimeInMillis()) + " milliseconds", Log.DEBUG_MODE);
        return persons;
    }

    public PersonProxy createPerson(KrollDict options) {
        return this.contactsApi.addContact(options);
    }

    public Object[] getPeopleWithName(String name) {
        return this.contactsApi.getPeopleWithName(name);
    }

    public void save(Object people) {
        this.contactsApi.save(people);
    }

    public PersonProxy getPersonByID(long id) {
        return this.contactsApi.getPersonById(id);
    }

    public void removePerson(PersonProxy person) {
        this.contactsApi.removePerson(person);
    }

    public void requestAuthorization(KrollFunction function) {
        KrollDict dict = new KrollDict();
        dict.putCodeAndMessage(0, null);
        function.callAsync(getKrollObject(), (HashMap) dict);
    }

    public void showContacts(@argument(optional = true) KrollDict d) {
        String[] callbacksToConsider;
        if (TiApplication.getInstance() == null) {
            Log.m33e(TAG, "Could not showContacts, application is null", Log.DEBUG_MODE);
            return;
        }
        Activity launchingActivity = TiApplication.getInstance().getCurrentActivity();
        if (launchingActivity == null) {
            Log.m32e(TAG, "Could not showContacts, current activity is null., Log.DEBUG_MODE");
            return;
        }
        Intent intent = this.contactsApi.getIntentForContactsPicker();
        Log.m29d(TAG, "Launching content picker activity", Log.DEBUG_MODE);
        int requestCode = this.requestCodeGen.getAndIncrement();
        if (this.requests == null) {
            this.requests = new HashMap();
        }
        Map<String, KrollFunction> callbacks = new HashMap<>();
        this.requests.put(new Integer(requestCode), callbacks);
        for (String callbackToConsider : new String[]{"selectedPerson", "cancel"}) {
            if (d.containsKey(callbackToConsider)) {
                Object test = d.get(callbackToConsider);
                if (test instanceof KrollFunction) {
                    callbacks.put(callbackToConsider, (KrollFunction) test);
                }
            }
            if (d.containsKey("proxy")) {
                Object test2 = d.get("proxy");
                if (test2 != null && (test2 instanceof KrollProxy)) {
                    launchingActivity = ((KrollProxy) test2).getActivity();
                }
            }
        }
        ((TiActivitySupport) launchingActivity).launchActivityForResult(intent, requestCode, this);
    }

    public void onError(Activity activity, int requestCode, Exception e) {
        Log.m34e(TAG, "Error from contact picker activity: " + e.getMessage(), (Throwable) e);
    }

    public void onResult(Activity activity, int requestCode, int resultCode, Intent data) {
        Integer rcode = new Integer(requestCode);
        if (this.requests.containsKey(rcode)) {
            Map<String, KrollFunction> request = (Map) this.requests.get(rcode);
            Log.m29d(TAG, "Received result from contact picker.  Result code: " + resultCode, Log.DEBUG_MODE);
            if (resultCode == 0) {
                if (request.containsKey("cancel")) {
                    KrollFunction callback = (KrollFunction) request.get("cancel");
                    if (callback != null) {
                        callback.callAsync(getKrollObject(), new Object[0]);
                    }
                }
            } else if (resultCode != -1) {
                Log.m44w(TAG, "Result code from contact picker activity not understood: " + resultCode);
            } else if (request.containsKey("selectedPerson")) {
                KrollFunction callback2 = (KrollFunction) request.get("selectedPerson");
                if (callback2 != null) {
                    PersonProxy person = this.contactsApi.getPersonByUri(data.getData());
                    KrollDict result = new KrollDict();
                    result.put("person", person);
                    callback2.callAsync(getKrollObject(), new Object[]{result});
                }
            }
            request.clear();
            this.requests.remove(rcode);
        }
    }

    public String getApiName() {
        return "Ti.Contacts";
    }
}
