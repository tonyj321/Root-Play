package play;

import java.io.Closeable;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 * @author tonyj
 */
public interface RootOutput extends Closeable, DataOutput {

    void writeObject(RootObject o) throws IOException;
    int length(RootObject o) throws IOException;
    
    boolean isLargeFile();
}
