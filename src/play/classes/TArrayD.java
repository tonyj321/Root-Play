package play.classes;

import java.io.IOException;
import play.RootObject;
import play.RootOutput;
import play.annotations.ClassDef;
import play.annotations.Title;

/**
 * An array of doubles.
 * @see <a href="http://root.cern.ch/root/htmldoc/TArrayD.html">TArrayD</a>
 * @author tonyj
 */
@ClassDef(version = 1, hasStandardHeader = false, suppressTStreamerInfo = true)
@Title("Array of doubles")
public class TArrayD implements RootObject {
    private double[] fArray;

    public TArrayD(double[] array) {
        this.fArray = array;
    }

    private void write(RootOutput out) throws IOException {
        out.writeInt(fArray.length);
        for (double d : fArray) {
            out.writeDouble(d);
        }
    }
    
}
