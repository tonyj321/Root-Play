package play;

import play.classes.TNamed;
import play.classes.TList;
import play.classes.TUUID;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import play.annotations.ClassDef;
import play.classes.TDatime;
import play.classes.TString;

/**
 * Top level class for interacting with a Root file. Currently this
 * implementation only supports writing.
 *
 * @author tonyj
 * @see <a href="http://root.cern.ch/download/doc/11InputOutput.pdf">Root Manual
 * Input/Output</a>
 */
public class TFile implements Closeable {

    private final RootRandomAccessFile out;
    private static final int fVersion = 52800;
    private static final int fBEGIN = 0x64;
    private Pointer fEND = new Pointer(0);
    private Pointer fSeekFree = new Pointer(0);
    private Pointer fNbytesFree = new Pointer(0);
    private int nfree = 0;
    private boolean largeFile = false;
    private int fCompress = 0;
    private Pointer fSeekInfo;
    private Pointer fNbytesInfo = new Pointer(0);
    private final List<TKey> dataRecords = new ArrayList<>();
    private final TDirectory topLevelDirectory;
    // This is the record that is always written at fBEGIN
    private final TKey topLevelRecord;
    // This is the record that is written at fSeekKeys
    private final TKey seekKeysRecord;
    // This is the record that is written at fSeekInfo
    private final TKey seekInfoRecord;
    // Collection of TStreamerInfos to be written to the seekInfoRecord;
    private Map<String, TStreamerInfo> streamerInfos = new HashMap<>();

    /**
     * Open a new file for writing, or overwrite an existing file.
     *
     * @param file The file to create, or overwrite.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public TFile(String file) throws FileNotFoundException, IOException {
        this(new File(file));
    }

    /**
     * Open a new file for writing, or overwrite an existing file.
     *
     * @param file
     * @throws FileNotFoundException
     * @throws IOException
     */
    public TFile(File file) throws FileNotFoundException, IOException {

        out = new RootRandomAccessFile(file, this);
        TString tFile = new TString("TFile");
        TString fName = new TString(file.getName());
        TString fTitle = new TString("");
        topLevelRecord = new TKey(tFile, fName, fTitle, Pointer.ZERO);
        seekKeysRecord = new TKey(tFile, fName, fTitle, new Pointer(fBEGIN));
        topLevelDirectory = new TDirectory(Pointer.ZERO, new Pointer(fBEGIN), seekKeysRecord.getSeekKey());
        topLevelDirectory.fNbytesName = 32 + 2 * fName.sizeOnDisk() + 2 * fTitle.sizeOnDisk();
        topLevelRecord.add(new WeirdExtraNameAndTitle(fName, fTitle));
        topLevelRecord.add(topLevelDirectory);
        seekKeysRecord.add(topLevelDirectory.getKeyList());

        tFile = new TString("TList");
        fName = new TString("StreamerInfo");
        fTitle = new TString("Doubly linked list");
        seekInfoRecord = new TKey(tFile, fName, fTitle, new Pointer(fBEGIN));
        fSeekInfo = seekInfoRecord.getSeekKey();
        TList<TStreamerInfo> list = new TList<>(streamerInfos.values());
        seekInfoRecord.add(list);
    }

    /**
     * Flush any uncommitted data to disk.
     *
     * @throws IOException
     */
    public void flush() throws IOException {
        out.seek(fBEGIN);
        topLevelRecord.writeRecord(out);
        for (TKey record : dataRecords) {
            record.writeRecord(out);
        }
        Map<String, TStreamerInfo> streamerInfosSave = streamerInfos;
        // Avoid concurrent modification exception while writing streamer infos
        streamerInfos = new HashMap<>();
        seekInfoRecord.writeRecord(out);
        streamerInfos = streamerInfosSave;
        fNbytesInfo.set(seekInfoRecord.size);
        seekKeysRecord.writeRecord(out);
        topLevelDirectory.fNbytesKeys = seekKeysRecord.size;
        fEND.set(out.getFilePointer());
        // Rewrite topLevelRecord to get updated fSeekKey pointer
        out.seek(fBEGIN);
        topLevelRecord.writeRecord(out);
        // Finally write the header
        writeHeader();
    }

