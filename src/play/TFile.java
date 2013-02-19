package play;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import play.annotations.RootClass;
import play.annotations.StreamerInfo;
import play.annotations.StreamerInfoException;
import play.annotations.TStreamerInfo;
import play.annotations.Utilities;

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
//    private final TKey seekInfoRecord;

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
        topLevelDirectory.fNbytesName = 32 + 2 * fName.length() + 2 * fTitle.length();
        topLevelRecord.add(new WeirdExtraNameAndTitle(fName, fTitle));
        topLevelRecord.add(topLevelDirectory);
        seekKeysRecord.add(topLevelDirectory.getKeyList());

        fSeekInfo = new Pointer(0);
        //Temporarily hard-wire list of TStreamerInfos to be written into fSeekInfo record
//        try {
//            tFile = new TString("TList");
//            fName = new TString("StreamerInfo");
//            fTitle = new TString("Doubly linked list");
//            seekInfoRecord = new TKey(tFile, fName, fTitle, new Pointer(fBEGIN));
//            fSeekInfo = seekInfoRecord.getSeekKey();
//            TList<TStreamerInfo> list = new TList<>();
//            seekInfoRecord.add(list);
//            list.add(Utilities.getStreamerInfo(TAttAxis.class));
////            list.add(Utilities.getStreamerInfo(TAttFill.class));
//            list.add(Utilities.getStreamerInfo(TAttLine.class));
//            list.add(Utilities.getStreamerInfo(TAttMarker.class));
//            list.add(Utilities.getStreamerInfo(TAxis.class));
//            list.add(Utilities.getStreamerInfo(TH1.class));
//            list.add(Utilities.getStreamerInfo(TH1D.class));
//            list.add(Utilities.getStreamerInfo(TList.class));
//            list.add(Utilities.getStreamerInfo(TSeqCollection.class));
//            list.add(Utilities.getStreamerInfo(TCollection.class));
//            list.add(Utilities.getStreamerInfo(TNamed.class));
//            list.add(Utilities.getStreamerInfo(TObject.class));
//            list.add(Utilities.getStreamerInfo(TString.class));
//        } catch (StreamerInfoException ex) {
//            throw new IOException(ex);
//        }

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
//        seekInfoRecord.writeRecord(out);
//        fNbytesInfo.set(seekInfoRecord.size);
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
     * @see <a href="http://google.com">http://google.com</a>
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
     * @param className
     * @param fName
     * @param fTitle
     */
    public void add(RootObject object) {
        TString className = new TString(Utilities.getClassInfo(object.getClass()).getName());
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

    /**
     * A class representing a record within the root file.
     */
    @RootClass(version=0, hasStandardHeader=false)
    private static class TKey implements RootObject {

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
            // Write all the objects associated with this record into a new DataBuffer
            RootBufferedOutputStream buffer = new RootBufferedOutputStream();
            for (RootObject object : objects) {
                buffer.writeObject(object);
            }
            buffer.close();
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
            buffer.writeTo(out);
            long endPos = out.getFilePointer();
            objLen = (int) (endPos - dataPos);
            size = (int) (endPos - seekKey);
            keyLen = (int) (dataPos - seekKey);
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
        @Override
        public void write(RootOutput out) throws IOException {
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
    @RootClass(version=0, hasStandardHeader=false)
    private static class Pointer implements RootObject {

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

        @Override
        public void write(RootOutput out) throws IOException {
            if (out.isLargeFile()) {
                out.writeLong(value);
            } else {
                out.writeInt((int) value);
            }

        }
    }

    /**
     * A root universal unique identifier written into the TFile header, and
     * each TDirectory within the file. This implementation uses the java
     * built-in UUID support which may or may not be strictly compatible with
     * Root's expected definition of the UUID.
     */
    @RootClass(version=1, hasStandardHeader=false)
    private static class TUUID implements RootObject {

        private UUID uuid = UUID.randomUUID();
        private final static int version = 1;

        @Override
        public void write(RootOutput out) throws IOException {
            out.writeShort(version);
            out.writeLong(uuid.getMostSignificantBits());
            out.writeLong(uuid.getLeastSignificantBits());
        }
    }

    @RootClass(version=0, hasStandardHeader=false)
    public static class TString implements RootObject {

        private String string;
        private static final TString empty = new TString("");

        public TString(String string) {
            this.string = string;
        }

        public static TString empty() {
            return empty;
        }

        @Override
        public void write(RootOutput out) throws IOException {
            byte[] chars = string.getBytes();
            int l = chars.length;
            if (l < 255) {
                out.writeByte(l);
            } else {
                out.writeByte(-1);
                out.writeInt(l);
            }
            out.write(chars);
        }

        int length() {
            int l = string.getBytes().length;
            return l < 255 ? l + 1 : l + 5;
        }
    }

    /**
     * Represents a directory within a root file. There is always a top-level
     * directory associated with a Root file, and may or may not be
     * subdirectories within the file.
     */
    @RootClass(version=5,hasStandardHeader=false)
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

        @Override
        public void write(RootOutput out) throws IOException {
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
    @RootClass(version=0, hasStandardHeader=false)
    private static class TKeyList implements RootObject {

        private List<RootObject> list = new ArrayList<>();

        @Override
        public void write(RootOutput out) throws IOException {
            out.writeInt(list.size());
            for (RootObject o : list) {
                out.writeObject(o);
            }
        }

        private void add(TKey record) {
            list.add(record);
        }
    }

    @RootClass(version = 3)
    public static class TObjArray<A extends RootObject> extends TSeqCollection<A> implements RootObject {

        private int fLowerBound = 0;
        private int fLast = 0;
        
        @Override
        public void write(RootOutput out) throws IOException {
            super.write(out);
            out.writeInt(fLowerBound);
            out.writeInt(fLast);
        }
    }
    
    @RootClass(version = 5)
    public static class TList<A extends RootObject> extends TSeqCollection<A> implements RootObject {

        @Override
        public void write(RootOutput out) throws IOException {
            super.write(out);
        }
    }

    @RootClass(version = 0)
    private static class TSeqCollection<A extends RootObject> extends TCollection<A> {
    }

    @RootClass(version = 3)
    private static class TCollection<A extends RootObject> extends TObject {

        @StreamerInfo("name of the collection")
        private TString name = TString.empty();
        @StreamerInfo("number of elements in the collection")
        private List<A> list = new ArrayList<>();

        @Override
        public void write(RootOutput out) throws IOException {
            super.write(out);
            out.writeObject(name);
            out.writeInt(list.size());
            for (RootObject o : list) {
                out.writeObjectRef(o);
            }
        }

        public void add(A record) {
            list.add(record);
        }
    }
    @RootClass(version=0,hasStandardHeader=false)
    private static class WeirdExtraNameAndTitle implements RootObject {

        private final TString fName;
        private final TString fTitle;

        public WeirdExtraNameAndTitle(TString fName, TString fTitle) {
            this.fName = fName;
            this.fTitle = fTitle;
        }

        @Override
        public void write(RootOutput out) throws IOException {
            out.writeObject(fName);
            out.writeObject(fTitle);
        }
    }

    @RootClass(version = 1, title = "Basic ROOT object", hasStandardHeader=false)
    public static class TObject implements RootObject {

        private final static int fUniqueID = 0;
        private int fBits = 0x03000000;
        private final static int version = 1;

        @Override
        public void write(RootOutput out) throws IOException {
            out.writeShort(version);
            out.writeInt(fUniqueID);
            out.writeInt(fBits);
        }

        @Override
        public String toString() {
            return "TObject{" + "fBits=" + fBits + '}';
        }
    }

    @RootClass(version=1)
    public static class TNamed extends TObject {

        private TString name;
        private TString title;

        public TNamed(TString name, TString title) {
            this.name = name;
            this.title = title;
        }

        @Override
        public void write(RootOutput out) throws IOException {
            super.write(out);
            out.writeObject(name);
            out.writeObject(title);
        }

        public void setTitle(TString title) {
            this.title = title;
        }

        private TString getName() {
            return name;
        }

        private TString getTitle() {
            return title;
        }
    }

    @RootClass(version = 1)
    static class TAttLine implements RootObject {

        @StreamerInfo("line color")
        private short fLineColor = 1;
        @StreamerInfo("line style")
        private short fLineStyle = 1;
        @StreamerInfo("line width")
        private short fLineWidth = 1;
        private final static int version = 1;

        @Override
        public void write(RootOutput out) throws IOException {
            out.writeShort(fLineColor);
            out.writeShort(fLineStyle);
            out.writeShort(fLineWidth);
        }
    }

    @RootClass(version = 1)
    static class TAttFill implements RootObject {

        @StreamerInfo("fill area color")
        private short fFillColor = 0;
        @StreamerInfo("fill area style")
        private short fFillStyle = 1001;

        @Override
        public void write(RootOutput out) throws IOException {
            out.writeShort(fFillColor);
            out.writeShort(fFillStyle);
        }
    }

    @RootClass(version = 2)
    static class TAttMarker implements RootObject {

        @StreamerInfo("Marker color index")
        private short fMarkerColor = 1;
        @StreamerInfo("Marker style")
        private short fMarkerStyle = 1;
        @StreamerInfo("Marker size")
        private float fMarkerSize = 1;

        @Override
        public void write(RootOutput out) throws IOException {
            out.writeShort(fMarkerColor);
            out.writeShort(fMarkerStyle);
            out.writeFloat(fMarkerSize);
        }
    }

    @RootClass(version = 4)
    public static class TAttAxis implements RootObject {

        @StreamerInfo("Number of divisions(10000*n3 + 100*n2 + n1)")
        private int fNdivisions = 510;
        @StreamerInfo("color of the line axis")
        private short fAxisColor = 1;
        @StreamerInfo("color of labels")
        private short fLabelColor = 1;
        @StreamerInfo("font for labels")
        private short fLabelFont = 62;
        @StreamerInfo("offset of labels")
        private float fLabelOffset = 0.005f;
        @StreamerInfo("size of labels")
        private float fLabelSize = 0.04f;
        @StreamerInfo("length of tick marks")
        private float fTickLength = 0.03f;
        @StreamerInfo("offset of axis title")
        private float fTitleOffset = 1.0f;
        @StreamerInfo("size of axis title")
        private float fTitleSize = 0.04f;
        @StreamerInfo("color of axis title")
        private short fTitleColor = 1;
        @StreamerInfo("font for axis title")
        private short fTitleFont = 62;
        private final static int version = 4;

        @Override
        public void write(RootOutput out) throws IOException {
            out.writeInt(fNdivisions);
            out.writeShort(fAxisColor);
            out.writeShort(fLabelColor);
            out.writeShort(fLabelFont);
            out.writeFloat(fLabelOffset);
            out.writeFloat(fLabelSize);
            out.writeFloat(fTickLength);
            out.writeFloat(fTitleOffset);
            out.writeFloat(fTitleSize);
            out.writeShort(fTitleColor);
            out.writeShort(fTitleFont);
        }
    }

    @RootClass(version = 9)
    public static class TAxis extends TNamed {

        @StreamerInfo(value = "Axis Attributes", type = "BASE")
        private TAttAxis tAttAxis = new TAttAxis();
        @StreamerInfo("Number of bins")
        private int fNbins;
        @StreamerInfo("low edge of first bin")
        private double fXmin;
        @StreamerInfo("upper edge of last bin")
        private double fXmax;
        @StreamerInfo("Bin edges array in X")
        private TArrayD fXbins;
        @StreamerInfo("first bin to display")
        private int fFirst = 0;
        @StreamerInfo("last bin to display")
        private int fLast = 0;
        @StreamerInfo(value = "second bit status word", type = "UShort_t")
        private short fBits2 = 0;
        @StreamerInfo("on/off displaying time values instead of numerics")
        private boolean fTimeDisplay = false;
        @StreamerInfo("Date&time format, ex: 09/12/99 12:34:00")
        private TString fTimeFormat;
        @StreamerInfo(value = "List of labels", type = "Pointer")
        private THashList fLabels;

        TAxis(TString name, int nBins, double xMin, double xMax) {
            super(name, TString.empty());
            this.fNbins = nBins;
            this.fXmin = xMin;
            this.fXmax = xMax;
        }

        @Override
        public void write(RootOutput out) throws IOException {
            super.write(out);
            out.writeObject(tAttAxis);
            out.writeInt(fNbins);
            out.writeDouble(fXmin);
            out.writeDouble(fXmax);
            out.writeObject(fXbins);
            out.writeInt(fFirst);
            out.writeInt(fLast);
            out.writeShort(fBits2);
            out.writeShort(fTimeDisplay ? 1 : 0); // TODO: Check this
            out.writeObject(fTimeFormat);
            //out.writeObject(fLabels);
        }
    }

    @RootClass(version=0, hasStandardHeader=false)
    static class TArrayD implements RootObject {

        private double[] fArray;

        TArrayD(double[] array) {
            this.fArray = array;
        }

        @Override
        public void write(RootOutput out) throws IOException {
            out.writeInt(fArray.length);
            for (double d : fArray) {
                out.writeDouble(d);
            }
        }
    }
    @RootClass(version=0)
    static class THashList extends TUnimplemented {
    }

    /**
     * A base class for object which could be implemented one day, but are
     * currently never instantiated (i.e. the references are always null)
     */
    static class TUnimplemented implements RootObject {

        @Override
        public void write(RootOutput out) throws IOException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @RootClass(version = 6)
    static class TH1 extends TNamed {

        @StreamerInfo(value = "Line Attributes", type = "BASE")
        private TAttLine tAttLine = new TAttLine();
        @StreamerInfo(value = "Fill area Attributes", type = "BASE")
        private TAttFill tAttFill = new TAttFill();
        @StreamerInfo(value = "Marker Attributes", type = "BASE")
        private TAttMarker tAttMarker = new TAttMarker();
        @StreamerInfo("number of bins(1D), cells (2D) +U/Overflows")
        private int fNcells;
        @StreamerInfo("X axis descriptor")
        private TAxis fXaxis;
        @StreamerInfo("Y axis descriptor")
        private TAxis fYaxis;
        @StreamerInfo("Z axis descriptor")
        private TAxis fZaxis;
        @StreamerInfo("(1000*offset) for bar charts or legos")
        private short fBarOffset = 0;
        @StreamerInfo("(1000*width) for bar charts or legos")
        private short fBarWidth = 1000;
        @StreamerInfo("Number of entries")
        private double fEntries = 0;
        @StreamerInfo("Total Sum of weights")
        private double fTsumw = 0;
        @StreamerInfo("Total Sum of squares of weights")
        private double fTsumw2 = 0;
        @StreamerInfo("Total Sum of weight*X")
        private double fTsumwx = 0;
        @StreamerInfo("Total Sum of weight*X*X")
        private double fTsumwx2 = 0;
        @StreamerInfo("Maximum value for plotting")
        private double fMaximum = -1111;
        @StreamerInfo("Minimum value for plotting")
        private double fMinimum = -1111;
        @StreamerInfo("Normalization factor")
        private double fNormFactor = 0;
        @StreamerInfo("Array to display contour levels")
        private TArrayD fContour;
        @StreamerInfo("Array of sum of squares of weights")
        private TArrayD fSumw2;
        @StreamerInfo("histogram options")
        private TString fOption = TString.empty();
        @StreamerInfo(value = "Pointer to list of functions (fits and user)", type = "POINTER")
        private TList fFunctions = new TList();
        @StreamerInfo("fBuffer size")
        private int fBufferSize = 0;
        @StreamerInfo(value = "entry buffer", size = "fBufferSize")
        private int[] fBuffer = null;
        @StreamerInfo("WTF?")
        private EBinErrorOpt fBinStatErrOpt = EBinErrorOpt.kNormal;

        private enum EBinErrorOpt {

            kNormal, // errors with Normal (Wald) approximation: errorUp=errorLow= sqrt(N)
            kPoisson, // errors from Poisson interval at 68.3% (1 sigma)
            kPoisson2   // errors from Poisson interval at 95% CL (~ 2 sigma)            
        };

        TH1(TString name, int nBins, double xMin, double xMax) {
            super(name, TString.empty);
            fXaxis = new TAxis(new TString("xaxis"), nBins, xMin, xMax);
            fYaxis = new TAxis(new TString("yaxis"), 1, 0, 1);
            fZaxis = new TAxis(new TString("zAxis"), 1, 0, 1);
            fNcells = nBins + 2;
        }

        @Override
        public void write(RootOutput out) throws IOException {
            super.write(out);
            out.writeObject(tAttLine);
            out.writeObject(tAttFill);
            out.writeObject(tAttMarker);
            out.writeInt(fNcells);
            out.writeObject(fXaxis);
            out.writeObject(fYaxis);
            out.writeObject(fZaxis);
            out.writeShort(fBarOffset);
            out.writeShort(fBarWidth);
            out.writeDouble(fEntries);
            out.writeDouble(fTsumw);
            out.writeDouble(fTsumw2);
            out.writeDouble(fTsumwx);
            out.writeDouble(fTsumwx2);
            out.writeDouble(fMaximum);
            out.writeDouble(fMinimum);
            out.writeDouble(fNormFactor);
            out.writeObject(fContour);
            out.writeObject(fSumw2);
            out.writeObject(fOption);
            out.writeObject(fFunctions);
            out.writeInt(fBufferSize);
            for (int i = 0; i < fBufferSize; i++) {
                out.writeInt(fBuffer[i]);
            }
            out.writeByte(fBinStatErrOpt.ordinal());
        }

        public void setEntries(double fEntries) {
            this.fEntries = fEntries;
        }

        public void setSumw(double fTsumw) {
            this.fTsumw = fTsumw;
        }

        public void setSumw2(double fTsumw2) {
            this.fTsumw2 = fTsumw2;
        }

        public void setSumx(double fTsumx) {
            this.fTsumwx = fTsumx;
        }

        public void setSumx2(double fTsumx2) {
            this.fTsumwx2 = fTsumx2;
        }
    }

    @RootClass(version = 1)
    static class TH1D extends TH1 {

        @StreamerInfo("Array of doubles")
        private TArrayD array;

        TH1D(TString name, int nBins, double xMin, double xMax, double[] data) {
            super(name, nBins, xMin, xMax);
            array = new TArrayD(data);
        }

        @Override
        public void write(RootOutput out) throws IOException {
            //FIXME: This does not work, because no longer writes the header!
            super.write(out);
            out.writeObject(array);
        }
    }
}
