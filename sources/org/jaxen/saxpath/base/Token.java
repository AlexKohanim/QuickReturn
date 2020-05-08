package org.jaxen.saxpath.base;

class Token {
    private String parseText;
    private int tokenBegin;
    private int tokenEnd;
    private int tokenType;

    Token(int tokenType2, String parseText2, int tokenBegin2, int tokenEnd2) {
        setTokenType(tokenType2);
        setParseText(parseText2);
        setTokenBegin(tokenBegin2);
        setTokenEnd(tokenEnd2);
    }

    private void setTokenType(int tokenType2) {
        this.tokenType = tokenType2;
    }

    /* access modifiers changed from: 0000 */
    public int getTokenType() {
        return this.tokenType;
    }

    private void setParseText(String parseText2) {
        this.parseText = parseText2;
    }

    /* access modifiers changed from: 0000 */
    public String getTokenText() {
        return this.parseText.substring(getTokenBegin(), getTokenEnd());
    }

    private void setTokenBegin(int tokenBegin2) {
        this.tokenBegin = tokenBegin2;
    }

    /* access modifiers changed from: 0000 */
    public int getTokenBegin() {
        return this.tokenBegin;
    }

    private void setTokenEnd(int tokenEnd2) {
        this.tokenEnd = tokenEnd2;
    }

    /* access modifiers changed from: 0000 */
    public int getTokenEnd() {
        return this.tokenEnd;
    }

    public String toString() {
        return "[ (" + this.tokenType + ") (" + getTokenText() + ")";
    }
}
