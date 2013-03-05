package play;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import play.annotations.ClassDef;
import play.classes.TDatime;
import play.classes.TNamed;
import play.classes.TString;
import play.classes.TUUID;

/**
 * Represents a directory within a root file. There is always a top-level
 * directory associated with a Root file, and may or may not be subdirectories
 * within the file.
 */
@ClassDef(version = 5, hasStandardHeader = false, suppressTStreamerInfo = true)
public class TDirectory extends TNamed {

    static Date timeWarp;
    static UUID uuidWarp;
    private TDatime fDatimeC;
    private TDatime fDatimeF;
    private int fNbytesKeys;
    int fNbytesName;
    Pointer fSeekDir;
    private Pointer fSeekParent;
    // The record containing this directory
    private TKey directoryRecord;
    // This is the record that contains the keys for this directory.
    // For the TFile this is written at fSeekKeys
    private TKey seekKeysRecord;
    Pointer fSeekKeys;
    TUUID fUUID = new TUUID(uuidWarp);
    private KeyList keyList = new KeyList();

    TDirectory(String name, String title, Pointer self, Pointer parent) {
        super(name, title);
        fDatimeC = fDatimeF = new TDatime(timeWarp);
        fSeekDir = self;
        fSeekParent = Pointer.ZERO;
    }
    /**
     * Each directory has two records in the corresponding TFile, one containing
     * the TDirectory itself, and the other containing the key list.
     * @param tFile The TFile in which the records will be created.
     */
    void addOwnRecords(TFile tFile) {
        directoryRecord = tFile.addRecord("TFile", getName(), getTitle(), Pointer.ZERO, true);
        fNbytesName = 32 + 2 * TString.sizeOnDisk(getName()) + 2 * TString.sizeOnDisk(getTitle());
        directoryRecord.add(this);
        seekKeysRecord = tFile.addKeyListRecord("TFile", getName(), getTitle(), fSeekDir, true);
        seekKeysRecord.add(keyList);
        fSeekKeys = seekKeysRecord.getSeekKey();
    }
    /**
     * Add an object to a directory. Note that this just registers the object
     * with the file, the data is not extracted from the object and written to
     * disk until flush() or close() is called.
     *
     * @param object The object to be written to disk.
     */
    public void add(Object object) throws IOException {
        String className = StreamerUtilities.getClassInfo(object.getClass()).getName();
        String fName, fTitle;
        if (object instanceof TNamed) {
            TNamed tNamed = (TNamed) object;
            fName = tNamed.getName();
            fTitle = tNamed.getTitle();
        } else {
            fName = className;
            fTitle = "";
        }
        TKey record = ((TFile) this).addRecord(className, fName, fTitle, fSeekDir, false);
        record.add(object);
        keyList.add(record);
    }

    void streamer(RootOutput out) throws IOException {
        fNbytesKeys = seekKeysRecord.size;
        out.writeShort(TDirectory.class.getAnnotation(ClassDef.class).version());
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

    @ClassDef(hasStandardHeader = false)
    private static class KeyList {

        private ArrayList<TKey> list = new ArrayList<>();

        private void write(RootOutput out) throws IOException {
            out.writeInt(list.size());
            for (TKey o : list) {
                out.writeObject(o);
            }
        }

        private void add(TKey record) {
            list.add(record);
        }
    }
}
