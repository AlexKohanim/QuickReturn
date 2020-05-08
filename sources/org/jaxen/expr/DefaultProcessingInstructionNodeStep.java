package org.jaxen.expr;

import org.jaxen.ContextSupport;
import org.jaxen.Navigator;
import org.jaxen.expr.iter.IterableAxis;

public class DefaultProcessingInstructionNodeStep extends DefaultStep implements ProcessingInstructionNodeStep {
    private static final long serialVersionUID = -4825000697808126927L;
    private String name;

    public DefaultProcessingInstructionNodeStep(IterableAxis axis, String name2, PredicateSet predicateSet) {
        super(axis, predicateSet);
        this.name = name2;
    }

    public String getName() {
        return this.name;
    }

    public String getText() {
        StringBuffer buf = new StringBuffer();
        buf.append(getAxisName());
        buf.append("::processing-instruction(");
        String name2 = getName();
        if (!(name2 == null || name2.length() == 0)) {
            buf.append("'");
            buf.append(name2);
            buf.append("'");
        }
        buf.append(")");
        buf.append(super.getText());
        return buf.toString();
    }

    public boolean matches(Object node, ContextSupport support) {
        Navigator nav = support.getNavigator();
        if (!nav.isProcessingInstruction(node)) {
            return false;
        }
        String name2 = getName();
        if (name2 == null || name2.length() == 0) {
            return true;
        }
        return name2.equals(nav.getProcessingInstructionTarget(node));
    }
}
