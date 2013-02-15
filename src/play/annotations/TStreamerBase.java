package play.annotations;

import play.TFile.TString;

/**
 *
 * @author tonyj
 */
class TStreamerBase extends TStreamerElement {

    int fBaseVersion;
    public TStreamerBase(Class s, RootClass rootClass) {
        super(s, rootClass);
        fBaseVersion = rootClass.version();
        setType(66);
        setTypeName(new TString("BASE"));
    }
    
}
