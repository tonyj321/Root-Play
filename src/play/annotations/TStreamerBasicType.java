package play.annotations;

import java.io.IOException;
import java.lang.reflect.Field;
import play.RootOutput;
import play.TFile.TString;

/**
 *
 * @author tonyj
 */
public class TStreamerBasicType extends TStreamerElement {

    private static int version = 2;

    TStreamerBasicType(Field f, StreamerInfo i, int type, int size, TString name) {
        super(f, i, type, size, name);
    }

    @Override
    public void write(RootOutput out) throws IOException {
        out.writeInt(0x40000000 | myLength(out));
        out.writeShort(version);
        super.write(out);
    }

    @Override
    public int length(RootOutput out) throws IOException {
        return 4 + myLength(out);
    }

    private int myLength(RootOutput out) throws IOException {
        return 2 + super.length(out);
    }
}
