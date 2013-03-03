package play.classes;

import java.io.IOException;
import play.RootObject;
import play.RootOutput;
import play.Type;
import play.annotations.ClassDef;
import play.annotations.FieldType;
import play.annotations.Title;

/**
 * Mother of all ROOT objects.
 * @see <a href="http://root.cern.ch/root/htmldoc/TObject.html">TObject</a>
 * @author tonyj
 */
@ClassDef(version = 1, checkSum = 1389979441, hasStandardHeader = false)
@Title("Basic ROOT object")
public class TObject implements RootObject {
    
    @Title("object unique identifier")
    @FieldType(Type.kUInt)
    private final int fUniqueID = 0;
    @Title("bit field status word")
    @FieldType(Type.kUInt)
    private int fBits = 0x03000000;
    private static final int version = 1;

    private void write(RootOutput out) throws IOException {
        out.writeShort(version);
        out.writeInt(fUniqueID);
        out.writeInt(fBits);
    }

    @Override
    public String toString() {
        return "TObject{" + "fBits=" + fBits + '}';
    }
    
}
