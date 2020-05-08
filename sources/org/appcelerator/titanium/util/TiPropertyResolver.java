package org.appcelerator.titanium.util;

import org.appcelerator.kroll.KrollDict;

public class TiPropertyResolver {
    private KrollDict[] propSets;

    public TiPropertyResolver(KrollDict... propSets2) {
        int len = propSets2.length;
        this.propSets = new KrollDict[len];
        for (int i = 0; i < len; i++) {
            this.propSets[i] = propSets2[i];
        }
    }

    public void release() {
        for (int i = 0; i < this.propSets.length; i++) {
            this.propSets[i] = null;
        }
        this.propSets = null;
    }

    public KrollDict findProperty(String key) {
        KrollDict[] krollDictArr;
        for (KrollDict d : this.propSets) {
            if (d != null && d.containsKey(key)) {
                return d;
            }
        }
        return null;
    }

    public boolean hasAnyOf(String[] keys) {
        KrollDict[] krollDictArr;
        boolean found = false;
        for (KrollDict d : this.propSets) {
            if (d != null) {
                int length = keys.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    } else if (d.containsKey(keys[i])) {
                        found = true;
                        break;
                    } else {
                        i++;
                    }
                }
                if (found) {
                    break;
                }
            }
        }
        return found;
    }
}
