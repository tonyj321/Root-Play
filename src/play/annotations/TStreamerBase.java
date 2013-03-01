package play.annotations;

import java.io.IOException;
import java.lang.reflect.Field;
import play.RootOutput;
import play.TFile.TString;

/**
 *
 * @author tonyj
 */
@RootClass(version=3)
class TStreamerBase extends TStreamerElement {

    private static final TString BASE = new TString("BASE");
    private int fBaseVersion;

    public TStreamerBase(Class c, RootClass rootClass, StreamerInfo.Type type, int size) {
        super(c,rootClass,type,size,BASE);
        fBaseVersion = rootClass.version();
    }

    private void write(RootOutput out) throws IOException {
        out.writeInt(fBaseVersion);
    }
}
