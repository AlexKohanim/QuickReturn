package org.appcelerator.kroll;

public class KrollPropertyChange {
    protected String name;
    protected Object newValue;
    protected Object oldValue;

    public KrollPropertyChange(String name2, Object oldValue2, Object newValue2) {
        this.name = name2;
        this.oldValue = oldValue2;
        this.newValue = newValue2;
    }

    public void fireEvent(KrollProxy proxy, KrollProxyListener listener) {
        if (listener != null) {
            listener.propertyChanged(this.name, this.oldValue, this.newValue, proxy);
        }
    }

    public String getName() {
        return this.name;
    }

    public Object getOldValue() {
        return this.oldValue;
    }

    public Object getNewValue() {
        return this.newValue;
    }
}
