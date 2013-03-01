package play.annotations;

import java.io.IOException;
import play.RootOutput;
import play.TFile.TNamed;
import play.TFile.TObjArray;
import play.TFile.TString;

/**
 *
 * @author tonyj
 */
@RootClass(version = 9)
public class TStreamerInfo extends TNamed {

    private int fClassVersion;
    private int fCheckSum;
    private TObjArray<TStreamerElement> fElements = new TObjArray<>();

    TStreamerInfo(Class c, RootClass rootClass) {
        super(new TString(getClassName(rootClass, c)), new TString(rootClass.title()));
        fClassVersion = rootClass.version();
        fCheckSum = rootClass.checkSum();
    }

    static String getClassName(RootClass rootClass, Class c) {
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
        }
        out.writeInt(fCheckSum);
        out.writeInt(fClassVersion);
        out.writeObjectRef(fElements);
    }

    TStreamerElement findElementByName(String name) {
        for (TStreamerElement e : fElements) {
            if (name.equals(e.getName().getString())) {
                return e;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "TStreamerInfo{" + "fClassVersion=" + fClassVersion + ", fCheckSum=" + fCheckSum + ", fElements=" + fElements + '}';
    }

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

    private int checksum(int id, TString string) {
        final String s = string.getString();
        for (int i = 0; i < s.length(); i++) {
            id = id * 3 + s.charAt(i);
        }
        return id;
    }
}
