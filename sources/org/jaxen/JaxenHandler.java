package org.jaxen;

import java.util.Iterator;
import java.util.LinkedList;
import org.jaxen.expr.DefaultXPathFactory;
import org.jaxen.expr.Expr;
import org.jaxen.expr.FilterExpr;
import org.jaxen.expr.FunctionCallExpr;
import org.jaxen.expr.LocationPath;
import org.jaxen.expr.Predicate;
import org.jaxen.expr.Predicated;
import org.jaxen.expr.Step;
import org.jaxen.expr.XPathExpr;
import org.jaxen.expr.XPathFactory;
import org.jaxen.saxpath.XPathHandler;

public class JaxenHandler implements XPathHandler {
    protected boolean simplified;
    protected LinkedList stack = new LinkedList();
    private XPathExpr xpath;
    private XPathFactory xpathFactory = new DefaultXPathFactory();

    public void setXPathFactory(XPathFactory xpathFactory2) {
        this.xpathFactory = xpathFactory2;
    }

    public XPathFactory getXPathFactory() {
        return this.xpathFactory;
    }

    public XPathExpr getXPathExpr() {
        return getXPathExpr(true);
    }

    public XPathExpr getXPathExpr(boolean shouldSimplify) {
        if (shouldSimplify && !this.simplified) {
            this.xpath.simplify();
            this.simplified = true;
        }
        return this.xpath;
    }

    public void startXPath() {
        this.simplified = false;
        pushFrame();
    }

    public void endXPath() throws JaxenException {
        this.xpath = getXPathFactory().createXPath((Expr) pop());
        popFrame();
    }

    public void startPathExpr() {
        pushFrame();
    }

    public void endPathExpr() throws JaxenException {
        LocationPath locationPath;
        FilterExpr filterExpr;
        if (stackSize() == 2) {
            locationPath = (LocationPath) pop();
            filterExpr = (FilterExpr) pop();
        } else {
            Object popped = pop();
            if (popped instanceof LocationPath) {
                locationPath = (LocationPath) popped;
                filterExpr = null;
            } else {
                locationPath = null;
                filterExpr = (FilterExpr) popped;
            }
        }
        popFrame();
        push(getXPathFactory().createPathExpr(filterExpr, locationPath));
    }

    public void startAbsoluteLocationPath() throws JaxenException {
        pushFrame();
        push(getXPathFactory().createAbsoluteLocationPath());
    }

    public void endAbsoluteLocationPath() throws JaxenException {
        endLocationPath();
    }

    public void startRelativeLocationPath() throws JaxenException {
        pushFrame();
        push(getXPathFactory().createRelativeLocationPath());
    }

    public void endRelativeLocationPath() throws JaxenException {
        endLocationPath();
    }

    /* access modifiers changed from: protected */
    public void endLocationPath() throws JaxenException {
        LocationPath path = (LocationPath) peekFrame().removeFirst();
        addSteps(path, popFrame().iterator());
        push(path);
    }

    /* access modifiers changed from: protected */
    public void addSteps(LocationPath locationPath, Iterator stepIter) {
        while (stepIter.hasNext()) {
            locationPath.addStep((Step) stepIter.next());
        }
    }

    public void startNameStep(int axis, String prefix, String localName) throws JaxenException {
        pushFrame();
        push(getXPathFactory().createNameStep(axis, prefix, localName));
    }

    public void endNameStep() {
        endStep();
    }

    public void startTextNodeStep(int axis) throws JaxenException {
        pushFrame();
        push(getXPathFactory().createTextNodeStep(axis));
    }

    public void endTextNodeStep() {
        endStep();
    }

    public void startCommentNodeStep(int axis) throws JaxenException {
        pushFrame();
        push(getXPathFactory().createCommentNodeStep(axis));
    }

    public void endCommentNodeStep() {
        endStep();
    }

    public void startAllNodeStep(int axis) throws JaxenException {
        pushFrame();
        push(getXPathFactory().createAllNodeStep(axis));
    }

    public void endAllNodeStep() {
        endStep();
    }

    public void startProcessingInstructionNodeStep(int axis, String name) throws JaxenException {
        pushFrame();
        push(getXPathFactory().createProcessingInstructionNodeStep(axis, name));
    }

    public void endProcessingInstructionNodeStep() {
        endStep();
    }

    /* access modifiers changed from: protected */
    public void endStep() {
        Step step = (Step) peekFrame().removeFirst();
        addPredicates(step, popFrame().iterator());
        push(step);
    }

    public void startPredicate() {
        pushFrame();
    }

    public void endPredicate() throws JaxenException {
        Predicate predicate = getXPathFactory().createPredicate((Expr) pop());
        popFrame();
        push(predicate);
    }

