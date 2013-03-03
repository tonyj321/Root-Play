package play.annotations;

import play.Type;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows overriding the default type given to fields as written to the root file.
 * @author tonyj
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FieldType {

    /**
     * The type of this field. 
     * @see play.Type
     */
    public Type value();
}