package org.appcelerator.titanium.view;

import android.graphics.Matrix;
import android.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;

public class Ti2DMatrix extends KrollProxy {
    public static final float DEFAULT_ANCHOR_VALUE = -1.0f;
    public static final float VALUE_UNSPECIFIED = Float.MIN_VALUE;
    protected Ti2DMatrix next;

    /* renamed from: op */
    protected Operation f40op;
    protected Ti2DMatrix prev;

    public static class Operation {
        public static final int TYPE_INVERT = 4;
        public static final int TYPE_MULTIPLY = 3;
        public static final int TYPE_ROTATE = 2;
        public static final int TYPE_SCALE = 0;
        public static final int TYPE_TRANSLATE = 1;
        public float anchorX = 0.5f;
        public float anchorY = 0.5f;
        public Ti2DMatrix multiplyWith;
        public float rotateFrom;
        public float rotateTo;
        public boolean rotationFromValueSpecified = false;
        public boolean scaleFromValuesSpecified = false;
        public float scaleFromX;
        public float scaleFromY;
        public float scaleToX;
        public float scaleToY;
        public float translateX;
        public float translateY;
        public int type;

        public Operation(int type2) {
            this.type = type2;
        }

        public void apply(float interpolatedTime, Matrix matrix, int childWidth, int childHeight, float anchorX2, float anchorY2) {
            if (anchorX2 == -1.0f) {
                anchorX2 = this.anchorX;
            }
            if (anchorY2 == -1.0f) {
                anchorY2 = this.anchorY;
            }
            switch (this.type) {
                case 0:
                    matrix.preScale(((this.scaleToX - this.scaleFromX) * interpolatedTime) + this.scaleFromX, ((this.scaleToY - this.scaleFromY) * interpolatedTime) + this.scaleFromY, ((float) childWidth) * anchorX2, ((float) childHeight) * anchorY2);
                    return;
                case 1:
                    matrix.preTranslate(this.translateX * interpolatedTime, this.translateY * interpolatedTime);
                    return;
                case 2:
                    matrix.preRotate(((this.rotateTo - this.rotateFrom) * interpolatedTime) + this.rotateFrom, ((float) childWidth) * anchorX2, ((float) childHeight) * anchorY2);
                    return;
                case 3:
                    matrix.preConcat(this.multiplyWith.interpolate(interpolatedTime, childWidth, childHeight, anchorX2, anchorY2));
                    return;
                case 4:
                    matrix.invert(matrix);
                    return;
                default:
                    return;
            }
        }
    }

    public Ti2DMatrix() {
    }

    protected Ti2DMatrix(Ti2DMatrix prev2, int opType) {
        if (prev2 != null) {
            this.prev = prev2;
            prev2.next = this;
        }
        this.f40op = new Operation(opType);
    }

    public void handleCreationDict(KrollDict dict) {
        super.handleCreationDict(dict);
        if (dict.containsKey(TiC.PROPERTY_ROTATE)) {
            this.f40op = new Operation(2);
            this.f40op.rotateFrom = 0.0f;
            this.f40op.rotateTo = TiConvert.toFloat((HashMap<String, Object>) dict, TiC.PROPERTY_ROTATE);
            handleAnchorPoint(dict);
            if (dict.containsKey("scale")) {
                KrollDict newDict = new KrollDict();
                newDict.put("scale", dict.get("scale"));
                if (dict.containsKey(TiC.PROPERTY_ANCHOR_POINT)) {
                    newDict.put(TiC.PROPERTY_ANCHOR_POINT, dict.get(TiC.PROPERTY_ANCHOR_POINT));
                }
                this.prev = new Ti2DMatrix();
                this.prev.handleCreationDict(newDict);
                this.prev.next = this;
            }
        } else if (dict.containsKey("scale")) {
            this.f40op = new Operation(0);
            Operation operation = this.f40op;
            this.f40op.scaleFromY = 1.0f;
            operation.scaleFromX = 1.0f;
            Operation operation2 = this.f40op;
            Operation operation3 = this.f40op;
            float f = TiConvert.toFloat((HashMap<String, Object>) dict, "scale");
            operation3.scaleToY = f;
            operation2.scaleToX = f;
            handleAnchorPoint(dict);
        }
    }

