package org.jaxen.expr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.appcelerator.titanium.util.TiUrl;
import org.jaxen.Context;
import org.jaxen.ContextSupport;
import org.jaxen.JaxenException;

abstract class DefaultLocationPath extends DefaultExpr implements LocationPath {
    private List steps = new LinkedList();

    DefaultLocationPath() {
    }

    public void addStep(Step step) {
        getSteps().add(step);
    }

    public List getSteps() {
        return this.steps;
    }

    public Expr simplify() {
        for (Step eachStep : getSteps()) {
            eachStep.simplify();
        }
        return this;
    }

    public String getText() {
        StringBuffer buf = new StringBuffer();
        Iterator stepIter = getSteps().iterator();
        while (stepIter.hasNext()) {
            buf.append(((Step) stepIter.next()).getText());
            if (stepIter.hasNext()) {
                buf.append(TiUrl.PATH_SEPARATOR);
            }
        }
        return buf.toString();
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        Iterator stepIter = getSteps().iterator();
        while (stepIter.hasNext()) {
            buf.append(stepIter.next().toString());
            if (stepIter.hasNext()) {
                buf.append(TiUrl.PATH_SEPARATOR);
            }
        }
        return buf.toString();
    }

    public boolean isAbsolute() {
        return false;
    }

    public Object evaluate(Context context) throws JaxenException {
        List contextNodeSet = new ArrayList(context.getNodeSet());
        ContextSupport support = context.getContextSupport();
        Context stepContext = new Context(support);
        for (Step eachStep : getSteps()) {
            stepContext.setNodeSet(contextNodeSet);
            contextNodeSet = eachStep.evaluate(stepContext);
            if (isReverseAxis(eachStep)) {
                Collections.reverse(contextNodeSet);
            }
        }
        if (getSteps().size() > 1) {
            Collections.sort(contextNodeSet, new NodeComparator(support.getNavigator()));
        }
        return contextNodeSet;
    }

    private boolean isReverseAxis(Step step) {
        int axis = step.getAxis();
        return axis == 8 || axis == 6 || axis == 4 || axis == 13;
    }
}
