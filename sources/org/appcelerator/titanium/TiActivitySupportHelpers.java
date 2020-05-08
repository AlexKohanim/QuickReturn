package org.appcelerator.titanium;

import android.util.SparseArray;
import java.util.concurrent.atomic.AtomicInteger;
import org.appcelerator.titanium.util.TiActivitySupportHelper;

public class TiActivitySupportHelpers {
    protected static AtomicInteger supportHelperIdGenerator = new AtomicInteger();
    protected static SparseArray<TiActivitySupportHelper> supportHelpers = new SparseArray<>();

    public static int addSupportHelper(TiActivitySupportHelper supportHelper) {
        int supportHelperId = supportHelperIdGenerator.incrementAndGet();
        supportHelpers.put(supportHelperId, supportHelper);
        return supportHelperId;
    }

    public static TiActivitySupportHelper retrieveSupportHelper(TiBaseActivity activity, int supportHelperId) {
        TiActivitySupportHelper supportHelper = (TiActivitySupportHelper) supportHelpers.get(supportHelperId);
        if (supportHelper != null) {
            supportHelper.setActivity(activity);
        }
        return supportHelper;
    }

    public static void removeSupportHelper(int supportHelperId) {
        supportHelpers.remove(supportHelperId);
    }

    public static void dispose() {
        supportHelpers.clear();
    }
}
