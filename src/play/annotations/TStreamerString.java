package play.annotations;

import java.io.IOException;
import java.lang.reflect.Field;
import play.RootOutput;
import play.TFile.TString;

/**
 *
 * @author tonyj
 */
public class TStreamerString extends TStreamerElement {
    public static final TString T_STRING = new TString("TString");
    private final static int version = 2;
    
    TStreamerString(Field f, StreamerInfo info) {
        super(f,info,65,8, T_STRING);
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
