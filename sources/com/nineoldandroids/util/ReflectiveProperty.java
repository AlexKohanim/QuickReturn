package com.nineoldandroids.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class ReflectiveProperty<T, V> extends Property<T, V> {
    private static final String PREFIX_GET = "get";
    private static final String PREFIX_IS = "is";
    private static final String PREFIX_SET = "set";
    private Field mField;
    private Method mGetter;
    private Method mSetter;

    public ReflectiveProperty(Class<T> propertyHolder, Class<V> valueType, String name) {
        super(valueType, name);
        char firstLetter = Character.toUpperCase(name.charAt(0));
        String capitalizedName = new StringBuilder(String.valueOf(firstLetter)).append(name.substring(1)).toString();
        String getterName = new StringBuilder(PREFIX_GET).append(capitalizedName).toString();
        try {
            this.mGetter = propertyHolder.getMethod(getterName, null);
        } catch (NoSuchMethodException e) {
            try {
                this.mGetter = propertyHolder.getDeclaredMethod(getterName, null);
                this.mGetter.setAccessible(true);
            } catch (NoSuchMethodException e2) {
                String getterName2 = new StringBuilder(PREFIX_IS).append(capitalizedName).toString();
                try {
                    this.mGetter = propertyHolder.getMethod(getterName2, null);
                } catch (NoSuchMethodException e3) {
                    try {
                        this.mGetter = propertyHolder.getDeclaredMethod(getterName2, null);
                        this.mGetter.setAccessible(true);
                    } catch (NoSuchMethodException e4) {
                        this.mField = propertyHolder.getField(name);
                        Class fieldType = this.mField.getType();
                        if (!typesMatch(valueType, fieldType)) {
                            throw new NoSuchPropertyException("Underlying type (" + fieldType + ") " + "does not match Property type (" + valueType + ")");
                        }
                        return;
                    } catch (NoSuchFieldException e5) {
                        throw new NoSuchPropertyException("No accessor method or field found for property with name " + name);
                    }
                }
            }
        }
        Class getterType = this.mGetter.getReturnType();
        if (!typesMatch(valueType, getterType)) {
            throw new NoSuchPropertyException("Underlying type (" + getterType + ") " + "does not match Property type (" + valueType + ")");
        }
        String setterName = new StringBuilder(PREFIX_SET).append(capitalizedName).toString();
        try {
            this.mSetter = propertyHolder.getDeclaredMethod(setterName, new Class[]{getterType});
            this.mSetter.setAccessible(true);
        } catch (NoSuchMethodException e6) {
        }
    }

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.lang.Class, code=java.lang.Class<V>, for r5v0, types: [java.lang.Class<V>, java.lang.Class] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean typesMatch(java.lang.Class<V> r4, java.lang.Class<V> r5) {
        /*
            r3 = this;
            r1 = 1
            r0 = 0
            if (r5 == r4) goto L_0x004d
            boolean r2 = r5.isPrimitive()
            if (r2 == 0) goto L_0x004a
            java.lang.Class r2 = java.lang.Float.TYPE
            if (r5 != r2) goto L_0x0012
            java.lang.Class<java.lang.Float> r2 = java.lang.Float.class
            if (r4 == r2) goto L_0x004b
        L_0x0012:
            java.lang.Class r2 = java.lang.Integer.TYPE
            if (r5 != r2) goto L_0x001a
            java.lang.Class<java.lang.Integer> r2 = java.lang.Integer.class
            if (r4 == r2) goto L_0x004b
        L_0x001a:
            java.lang.Class r2 = java.lang.Boolean.TYPE
            if (r5 != r2) goto L_0x0022
            java.lang.Class<java.lang.Boolean> r2 = java.lang.Boolean.class
            if (r4 == r2) goto L_0x004b
        L_0x0022:
            java.lang.Class r2 = java.lang.Long.TYPE
            if (r5 != r2) goto L_0x002a
            java.lang.Class<java.lang.Long> r2 = java.lang.Long.class
            if (r4 == r2) goto L_0x004b
        L_0x002a:
            java.lang.Class r2 = java.lang.Double.TYPE
            if (r5 != r2) goto L_0x0032
            java.lang.Class<java.lang.Double> r2 = java.lang.Double.class
            if (r4 == r2) goto L_0x004b
        L_0x0032:
            java.lang.Class r2 = java.lang.Short.TYPE
            if (r5 != r2) goto L_0x003a
            java.lang.Class<java.lang.Short> r2 = java.lang.Short.class
            if (r4 == r2) goto L_0x004b
        L_0x003a:
            java.lang.Class r2 = java.lang.Byte.TYPE
            if (r5 != r2) goto L_0x0042
            java.lang.Class<java.lang.Byte> r2 = java.lang.Byte.class
            if (r4 == r2) goto L_0x004b
        L_0x0042:
            java.lang.Class r2 = java.lang.Character.TYPE
            if (r5 != r2) goto L_0x004a
            java.lang.Class<java.lang.Character> r2 = java.lang.Character.class
            if (r4 == r2) goto L_0x004b
        L_0x004a:
            return r0
        L_0x004b:
            r0 = r1
            goto L_0x004a
        L_0x004d:
            r0 = r1
            goto L_0x004a
        */
        throw new UnsupportedOperationException("Method not decompiled: com.nineoldandroids.util.ReflectiveProperty.typesMatch(java.lang.Class, java.lang.Class):boolean");
    }

    public void set(T object, V value) {
        if (this.mSetter != null) {
            try {
                this.mSetter.invoke(object, new Object[]{value});
            } catch (IllegalAccessException e) {
                throw new AssertionError();
            } catch (InvocationTargetException e2) {
                throw new RuntimeException(e2.getCause());
            }
        } else if (this.mField != null) {
            try {
                this.mField.set(object, value);
            } catch (IllegalAccessException e3) {
                throw new AssertionError();
            }
        } else {
            throw new UnsupportedOperationException("Property " + getName() + " is read-only");
        }
    }

    public V get(T object) {
        if (this.mGetter != null) {
            try {
                return this.mGetter.invoke(object, null);
            } catch (IllegalAccessException e) {
                throw new AssertionError();
            } catch (InvocationTargetException e2) {
                throw new RuntimeException(e2.getCause());
            }
        } else if (this.mField != null) {
            try {
                return this.mField.get(object);
            } catch (IllegalAccessException e3) {
                throw new AssertionError();
            }
        } else {
            throw new AssertionError();
        }
    }

    public boolean isReadOnly() {
        return this.mSetter == null && this.mField == null;
    }
}
