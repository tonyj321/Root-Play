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
public class TStreamerString extends TStreamerElement {
    public static final TString T_STRING = new TString("TString");
    
    TStreamerString(Field f, StreamerInfo info) {
        super(f,info,65,8, T_STRING);
    }
    @Override
    public void write(RootOutput out) throws IOException {
        super.write(out);
    }
}
