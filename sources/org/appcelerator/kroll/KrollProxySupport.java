package org.appcelerator.kroll;

public interface KrollProxySupport {
    Object getIndexedProperty(int i);

    KrollObject getKrollObject();

    void onEventFired(String str, Object obj);

    void onHasListenersChanged(String str, boolean z);

    void onPropertiesChanged(Object[][] objArr);

    void onPropertyChanged(String str, Object obj);

    void setIndexedProperty(int i, Object obj);

    void setKrollObject(KrollObject krollObject);
}
