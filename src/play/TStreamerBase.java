package play;

import java.io.IOException;
import play.annotations.ClassDef;

/**
 *
 * @author tonyj
 */
@ClassDef(version=3)
class TStreamerBase extends TStreamerElement {

    private int fBaseVersion;

    public TStreamerBase(StreamerClassInfo info) {
        super(info);
        fBaseVersion = info.getVersion();
    }

    private void write(RootOutput out) throws IOException {
        out.writeInt(fBaseVersion);
    }
}
