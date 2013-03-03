package play;

import java.io.IOException;
import play.annotations.ClassDef;
import play.classes.TNamed;
import play.classes.TObjArray;
import play.classes.TString;

/**
 *
 * @author tonyj
 */
@ClassDef(version = 9)
class TStreamerInfo extends TNamed {

    private int fClassVersion;
    private int fCheckSum;
    private TObjArray<TStreamerElement> fElements = new TObjArray<>();

    TStreamerInfo(StreamerClassInfo info) {
        super(new TString(info.getName()), new TString(info.getTitle()));
        fClassVersion = info.getVersion();
        fCheckSum = info.getCheckSum();
        if (info.getSuperClass()!=null) {
            fElements.add(TStreamerElement.create(info.getSuperClass()));
        }
        for (StreamerFieldInfo sfi : info.getFields()) {
            fElements.add(TStreamerElement.create(sfi));
        }
    }

    private void write(RootOutput out) throws IOException {
        out.writeInt(fCheckSum);
        out.writeInt(fClassVersion);
        out.writeObjectRef(fElements);
    }
}
