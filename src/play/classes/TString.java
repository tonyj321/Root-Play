package play.classes;

import java.io.IOException;
import play.RootObject;
import play.RootOutput;
import play.annotations.ClassDef;

/**
 * Basic string class.
 * @see <a href="http://root.cern.ch/root/htmldoc/TString.html">TString</a>
 * @author tonyj
 */
@ClassDef(version = 0, hasStandardHeader = false)
public class TString implements RootObject {
    private String string;
    static final TString empty = new TString("");

    public TString(String string) {
        this.string = string;
    }

    public static TString empty() {
        return empty;
    }

    private void write(RootOutput out) throws IOException {
        byte[] chars = string.getBytes();
        int l = chars.length;
        if (l < 255) {
            out.writeByte(l);
        } else {
            out.writeByte(-1);
            out.writeInt(l);
        }
        out.write(chars);
    }

    public int sizeOnDisk() {
        int l = string.getBytes().length;
        return l < 255 ? l + 1 : l + 5;
    }

    @Override
    public String toString() {
        return string;
    }
    
}
