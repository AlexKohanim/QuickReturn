package org.jaxen.expr;

import java.util.List;
import org.jaxen.Context;
import org.jaxen.JaxenException;

public class DefaultRelativeLocationPath extends DefaultLocationPath {
    private static final long serialVersionUID = -1006862529366150615L;

    public /* bridge */ /* synthetic */ void addStep(Step x0) {
        super.addStep(x0);
    }

    public /* bridge */ /* synthetic */ Object evaluate(Context x0) throws JaxenException {
        return super.evaluate(x0);
    }

    public /* bridge */ /* synthetic */ List getSteps() {
        return super.getSteps();
    }

    public /* bridge */ /* synthetic */ String getText() {
        return super.getText();
    }

    public /* bridge */ /* synthetic */ boolean isAbsolute() {
        return super.isAbsolute();
    }

    public /* bridge */ /* synthetic */ Expr simplify() {
        return super.simplify();
    }

    public String toString() {
        return "[(DefaultRelativeLocationPath): " + super.toString() + "]";
    }
}
