package p006ti.modules.titanium.p007ui.widget.searchview;

import android.support.p003v7.widget.SearchView;
import android.support.p003v7.widget.SearchView.OnCloseListener;
import android.support.p003v7.widget.SearchView.OnQueryTextListener;
import android.widget.EditText;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiRHelper;
import org.appcelerator.titanium.util.TiRHelper.ResourceNotFoundException;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.widget.searchbar.TiUISearchBar.OnSearchChangeListener;

/* renamed from: ti.modules.titanium.ui.widget.searchview.TiUISearchView */
public class TiUISearchView extends TiUIView implements OnQueryTextListener, OnCloseListener {
    public static final String TAG = "SearchView";
    private boolean changeEventEnabled = true;
    protected OnSearchChangeListener searchChangeListener;
    private SearchView searchView;

    public TiUISearchView(TiViewProxy proxy) {
        super(proxy);
        this.searchView = new SearchView(proxy.getActivity());
        this.searchView.setOnQueryTextListener(this);
        this.searchView.setOnCloseListener(this);
        this.searchView.setOnQueryTextFocusChangeListener(this);
        setNativeView(this.searchView);
    }

    public void processProperties(KrollDict props) {
        super.processProperties(props);
        if (props.containsKey(TiC.PROPERTY_HINT_TEXT)) {
            this.searchView.setQueryHint(props.getString(TiC.PROPERTY_HINT_TEXT));
        }
        if (props.containsKey(TiC.PROPERTY_VALUE)) {
            this.changeEventEnabled = false;
            this.searchView.setQuery(props.getString(TiC.PROPERTY_VALUE), false);
            this.changeEventEnabled = true;
        }
        if (props.containsKey(TiC.PROPERTY_ICONIFIED)) {
            this.searchView.setIconified(props.getBoolean(TiC.PROPERTY_ICONIFIED));
        }
        if (props.containsKey(TiC.PROPERTY_ICONIFIED_BY_DEFAULT)) {
            this.searchView.setIconifiedByDefault(props.getBoolean(TiC.PROPERTY_ICONIFIED_BY_DEFAULT));
        }
        if (props.containsKey(TiC.PROPERTY_SUBMIT_ENABLED)) {
            this.searchView.setSubmitButtonEnabled(props.getBoolean(TiC.PROPERTY_SUBMIT_ENABLED));
        }
        if (props.containsKey(TiC.PROPERTY_COLOR)) {
            try {
                EditText text = (EditText) this.searchView.findViewById(TiRHelper.getResource("id.search_src_text"));
                if (text != null) {
                    text.setTextColor(TiConvert.toColor(props, TiC.PROPERTY_COLOR));
                }
            } catch (ResourceNotFoundException e) {
                Log.m32e(TAG, "Could not find SearchView EditText");
            }
        }
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        if (key.equals(TiC.PROPERTY_COLOR)) {
            try {
                EditText text = (EditText) this.searchView.findViewById(TiRHelper.getResource("id.search_src_text"));
                if (text != null) {
                    text.setTextColor(TiConvert.toColor((String) newValue));
                }
            } catch (ResourceNotFoundException e) {
                Log.m32e(TAG, "Could not find SearchView EditText");
            }
        } else if (key.equals(TiC.PROPERTY_HINT_TEXT)) {
            this.searchView.setQueryHint((String) newValue);
        } else if (key.equals(TiC.PROPERTY_VALUE)) {
            this.searchView.setQuery((String) newValue, false);
        } else if (key.equals(TiC.PROPERTY_ICONIFIED)) {
            this.searchView.setIconified(TiConvert.toBoolean(newValue));
        } else if (key.equals(TiC.PROPERTY_ICONIFIED_BY_DEFAULT)) {
            this.searchView.setIconifiedByDefault(TiConvert.toBoolean(newValue));
        } else if (key.equals(TiC.PROPERTY_SUBMIT_ENABLED)) {
            this.searchView.setSubmitButtonEnabled(TiConvert.toBoolean(newValue));
        } else {
            super.propertyChanged(key, oldValue, newValue, proxy);
        }
    }

    public boolean onClose() {
        fireEvent("cancel", null);
        return false;
    }

    public boolean onQueryTextChange(String query) {
        this.proxy.setProperty(TiC.PROPERTY_VALUE, query);
        if (this.searchChangeListener != null) {
            this.searchChangeListener.filterBy(query);
        }
        if (this.changeEventEnabled) {
            fireEvent("change", null);
        }
        return false;
    }

    public boolean onQueryTextSubmit(String query) {
        TiUIHelper.showSoftKeyboard(this.nativeView, false);
        fireEvent(TiC.EVENT_SUBMIT, null);
        return false;
    }

    public void setOnSearchChangeListener(OnSearchChangeListener listener) {
        this.searchChangeListener = listener;
    }
}
