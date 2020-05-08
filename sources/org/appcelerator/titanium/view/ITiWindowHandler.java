package org.appcelerator.titanium.view;

import android.view.View;
import org.appcelerator.titanium.view.TiCompositeLayout.LayoutParams;

public interface ITiWindowHandler {
    void addWindow(View view, LayoutParams layoutParams);

    void removeWindow(View view);
}
