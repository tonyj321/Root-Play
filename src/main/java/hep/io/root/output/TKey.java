package hep.io.root.output;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import hep.io.root.output.annotations.ClassDef;
import hep.io.root.output.classes.TDatime;
import hep.io.root.output.classes.TNamed;
import java.nio.channels.SeekableByteChannel;

/**
 * A class representing a record within the root file.
 */
@ClassDef(hasStandardHeader = false)
class TKey extends TNamed {
    private final Class objectClass;
    private final String className;
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
    TKey(TFile tFile, Class objectClass, String fName, String fTitle, Pointer seekPDir, boolean suppressStreamerInfo) {
        super(fName,fTitle);
        this.tFile = tFile;
        this.objectClass = objectClass;
        this.seekPDir = seekPDir;
        this.suppressStreamerInfo = suppressStreamerInfo;
        this.className = StreamerUtilities.getClassInfo(objectClass).getName();
    }

    /**
     * Write the record to the file. A side effect of calling this method is
     * to set various member variables representing the size and position of
     * the record in the file.
     *
     * @param out
     * @throws IOException
     */
    void writeRecord(SeekableByteChannel out) throws IOException {
        fDatimeC = new TDatime(TDirectory.getTimeWarp());
        long seekKey = out.position();
        fSeekKey.set(seekKey);
        // Key buffer is used to buffer the data for the key header
        RootBufferedOutputStream keyBuffer = new RootBufferedOutputStream(tFile, 0, true);
        keyBuffer.seek(18);
        keyBuffer.writeObject(fSeekKey); // Pointer to record itself (consistency check)
        keyBuffer.writeObject(seekPDir); // Pointer to directory header
        keyBuffer.writeObject(className);
        keyBuffer.writeObject(getName());
        keyBuffer.writeObject(getTitle());
        keyLen = (int) keyBuffer.getFilePointer();
        out.position(seekKey+keyLen);
        // Write all the data objects associated with this record into a new DataBuffer
        RootBufferedOutputStream dataBuffer = new RootBufferedOutputStream(tFile, keyLen, suppressStreamerInfo);
        for (Object object : objects) {
            dataBuffer.writeObject(object);
        }
        dataBuffer.close();
        dataBuffer.writeTo(out, tFile.getCompressionLevel());
        long endPos = out.position();
        objLen = dataBuffer.uncompressedSize();
        size = (int) (endPos - seekKey);
        keyBuffer.seek(0);
        keyBuffer.writeInt(size); // Length of compressed object
        keyBuffer.writeShort(keyVersion); // TKey version identifier
        keyBuffer.writeInt(objLen); // Length of uncompressed object
        keyBuffer.writeObject(fDatimeC); // Date and time when object was written to file
        keyBuffer.writeShort(keyLen); // Length of the key structure (in bytes)
        keyBuffer.writeShort(cycle); // Cycle of key
        keyBuffer.seek(keyLen);
        out.position(seekKey);
        keyBuffer.writeTo(out,0);
        out.position(endPos);
    }
    
    void rewrite(SeekableByteChannel out) throws IOException {
        out.position(fSeekKey.get());
        writeRecord(out);
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
    
    Class getObjectClass() {
        return objectClass;
    }
    
    /**
     * Used to write the short version of the record into the seekKeysRecord
     * at the end of the file.
     *
     * @param out
     * @throws IOException
     */
    void streamer(RootOutput out) throws IOException {
        out.writeInt(size);
        out.writeShort(keyVersion);
        out.writeInt(objLen);
        out.writeObject(fDatimeC);
        out.writeShort(keyLen);
        out.writeShort(cycle);
        out.writeObject(fSeekKey);
        out.writeObject(seekPDir);
        out.writeObject(className);
        out.writeObject(getName());
        out.writeObject(getTitle());
    } 
}
