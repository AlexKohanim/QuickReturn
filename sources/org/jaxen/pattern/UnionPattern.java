package org.jaxen.pattern;

import org.jaxen.Context;
import org.jaxen.JaxenException;

public class UnionPattern extends Pattern {
    private Pattern lhs;
    private String matchesNodeName = null;
    private short nodeType = 0;
    private Pattern rhs;

    public UnionPattern() {
    }

    public UnionPattern(Pattern lhs2, Pattern rhs2) {
        this.lhs = lhs2;
        this.rhs = rhs2;
        init();
    }

    public Pattern getLHS() {
        return this.lhs;
    }

    public void setLHS(Pattern lhs2) {
        this.lhs = lhs2;
        init();
    }

    public Pattern getRHS() {
        return this.rhs;
    }

    public void setRHS(Pattern rhs2) {
        this.rhs = rhs2;
        init();
    }

    public boolean matches(Object node, Context context) throws JaxenException {
        return this.lhs.matches(node, context) || this.rhs.matches(node, context);
    }

    public Pattern[] getUnionPatterns() {
        return new Pattern[]{this.lhs, this.rhs};
    }

    public short getMatchType() {
        return this.nodeType;
    }

    public String getMatchesNodeName() {
        return this.matchesNodeName;
    }

    public Pattern simplify() {
        this.lhs = this.lhs.simplify();
        this.rhs = this.rhs.simplify();
        init();
        return this;
    }

    public String getText() {
        return this.lhs.getText() + " | " + this.rhs.getText();
    }

    public String toString() {
        return super.toString() + "[ lhs: " + this.lhs + " rhs: " + this.rhs + " ]";
    }

    private void init() {
        short type1 = this.lhs.getMatchType();
        if (type1 != this.rhs.getMatchType()) {
            type1 = 0;
        }
        this.nodeType = type1;
        String name1 = this.lhs.getMatchesNodeName();
        String name2 = this.rhs.getMatchesNodeName();
        this.matchesNodeName = null;
        if (name1 != null && name2 != null && name1.equals(name2)) {
            this.matchesNodeName = name1;
        }
    }
}
