package play.annotations;

import java.io.IOException;
import play.RootOutput;
import play.TFile.TList;
import play.TFile.TNamed;
import play.TFile.TString;

/**
 *
 * @author tonyj
 */
public class TStreamerInfo extends TNamed {

    private static int version = 9;
    private int fClassVersion;
    private int fCheckSum;
    private TList<TStreamerElement> fElements = new TList<>();

    TStreamerInfo(Class c, RootClass rootClass) {
        super(new TString(getClassName(rootClass, c)), new TString(rootClass.title()));
        fClassVersion = rootClass.version();
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

    @Override
    public void write(RootOutput out) throws IOException {
        out.writeInt(0x40000000 | myLength(out));
        out.writeShort(version);
        super.write(out);
        out.writeInt(fClassVersion);
        out.writeInt(fCheckSum);
    }

    @Override
    public int length(RootOutput out) throws IOException {
        return 4 + myLength(out);
    }

    private int myLength(RootOutput out) throws IOException {
        return 10+super.length(out);
    }

    @Override
    public String toString() {
        return "TStreamerInfo{" + "fClassVersion=" + fClassVersion + ", fCheckSum=" + fCheckSum + ", fElements=" + fElements + '}';
    }
}
