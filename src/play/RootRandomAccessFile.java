package play;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author tonyj
 */
class RootRandomAccessFile extends RandomAccessFile implements RootOutput {

    private final TFile tFile;

    public RootRandomAccessFile(File file, TFile tFile) throws FileNotFoundException {
        super(file, "rw");
        this.tFile = tFile;
    }

    @Override
    public void writeObject(RootObject o) throws IOException {
        o.write(this);
    }

    @Override
    public int length(RootObject o) throws IOException {
        return o.length(this);
    }

    @Override
    public boolean isLargeFile() {
        return tFile.isLargeFile();
    }

}
