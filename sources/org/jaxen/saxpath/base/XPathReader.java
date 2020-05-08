package org.jaxen.saxpath.base;

import java.util.ArrayList;
import org.appcelerator.titanium.TiC;
import org.jaxen.saxpath.Axis;
import org.jaxen.saxpath.SAXPathException;
import org.jaxen.saxpath.XPathHandler;
import org.jaxen.saxpath.XPathSyntaxException;
import org.jaxen.saxpath.helpers.DefaultXPathHandler;

public class XPathReader implements org.jaxen.saxpath.XPathReader {
    private static XPathHandler defaultHandler = new DefaultXPathHandler();
    private XPathHandler handler;
    private XPathLexer lexer;
    private ArrayList tokens;

    public XPathReader() {
        setXPathHandler(defaultHandler);
    }

    public void setXPathHandler(XPathHandler handler2) {
        this.handler = handler2;
    }

    public XPathHandler getXPathHandler() {
        return this.handler;
    }

    public void parse(String xpath) throws SAXPathException {
        setUpParse(xpath);
        getXPathHandler().startXPath();
        expr();
        getXPathHandler().endXPath();
        if (m54LA(1) != -1) {
            throw createSyntaxException("Unexpected '" + m55LT(1).getTokenText() + "'");
        }
        this.lexer = null;
        this.tokens = null;
    }

    /* access modifiers changed from: 0000 */
    public void setUpParse(String xpath) {
        this.tokens = new ArrayList();
        this.lexer = new XPathLexer(xpath);
    }

    private void pathExpr() throws SAXPathException {
        getXPathHandler().startPathExpr();
        switch (m54LA(1)) {
            case 9:
            case 14:
            case 15:
            case 17:
                locationPath(false);
                break;
            case 12:
            case 13:
                locationPath(true);
                break;
            case 16:
                if ((m54LA(2) == 23 && !isNodeTypeName(m55LT(1))) || (m54LA(2) == 19 && m54LA(4) == 23)) {
                    filterExpr();
                    if (m54LA(1) == 12 || m54LA(1) == 13) {
                        locationPath(false);
                        break;
                    }
                } else {
                    locationPath(false);
                    break;
                }
                break;
            case 23:
            case 25:
                filterExpr();
                if (m54LA(1) == 12 || m54LA(1) == 13) {
                    locationPath(false);
                    break;
                }
            case 26:
            case 29:
                filterExpr();
                if (m54LA(1) == 12 || m54LA(1) == 13) {
                    throw createSyntaxException("Node-set expected");
                }
            default:
                throw createSyntaxException("Unexpected '" + m55LT(1).getTokenText() + "'");
        }
        getXPathHandler().endPathExpr();
    }

    private void literal() throws SAXPathException {
        getXPathHandler().literal(match(26).getTokenText());
    }

    private void functionCall() throws SAXPathException {
        String prefix;
        if (m54LA(2) == 19) {
            prefix = match(16).getTokenText();
            match(19);
        } else {
            prefix = "";
        }
        getXPathHandler().startFunction(prefix, match(16).getTokenText());
        match(23);
        arguments();
        match(24);
        getXPathHandler().endFunction();
    }

    private void arguments() throws SAXPathException {
        while (m54LA(1) != 24) {
            expr();
            if (m54LA(1) == 30) {
                match(30);
            } else {
                return;
            }
        }
    }

    private void filterExpr() throws SAXPathException {
        getXPathHandler().startFilterExpr();
        switch (m54LA(1)) {
            case 16:
                functionCall();
                break;
            case 23:
                match(23);
                expr();
                match(24);
                break;
            case 25:
                variableReference();
                break;
            case 26:
                literal();
                break;
            case 29:
                getXPathHandler().number(Double.parseDouble(match(29).getTokenText()));
                break;
        }
        predicates();
        getXPathHandler().endFilterExpr();
    }

    private void variableReference() throws SAXPathException {
        String prefix;
        match(25);
        if (m54LA(2) == 19) {
            prefix = match(16).getTokenText();
            match(19);
        } else {
            prefix = "";
        }
        getXPathHandler().variableReference(prefix, match(16).getTokenText());
    }

