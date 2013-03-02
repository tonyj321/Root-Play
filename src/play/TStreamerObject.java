package play;

import java.io.IOException;
import java.lang.reflect.Field;
import play.RootOutput;
import play.annotations.ClassDef;
import play.annotations.StreamerInfo;
import play.classes.TString;

/**
 *
 * @author tonyj
 */
@ClassDef(version=2)
class TStreamerObject extends TStreamerElement {
    
    TStreamerObject(Field f, StreamerInfo i, Type type, int size, TString name) {
        super(f, i, type, size, name);
    }
    private void write(RootOutput out) throws IOException {
    }
}
