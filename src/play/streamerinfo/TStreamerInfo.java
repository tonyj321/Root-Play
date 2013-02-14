package play.streamerinfo;

import play.TFile.TList;
import play.TFile.TNamed;
import play.TFile.TString;

/**
 *
 * @author tonyj
 */
public class TStreamerInfo extends TNamed {
    private int fClassVersion;
    private int fCheckSum;
    private TList<TStreamerInfo> fElements = new TList<>();
    
    TStreamerInfo(TString name, TString title) {
        super(name,title);
    }
    
}
