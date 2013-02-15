package play.annotations;

import java.lang.reflect.Field;
import play.TFile.TString;

/**
 *
 * @author tonyj
 */
public class TStreamerString extends TStreamerElement {
    public static final TString T_STRING = new TString("TString");
    TStreamerString(Field f, StreamerInfo info) {
        super(f,info,65,8, T_STRING);
    }
}
