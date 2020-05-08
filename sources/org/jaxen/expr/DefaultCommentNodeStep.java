package org.jaxen.expr;

import org.jaxen.ContextSupport;
import org.jaxen.expr.iter.IterableAxis;

public class DefaultCommentNodeStep extends DefaultStep implements CommentNodeStep {
    private static final long serialVersionUID = 4340788283861875606L;

    public DefaultCommentNodeStep(IterableAxis axis, PredicateSet predicateSet) {
        super(axis, predicateSet);
    }

    public String toString() {
        return "[(DefaultCommentNodeStep): " + getAxis() + "]";
    }

    public String getText() {
        return getAxisName() + "::comment()";
    }

    public boolean matches(Object node, ContextSupport contextSupport) {
        return contextSupport.getNavigator().isComment(node);
    }
}
