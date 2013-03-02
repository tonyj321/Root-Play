package play.classes;

import java.io.IOException;
import play.RootOutput;
import play.annotations.ClassDef;
import play.annotations.StreamerInfo;

/**
 * The TNamed class is the base class for all named ROOT classes.
 * <a href="http://root.cern.ch/root/htmldoc/TNamed.html">TNamed</a>
 * @author tonyj
 */
@ClassDef(version = 1, checkSum = -68599943, title = "The basis for a named object (name, title)")
public class TNamed extends TObject {
    @StreamerInfo(value = "object identifier")
     TString name;
    @StreamerInfo(value = "object title")
     TString title;

    public TNamed(TString name, TString title) {
        this.name = name;
        this.title = title;
    }

    private void write(RootOutput out) throws IOException {
        out.writeObject(name);
        out.writeObject(title);
    }

    public void setTitle(TString title) {
        this.title = title;
    }

    public TString getName() {
        return name;
    }

    public TString getTitle() {
        return title;
    }
    
}
