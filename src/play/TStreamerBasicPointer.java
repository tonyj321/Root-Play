package play;

import java.io.IOException;
import play.annotations.ClassDef;
import play.annotations.Title;

/**
 *
 * @author tonyj
 */
@ClassDef(version=2)
class TStreamerBasicPointer extends TStreamerElement {

    @Title("version number of the class with the counter")
    private int      fCountVersion;
    @Title("name of data member holding the array count")
    private String  fCountName;
    @Title("name of the class with the counter")
    private String  fCountClass;
   
    TStreamerBasicPointer(StreamerFieldInfo field) {
        super(field);
        fCountName = field.getCountName();
        fCountClass = field.getCountClass();
        fCountVersion = field.getCountVersion();
    }

    private void write(RootOutput out) throws IOException {
        out.writeInt(fCountVersion);
        out.writeObject(fCountName);
        out.writeObject(fCountClass);
    }
}
