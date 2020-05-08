package org.jaxen.function;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;

public class TranslateFunction implements Function {
    public Object call(Context context, List args) throws FunctionCallException {
        if (args.size() == 3) {
            return evaluate(args.get(0), args.get(1), args.get(2), context.getNavigator());
        }
        throw new FunctionCallException("translate() requires three arguments.");
    }

    public static String evaluate(Object strArg, Object fromArg, Object toArg, Navigator nav) throws FunctionCallException {
        String[] inCharacters;
        String inStr = StringFunction.evaluate(strArg, nav);
        String fromStr = StringFunction.evaluate(fromArg, nav);
        String toStr = StringFunction.evaluate(toArg, nav);
        Map characterMap = new HashMap();
        String[] fromCharacters = toUnicodeCharacters(fromStr);
        String[] toCharacters = toUnicodeCharacters(toStr);
        int fromLen = fromCharacters.length;
        int toLen = toCharacters.length;
        for (int i = 0; i < fromLen; i++) {
            String cFrom = fromCharacters[i];
            if (!characterMap.containsKey(cFrom)) {
                if (i < toLen) {
                    characterMap.put(cFrom, toCharacters[i]);
                } else {
                    characterMap.put(cFrom, null);
                }
            }
        }
        StringBuffer outStr = new StringBuffer(inStr.length());
        for (String cIn : toUnicodeCharacters(inStr)) {
            if (characterMap.containsKey(cIn)) {
                String cTo = (String) characterMap.get(cIn);
                if (cTo != null) {
                    outStr.append(cTo);
                }
            } else {
                outStr.append(cIn);
            }
        }
        return outStr.toString();
    }

    private static String[] toUnicodeCharacters(String s) throws FunctionCallException {
        String[] result = new String[s.length()];
        int stringLength = 0;
        int i = 0;
        while (i < s.length()) {
            char c1 = s.charAt(i);
            if (isHighSurrogate(c1)) {
                try {
                    char c2 = s.charAt(i + 1);
                    if (isLowSurrogate(c2)) {
                        result[stringLength] = (c1 + "" + c2).intern();
                        i++;
                    } else {
                        throw new FunctionCallException("Mismatched surrogate pair in translate function");
                    }
                } catch (StringIndexOutOfBoundsException e) {
                    throw new FunctionCallException("High surrogate without low surrogate at end of string passed to translate function");
                }
            } else {
                result[stringLength] = String.valueOf(c1).intern();
            }
            stringLength++;
            i++;
        }
        if (stringLength == result.length) {
            return result;
        }
        String[] trimmed = new String[stringLength];
        System.arraycopy(result, 0, trimmed, 0, stringLength);
        return trimmed;
    }

    private static boolean isHighSurrogate(char c) {
        return c >= 55296 && c <= 56319;
    }

    private static boolean isLowSurrogate(char c) {
        return c >= 56320 && c <= 57343;
    }
}
