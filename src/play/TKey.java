package play;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import play.annotations.ClassDef;
import play.classes.TDatime;

/**
 * A class representing a record within the root file.
 */
@ClassDef(hasStandardHeader = false)
class TKey {
    private String className;
    private String fName;
    private String fTitle;
    private static final int keyVersion = 4;
    private static final int cycle = 1;
    private Pointer seekPDir;
    private Pointer fSeekKey = new Pointer(0);
    private List<Object> objects = new ArrayList<>();
    private int objLen;
    private TDatime fDatimeC;
    private int keyLen;
    int size;
    // Indicates that classes written to this record should not cause
    // streamer infos to be added to the file.
    private boolean suppressStreamerInfo = false;
    private TFile tFile;

    /**
     * Create a new record.
     *
     * @param className The class name of objects to be stored in the file
     * @param fName The name of the record
     * @param fTitle The title of the record
     * @param seekPDir A pointer to the parent directory
     */
    TKey(TFile tFile, String className, String fName, String fTitle, Pointer seekPDir, boolean suppressStreamerInfo) {
        this.tFile = tFile;
        this.className = className;
        this.fName = fName;
        this.fTitle = fTitle;
        this.seekPDir = seekPDir;
        this.suppressStreamerInfo = suppressStreamerInfo;
    }

    /**
     * Write the record to the file. A side effect of calling this method is
     * to set various member variables representing the size and position of
     * the record in the file.
     *
     * @param out
     * @throws IOException
     */
    void writeRecord(RootRandomAccessFile out) throws IOException {
        fDatimeC = new TDatime(TDirectory.timeWarp);
        long seekKey = out.getFilePointer();
        fSeekKey.set(seekKey);
        out.seek(seekKey + 18);
        out.writeObject(fSeekKey); // Pointer to record itself (consistency check)
        out.writeObject(seekPDir); // Pointer to directory header
        out.writeObject(className);
        out.writeObject(fName);
        out.writeObject(fTitle);
        long dataPos = out.getFilePointer();
        keyLen = (int) (dataPos - seekKey);
        // Write all the objects associated with this record into a new DataBuffer
        // TODO: Is there any reason to buffer if we are not going to compress?
        RootBufferedOutputStream buffer = new RootBufferedOutputStream(tFile, keyLen, suppressStreamerInfo);
        for (Object object : objects) {
            buffer.writeObject(object);
        }
        buffer.close();
        buffer.writeTo(out);
        long endPos = out.getFilePointer();
        objLen = (int) (endPos - dataPos);
        size = (int) (endPos - seekKey);
        out.seek(seekKey);
        out.writeInt(size); // Length of compressed object
        out.writeShort(keyVersion); // TKey version identifier
        out.writeInt(objLen); // Length of uncompressed object
        out.writeObject(fDatimeC); // Date and time when object was written to file
        out.writeShort(keyLen); // Length of the key structure (in bytes)
        out.writeShort(cycle); // Cycle of key
        out.seek(endPos);
    }

    /**
     * The position of this record within the file. This method can be
     * called any time, but the return pointer will not be valid until the
     * record has been written using the writeRecord method.
     *
     * @return A pointer to the record location.
     */
    Pointer getSeekKey() {
        return fSeekKey;
    }

    /**
     * Adds an object to the record. The contents of the object are not
     * transfered until writeRecord is called.
     *
     * @param object The object to be stored in the record
     */
    void add(Object object) {
        objects.add(object);
    }

    /**
     * Used to write the short version of the record into the seekKeysRecord
     * at the end of the file.
     *
     * @param out
     * @throws IOException
     */
    private void write(RootOutput out) throws IOException {
        out.writeInt(size);
        out.writeShort(keyVersion);
        out.writeInt(objLen);
        out.writeObject(fDatimeC);
        out.writeShort(keyLen);
        out.writeShort(cycle);
        out.writeObject(fSeekKey);
        out.writeObject(seekPDir);
        out.writeObject(className);
        out.writeObject(fName);
        out.writeObject(fTitle);
    }
    
}
