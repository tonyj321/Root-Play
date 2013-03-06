package hep.io.root.output;

import java.io.IOException;
import hep.io.root.output.annotations.ClassDef;

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
}
