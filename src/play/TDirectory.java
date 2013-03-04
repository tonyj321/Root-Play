package play;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import play.annotations.ClassDef;
import play.classes.TDatime;
import play.classes.TNamed;
import play.classes.TUUID;

/**
 * Represents a directory within a root file. There is always a top-level
 * directory associated with a Root file, and may or may not be
 * subdirectories within the file.
 */
@ClassDef(version = 5, hasStandardHeader = false, suppressTStreamerInfo = true)
public class TDirectory {
    static Date timeWarp;
    static UUID uuidWarp;
    private TDatime fDatimeC;
    private TDatime fDatimeF;
    int fNbytesKeys;
    int fNbytesName;
    Pointer fSeekDir;
    private Pointer fSeekParent;
    Pointer fSeekKeys;
    TUUID fUUID = new TUUID(TFile.uuidWarp);
    private TKeyList tList = new TKeyList();
    final List<TKey> dataRecords = new ArrayList<>();

    TDirectory(Pointer self) {
        fDatimeC = fDatimeF = new TDatime(TFile.timeWarp);
        fSeekDir = self;
        fSeekParent = Pointer.ZERO;
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
        TKey record = new TKey((TFile) this, className, fName, fTitle, fSeekDir, false);
        record.add(object);
        dataRecords.add(record);
        this.add(record);
    }

    private void write(RootOutput out) throws IOException {
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

    TKeyList getKeyList() {
        return tList;
    }

    void add(TKey record) {
        tList.add(record);
    }
    
    @ClassDef(hasStandardHeader = false)
    private static class TKeyList {
        
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
