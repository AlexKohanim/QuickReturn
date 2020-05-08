package org.appcelerator.kroll;

public interface KrollExceptionHandler {

    public static class ExceptionMessage {
        public int line;
        public int lineOffset;
        public String lineSource;
        public String message;
        public String sourceName;
        public String title;

        public ExceptionMessage(String title2, String message2, String sourceName2, int line2, String lineSource2, int lineOffset2) {
            this.title = title2;
            this.message = message2;
            this.sourceName = sourceName2;
            this.lineSource = lineSource2;
            this.line = line2;
            this.lineOffset = lineOffset2;
        }
    }

    void handleException(ExceptionMessage exceptionMessage);
}