    /* access modifiers changed from: 0000 */
    public void locationPath(boolean isAbsolute) throws SAXPathException {
        switch (m54LA(1)) {
            case 9:
            case 14:
            case 15:
            case 16:
            case 17:
                relativeLocationPath();
                return;
            case 12:
            case 13:
                if (isAbsolute) {
                    absoluteLocationPath();
                    return;
                } else {
                    relativeLocationPath();
                    return;
                }
            default:
                throw createSyntaxException("Unexpected '" + m55LT(1).getTokenText() + "'");
        }
    }

    private void absoluteLocationPath() throws SAXPathException {
        getXPathHandler().startAbsoluteLocationPath();
        switch (m54LA(1)) {
            case 12:
                match(12);
                switch (m54LA(1)) {
                    case 9:
                    case 14:
                    case 15:
                    case 16:
                    case 17:
                        steps();
                        break;
                }
            case 13:
                getXPathHandler().startAllNodeStep(12);
                getXPathHandler().endAllNodeStep();
                match(13);
                switch (m54LA(1)) {
                    case 9:
                    case 14:
                    case 15:
                    case 16:
                    case 17:
                        steps();
                        break;
                    default:
                        throw createSyntaxException("Location path cannot end with //");
                }
        }
        getXPathHandler().endAbsoluteLocationPath();
    }

    private void relativeLocationPath() throws SAXPathException {
        getXPathHandler().startRelativeLocationPath();
        switch (m54LA(1)) {
            case 12:
                match(12);
                break;
            case 13:
                getXPathHandler().startAllNodeStep(12);
                getXPathHandler().endAllNodeStep();
                match(13);
                break;
        }
        steps();
        getXPathHandler().endRelativeLocationPath();
    }

    private void steps() throws SAXPathException {
        switch (m54LA(1)) {
            case -1:
                return;
            case 9:
            case 14:
            case 15:
            case 16:
            case 17:
                step();
                while (true) {
                    if (m54LA(1) == 12 || m54LA(1) == 13) {
                        switch (m54LA(1)) {
                            case 12:
                                match(12);
                                break;
                            case 13:
                                getXPathHandler().startAllNodeStep(12);
                                getXPathHandler().endAllNodeStep();
                                match(13);
                                break;
                        }
                        switch (m54LA(1)) {
                            case 9:
                            case 14:
                            case 15:
                            case 16:
                            case 17:
                                step();
                            default:
                                throw createSyntaxException("Expected one of '.', '..', '@', '*', <QName>");
                        }
                    } else {
                        return;
                    }
                }
                break;
            default:
                throw createSyntaxException("Expected one of '.', '..', '@', '*', <QName>");
        }
    }

    /* access modifiers changed from: 0000 */
    public void step() throws SAXPathException {
        int axis = 0;
        switch (m54LA(1)) {
            case 9:
                axis = 1;
                break;
            case 14:
            case 15:
                abbrStep();
                return;
            case 16:
                if (m54LA(2) != 20) {
                    axis = 1;
                    break;
                } else {
                    axis = axisSpecifier();
                    break;
                }
            case 17:
                axis = axisSpecifier();
                break;
        }
        nodeTest(axis);
    }

    private int axisSpecifier() throws SAXPathException {
        switch (m54LA(1)) {
            case 16:
                Token token = m55LT(1);
                int axis = Axis.lookup(token.getTokenText());
                if (axis == 0) {
                    throwInvalidAxis(token.getTokenText());
                }
                match(16);
                match(20);
                return axis;
            case 17:
                match(17);
                return 9;
            default:
                return 0;
        }
    }

    private void nodeTest(int axis) throws SAXPathException {
        switch (m54LA(1)) {
            case 9:
                nameTest(axis);
                return;
            case 16:
                switch (m54LA(2)) {
                    case 23:
                        nodeTypeTest(axis);
                        return;
                    default:
                        nameTest(axis);
                        return;
                }
            default:
                throw createSyntaxException("Expected <QName> or *");
        }
    }

