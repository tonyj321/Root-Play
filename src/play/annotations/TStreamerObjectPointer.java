package play.annotations;

import java.io.IOException;
import java.lang.reflect.Field;
import play.RootOutput;
import play.TFile.TString;

/**
 *
 * @author tonyj
 */
@RootClass(version=2)
public class TStreamerObjectPointer extends TStreamerElement {
    
    TStreamerObjectPointer(Field f, StreamerInfo i, StreamerInfo.Type type, int size, TString name) {
        super(f, i, type, size, name);
    }
    private void write(RootOutput out) throws IOException {
    }
}