    /* access modifiers changed from: protected */
    public void handleAnchorPoint(KrollDict dict) {
        if (dict.containsKey(TiC.PROPERTY_ANCHOR_POINT)) {
            KrollDict anchorPoint = dict.getKrollDict(TiC.PROPERTY_ANCHOR_POINT);
            if (anchorPoint != null) {
                this.f40op.anchorX = TiConvert.toFloat((HashMap<String, Object>) anchorPoint, "x");
                this.f40op.anchorY = TiConvert.toFloat((HashMap<String, Object>) anchorPoint, "y");
            }
        }
    }

    public Ti2DMatrix translate(double x, double y) {
        Ti2DMatrix newMatrix = new Ti2DMatrix(this, 1);
        newMatrix.f40op.translateX = (float) x;
        newMatrix.f40op.translateY = (float) y;
        return newMatrix;
    }

    public Ti2DMatrix scale(Object[] args) {
        Ti2DMatrix newMatrix = new Ti2DMatrix(this, 0);
        Operation operation = newMatrix.f40op;
        newMatrix.f40op.scaleFromY = Float.MIN_VALUE;
        operation.scaleFromX = Float.MIN_VALUE;
        Operation operation2 = newMatrix.f40op;
        newMatrix.f40op.scaleToY = 1.0f;
        operation2.scaleToX = 1.0f;
        if (args.length == 4) {
            newMatrix.f40op.scaleFromValuesSpecified = true;
            newMatrix.f40op.scaleFromX = TiConvert.toFloat(args[0]);
            newMatrix.f40op.scaleFromY = TiConvert.toFloat(args[1]);
            newMatrix.f40op.scaleToX = TiConvert.toFloat(args[2]);
            newMatrix.f40op.scaleToY = TiConvert.toFloat(args[3]);
        }
        if (args.length == 2) {
            newMatrix.f40op.scaleFromValuesSpecified = false;
            newMatrix.f40op.scaleToX = TiConvert.toFloat(args[0]);
            newMatrix.f40op.scaleToY = TiConvert.toFloat(args[1]);
        } else if (args.length == 1) {
            newMatrix.f40op.scaleFromValuesSpecified = false;
            Operation operation3 = newMatrix.f40op;
            Operation operation4 = newMatrix.f40op;
            float f = TiConvert.toFloat(args[0]);
            operation4.scaleToY = f;
            operation3.scaleToX = f;
        }
        return newMatrix;
    }

    public Ti2DMatrix rotate(Object[] args) {
        Ti2DMatrix newMatrix = new Ti2DMatrix(this, 2);
        if (args.length == 1) {
            newMatrix.f40op.rotationFromValueSpecified = false;
            newMatrix.f40op.rotateFrom = Float.MIN_VALUE;
            newMatrix.f40op.rotateTo = TiConvert.toFloat(args[0]);
        } else if (args.length == 2) {
            newMatrix.f40op.rotationFromValueSpecified = true;
            newMatrix.f40op.rotateFrom = TiConvert.toFloat(args[0]);
            newMatrix.f40op.rotateTo = TiConvert.toFloat(args[1]);
        }
        return newMatrix;
    }

    public Ti2DMatrix invert() {
        return new Ti2DMatrix(this, 4);
    }

    public Ti2DMatrix multiply(Ti2DMatrix other) {
        Ti2DMatrix newMatrix = new Ti2DMatrix(other, 3);
        newMatrix.f40op.multiplyWith = this;
        return newMatrix;
    }

    public float[] finalValuesAfterInterpolation(int width, int height) {
        float[] result = new float[9];
        interpolate(1.0f, width, height, 0.5f, 0.5f).getValues(result);
        return result;
    }

    public Matrix interpolate(float interpolatedTime, int childWidth, int childHeight, float anchorX, float anchorY) {
        Ti2DMatrix first = this;
        ArrayList<Ti2DMatrix> preMatrixList = new ArrayList<>();
        while (first.prev != null) {
            first = first.prev;
            preMatrixList.add(0, first);
        }
        Matrix matrix = new Matrix();
        Iterator it = preMatrixList.iterator();
        while (it.hasNext()) {
            Ti2DMatrix current = (Ti2DMatrix) it.next();
            if (current.f40op != null) {
                current.f40op.apply(interpolatedTime, matrix, childWidth, childHeight, anchorX, anchorY);
            }
        }
        if (this.f40op != null) {
            this.f40op.apply(interpolatedTime, matrix, childWidth, childHeight, anchorX, anchorY);
        }
        return matrix;
    }

