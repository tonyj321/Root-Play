package play;
import java.io.IOException;

/**
 *
 * @author tonyj
 */
interface RootObject {

    void write(RootOutput out) throws IOException;
    int length(RootOutput out) throws IOException;
}
