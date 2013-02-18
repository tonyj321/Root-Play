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
public class TStreamerBasicType extends TStreamerElement {


    TStreamerBasicType(Field f, StreamerInfo i, int type, int size, TString name) {
        super(f, i, type, size, name);
    }

    @Override
    public void write(RootOutput out) throws IOException {
        super.write(out);
    }
}
