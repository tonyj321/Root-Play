package play.streamerinfo;

import play.TFile.TNamed;
import play.TFile.TString;

/**
 *
 * @author tonyj
 */
public class TStreamerElement extends TNamed {
    
    private int fType;
    private int fSize;
    private int fArrayLength = 0;
    private int fArrayDim = 0;
    private int[] fMaxIndex;
    private TString fTypeName;
    
    TStreamerElement(TString name, TString title) {
        super(name,title);
    }
    
}
