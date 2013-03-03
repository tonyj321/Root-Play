package play.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks fields which should be considered as super classes as far as Root
 * streamer infos are concerned. Root supports multiple inheritance (and uses massively).
 * This is mapped in Java to a single base class plus fields representing the other super
 * classes.
 * @author tonyj
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Super {
}