    public void startFilterExpr() {
        pushFrame();
    }

    public void endFilterExpr() throws JaxenException {
        FilterExpr filter = getXPathFactory().createFilterExpr((Expr) peekFrame().removeFirst());
        addPredicates(filter, popFrame().iterator());
        push(filter);
    }

    /* access modifiers changed from: protected */
    public void addPredicates(Predicated obj, Iterator predIter) {
        while (predIter.hasNext()) {
            obj.addPredicate((Predicate) predIter.next());
        }
    }

    /* access modifiers changed from: protected */
    public void returnExpr() {
        Expr expr = (Expr) pop();
        popFrame();
        push(expr);
    }

    public void startOrExpr() {
    }

    public void endOrExpr(boolean create) throws JaxenException {
        if (create) {
            Expr lhs = (Expr) pop();
            push(getXPathFactory().createOrExpr(lhs, (Expr) pop()));
        }
    }

    public void startAndExpr() {
    }

    public void endAndExpr(boolean create) throws JaxenException {
        if (create) {
            Expr lhs = (Expr) pop();
            push(getXPathFactory().createAndExpr(lhs, (Expr) pop()));
        }
    }

    public void startEqualityExpr() {
    }

    public void endEqualityExpr(int operator) throws JaxenException {
        if (operator != 0) {
            Expr lhs = (Expr) pop();
            push(getXPathFactory().createEqualityExpr(lhs, (Expr) pop(), operator));
        }
    }

    public void startRelationalExpr() {
    }

    public void endRelationalExpr(int operator) throws JaxenException {
        if (operator != 0) {
            Expr lhs = (Expr) pop();
            push(getXPathFactory().createRelationalExpr(lhs, (Expr) pop(), operator));
        }
    }

    public void startAdditiveExpr() {
    }

    public void endAdditiveExpr(int operator) throws JaxenException {
        if (operator != 0) {
            Expr lhs = (Expr) pop();
            push(getXPathFactory().createAdditiveExpr(lhs, (Expr) pop(), operator));
        }
    }

    public void startMultiplicativeExpr() {
    }

    public void endMultiplicativeExpr(int operator) throws JaxenException {
        if (operator != 0) {
            Expr lhs = (Expr) pop();
            push(getXPathFactory().createMultiplicativeExpr(lhs, (Expr) pop(), operator));
        }
    }

    public void startUnaryExpr() {
    }

    public void endUnaryExpr(int operator) throws JaxenException {
        if (operator != 0) {
            push(getXPathFactory().createUnaryExpr((Expr) pop(), operator));
        }
    }

    public void startUnionExpr() {
    }

    public void endUnionExpr(boolean create) throws JaxenException {
        if (create) {
            Expr lhs = (Expr) pop();
            push(getXPathFactory().createUnionExpr(lhs, (Expr) pop()));
        }
    }

    public void number(int number) throws JaxenException {
        push(getXPathFactory().createNumberExpr(number));
    }

    public void number(double number) throws JaxenException {
        push(getXPathFactory().createNumberExpr(number));
    }

    public void literal(String literal) throws JaxenException {
        push(getXPathFactory().createLiteralExpr(literal));
    }

    public void variableReference(String prefix, String variableName) throws JaxenException {
        push(getXPathFactory().createVariableReferenceExpr(prefix, variableName));
    }

    public void startFunction(String prefix, String functionName) throws JaxenException {
        pushFrame();
        push(getXPathFactory().createFunctionCallExpr(prefix, functionName));
    }

    public void endFunction() {
        FunctionCallExpr function = (FunctionCallExpr) peekFrame().removeFirst();
        addParameters(function, popFrame().iterator());
        push(function);
    }

    /* access modifiers changed from: protected */
    public void addParameters(FunctionCallExpr function, Iterator paramIter) {
        while (paramIter.hasNext()) {
            function.addParameter((Expr) paramIter.next());
        }
    }

    /* access modifiers changed from: protected */
    public int stackSize() {
        return peekFrame().size();
    }

    /* access modifiers changed from: protected */
    public void push(Object obj) {
        peekFrame().addLast(obj);
    }

    /* access modifiers changed from: protected */
    public Object pop() {
        return peekFrame().removeLast();
    }

    /* access modifiers changed from: protected */
    public boolean canPop() {
        return peekFrame().size() > 0;
    }

    /* access modifiers changed from: protected */
    public void pushFrame() {
        this.stack.addLast(new LinkedList());
    }

    /* access modifiers changed from: protected */
    public LinkedList popFrame() {
        return (LinkedList) this.stack.removeLast();
    }

    /* access modifiers changed from: protected */
    public LinkedList peekFrame() {
        return (LinkedList) this.stack.getLast();
    }
}
