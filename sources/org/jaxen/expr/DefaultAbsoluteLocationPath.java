package org.jaxen.expr;

import java.util.Collections;
import java.util.List;
import org.appcelerator.titanium.util.TiUrl;
import org.jaxen.Context;
import org.jaxen.ContextSupport;
import org.jaxen.JaxenException;
import org.jaxen.Navigator;
import org.jaxen.util.SingletonList;

public class DefaultAbsoluteLocationPath extends DefaultLocationPath {
    private static final long serialVersionUID = 2174836928310146874L;

    public /* bridge */ /* synthetic */ void addStep(Step x0) {
        super.addStep(x0);
    }

    public /* bridge */ /* synthetic */ List getSteps() {
        return super.getSteps();
    }

    public /* bridge */ /* synthetic */ Expr simplify() {
        return super.simplify();
    }

    public String toString() {
        return "[(DefaultAbsoluteLocationPath): " + super.toString() + "]";
    }

    public boolean isAbsolute() {
        return true;
    }

    public String getText() {
        return TiUrl.PATH_SEPARATOR + super.getText();
    }

    public Object evaluate(Context context) throws JaxenException {
        ContextSupport support = context.getContextSupport();
        Navigator nav = support.getNavigator();
        Context absContext = new Context(support);
        List contextNodes = context.getNodeSet();
        if (contextNodes.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        Object docNode = nav.getDocumentNode(contextNodes.get(0));
        if (docNode == null) {
            return Collections.EMPTY_LIST;
        }
        absContext.setNodeSet(new SingletonList(docNode));
        return super.evaluate(absContext);
    }
}
