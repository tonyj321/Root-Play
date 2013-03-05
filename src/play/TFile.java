package play;

import play.classes.TList;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import play.annotations.ClassDef;

/**
 * Top level class for interacting with a Root file. Currently this
 * implementation only supports writing.
 *
 * @author tonyj
 * @see <a href="http://root.cern.ch/download/doc/11InputOutput.pdf">Root Manual
 * Input/Output</a>
 */
@ClassDef(hasStandardHeader = false, suppressTStreamerInfo = true)
public class TFile extends TDirectory implements Closeable {

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
    // This is the record that is written at fSeekInfo
    private final TKey seekInfoRecord;
    // Collection of TStreamerInfos to be written to the seekInfoRecord;
    private Map<String, TStreamerInfo> streamerInfos = new HashMap<>();
    private static String nameWarp;
    // We don't really need two lists, this is just to keep the tests happy.
    private final List<TKey> dataRecords = new ArrayList<>();
    private final List<TKey> keyRecords = new ArrayList<>();

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

        super(nameWarp == null ? file.getName() : nameWarp, "", new Pointer(fBEGIN), Pointer.ZERO);
        addOwnRecords(this);
        out = new RootRandomAccessFile(file, this);
        seekInfoRecord = new TKey(this, "TList", "StreamerInfo", "Doubly linked list", new Pointer(fBEGIN), true);
        fSeekInfo = seekInfoRecord.getSeekKey();
        TList<TStreamerInfo> list = new TList<>(streamerInfos.values());
        seekInfoRecord.add(list);
    }

    /**
     * Flush any uncommitted data to disk. Currently we do not keep of objects
     * already committed, so we flush everything to disk.
     *
     * @throws IOException
     */
    public void flush() throws IOException {
        out.seek(fBEGIN);
        for (TKey record : dataRecords) {
            record.writeRecord(out);
        }
        seekInfoRecord.writeRecord(out);
        fNbytesInfo.set(seekInfoRecord.size);
        for (TKey record : keyRecords) {
            record.writeRecord(out);
        }
        fEND.set(out.getFilePointer());
        // Rewrite topLevelRecord to get updated fSeekKey pointer
        out.seek(fBEGIN);
        dataRecords.get(0).writeRecord(out);
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
        out.writeInt(fNbytesName);
        out.writeByte(largeFile ? 8 : 4); // Number of bytes for file pointers
        out.writeInt(fCompress);          // Compression level and algorithm
        out.writeObject(fSeekInfo);       // Pointer to TStreamerInfo record
        out.writeObject(fNbytesInfo);     // Number of bytes in TStreamerInfo record
        out.writeObject(fUUID);
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

    TKey addRecord(String className, String fName, String fTitle, Pointer fSeekDir, boolean suppressStreamerInfo) {
        TKey tKey = new TKey(this, className, fName, fTitle, fSeekDir, suppressStreamerInfo);
        dataRecords.add(tKey); 
        return tKey;
    }

    TKey addKeyListRecord(String className, String fName, String fTitle, Pointer fSeekDir, boolean suppressStreamerInfo) {
        TKey tKey = new TKey(this, className, fName, fTitle, fSeekDir, suppressStreamerInfo);
        keyRecords.add(tKey); 
        return tKey;
    }
    
    /**
     * Just for testing, sets all timestamps, UUID's and filenames in the file
     * to arbitrary fixed values, so that the file is reproducible.
     *
     * @param testMode <code>true</code> to set test mode
     */
    public static void setTimeWarp(boolean testMode) {
        if (testMode) {
            timeWarp = new Date(1362336450390L);
            uuidWarp = UUID.fromString("3e3260c7-303a-4ea9-83b9-f43c34c96908");
            nameWarp = "timewarp.root";
        } else {
            timeWarp = null;
            uuidWarp = null;
            nameWarp = null;
        }
    }

    @Override
    void streamer(RootOutput out) throws IOException {
        out.writeObject(getName());
        out.writeObject(getTitle());
        super.streamer(out);
    }
}