    private boolean containsOperationOfType(int operationType) {
        for (Ti2DMatrix check = this; check != null; check = check.prev) {
            if (check.f40op != null && check.f40op.type == operationType) {
                return true;
            }
        }
        return false;
    }

    public boolean hasScaleOperation() {
        return containsOperationOfType(0);
    }

    public boolean hasRotateOperation() {
        return containsOperationOfType(2);
    }

    public Pair<Float, Float> verifyScaleValues(TiUIView view, boolean autoreverse) {
        Pair<Float, Float> viewCurrentScale;
        ArrayList<Operation> scaleOps = new ArrayList<>();
        for (Ti2DMatrix check = this; check != null; check = check.prev) {
            if (check.f40op != null && check.f40op.type == 0) {
                scaleOps.add(0, check.f40op);
            }
        }
        if (view == null) {
            viewCurrentScale = Pair.create(Float.valueOf(1.0f), Float.valueOf(1.0f));
        } else {
            viewCurrentScale = view.getAnimatedScaleValues();
        }
        if (scaleOps.size() == 0) {
            return viewCurrentScale;
        }
        float lastToX = ((Float) viewCurrentScale.first).floatValue();
        float lastToY = ((Float) viewCurrentScale.second).floatValue();
        Iterator it = scaleOps.iterator();
        while (it.hasNext()) {
            Operation op = (Operation) it.next();
            if (!op.scaleFromValuesSpecified) {
                op.scaleFromX = lastToX;
                op.scaleFromY = lastToY;
            }
            lastToX = op.scaleToX;
            lastToY = op.scaleToY;
        }
        return !autoreverse ? Pair.create(Float.valueOf(lastToX), Float.valueOf(lastToY)) : viewCurrentScale;
    }

    public float verifyRotationValues(TiUIView view, boolean autoreverse) {
        ArrayList<Operation> rotationOps = new ArrayList<>();
        for (Ti2DMatrix check = this; check != null; check = check.prev) {
            if (check.f40op != null && check.f40op.type == 2) {
                rotationOps.add(0, check.f40op);
            }
        }
        float viewCurrentRotation = view == null ? 0.0f : view.getAnimatedRotationDegrees();
        if (rotationOps.size() == 0) {
            return viewCurrentRotation;
        }
        float lastRotation = viewCurrentRotation;
        Iterator it = rotationOps.iterator();
        while (it.hasNext()) {
            Operation op = (Operation) it.next();
            if (!op.rotationFromValueSpecified) {
                op.rotateFrom = lastRotation;
            }
            lastRotation = op.rotateTo;
        }
        if (!autoreverse) {
            return lastRotation;
        }
        return viewCurrentRotation;
    }

    public float[] getRotateOperationParameters() {
        if (this.f40op == null) {
            return new float[4];
        }
        return new float[]{this.f40op.rotateFrom, this.f40op.rotateTo, this.f40op.anchorX, this.f40op.anchorY};
    }

    public void setRotationFromDegrees(float degrees) {
        if (this.f40op != null) {
            this.f40op.rotateFrom = degrees;
        }
    }

    public boolean canUsePropertyAnimators() {
        boolean hasScale = false;
        boolean hasRotate = false;
        boolean hasTranslate = false;
        for (Operation op : getAllOperations()) {
            if (op != null) {
                switch (op.type) {
                    case 0:
                        if (!hasScale) {
                            hasScale = true;
                            break;
                        } else {
                            return false;
                        }
                    case 1:
                        if (!hasTranslate) {
                            hasTranslate = true;
                            break;
                        } else {
                            return false;
                        }
                    case 2:
                        if (!hasRotate) {
                            hasRotate = true;
                            break;
                        } else {
                            return false;
                        }
                    case 3:
                    case 4:
                        return false;
                }
            }
        }
        return true;
    }

    public List<Operation> getAllOperations() {
        List<Operation> ops = new ArrayList<>();
        for (Ti2DMatrix toCheck = this; toCheck != null; toCheck = toCheck.prev) {
            if (toCheck.f40op != null) {
                ops.add(toCheck.f40op);
            }
        }
        return ops;
    }
}