    private void nodeTypeTest(int axis) throws SAXPathException {
        String nodeType = match(16).getTokenText();
        match(23);
        if ("processing-instruction".equals(nodeType)) {
            String piName = "";
            if (m54LA(1) == 26) {
                piName = match(26).getTokenText();
            }
            match(24);
            getXPathHandler().startProcessingInstructionNodeStep(axis, piName);
            predicates();
            getXPathHandler().endProcessingInstructionNodeStep();
        } else if ("node".equals(nodeType)) {
            match(24);
            getXPathHandler().startAllNodeStep(axis);
            predicates();
            getXPathHandler().endAllNodeStep();
        } else if (TiC.PROPERTY_TEXT.equals(nodeType)) {
            match(24);
            getXPathHandler().startTextNodeStep(axis);
            predicates();
            getXPathHandler().endTextNodeStep();
        } else if (TiC.PROPERTY_COMMENT.equals(nodeType)) {
            match(24);
            getXPathHandler().startCommentNodeStep(axis);
            predicates();
            getXPathHandler().endCommentNodeStep();
        } else {
            throw createSyntaxException("Expected node-type");
        }
    }

    private void nameTest(int axis) throws SAXPathException {
        String prefix = null;
        String localName = null;
        switch (m54LA(2)) {
            case 19:
                switch (m54LA(1)) {
                    case 16:
                        prefix = match(16).getTokenText();
                        match(19);
                        break;
                }
        }
        switch (m54LA(1)) {
            case 9:
                match(9);
                localName = "*";
                break;
            case 16:
                localName = match(16).getTokenText();
                break;
        }
        if (prefix == null) {
            prefix = "";
        }
        getXPathHandler().startNameStep(axis, prefix, localName);
        predicates();
        getXPathHandler().endNameStep();
    }

    private void abbrStep() throws SAXPathException {
        switch (m54LA(1)) {
            case 14:
                match(14);
                getXPathHandler().startAllNodeStep(11);
                predicates();
                getXPathHandler().endAllNodeStep();
                return;
            case 15:
                match(15);
                getXPathHandler().startAllNodeStep(3);
                predicates();
                getXPathHandler().endAllNodeStep();
                return;
            default:
                return;
        }
    }

    private void predicates() throws SAXPathException {
        while (m54LA(1) == 21) {
            predicate();
        }
    }

    /* access modifiers changed from: 0000 */
    public void predicate() throws SAXPathException {
        getXPathHandler().startPredicate();
        match(21);
        predicateExpr();
        match(22);
        getXPathHandler().endPredicate();
    }

    private void predicateExpr() throws SAXPathException {
        expr();
    }

    private void expr() throws SAXPathException {
        orExpr();
    }

    private void orExpr() throws SAXPathException {
        getXPathHandler().startOrExpr();
        andExpr();
        boolean create = false;
        switch (m54LA(1)) {
            case 28:
                create = true;
                match(28);
                orExpr();
                break;
        }
        getXPathHandler().endOrExpr(create);
    }

    private void andExpr() throws SAXPathException {
        getXPathHandler().startAndExpr();
        equalityExpr();
        boolean create = false;
        switch (m54LA(1)) {
            case 27:
                create = true;
                match(27);
                andExpr();
                break;
        }
        getXPathHandler().endAndExpr(create);
    }

    private void equalityExpr() throws SAXPathException {
        relationalExpr();
        int la = m54LA(1);
        while (true) {
            if (la == 1 || la == 2) {
                switch (la) {
                    case 1:
                        match(1);
                        getXPathHandler().startEqualityExpr();
                        relationalExpr();
                        getXPathHandler().endEqualityExpr(1);
                        break;
                    case 2:
                        match(2);
                        getXPathHandler().startEqualityExpr();
                        relationalExpr();
                        getXPathHandler().endEqualityExpr(2);
                        break;
                }
                la = m54LA(1);
            } else {
                return;
            }
        }
    }

