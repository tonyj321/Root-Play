package play.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author tonyj
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface StreamerInfo {

    public enum Type {

        kNone(-1), kBase(0), kCounter(6), kCharStar(7),
        kChar(1), kShort(2), kInt(3), kLong(4), kFloat(5),
        kDouble(8), kDouble32(9),
        kLegacyChar(10), // Equal to TDataType's kchar
        kUChar(11), kUShort(12), kUInt(13), kULong(14), kBits(15),
        kLong64(16), kULong64(17), kBool(18), kFloat16(19),
        kObject(61), kAny(62), kObjectp(63), kObjectP(64),
        kTString(65), kTObject(66), kTNamed(67);
        private int value;
        private boolean isArray;

        Type(int v) {
            this.value = v;
        }

        public int getValue() {
            return value + (isArray?40:0);
        }

        void setIsArray(boolean b) {
            isArray = b;
        }
    }

    String value();

    public Type type() default Type.kNone;

    public String counter() default "";
}
