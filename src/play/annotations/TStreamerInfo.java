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
@RootClass(version=9)
public class TStreamerInfo extends TNamed {

    private int fClassVersion;
    private int fCheckSum;
    private TObjArray<TStreamerElement> fElements = new TObjArray<>();

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
        super.write(out);
        out.writeInt(fCheckSum);
        out.writeInt(fClassVersion);
    }

    @Override
    public String toString() {
        return "TStreamerInfo{" + "fClassVersion=" + fClassVersion + ", fCheckSum=" + fCheckSum + ", fElements=" + fElements + '}';
    }
}