    private void relationalExpr() throws SAXPathException {
        additiveExpr();
        int la = m54LA(1);
        while (true) {
            if (la == 3 || la == 5 || la == 4 || la == 6) {
                switch (la) {
                    case 3:
                        match(3);
                        getXPathHandler().startRelationalExpr();
                        additiveExpr();
                        getXPathHandler().endRelationalExpr(3);
                        break;
                    case 4:
                        match(4);
                        getXPathHandler().startRelationalExpr();
                        additiveExpr();
                        getXPathHandler().endRelationalExpr(4);
                        break;
                    case 5:
                        match(5);
                        getXPathHandler().startRelationalExpr();
                        additiveExpr();
                        getXPathHandler().endRelationalExpr(5);
                        break;
                    case 6:
                        match(6);
                        getXPathHandler().startRelationalExpr();
                        additiveExpr();
                        getXPathHandler().endRelationalExpr(6);
                        break;
                }
                la = m54LA(1);
            } else {
                return;
            }
        }
    }

    private void additiveExpr() throws SAXPathException {
        multiplicativeExpr();
        int la = m54LA(1);
        while (true) {
            if (la == 7 || la == 8) {
                switch (la) {
                    case 7:
                        match(7);
                        getXPathHandler().startAdditiveExpr();
                        multiplicativeExpr();
                        getXPathHandler().endAdditiveExpr(7);
                        break;
                    case 8:
                        match(8);
                        getXPathHandler().startAdditiveExpr();
                        multiplicativeExpr();
                        getXPathHandler().endAdditiveExpr(8);
                        break;
                }
                la = m54LA(1);
            } else {
                return;
            }
        }
    }

    private void multiplicativeExpr() throws SAXPathException {
        unaryExpr();
        int la = m54LA(1);
        while (true) {
            if (la == 9 || la == 11 || la == 10) {
                switch (la) {
                    case 9:
                        match(9);
                        getXPathHandler().startMultiplicativeExpr();
                        unaryExpr();
                        getXPathHandler().endMultiplicativeExpr(9);
                        break;
                    case 10:
                        match(10);
                        getXPathHandler().startMultiplicativeExpr();
                        unaryExpr();
                        getXPathHandler().endMultiplicativeExpr(10);
                        break;
                    case 11:
                        match(11);
                        getXPathHandler().startMultiplicativeExpr();
                        unaryExpr();
                        getXPathHandler().endMultiplicativeExpr(11);
                        break;
                }
                la = m54LA(1);
            } else {
                return;
            }
        }
    }

    private void unaryExpr() throws SAXPathException {
        switch (m54LA(1)) {
            case 8:
                getXPathHandler().startUnaryExpr();
                match(8);
                unaryExpr();
                getXPathHandler().endUnaryExpr(12);
                return;
            default:
                unionExpr();
                return;
        }
    }

    private void unionExpr() throws SAXPathException {
        getXPathHandler().startUnionExpr();
        pathExpr();
        boolean create = false;
        switch (m54LA(1)) {
            case 18:
                match(18);
                create = true;
                expr();
                break;
        }
        getXPathHandler().endUnionExpr(create);
    }

    private Token match(int tokenType) throws XPathSyntaxException {
        m55LT(1);
        Token token = (Token) this.tokens.get(0);
        if (token.getTokenType() == tokenType) {
            this.tokens.remove(0);
            return token;
        }
        throw createSyntaxException("Expected: " + TokenTypes.getTokenText(tokenType));
    }

    /* renamed from: LA */
    private int m54LA(int position) {
        return m55LT(position).getTokenType();
    }

    /* renamed from: LT */
    private Token m55LT(int position) {
        if (this.tokens.size() <= position - 1) {
            for (int i = 0; i < position; i++) {
                this.tokens.add(this.lexer.nextToken());
            }
        }
        return (Token) this.tokens.get(position - 1);
    }

    private boolean isNodeTypeName(Token name) {
        String text = name.getTokenText();
        if ("node".equals(text) || TiC.PROPERTY_COMMENT.equals(text) || TiC.PROPERTY_TEXT.equals(text) || "processing-instruction".equals(text)) {
            return true;
        }
        return false;
    }

    private XPathSyntaxException createSyntaxException(String message) {
        return new XPathSyntaxException(this.lexer.getXPath(), m55LT(1).getTokenBegin(), message);
    }

    private void throwInvalidAxis(String invalidAxis) throws SAXPathException {
        throw new XPathSyntaxException(this.lexer.getXPath(), m55LT(1).getTokenBegin(), "Expected valid axis name instead of [" + invalidAxis + "]");
    }
}
