package org.jaxen.pattern;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.appcelerator.titanium.util.TiUrl;
import org.jaxen.Context;
import org.jaxen.JaxenException;
import org.jaxen.Navigator;
import org.jaxen.expr.FilterExpr;
import org.jaxen.util.SingletonList;

public class LocationPathPattern extends Pattern {
    private boolean absolute;
    private Pattern ancestorPattern;
    private List filters;
    private NodeTest nodeTest = AnyNodeTest.getInstance();
    private Pattern parentPattern;

    public LocationPathPattern() {
    }

    public LocationPathPattern(NodeTest nodeTest2) {
        this.nodeTest = nodeTest2;
    }

    /* Debug info: failed to restart local var, previous not found, register: 1 */
    public Pattern simplify() {
        if (this.parentPattern != null) {
            this.parentPattern = this.parentPattern.simplify();
        }
        if (this.ancestorPattern != null) {
            this.ancestorPattern = this.ancestorPattern.simplify();
        }
        if (this.filters != null) {
            return this;
        }
        if (this.parentPattern == null && this.ancestorPattern == null) {
            return this.nodeTest;
        }
        if (this.parentPattern == null || this.ancestorPattern != null || !(this.nodeTest instanceof AnyNodeTest)) {
            return this;
        }
        return this.parentPattern;
    }

    public void addFilter(FilterExpr filter) {
        if (this.filters == null) {
            this.filters = new ArrayList();
        }
        this.filters.add(filter);
    }

    public void setParentPattern(Pattern parentPattern2) {
        this.parentPattern = parentPattern2;
    }

    public void setAncestorPattern(Pattern ancestorPattern2) {
        this.ancestorPattern = ancestorPattern2;
    }

    public void setNodeTest(NodeTest nodeTest2) throws JaxenException {
        if (this.nodeTest instanceof AnyNodeTest) {
            this.nodeTest = nodeTest2;
            return;
        }
        throw new JaxenException("Attempt to overwrite nodeTest: " + this.nodeTest + " with: " + nodeTest2);
    }

    public boolean matches(Object node, Context context) throws JaxenException {
        Navigator navigator = context.getNavigator();
        if (!this.nodeTest.matches(node, context)) {
            return false;
        }
        if (this.parentPattern != null) {
            Object parent = navigator.getParentNode(node);
            if (parent == null || !this.parentPattern.matches(parent, context)) {
                return false;
            }
        }
        if (this.ancestorPattern != null) {
            for (Object ancestor = navigator.getParentNode(node); !this.ancestorPattern.matches(ancestor, context); ancestor = navigator.getParentNode(ancestor)) {
                if (ancestor == null || navigator.isDocument(ancestor)) {
                    return false;
                }
            }
        }
        if (this.filters == null) {
            return true;
        }
        List list = new SingletonList(node);
        context.setNodeSet(list);
        boolean answer = true;
        Iterator iter = this.filters.iterator();
        while (true) {
            if (iter.hasNext()) {
                if (!((FilterExpr) iter.next()).asBoolean(context)) {
                    answer = false;
                    break;
                }
            } else {
                break;
            }
        }
        context.setNodeSet(list);
        return answer;
    }

    public double getPriority() {
        if (this.filters != null) {
            return 0.5d;
        }
        return this.nodeTest.getPriority();
    }

    public short getMatchType() {
        return this.nodeTest.getMatchType();
    }

    public String getText() {
        StringBuffer buffer = new StringBuffer();
        if (this.absolute) {
            buffer.append(TiUrl.PATH_SEPARATOR);
        }
        if (this.ancestorPattern != null) {
            String text = this.ancestorPattern.getText();
            if (text.length() > 0) {
                buffer.append(text);
                buffer.append("//");
            }
        }
        if (this.parentPattern != null) {
            String text2 = this.parentPattern.getText();
            if (text2.length() > 0) {
                buffer.append(text2);
                buffer.append(TiUrl.PATH_SEPARATOR);
            }
        }
        buffer.append(this.nodeTest.getText());
        if (this.filters != null) {
            buffer.append("[");
            for (FilterExpr filter : this.filters) {
                buffer.append(filter.getText());
            }
            buffer.append("]");
        }
        return buffer.toString();
    }

    public String toString() {
        return super.toString() + "[ absolute: " + this.absolute + " parent: " + this.parentPattern + " ancestor: " + this.ancestorPattern + " filters: " + this.filters + " nodeTest: " + this.nodeTest + " ]";
    }

    public boolean isAbsolute() {
        return this.absolute;
    }

    public void setAbsolute(boolean absolute2) {
        this.absolute = absolute2;
    }

    public boolean hasAnyNodeTest() {
        return this.nodeTest instanceof AnyNodeTest;
    }
}
