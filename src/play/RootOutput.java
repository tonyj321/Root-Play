package play;

import java.io.Closeable;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

/**
 * An output stream which is used to write root classes to a file.
 * @author tonyj
 */
public interface RootOutput extends Closeable, DataOutput {

    void writeObject(RootObject o) throws IOException;
    void writeObjectRef(RootObject o) throws IOException;
    
    boolean isLargeFile();
    
    void seek(long position) throws IOException;
    long getFilePointer() throws IOException;

    public Map<String,Long> getClassMap();
}
