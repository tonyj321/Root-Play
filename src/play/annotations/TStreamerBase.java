package play.annotations;

import java.io.IOException;
import play.RootOutput;
import play.TFile.TString;

/**
 *
 * @author tonyj
 */
class TStreamerBase extends TStreamerElement {

    private static final int version = 3;
    private static final TString BASE = new TString("BASE");
    private int fBaseVersion;

    public TStreamerBase(Class s, RootClass rootClass) {
        super(s, rootClass, 66, 0, BASE);
        fBaseVersion = rootClass.version();
    }

    @Override
    public void write(RootOutput out) throws IOException {
        out.writeInt(0x40000000 | myLength(out));
        out.writeShort(version);
        out.writeInt(fBaseVersion);
        super.write(out);
    }

    @Override
    public int length(RootOutput out) throws IOException {
        return 4 + myLength(out);
    }

    private int myLength(RootOutput out) throws IOException {
        return 6 + super.length(out);
    }
}
