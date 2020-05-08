package org.jaxen.expr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jaxen.Context;
import org.jaxen.ContextSupport;
import org.jaxen.JaxenException;
import org.jaxen.function.BooleanFunction;

public class PredicateSet implements Serializable {
    private static final long serialVersionUID = -7166491740228977853L;
    private List predicates = Collections.EMPTY_LIST;

    public void addPredicate(Predicate predicate) {
        if (this.predicates == Collections.EMPTY_LIST) {
            this.predicates = new ArrayList();
        }
        this.predicates.add(predicate);
    }

    public List getPredicates() {
        return this.predicates;
    }

    public void simplify() {
        for (Predicate eachPred : this.predicates) {
            eachPred.simplify();
        }
    }

    public String getText() {
        StringBuffer buf = new StringBuffer();
        for (Predicate eachPred : this.predicates) {
            buf.append(eachPred.getText());
        }
        return buf.toString();
    }

    /* access modifiers changed from: protected */
    public boolean evaluateAsBoolean(List contextNodeSet, ContextSupport support) throws JaxenException {
        return anyMatchingNode(contextNodeSet, support);
    }

    private boolean anyMatchingNode(List contextNodeSet, ContextSupport support) throws JaxenException {
        if (this.predicates.size() == 0) {
            return false;
        }
        List nodes2Filter = contextNodeSet;
        for (Predicate evaluate : this.predicates) {
            int nodes2FilterSize = nodes2Filter.size();
            Context predContext = new Context(support);
            List tempList = new ArrayList(1);
            predContext.setNodeSet(tempList);
            int i = 0;
            while (true) {
                if (i < nodes2FilterSize) {
                    Object contextNode = nodes2Filter.get(i);
                    tempList.clear();
                    tempList.add(contextNode);
                    predContext.setNodeSet(tempList);
                    predContext.setPosition(i + 1);
                    predContext.setSize(nodes2FilterSize);
                    Object predResult = evaluate.evaluate(predContext);
                    if (predResult instanceof Number) {
                        if (((Number) predResult).intValue() == i + 1) {
                            return true;
                        }
                    } else if (BooleanFunction.evaluate(predResult, predContext.getNavigator()).booleanValue()) {
                        return true;
                    }
                    i++;
                }
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public List evaluatePredicates(List contextNodeSet, ContextSupport support) throws JaxenException {
        if (this.predicates.size() == 0) {
            return contextNodeSet;
        }
        List nodes2Filter = contextNodeSet;
        for (Predicate applyPredicate : this.predicates) {
            nodes2Filter = applyPredicate(applyPredicate, nodes2Filter, support);
        }
        return nodes2Filter;
    }

    public List applyPredicate(Predicate predicate, List nodes2Filter, ContextSupport support) throws JaxenException {
        int nodes2FilterSize = nodes2Filter.size();
        List filteredNodes = new ArrayList(nodes2FilterSize);
        Context predContext = new Context(support);
        List tempList = new ArrayList(1);
        predContext.setNodeSet(tempList);
        for (int i = 0; i < nodes2FilterSize; i++) {
            Object contextNode = nodes2Filter.get(i);
            tempList.clear();
            tempList.add(contextNode);
            predContext.setNodeSet(tempList);
            predContext.setPosition(i + 1);
            predContext.setSize(nodes2FilterSize);
            Object predResult = predicate.evaluate(predContext);
            if (predResult instanceof Number) {
                if (((Number) predResult).intValue() == i + 1) {
                    filteredNodes.add(contextNode);
                }
            } else if (BooleanFunction.evaluate(predResult, predContext.getNavigator()).booleanValue()) {
                filteredNodes.add(contextNode);
            }
        }
        return filteredNodes;
    }
}
