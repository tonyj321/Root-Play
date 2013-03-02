package play.classes;

import java.io.IOException;
import play.RootOutput;
import play.annotations.ClassDef;
import play.annotations.StreamerInfo;

/**
 * Collectable string class.
 * @see <a href="http://root.cern.ch/root/htmldoc/TObjString.html">TObjString</a>
 * @author tonyj
 * 
 */
@ClassDef( version = 1 )
public class TObjString extends TObject {

    @StreamerInfo("wrapped TString")
     TString fString;

    public TObjString(String string) {
        this.fString = new TString(string);
    }

    private void write(RootOutput out) throws IOException {
        out.writeObject(fString);
    }
}
