package org.jaxen.expr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jaxen.Context;
import org.jaxen.ContextSupport;
import org.jaxen.JaxenException;
import org.jaxen.Navigator;
import org.jaxen.UnresolvableException;
import org.jaxen.expr.iter.IterableAxis;

public class DefaultNameStep extends DefaultStep implements NameStep {
    private static final long serialVersionUID = 428414912247718390L;
    private boolean hasPrefix;
    private String localName;
    private boolean matchesAnyName;
    private String prefix;

    public DefaultNameStep(IterableAxis axis, String prefix2, String localName2, PredicateSet predicateSet) {
        super(axis, predicateSet);
        this.prefix = prefix2;
        this.localName = localName2;
        this.matchesAnyName = "*".equals(localName2);
        this.hasPrefix = this.prefix != null && this.prefix.length() > 0;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getLocalName() {
        return this.localName;
    }

    public boolean isMatchesAnyName() {
        return this.matchesAnyName;
    }

    public String getText() {
        StringBuffer buf = new StringBuffer(64);
        buf.append(getAxisName()).append("::");
        if (getPrefix() != null && getPrefix().length() > 0) {
            buf.append(getPrefix()).append(':');
        }
        return buf.append(getLocalName()).append(super.getText()).toString();
    }

    public List evaluate(Context context) throws JaxenException {
        List contextNodeSet = context.getNodeSet();
        int contextSize = contextNodeSet.size();
        if (contextSize == 0) {
            return Collections.EMPTY_LIST;
        }
        ContextSupport support = context.getContextSupport();
        IterableAxis iterableAxis = getIterableAxis();
        boolean namedAccess = !this.matchesAnyName && iterableAxis.supportsNamedAccess(support);
        if (contextSize == 1) {
            Object contextNode = contextNodeSet.get(0);
            if (namedAccess) {
                String uri = null;
                if (this.hasPrefix) {
                    uri = support.translateNamespacePrefixToUri(this.prefix);
                    if (uri == null) {
                        throw new UnresolvableException("XPath expression uses unbound namespace prefix " + this.prefix);
                    }
                }
                Iterator axisNodeIter = iterableAxis.namedAccessIterator(contextNode, support, this.localName, this.prefix, uri);
                if (axisNodeIter == null || !axisNodeIter.hasNext()) {
                    return Collections.EMPTY_LIST;
                }
                ArrayList arrayList = new ArrayList();
                while (axisNodeIter.hasNext()) {
                    arrayList.add(axisNodeIter.next());
                }
                return getPredicateSet().evaluatePredicates(arrayList, support);
            }
            Iterator axisNodeIter2 = iterableAxis.iterator(contextNode, support);
            if (axisNodeIter2 == null || !axisNodeIter2.hasNext()) {
                return Collections.EMPTY_LIST;
            }
            ArrayList arrayList2 = new ArrayList(contextSize);
            while (axisNodeIter2.hasNext()) {
                Object eachAxisNode = axisNodeIter2.next();
                if (matches(eachAxisNode, support)) {
                    arrayList2.add(eachAxisNode);
                }
            }
            return getPredicateSet().evaluatePredicates(arrayList2, support);
        }
        IdentitySet unique = new IdentitySet();
        ArrayList arrayList3 = new ArrayList(contextSize);
        ArrayList arrayList4 = new ArrayList(contextSize);
        if (namedAccess) {
            String uri2 = null;
            if (this.hasPrefix) {
                uri2 = support.translateNamespacePrefixToUri(this.prefix);
                if (uri2 == null) {
                    throw new UnresolvableException("XPath expression uses unbound namespace prefix " + this.prefix);
                }
            }
            for (int i = 0; i < contextSize; i++) {
                Iterator axisNodeIter3 = iterableAxis.namedAccessIterator(contextNodeSet.get(i), support, this.localName, this.prefix, uri2);
                if (axisNodeIter3 != null && axisNodeIter3.hasNext()) {
                    while (axisNodeIter3.hasNext()) {
                        arrayList3.add(axisNodeIter3.next());
                    }
                    for (Object eachPredicateNode : getPredicateSet().evaluatePredicates(arrayList3, support)) {
                        if (!unique.contains(eachPredicateNode)) {
                            unique.add(eachPredicateNode);
                            arrayList4.add(eachPredicateNode);
                        }
                    }
                    arrayList3.clear();
                }
            }
            return arrayList4;
        }
        for (int i2 = 0; i2 < contextSize; i2++) {
            Iterator axisNodeIter4 = axisIterator(contextNodeSet.get(i2), support);
            if (axisNodeIter4 != null && axisNodeIter4.hasNext()) {
                while (axisNodeIter4.hasNext()) {
                    Object eachAxisNode2 = axisNodeIter4.next();
                    if (matches(eachAxisNode2, support)) {
                        arrayList3.add(eachAxisNode2);
                    }
                }
                for (Object eachPredicateNode2 : getPredicateSet().evaluatePredicates(arrayList3, support)) {
                    if (!unique.contains(eachPredicateNode2)) {
                        unique.add(eachPredicateNode2);
                        arrayList4.add(eachPredicateNode2);
                    }
                }
                arrayList3.clear();
            }
        }
        return arrayList4;
    }

    public boolean matches(Object node, ContextSupport contextSupport) throws JaxenException {
        String nodeName;
        Navigator nav = contextSupport.getNavigator();
        String myUri = null;
        String nodeUri = null;
        if (nav.isElement(node)) {
            nodeName = nav.getElementName(node);
            nodeUri = nav.getElementNamespaceUri(node);
        } else if (nav.isText(node)) {
            return false;
        } else {
            if (nav.isAttribute(node)) {
                if (getAxis() != 9) {
                    return false;
                }
                nodeName = nav.getAttributeName(node);
                nodeUri = nav.getAttributeNamespaceUri(node);
            } else if (nav.isDocument(node) || !nav.isNamespace(node) || getAxis() != 10) {
                return false;
            } else {
                nodeName = nav.getNamespacePrefix(node);
            }
        }
        if (this.hasPrefix) {
            myUri = contextSupport.translateNamespacePrefixToUri(this.prefix);
            if (myUri == null) {
                throw new UnresolvableException("Cannot resolve namespace prefix '" + this.prefix + "'");
            }
        } else if (this.matchesAnyName) {
            return true;
        }
        if (hasNamespace(myUri) != hasNamespace(nodeUri)) {
            return false;
        }
        if (this.matchesAnyName || nodeName.equals(getLocalName())) {
            return matchesNamespaceURIs(myUri, nodeUri);
        }
        return false;
    }

    private boolean hasNamespace(String uri) {
        return uri != null && uri.length() > 0;
    }

    /* access modifiers changed from: protected */
    public boolean matchesNamespaceURIs(String uri1, String uri2) {
        if (uri1 == uri2) {
            return true;
        }
        if (uri1 == null) {
            if (uri2.length() != 0) {
                return false;
            }
            return true;
        } else if (uri2 != null) {
            return uri1.equals(uri2);
        } else {
            if (uri1.length() != 0) {
                return false;
            }
            return true;
        }
    }

    public String toString() {
        return "[(DefaultNameStep): " + ("".equals(getPrefix()) ? getLocalName() : getPrefix() + ":" + getLocalName()) + "]";
    }
}
