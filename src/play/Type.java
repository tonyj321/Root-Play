package play;

/**
 * Used to mark the type of fields within a RootObject.
 * @see play.annotations.StreamerInfo
 * @author tonyj
 */
public enum Type {
    kNone(-1), kBase(0), kCounter(6), kCharStar(7), kChar(1), kShort(2), kInt(3), kLong(4), kFloat(5), kDouble(8), kDouble32(9), kLegacyChar(10), // Equal to TDataType's kchar
    kUChar(11), kUShort(12), kUInt(13), kULong(14), kBits(15), kLong64(16), kULong64(17), kBool(18), kFloat16(19), 
    kCharArray(40+1), kShortArray(40+2), kIntArray(40+3), kLongArray(40+4), kFloatArray(40+5), kDoubleArray(40+8), kDouble32Array(40+9),
    kObject(61), kAny(62), kObjectp(63), kObjectP(64), kTString(65), kTObject(66), kTNamed(67);
    private int value;
    private static Type[] types;

    Type(int v) {
        this.value = v;
    }

    int getValue() {
        return value;
    }

    private static Type[] types() {
        if (types == null) {
            types = new Type[100];
            for (Type t : values()) {
                if (t.getValue()>=0) {
                    types[t.getValue()] = t;
                }
            }
        }
        return types;
    }
    
    Type arrayType() {
        return types()[value+40];
    }
}
