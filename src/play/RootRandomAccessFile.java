package play;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author tonyj
 */
class RootRandomAccessFile extends RandomAccessFile implements RootOutput {

    private final TFile tFile;
    private Map<String, Long> classMap = new HashMap<>();

    public RootRandomAccessFile(File file, TFile tFile) throws FileNotFoundException {
        super(file, "rw");
        this.tFile = tFile;
    }

    @Override
    public void writeObject(RootObject o) throws IOException {
        RootBufferedOutputStream.writeObject(this, o);
    }

    @Override
    public boolean isLargeFile() {
        return tFile.isLargeFile();
    }

    @Override
    public void writeObjectRef(RootObject o) throws IOException {
        RootBufferedOutputStream.writeObjectRef(this, o);
    }

    @Override
    public Map<String, Long> getClassMap() {
        return classMap;
    }
}
