package play.annotations;

import java.lang.reflect.Field;
import play.TFile.TString;

/**
 *
 * @author tonyj
 */
public class TStreamerString extends TStreamerElement {
    TStreamerString(Field f, StreamerInfo info) {
        super(f,info);
        setType(65);
        setTypeName(new TString("TString"));
        setSize(8);
    }
}
