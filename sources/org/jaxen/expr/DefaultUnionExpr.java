package org.jaxen.expr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jaxen.Context;
import org.jaxen.JaxenException;
import org.jaxen.XPathSyntaxException;

public class DefaultUnionExpr extends DefaultBinaryExpr implements UnionExpr {
    private static final long serialVersionUID = 7629142718276852707L;

    public /* bridge */ /* synthetic */ Expr getLHS() {
        return super.getLHS();
    }

    public /* bridge */ /* synthetic */ Expr getRHS() {
        return super.getRHS();
    }

    public /* bridge */ /* synthetic */ String getText() {
        return super.getText();
    }

    public /* bridge */ /* synthetic */ void setLHS(Expr x0) {
        super.setLHS(x0);
    }

    public /* bridge */ /* synthetic */ void setRHS(Expr x0) {
        super.setRHS(x0);
    }

    public /* bridge */ /* synthetic */ Expr simplify() {
        return super.simplify();
    }

    public DefaultUnionExpr(Expr lhs, Expr rhs) {
        super(lhs, rhs);
    }

    public String getOperator() {
        return "|";
    }

    public String toString() {
        return "[(DefaultUnionExpr): " + getLHS() + ", " + getRHS() + "]";
    }

    public Object evaluate(Context context) throws JaxenException {
        List results = new ArrayList();
        try {
            List lhsResults = (List) getLHS().evaluate(context);
            List rhsResults = (List) getRHS().evaluate(context);
            Set unique = new HashSet();
            results.addAll(lhsResults);
            unique.addAll(lhsResults);
            for (Object each : rhsResults) {
                if (!unique.contains(each)) {
                    results.add(each);
                    unique.add(each);
                }
            }
            Collections.sort(results, new NodeComparator(context.getNavigator()));
            return results;
        } catch (ClassCastException e) {
            throw new XPathSyntaxException(getText(), context.getPosition(), "Unions are only allowed over node-sets");
        }
    }
}
