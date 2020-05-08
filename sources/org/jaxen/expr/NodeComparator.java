package org.jaxen.expr;

import java.util.Comparator;
import java.util.Iterator;
import org.jaxen.Navigator;
import org.jaxen.UnsupportedAxisException;

class NodeComparator implements Comparator {
    private Navigator navigator;

    NodeComparator(Navigator navigator2) {
        this.navigator = navigator2;
    }

    public int compare(Object o1, Object o2) {
        if (this.navigator == null) {
            return 0;
        }
        if (!isNonChild(o1) || !isNonChild(o2)) {
            try {
                int depth1 = getDepth(o1);
                int depth2 = getDepth(o2);
                Object a1 = o1;
                Object a2 = o2;
                while (depth1 > depth2) {
                    a1 = this.navigator.getParentNode(a1);
                    depth1--;
                }
                if (a1 == o2) {
                    return 1;
                }
                while (depth2 > depth1) {
                    a2 = this.navigator.getParentNode(a2);
                    depth2--;
                }
                if (a2 == o1) {
                    return -1;
                }
                while (true) {
                    Object p1 = this.navigator.getParentNode(a1);
                    Object p2 = this.navigator.getParentNode(a2);
                    if (p1 == p2) {
                        return compareSiblings(a1, a2);
                    }
                    a1 = p1;
                    a2 = p2;
                }
            } catch (UnsupportedAxisException e) {
                return 0;
            }
        } else {
            try {
                Object p12 = this.navigator.getParentNode(o1);
                Object p22 = this.navigator.getParentNode(o2);
                if (p12 == p22) {
                    if (this.navigator.isNamespace(o1) && this.navigator.isAttribute(o2)) {
                        return -1;
                    }
                    if (this.navigator.isNamespace(o2) && this.navigator.isAttribute(o1)) {
                        return 1;
                    }
                }
                return compare(p12, p22);
            } catch (UnsupportedAxisException e2) {
                return 0;
            }
        }
    }

    private boolean isNonChild(Object o) {
        return this.navigator.isAttribute(o) || this.navigator.isNamespace(o);
    }

    private int compareSiblings(Object sib1, Object sib2) throws UnsupportedAxisException {
        Iterator following = this.navigator.getFollowingSiblingAxisIterator(sib1);
        while (following.hasNext()) {
            if (following.next().equals(sib2)) {
                return -1;
            }
        }
        return 1;
    }

    private int getDepth(Object o) throws UnsupportedAxisException {
        int depth = 0;
        Object parent = o;
        while (true) {
            parent = this.navigator.getParentNode(parent);
            if (parent == null) {
                return depth;
            }
            depth++;
        }
    }
}
