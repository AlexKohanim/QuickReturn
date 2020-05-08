package p006ti.modules.titanium.p007ui.android;

import android.os.Bundle;
import org.appcelerator.titanium.TiActivity;

/* renamed from: ti.modules.titanium.ui.android.TiPreferencesActivity */
public class TiPreferencesActivity extends TiActivity {
    protected static final String DEFAULT_PREFS_RNAME = "preferences";
    protected static final String PREFS_KEY = "prefsName";

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String prefsName = DEFAULT_PREFS_RNAME;
        if (getIntent() != null && getIntent().hasExtra(PREFS_KEY)) {
            String name = getIntent().getExtras().getString(PREFS_KEY);
            if (name != null && name.length() > 0) {
                prefsName = name;
            }
        }
        TiPreferencesFragment fragment = new TiPreferencesFragment();
        Bundle args = new Bundle();
        args.putString(PREFS_KEY, prefsName);
        fragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(16908290, fragment).commit();
    }
}
