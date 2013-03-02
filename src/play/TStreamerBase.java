package play;

import java.io.IOException;
import play.annotations.ClassDef;
import play.classes.TString;

/**
 *
 * @author tonyj
 */
@ClassDef(version=3)
class TStreamerBase extends TStreamerElement {

    private static final TString BASE = new TString("BASE");
    private int fBaseVersion;

    public TStreamerBase(Class c, ClassDef rootClass, Type type, int size) {
        super(c,rootClass,type,size,BASE);
        fBaseVersion = rootClass.version();
    }

    private void write(RootOutput out) throws IOException {
        out.writeInt(fBaseVersion);
    }
}