    /**
     * Close the file, first flushing any uncommitted data to disk.
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        flush();
        out.close();
    }

    /**
     * Add an object to a root file. Note that this just registers the object
     * with the file, the data is not extracted from the object and written to
     * disk until flush() or close() is called.
     *
     * @param object The object to be written to disk.
     */
    public void add(RootObject object) throws IOException {
        try {
            TString className = new TString(StreamerUtilities.getClassInfo(object.getClass()).getName());
            TString fName, fTitle;
            if (object instanceof TNamed) {
                TNamed tNamed = (TNamed) object;
                fName = tNamed.getName();
                fTitle = tNamed.getTitle();
            } else {
                fName = new TString("string");
                fTitle = TString.empty();
            }
            TKey record = new TKey(className, fName, fTitle, topLevelDirectory.fSeekDir);
            record.add(object);
            dataRecords.add(record);
            topLevelDirectory.add(record);
        } catch (StreamerInfoException ex) {
            throw new IOException("Error getting classinfo for object of class "+object.getClass(),ex);
        }
    }

    /**
     * Write the file header at the top of a root file.
     *
     * @throws IOException
     */
    private void writeHeader() throws IOException {
        out.seek(0);
        out.writeByte('r');
        out.writeByte('o');
        out.writeByte('o');
        out.writeByte('t');
        out.writeInt(fVersion);           // File format version
        out.writeInt(fBEGIN);             // Pointer to first data record
        out.writeObject(fEND);            // Pointer to first free word at the EOF
        out.writeObject(fSeekFree);       // Pointer to FREE data record
        out.writeObject(fNbytesFree);     // Number of bytes in FREE data record
        out.writeInt(nfree);              // Number of free data records
        // Number of bytes in TNamed at creation time
        out.writeInt(topLevelDirectory.fNbytesName);
        out.writeByte(largeFile ? 8 : 4); // Number of bytes for file pointers
        out.writeInt(fCompress);          // Compression level and algorithm
        out.writeObject(fSeekInfo);       // Pointer to TStreamerInfo record
        out.writeObject(fNbytesInfo);     // Number of bytes in TStreamerInfo record
        out.writeObject(topLevelDirectory.fUUID);
    }

    /**
     * Returns true if the file represents pointers within the file as 64 bit
     * values.
     *
     * @return <code>true</code> if the file is >2GB.
     */
    boolean isLargeFile() {
        return largeFile;
    }

    Map<String, TStreamerInfo> getStreamerInfos() {
        return streamerInfos;
    }

    /**
     * A class representing a record within the root file.
     */
    @ClassDef(version = 0, hasStandardHeader = false)
    private class TKey implements RootObject {

        private TString className;
        private TString fName;
        private TString fTitle;
        private final static int keyVersion = 4;
        private final static int cycle = 1;
        private Pointer seekPDir;
        private Pointer fSeekKey = new Pointer(0);
        private List<RootObject> objects = new ArrayList<>();
        private int objLen;
        private TDatime fDatimeC;
        private int keyLen;
        private int size;

