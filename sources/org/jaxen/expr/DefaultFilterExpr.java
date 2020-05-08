package org.jaxen.expr;

import java.util.ArrayList;
import java.util.List;
import org.jaxen.Context;
import org.jaxen.JaxenException;

public class DefaultFilterExpr extends DefaultExpr implements FilterExpr, Predicated {
    private static final long serialVersionUID = -549640659288005735L;
    private Expr expr;
    private PredicateSet predicates;

    public DefaultFilterExpr(PredicateSet predicateSet) {
        this.predicates = predicateSet;
    }

    public DefaultFilterExpr(Expr expr2, PredicateSet predicateSet) {
        this.expr = expr2;
        this.predicates = predicateSet;
    }

    public void addPredicate(Predicate predicate) {
        this.predicates.addPredicate(predicate);
    }

    public List getPredicates() {
        return this.predicates.getPredicates();
    }

    public PredicateSet getPredicateSet() {
        return this.predicates;
    }

    public Expr getExpr() {
        return this.expr;
    }

    public String toString() {
        return "[(DefaultFilterExpr): expr: " + this.expr + " predicates: " + this.predicates + " ]";
    }

    public String getText() {
        String text = "";
        if (this.expr != null) {
            text = this.expr.getText();
        }
        return text + this.predicates.getText();
    }

    public Expr simplify() {
        this.predicates.simplify();
        if (this.expr != null) {
            this.expr = this.expr.simplify();
        }
        if (this.predicates.getPredicates().size() == 0) {
            return getExpr();
        }
        return this;
    }

    public boolean asBoolean(Context context) throws JaxenException {
        Object results;
        if (this.expr != null) {
            results = this.expr.evaluate(context);
        } else {
            List nodeSet = context.getNodeSet();
            ArrayList list = new ArrayList(nodeSet.size());
            list.addAll(nodeSet);
            results = list;
        }
        if (results instanceof Boolean) {
            return ((Boolean) results).booleanValue();
        }
        if (results instanceof List) {
            return getPredicateSet().evaluateAsBoolean((List) results, context.getContextSupport());
        }
        return false;
    }

    public Object evaluate(Context context) throws JaxenException {
        Object results = getExpr().evaluate(context);
        if (results instanceof List) {
            return getPredicateSet().evaluatePredicates((List) results, context.getContextSupport());
        }
        return results;
    }
}
