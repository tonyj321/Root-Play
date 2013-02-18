package play;
import java.io.IOException;

/**
 *
 * @author tonyj
 */
public interface RootObject {
    void write(RootOutput out) throws IOException;
}
