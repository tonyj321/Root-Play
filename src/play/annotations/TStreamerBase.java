package play.annotations;

import java.io.IOException;
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

    public TStreamerBase(Class s, RootClass rootClass) {
        super(s, rootClass, 66, 0, BASE);
        fBaseVersion = rootClass.version();
    }

    @Override
    public void write(RootOutput out) throws IOException {
        out.writeInt(fBaseVersion);
        super.write(out);
    }
}
