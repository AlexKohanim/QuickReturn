package p006ti.modules.titanium.xml;

import java.util.List;
import org.appcelerator.kroll.KrollProxy;
import org.w3c.dom.Node;

/* renamed from: ti.modules.titanium.xml.XPathNodeListProxy */
public class XPathNodeListProxy extends KrollProxy {
    private List nodeList;

    public XPathNodeListProxy(List nodeList2) {
        this.nodeList = nodeList2;
    }

    public int getLength() {
        return this.nodeList.size();
    }

    public NodeProxy item(int index) {
        return NodeProxy.getNodeProxy((Node) this.nodeList.get(index));
    }
}
