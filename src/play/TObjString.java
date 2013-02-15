package play;

import java.io.IOException;
import play.annotations.RootClass;
import play.annotations.StreamerInfo;

/**
 *
 * @author tonyj
 * 
 */
@RootClass( version = 1 )
public class TObjString extends TFile.TObject {

    @StreamerInfo("wrapped TString")
    private TFile.TString string;
    private final static int version = 1;

    TObjString(String string) {
        this.string = new TFile.TString(string);
    }

    @Override
    public void write(RootOutput out) throws IOException {
        out.writeInt(0x40000000 | (length(out)-4));
        out.writeShort(version);
        super.write(out);
        out.writeObject(string);
    }

    @Override
    public int length(RootOutput out) throws IOException {
        return 6+super.length(out)+string.length(out);
    }
    
}
