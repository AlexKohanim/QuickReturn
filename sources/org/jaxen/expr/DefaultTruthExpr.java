package org.jaxen.expr;

import java.util.List;

abstract class DefaultTruthExpr extends DefaultBinaryExpr {
    DefaultTruthExpr(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    public String toString() {
        return "[(DefaultTruthExpr): " + getLHS() + ", " + getRHS() + "]";
    }

    /* access modifiers changed from: protected */
    public boolean bothAreSets(Object lhs, Object rhs) {
        return (lhs instanceof List) && (rhs instanceof List);
    }

    /* access modifiers changed from: protected */
    public boolean eitherIsSet(Object lhs, Object rhs) {
        return (lhs instanceof List) || (rhs instanceof List);
    }

    /* access modifiers changed from: protected */
    public boolean isSet(Object obj) {
        return obj instanceof List;
    }

    /* access modifiers changed from: protected */
    public boolean setIsEmpty(List set) {
        return set == null || set.size() == 0;
    }

    /* access modifiers changed from: protected */
    public boolean eitherIsBoolean(Object lhs, Object rhs) {
        return (lhs instanceof Boolean) || (rhs instanceof Boolean);
    }

    /* access modifiers changed from: protected */
    public boolean bothAreBoolean(Object lhs, Object rhs) {
        return (lhs instanceof Boolean) && (rhs instanceof Boolean);
    }

    /* access modifiers changed from: protected */
    public boolean eitherIsNumber(Object lhs, Object rhs) {
        return (lhs instanceof Number) || (rhs instanceof Number);
    }
}
