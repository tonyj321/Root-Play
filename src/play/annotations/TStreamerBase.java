package play.annotations;

import play.TFile.TString;

/**
 *
 * @author tonyj
 */
class TStreamerBase extends TStreamerElement {
    private static final TString BASE = new TString("BASE");

    private int fBaseVersion;
    public TStreamerBase(Class s, RootClass rootClass) {
        super(s, rootClass, 66, 0, BASE);
        fBaseVersion = rootClass.version();
    }  
}
