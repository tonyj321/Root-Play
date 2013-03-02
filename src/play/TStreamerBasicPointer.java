package play;

import java.io.IOException;
import java.lang.reflect.Field;
import play.RootOutput;
import play.annotations.ClassDef;
import play.annotations.StreamerInfo;
import play.classes.TString;

/**
 *
 * @author tonyj
 */
@ClassDef(version=2)
class TStreamerBasicPointer extends TStreamerElement {

    @StreamerInfo("version number of the class with the counter")
    private int      fCountVersion;
    @StreamerInfo("name of data member holding the array count")
    private TString  fCountName;
    @StreamerInfo("name of the class with the counter")
    private TString  fCountClass;
   
    TStreamerBasicPointer(Field f, StreamerInfo i, Type type, int size, TString name, 
            TString countName, TString countClass, int countVersion) {
        super(f, i, type, size, name);
        fCountName = countName;
        fCountClass = countClass;
        fCountVersion = countVersion;
    }

    private void write(RootOutput out) throws IOException {
        out.writeInt(fCountVersion);
        out.writeObject(fCountName);
        out.writeObject(fCountClass);
    }
}
