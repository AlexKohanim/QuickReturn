package org.appcelerator.kroll;

public class KrollException {
    private String fileName;
    private String lineNumber;
    private String message;
    private String stack;

    public KrollException(String message2, String stack2) {
        this.message = message2;
        this.stack = stack2;
        parseInfo();
    }

    private void parseInfo() {
        if (this.stack != null) {
            String[] split = this.stack.split("\\n");
            if (split.length >= 2) {
                String[] info = split[1].replace("at", " ").trim().split(":");
                if (info.length >= 2) {
                    this.lineNumber = info[1];
                    this.fileName = info[0];
                }
            }
        }
    }

    public String getStack() {
        return this.stack;
    }

    public String getMessage() {
        return this.message;
    }

    public String getLineNumber() {
        return this.lineNumber;
    }

    public String getFileName() {
        return this.fileName;
    }
}
