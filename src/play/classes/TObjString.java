package play.classes;

import java.io.IOException;
import play.RootOutput;
import play.annotations.ClassDef;
import play.annotations.Title;

/**
 * Collectable string class.
 *
 * @see <a
 * href="http://root.cern.ch/root/htmldoc/TObjString.html">TObjString</a>
 * @author tonyj
 *
 */
@ClassDef( version = 1)
public class TObjString extends TObject {

    @Title("wrapped TString")
    String fString;

    public TObjString(String string) {
        this.fString = string;
    }

    private void write(RootOutput out) throws IOException {
        out.writeObject(fString);
    }
}
