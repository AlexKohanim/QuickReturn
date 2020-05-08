package p006ti.modules.titanium.xml;

import java.util.ArrayList;
import org.appcelerator.kroll.common.Log;
import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;

/* renamed from: ti.modules.titanium.xml.XPathUtil */
public class XPathUtil {
    private static final String TAG = "XPath";

    public static XPathNodeListProxy evaluate(NodeProxy start, String xpathExpr) {
        try {
            return new XPathNodeListProxy(new DOMXPath(xpathExpr).selectNodes(start.getNode()));
        } catch (JaxenException e) {
            Log.m34e(TAG, "Exception selecting nodes in XPath (" + xpathExpr + ")", (Throwable) e);
            return new XPathNodeListProxy(new ArrayList());
        }
    }
}
