package org.appcelerator.kroll;

import java.util.HashMap;

public interface KrollFunction {

    public static class FunctionArgs {
        public Object[] args;
        public KrollObject krollObject;

        public FunctionArgs(KrollObject krollObject2, Object[] args2) {
            this.krollObject = krollObject2;
            this.args = args2;
        }
    }

    Object call(KrollObject krollObject, HashMap hashMap);

    Object call(KrollObject krollObject, Object[] objArr);

    void callAsync(KrollObject krollObject, HashMap hashMap);

    void callAsync(KrollObject krollObject, Object[] objArr);
}
