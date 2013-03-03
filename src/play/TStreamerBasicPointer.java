package play;

import java.io.IOException;
import play.annotations.ClassDef;
import play.annotations.Title;
import play.classes.TString;

/**
 *
 * @author tonyj
 */
@ClassDef(version=2)
class TStreamerBasicPointer extends TStreamerElement {

    @Title("version number of the class with the counter")
    private int      fCountVersion;
    @Title("name of data member holding the array count")
    private TString  fCountName;
    @Title("name of the class with the counter")
    private TString  fCountClass;
   
    TStreamerBasicPointer(StreamerFieldInfo field) {
        super(field);
        fCountName = new TString(field.getCountName());
        fCountClass = new TString(field.getCountClass());
        fCountVersion = field.getCountVersion();
    }

    private void write(RootOutput out) throws IOException {
        out.writeInt(fCountVersion);
        out.writeObject(fCountName);
        out.writeObject(fCountClass);
    }
}
