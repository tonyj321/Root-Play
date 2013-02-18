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

    TObjString(String string) {
        this.string = new TFile.TString(string);
    }

    @Override
    public void write(RootOutput out) throws IOException {
        super.write(out);
        out.writeObject(string);
    }
}
