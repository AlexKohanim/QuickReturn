package org.jaxen.pattern;

import java.util.List;
import java.util.ListIterator;
import org.jaxen.JaxenException;
import org.jaxen.JaxenHandler;
import org.jaxen.expr.DefaultAllNodeStep;
import org.jaxen.expr.DefaultCommentNodeStep;
import org.jaxen.expr.DefaultFilterExpr;
import org.jaxen.expr.DefaultNameStep;
import org.jaxen.expr.DefaultProcessingInstructionNodeStep;
import org.jaxen.expr.DefaultStep;
import org.jaxen.expr.DefaultTextNodeStep;
import org.jaxen.expr.DefaultXPathFactory;
import org.jaxen.expr.Expr;
import org.jaxen.expr.FilterExpr;
import org.jaxen.expr.LocationPath;
import org.jaxen.expr.Predicate;
import org.jaxen.expr.PredicateSet;
import org.jaxen.expr.Step;
import org.jaxen.expr.UnionExpr;
import org.jaxen.saxpath.SAXPathException;
import org.jaxen.saxpath.XPathReader;
import org.jaxen.saxpath.helpers.XPathReaderFactory;

public class PatternParser {
    private static final boolean TRACE = false;
    private static final boolean USE_HANDLER = false;

    public static Pattern parse(String text) throws JaxenException, SAXPathException {
        XPathReader reader = XPathReaderFactory.createReader();
        JaxenHandler handler = new JaxenHandler();
        handler.setXPathFactory(new DefaultXPathFactory());
        reader.setXPathHandler(handler);
        reader.parse(text);
        return convertExpr(handler.getXPathExpr().getRootExpr()).simplify();
    }

    protected static Pattern convertExpr(Expr expr) throws JaxenException {
        if (expr instanceof LocationPath) {
            return convertExpr((LocationPath) expr);
        }
        if (expr instanceof FilterExpr) {
            LocationPathPattern answer = new LocationPathPattern();
            answer.addFilter((FilterExpr) expr);
            return answer;
        } else if (expr instanceof UnionExpr) {
            UnionExpr unionExpr = (UnionExpr) expr;
            return new UnionPattern(convertExpr(unionExpr.getLHS()), convertExpr(unionExpr.getRHS()));
        } else {
            LocationPathPattern answer2 = new LocationPathPattern();
            answer2.addFilter(new DefaultFilterExpr(expr, new PredicateSet()));
            return answer2;
        }
    }

    protected static LocationPathPattern convertExpr(LocationPath locationPath) throws JaxenException {
        LocationPathPattern answer = new LocationPathPattern();
        List steps = locationPath.getSteps();
        LocationPathPattern path = answer;
        boolean first = true;
        ListIterator iter = steps.listIterator(steps.size());
        while (iter.hasPrevious()) {
            Step step = (Step) iter.previous();
            if (first) {
                first = false;
                path = convertStep(path, step);
            } else {
                if (navigationStep(step)) {
                    LocationPathPattern parent = new LocationPathPattern();
                    int axis = step.getAxis();
                    if (axis == 2 || axis == 12) {
                        path.setAncestorPattern(parent);
                    } else {
                        path.setParentPattern(parent);
                    }
                    path = parent;
                }
                path = convertStep(path, step);
            }
        }
        if (locationPath.isAbsolute()) {
            path.setParentPattern(new LocationPathPattern(NodeTypeTest.DOCUMENT_TEST));
        }
        return answer;
    }

    protected static LocationPathPattern convertStep(LocationPathPattern path, Step step) throws JaxenException {
        if (step instanceof DefaultAllNodeStep) {
            if (step.getAxis() == 9) {
                path.setNodeTest(NodeTypeTest.ATTRIBUTE_TEST);
                return path;
            }
            path.setNodeTest(NodeTypeTest.ELEMENT_TEST);
            return path;
        } else if (step instanceof DefaultCommentNodeStep) {
            path.setNodeTest(NodeTypeTest.COMMENT_TEST);
            return path;
        } else if (step instanceof DefaultProcessingInstructionNodeStep) {
            path.setNodeTest(NodeTypeTest.PROCESSING_INSTRUCTION_TEST);
            return path;
        } else if (step instanceof DefaultTextNodeStep) {
            path.setNodeTest(TextNodeTest.SINGLETON);
            return path;
        } else if (step instanceof DefaultCommentNodeStep) {
            path.setNodeTest(NodeTypeTest.COMMENT_TEST);
            return path;
        } else if (step instanceof DefaultNameStep) {
            DefaultNameStep nameStep = (DefaultNameStep) step;
            String localName = nameStep.getLocalName();
            String prefix = nameStep.getPrefix();
            int axis = nameStep.getAxis();
            short nodeType = 1;
            if (axis == 9) {
                nodeType = 2;
            }
            if (!nameStep.isMatchesAnyName()) {
                path.setNodeTest(new NameTest(localName, nodeType));
            } else if (prefix.length() != 0 && !prefix.equals("*")) {
                path.setNodeTest(new NamespaceTest(prefix, nodeType));
            } else if (axis == 9) {
                path.setNodeTest(NodeTypeTest.ATTRIBUTE_TEST);
            } else {
                path.setNodeTest(NodeTypeTest.ELEMENT_TEST);
            }
            return convertDefaultStep(path, nameStep);
        } else if (step instanceof DefaultStep) {
            return convertDefaultStep(path, (DefaultStep) step);
        } else {
            throw new JaxenException("Cannot convert: " + step + " to a Pattern");
        }
    }

    protected static LocationPathPattern convertDefaultStep(LocationPathPattern path, DefaultStep step) throws JaxenException {
        List<Predicate> predicates = step.getPredicates();
        if (!predicates.isEmpty()) {
            FilterExpr filter = new DefaultFilterExpr(new PredicateSet());
            for (Predicate addPredicate : predicates) {
                filter.addPredicate(addPredicate);
            }
            path.addFilter(filter);
        }
        return path;
    }

    protected static boolean navigationStep(Step step) {
        if (!(step instanceof DefaultNameStep) && step.getClass().equals(DefaultStep.class) && step.getPredicates().isEmpty()) {
            return false;
        }
        return true;
    }
}
