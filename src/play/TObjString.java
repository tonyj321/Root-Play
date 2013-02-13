package play;

import java.io.IOException;

/**
 *
 * @author tonyj
 */
class TObjString extends TFile.TObject {

    private TFile.TString string;
    private final static int version = 1;

    TObjString(String string) {
        this.string = new TFile.TString(string);
    }

    @Override
    public void write(RootOutput out) throws IOException {
        out.writeShort(version);
        super.write(out);
        out.writeObject(string);
    }

    @Override
    public int length(RootOutput out) throws IOException {
        return 2+super.length(out)+string.length(out);
    }
    
}
