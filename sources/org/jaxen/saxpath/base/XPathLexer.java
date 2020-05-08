package org.jaxen.saxpath.base;

class XPathLexer {
    private int currentPosition;
    private int endPosition;
    private Token previousToken;
    private String xpath;

    XPathLexer(String xpath2) {
        setXPath(xpath2);
    }

    private void setXPath(String xpath2) {
        this.xpath = xpath2;
        this.currentPosition = 0;
        this.endPosition = xpath2.length();
    }

    /* access modifiers changed from: 0000 */
    public String getXPath() {
        return this.xpath;
    }

    /* access modifiers changed from: 0000 */
    public Token nextToken() {
        Token token;
        do {
            token = null;
            switch (m51LA(1)) {
                case 9:
                case 10:
                case 13:
                case ' ':
                    token = whitespace();
                    break;
                case '!':
                    if (m51LA(2) == '=') {
                        token = notEquals();
                        break;
                    }
                    break;
                case '\"':
                case '\'':
                    token = literal();
                    break;
                case '$':
                    token = dollar();
                    break;
                case '(':
                    token = leftParen();
                    break;
                case ')':
                    token = rightParen();
                    break;
                case '*':
                    token = star();
                    break;
                case '+':
                    token = plus();
                    break;
                case ',':
                    token = comma();
                    break;
                case '-':
                    token = minus();
                    break;
                case '.':
                    switch (m51LA(2)) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            token = number();
                            break;
                        default:
                            token = dots();
                            break;
                    }
                case '/':
                    token = slashes();
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    token = number();
                    break;
                case ':':
                    if (m51LA(2) != ':') {
                        token = colon();
                        break;
                    } else {
                        token = doubleColon();
                        break;
                    }
                case '<':
                case '>':
                    token = relationalOperator();
                    break;
                case '=':
                    token = equals();
                    break;
                case '@':
                    token = m52at();
                    break;
                case '[':
                    token = leftBracket();
                    break;
                case ']':
                    token = rightBracket();
                    break;
                case '|':
                    token = pipe();
                    break;
                default:
                    if (isIdentifierStartChar(m51LA(1))) {
                        token = identifierOrOperatorName();
                        break;
                    }
                    break;
            }
            if (token == null) {
                if (!hasMoreChars()) {
                    token = new Token(-1, getXPath(), currentPosition(), endPosition());
                } else {
                    token = new Token(-3, getXPath(), currentPosition(), endPosition());
                }
            }
        } while (token.getTokenType() == -2);
        setPreviousToken(token);
        return token;
    }

    private Token identifierOrOperatorName() {
        if (this.previousToken == null) {
            return identifier();
        }
        switch (this.previousToken.getTokenType()) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 23:
            case 25:
            case 27:
            case 28:
            case 30:
                return identifier();
            default:
                return operatorName();
        }
    }

    private Token identifier() {
        int start = currentPosition();
        while (hasMoreChars() && isIdentifierChar(m51LA(1))) {
            consume();
        }
        return new Token(16, getXPath(), start, currentPosition());
    }

    private Token operatorName() {
        switch (m51LA(1)) {
            case 'a':
                return and();
            case 'd':
                return div();
            case 'm':
                return mod();
            case 'o':
                return m53or();
            default:
                return null;
        }
    }

    private Token mod() {
        if (m51LA(1) != 'm' || m51LA(2) != 'o' || m51LA(3) != 'd') {
            return null;
        }
        Token token = new Token(10, getXPath(), currentPosition(), currentPosition() + 3);
        consume();
        consume();
        consume();
        return token;
    }

    private Token div() {
        if (m51LA(1) != 'd' || m51LA(2) != 'i' || m51LA(3) != 'v') {
            return null;
        }
        Token token = new Token(11, getXPath(), currentPosition(), currentPosition() + 3);
        consume();
        consume();
        consume();
        return token;
    }

    private Token and() {
        if (m51LA(1) != 'a' || m51LA(2) != 'n' || m51LA(3) != 'd') {
            return null;
        }
        Token token = new Token(27, getXPath(), currentPosition(), currentPosition() + 3);
        consume();
        consume();
        consume();
        return token;
    }

    /* renamed from: or */
    private Token m53or() {
        if (m51LA(1) != 'o' || m51LA(2) != 'r') {
            return null;
        }
        Token token = new Token(28, getXPath(), currentPosition(), currentPosition() + 2);
        consume();
        consume();
        return token;
    }

    private Token number() {
        int start = currentPosition();
        boolean periodAllowed = true;
        while (true) {
            switch (m51LA(1)) {
                case '.':
                    if (!periodAllowed) {
                        break;
                    } else {
                        periodAllowed = false;
                        consume();
                        continue;
                    }
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    consume();
                    continue;
            }
        }
        return new Token(29, getXPath(), start, currentPosition());
    }

    private Token whitespace() {
        consume();
        while (hasMoreChars()) {
            switch (m51LA(1)) {
                case 9:
                case 10:
                case 13:
                case ' ':
                    consume();
            }
            return new Token(-2, getXPath(), 0, 0);
        }
        return new Token(-2, getXPath(), 0, 0);
    }

    private Token comma() {
        Token token = new Token(30, getXPath(), currentPosition(), currentPosition() + 1);
        consume();
        return token;
    }

    private Token equals() {
        Token token = new Token(1, getXPath(), currentPosition(), currentPosition() + 1);
        consume();
        return token;
    }

    private Token minus() {
        Token token = new Token(8, getXPath(), currentPosition(), currentPosition() + 1);
        consume();
        return token;
    }

    private Token plus() {
        Token token = new Token(7, getXPath(), currentPosition(), currentPosition() + 1);
        consume();
        return token;
    }

    private Token dollar() {
        Token token = new Token(25, getXPath(), currentPosition(), currentPosition() + 1);
        consume();
        return token;
    }

    private Token pipe() {
        Token token = new Token(18, getXPath(), currentPosition(), currentPosition() + 1);
        consume();
        return token;
    }

    /* renamed from: at */
    private Token m52at() {
        Token token = new Token(17, getXPath(), currentPosition(), currentPosition() + 1);
        consume();
        return token;
    }

    private Token colon() {
        Token token = new Token(19, getXPath(), currentPosition(), currentPosition() + 1);
        consume();
        return token;
    }

    private Token doubleColon() {
        Token token = new Token(20, getXPath(), currentPosition(), currentPosition() + 2);
        consume();
        consume();
        return token;
    }

    private Token notEquals() {
        Token token = new Token(2, getXPath(), currentPosition(), currentPosition() + 2);
        consume();
        consume();
        return token;
    }

    private Token relationalOperator() {
        Token token;
        Token token2 = null;
        switch (m51LA(1)) {
            case '<':
                if (m51LA(2) == '=') {
                    token2 = new Token(4, getXPath(), currentPosition(), currentPosition() + 2);
                    consume();
                } else {
                    token2 = new Token(3, getXPath(), currentPosition(), currentPosition() + 1);
                }
                consume();
                break;
            case '>':
                if (m51LA(2) == '=') {
                    token = new Token(6, getXPath(), currentPosition(), currentPosition() + 2);
                    consume();
                } else {
                    token = new Token(5, getXPath(), currentPosition(), currentPosition() + 1);
                }
                consume();
                break;
        }
        return token2;
    }

    private Token star() {
        Token token = new Token(9, getXPath(), currentPosition(), currentPosition() + 1);
        consume();
        return token;
    }

    private Token literal() {
        Token token = null;
        char match = m51LA(1);
        consume();
        int start = currentPosition();
        while (token == null && hasMoreChars()) {
            if (m51LA(1) == match) {
                token = new Token(26, getXPath(), start, currentPosition());
            }
            consume();
        }
        return token;
    }

    private Token dots() {
        switch (m51LA(2)) {
            case '.':
                Token token = new Token(15, getXPath(), currentPosition(), currentPosition() + 2);
                consume();
                consume();
                return token;
            default:
                Token token2 = new Token(14, getXPath(), currentPosition(), currentPosition() + 1);
                consume();
                return token2;
        }
    }

    private Token leftBracket() {
        Token token = new Token(21, getXPath(), currentPosition(), currentPosition() + 1);
        consume();
        return token;
    }

    private Token rightBracket() {
        Token token = new Token(22, getXPath(), currentPosition(), currentPosition() + 1);
        consume();
        return token;
    }

    private Token leftParen() {
        Token token = new Token(23, getXPath(), currentPosition(), currentPosition() + 1);
        consume();
        return token;
    }

    private Token rightParen() {
        Token token = new Token(24, getXPath(), currentPosition(), currentPosition() + 1);
        consume();
        return token;
    }

    private Token slashes() {
        switch (m51LA(2)) {
            case '/':
                Token token = new Token(13, getXPath(), currentPosition(), currentPosition() + 2);
                consume();
                consume();
                return token;
            default:
                Token token2 = new Token(12, getXPath(), currentPosition(), currentPosition() + 1);
                consume();
                return token2;
        }
    }

    /* renamed from: LA */
    private char m51LA(int i) {
        if (this.currentPosition + (i - 1) >= endPosition()) {
            return 65535;
        }
        return getXPath().charAt(currentPosition() + (i - 1));
    }

    private void consume() {
        this.currentPosition++;
    }

    private int currentPosition() {
        return this.currentPosition;
    }

    private int endPosition() {
        return this.endPosition;
    }

    private void setPreviousToken(Token previousToken2) {
        this.previousToken = previousToken2;
    }

    private boolean hasMoreChars() {
        return currentPosition() < endPosition();
    }

    private boolean isIdentifierChar(char c) {
        return Verifier.isXMLNCNameCharacter(c);
    }

    private boolean isIdentifierStartChar(char c) {
        return Verifier.isXMLNCNameStartCharacter(c);
    }
}
