package play.annotations;

import play.TFile.TList;
import play.TFile.TNamed;
import play.TFile.TString;

/**
 *
 * @author tonyj
 */
public class TStreamerInfo extends TNamed {
    private int fClassVersion;
    private int fCheckSum;
    private TList<TStreamerElement> fElements = new TList<>();

    TStreamerInfo(Class c, RootClass rootClass) {
        super(new TString(getClassName(rootClass,c)),new TString(rootClass.title()));
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
    public String toString() {
        return "TStreamerInfo{" + "fClassVersion=" + fClassVersion + ", fCheckSum=" + fCheckSum + ", fElements=" + fElements + '}';
    }
    
}
