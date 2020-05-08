package org.appcelerator.kroll;

import java.util.List;

public interface KrollProxyListener {
    void listenerAdded(String str, int i, KrollProxy krollProxy);

    void listenerRemoved(String str, int i, KrollProxy krollProxy);

    void processProperties(KrollDict krollDict);

    void propertiesChanged(List<KrollPropertyChange> list, KrollProxy krollProxy);

    void propertyChanged(String str, Object obj, Object obj2, KrollProxy krollProxy);
}
