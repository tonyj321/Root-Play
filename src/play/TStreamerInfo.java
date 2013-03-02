package play;

import java.io.IOException;
import play.RootOutput;
import play.annotations.ClassDef;
import play.classes.TNamed;
import play.classes.TObjArray;
import play.classes.TString;

/**
 *
 * @author tonyj
 */
@ClassDef(version = 9)
class TStreamerInfo extends TNamed {

    private int fClassVersion;
    private int fCheckSum;
    private TObjArray<TStreamerElement> fElements = new TObjArray<>();

    TStreamerInfo(Class c, ClassDef rootClass) {
        super(new TString(getClassName(rootClass, c)), new TString(rootClass.title()));
        fClassVersion = rootClass.version();
        fCheckSum = rootClass.checkSum();
    }

    static String getClassName(ClassDef rootClass, Class c) {
        String className = rootClass.className();
        if (className.length() == 0) {
            className = c.getSimpleName();
        }
        return className;
    }

    void add(TStreamerElement tStreamerElement) {
        fElements.add(tStreamerElement);
    }

    int getClassVersion() {
        return fClassVersion;
    }

    private void write(RootOutput out) throws IOException {
        if (fCheckSum == 0) {
            fCheckSum = computeCheckSum();
        } else {
            System.out.printf("Checksum comparison class %s set: %d  computed %d\n", getName(), fCheckSum, computeCheckSum());
        }
        out.writeInt(fCheckSum);
        out.writeInt(fClassVersion);
        out.writeObjectRef(fElements);
    }

    TStreamerElement findElementByName(String name) {
        for (TStreamerElement e : fElements) {
            if (name.equals(e.getName().toString())) {
                return e;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "TStreamerInfo{" + "fClassVersion=" + fClassVersion + ", fCheckSum=" + fCheckSum + ", fElements=" + fElements + '}';
    }
    /** 
     * Based on: http://root.cern.ch/root/html/src/TStreamerInfo.cxx.html#erZjI 
     */
    private int computeCheckSum() {
        int id = 0;
        id = checksum(id, getName());
        for (TStreamerElement e : fElements) {
            if (e instanceof TStreamerBase) {
                id = checksum(id, e.getName());
            }
        }
        for (TStreamerElement e : fElements) {
            if (!(e instanceof TStreamerBase)) {
                id = checksum(id, e.getName());
                id = checksum(id, e.getTypeName());
                int dim = e.getArrayDim();
                for (int i = 0; i < dim; i++) {
                    id = id * 3 + e.getMaxIndex(i);
                }
            }
        }
        return id;
    }

    private static int checksum(int id, TString string) {
        final String s = string.toString();
        for (int i = 0; i < s.length(); i++) {
            id = id * 3 + s.charAt(i);
        }
        return id;
    }

    public static void main(String[] args) {
        int id = 0;
//        id = checksum(id, new TString("TH1D"));
//        id = checksum(id, new TString("TH1"));
//        id = checksum(id, new TString("TArrayD"));
        id = checksum(id, new TString("TAttFill"));
        id = checksum(id, new TString("short"));
        id = checksum(id, new TString("fFillColor"));
        id = checksum(id, new TString("short"));
        id = checksum(id, new TString("fFillStyle"));
        System.out.printf("%d\n", id);
    }
}
