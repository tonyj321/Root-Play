package play;

import java.io.IOException;
import play.annotations.ClassDef;

/**
 *
 * @author tonyj
 */
@ClassDef(version = 2)
class TStreamerObjectAny extends TStreamerElement {

    TStreamerObjectAny(StreamerFieldInfo field) {
        super(field);
    }

    private void write(RootOutput out) throws IOException {
    }
}
