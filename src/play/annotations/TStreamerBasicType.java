package play.annotations;

import java.lang.reflect.Field;
import play.TFile.TString;

/**
 *
 * @author tonyj
 */
public class TStreamerBasicType extends TStreamerElement {
 

    TStreamerBasicType(Field f, StreamerInfo i, int type, int size, TString name) {
        super(f,i, type, size, name);
    }
}