        /**
         * Create a new record.
         *
         * @param className The class name of objects to be stored in the file
         * @param fName The name of the record
         * @param fTitle The title of the record
         * @param seekPDir A pointer to the parent directory
         */
        TKey(TString className, TString fName, TString fTitle, Pointer seekPDir) {
            this.className = className;
            this.fName = fName;
            this.fTitle = fTitle;
            this.seekPDir = seekPDir;
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

            fDatimeC = new TDatime();
            long seekKey = out.getFilePointer();
            fSeekKey.set(seekKey);
            out.seek(seekKey + 18);
            out.writeObject(fSeekKey);            // Pointer to record itself (consistency check)
            out.writeObject(seekPDir);            // Pointer to directory header
            out.writeObject(className);
            out.writeObject(fName);
            out.writeObject(fTitle);
            long dataPos = out.getFilePointer();
            keyLen = (int) (dataPos - seekKey);
            // Write all the objects associated with this record into a new DataBuffer
            // TODO: Is there any reason to buffer if we are not going to compress?
            RootBufferedOutputStream buffer = new RootBufferedOutputStream(TFile.this,keyLen);
            for (RootObject object : objects) {
                buffer.writeObject(object);
            }
            buffer.close();
            buffer.writeTo(out);
            long endPos = out.getFilePointer();
            objLen = (int) (endPos - dataPos);
            size = (int) (endPos - seekKey);
            out.seek(seekKey);
            out.writeInt(size);                      // Length of compressed object
            out.writeShort(keyVersion);           // TKey version identifier
            out.writeInt(objLen);                 // Length of uncompressed object
            out.writeObject(fDatimeC);            // Date and time when object was written to file
            out.writeShort(keyLen);           // Length of the key structure (in bytes)
            out.writeShort(cycle);                // Cycle of key
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
        private void add(RootObject object) {
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

    /**
     * A class which encapsulated a "pointer" within a root file. Depending on
     * how big the file is, this may be written as either a 32bit or 64 bit
     * integer.
     */
    @ClassDef(hasStandardHeader = false, suppressTStreamerInfo=true)
    static class Pointer implements RootObject {

        private long value;
        private final boolean immutable;
        public static Pointer ZERO = new Pointer(0, true);

        Pointer(long value) {
            this.value = value;
            this.immutable = false;
        }

        private Pointer(long value, boolean immutable) {
            this.value = value;
            this.immutable = immutable;
        }

        void set(long value) {
            if (immutable) {
                throw new RuntimeException("Attempt to modify immutable pointer");
            }
            this.value = value;
        }

        long get() {
            return value;
        }

        private void write(RootOutput out) throws IOException {
            if (out.isLargeFile()) {
                out.writeLong(value);
            } else {
                out.writeInt((int) value);
            }

        }
    }

    /**
     * Represents a directory within a root file. There is always a top-level
     * directory associated with a Root file, and may or may not be
     * subdirectories within the file.
     */
    @ClassDef(version = 5, hasStandardHeader = false)
    private static class TDirectory implements RootObject {

        private TDatime fDatimeC;
        private TDatime fDatimeF;
        private int fNbytesKeys;
        private int fNbytesName;
        private Pointer fSeekDir;
        private Pointer fSeekParent;
        private Pointer fSeekKeys;
        private static final int version = 5;
        private TUUID fUUID = new TUUID();
        private TKeyList tList = new TKeyList();

        TDirectory(Pointer parent, Pointer self, Pointer keys) {
            fDatimeC = fDatimeF = new TDatime();
            fSeekDir = self;
            fSeekParent = parent;
            fSeekKeys = keys;
        }

        private void write(RootOutput out) throws IOException {
            out.writeShort(version);
            out.writeObject(fDatimeC);
            out.writeObject(fDatimeF);
            out.writeInt(fNbytesKeys);
            out.writeInt(fNbytesName);
            out.writeObject(fSeekDir);
            out.writeObject(fSeekParent);
            out.writeObject(fSeekKeys);
            out.writeObject(fUUID);
            if (!out.isLargeFile()) {
                for (int i = 0; i < 3; i++) {
                    out.writeInt(0);
                }
            }
        }

        private TKeyList getKeyList() {
            return tList;
        }

        private void add(TKey record) {
            tList.add(record);
        }
    }

    @ClassDef(version = 0, hasStandardHeader = false)
    private static class TKeyList implements RootObject {

        private List<RootObject> list = new ArrayList<>();

        private void write(RootOutput out) throws IOException {
            out.writeInt(list.size());
            for (RootObject o : list) {
                out.writeObject(o);
            }
        }

        private void add(TKey record) {
            list.add(record);
        }
    }

    @ClassDef(version = 0, hasStandardHeader = false, suppressTStreamerInfo=true)
    private static class WeirdExtraNameAndTitle implements RootObject {

        private final TString fName;
        private final TString fTitle;

        public WeirdExtraNameAndTitle(TString fName, TString fTitle) {
            this.fName = fName;
            this.fTitle = fTitle;
        }

        private void write(RootOutput out) throws IOException {
            out.writeObject(fName);
            out.writeObject(fTitle);
        }
    }
}
