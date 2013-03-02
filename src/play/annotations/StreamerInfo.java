package play.annotations;

import play.Type;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark fields which should be persisted to the root file.
 * @author tonyj
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface StreamerInfo {

    /**
     * The description (title) of this variable. 
     */
    String value();

    /**
     * The type of this variable. If not specified the type is derived from the 
     * Java class of the field.
     * @see play.Type
     */
    public Type type() default Type.kNone;

    /**
     * Specify the variable which controls the size of this array.
     */
    public String counter() default "";
}
