package play.classes.hist;

import java.io.IOException;
import play.RootOutput;
import play.annotations.ClassDef;
import play.annotations.StreamerInfo;
import play.Type;
import play.classes.TArrayD;
import play.classes.THashList;
import play.classes.TNamed;
import play.classes.TString;

/**
 * This class manages histogram axis.
 *
 * @see <a href="http://root.cern.ch/root/htmldoc/TAxis.html">TAxis</a>
 * @author tonyj
 */
@ClassDef(version = 9, checkSum = 2116140609)
public class TAxis extends TNamed {

    @StreamerInfo(value = "Axis Attributes", type = Type.kBase)
    private TAttAxis tAttAxis = new TAttAxis();
    @StreamerInfo(value = "Number of bins")
    int fNbins;
    @StreamerInfo(value = "low edge of first bin")
    double fXmin;
    @StreamerInfo(value = "upper edge of last bin")
    double fXmax;
    @StreamerInfo(value = "Bin edges array in X")
    private TArrayD fXbins;
    @StreamerInfo(value = "first bin to display")
    private int fFirst = 0;
    @StreamerInfo(value = "last bin to display")
    private int fLast = 0;
    @StreamerInfo(value = "second bit status word", type = Type.kUShort)
    private short fBits2 = 0;
    @StreamerInfo(value = "on/off displaying time values instead of numerics")
    private boolean fTimeDisplay = false;
    @StreamerInfo(value = "Date&time format, ex: 09/12/99 12:34:00")
    private TString fTimeFormat;
    @StreamerInfo(value = "List of labels", type = Type.kObjectP)
    private THashList fLabels;

    TAxis(TString name, int nBins, double xMin, double xMax) {
        super(name, TString.empty());
        this.fNbins = nBins;
        this.fXmin = xMin;
        this.fXmax = xMax;
    }

    private void write(RootOutput out) throws IOException {
        out.writeObject(tAttAxis);
        out.writeInt(fNbins);
        out.writeDouble(fXmin);
        out.writeDouble(fXmax);
        out.writeObject(fXbins);
        out.writeInt(fFirst);
        out.writeInt(fLast);
        out.writeShort(fBits2);
        out.writeShort(fTimeDisplay ? 1 : 0); // TODO: Check this
        out.writeObject(fTimeFormat);
        //out.writeObject(fLabels);
    }
}
