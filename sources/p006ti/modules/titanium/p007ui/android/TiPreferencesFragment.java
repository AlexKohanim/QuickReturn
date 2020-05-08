package p006ti.modules.titanium.p007ui.android;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.util.TiRHelper;
import org.appcelerator.titanium.util.TiRHelper.ResourceNotFoundException;

/* renamed from: ti.modules.titanium.ui.android.TiPreferencesFragment */
public class TiPreferencesFragment extends PreferenceFragment {
    private static final String TAG = "TiPreferencesFragment";
    private CharSequence title = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String prefsName = getArguments().getString("prefsName");
        try {
            getPreferenceManager().setSharedPreferencesName(TiApplication.APPLICATION_PREFERENCES_NAME);
            int resid = TiRHelper.getResource("xml." + prefsName);
            if (resid != 0) {
                addPreferencesFromResource(resid);
                if (getPreferenceScreen() != null) {
                    this.title = getPreferenceScreen().getTitle();
                    return;
                }
                return;
            }
            Log.m32e(TAG, "xml." + prefsName + " preferences not found.");
        } catch (ResourceNotFoundException e) {
            Log.m32e(TAG, "Error loading preferences: " + e.getMessage());
        }
    }

    public void onResume() {
        super.onResume();
        if (this.title != null && getActivity() != null) {
            getActivity().setTitle(this.title);
        }
    }
}
