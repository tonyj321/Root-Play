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
    private TFile.TString fString;

    TObjString(String string) {
        this.fString = new TFile.TString(string);
    }

    private void write(RootOutput out) throws IOException {
        out.writeObject(fString);
    }
}
