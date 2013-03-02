package play.classes.hist;

import java.io.IOException;
import play.RootOutput;
import play.annotations.ClassDef;
import play.annotations.StreamerInfo;
import play.Type;
import play.classes.TArrayD;
import play.classes.TList;
import play.classes.TNamed;
import play.classes.TString;

/**
 * The base histogram class.
 * @see <a href="http://root.cern.ch/root/htmldoc/TH1.html">TH1</a>
 * @author tonyj
 */
@ClassDef(version = 6, checkSum = -381522971, title = "1-Dim histogram base class")
public class TH1 extends TNamed {
    @StreamerInfo(value = "Line Attributes", type = Type.kBase)
    private TAttLine tAttLine = new TAttLine();
    @StreamerInfo(value = "Fill area Attributes", type = Type.kBase)
    private TAttFill tAttFill = new TAttFill();
    @StreamerInfo(value = "Marker Attributes", type = Type.kBase)
    private TAttMarker tAttMarker = new TAttMarker();
    @StreamerInfo(value = "number of bins(1D), cells (2D) +U/Overflows")
    private int fNcells;
    @StreamerInfo(value = "X axis descriptor")
    private TAxis fXaxis;
    @StreamerInfo(value = "Y axis descriptor")
    private TAxis fYaxis;
    @StreamerInfo(value = "Z axis descriptor")
    private TAxis fZaxis;
    @StreamerInfo(value = "(1000*offset) for bar charts or legos")
    private short fBarOffset = 0;
    @StreamerInfo(value = "(1000*width) for bar charts or legos")
    private short fBarWidth = 1000;
    @StreamerInfo(value = "Number of entries")
     double fEntries = 0;
    @StreamerInfo(value = "Total Sum of weights")
     double fTsumw = 0;
    @StreamerInfo(value = "Total Sum of squares of weights")
     double fTsumw2 = 0;
    @StreamerInfo(value = "Total Sum of weight*X")
     double fTsumwx = 0;
    @StreamerInfo(value = "Total Sum of weight*X*X")
     double fTsumwx2 = 0;
    @StreamerInfo(value = "Maximum value for plotting")
    private double fMaximum = -1111;
    @StreamerInfo(value = "Minimum value for plotting")
    private double fMinimum = -1111;
    @StreamerInfo(value = "Normalization factor")
    private double fNormFactor = 0;
    @StreamerInfo(value = "Array to display contour levels")
    private TArrayD fContour;
    @StreamerInfo(value = "Array of sum of squares of weights")
    private TArrayD fSumw2;
    @StreamerInfo(value = "histogram options")
    private TString fOption = TString.empty();
    @StreamerInfo(value = "Pointer to list of functions (fits and user)", type = Type.kObjectp)
    private TList fFunctions = new TList();
    @StreamerInfo(value = "fBuffer size")
    private int fBufferSize = 0;
    @StreamerInfo(value = "entry buffer", counter = "fBufferSize")
    private double[] fBuffer = null;
    private EBinErrorOpt fBinStatErrOpt = EBinErrorOpt.kNormal;

    private enum EBinErrorOpt {

        kNormal, // errors with Normal (Wald) approximation: errorUp=errorLow= sqrt(N)
        kPoisson, // errors from Poisson interval at 68.3% (1 sigma)
        kPoisson2 // errors from Poisson interval at 95% CL (~ 2 sigma)
    }

    public TH1(TString name, int nBins, double xMin, double xMax) {
        super(name, TString.empty());
        fXaxis = new TAxis(new TString("xaxis"), nBins, xMin, xMax);
        fYaxis = new TAxis(new TString("yaxis"), 1, 0, 1);
        fZaxis = new TAxis(new TString("zAxis"), 1, 0, 1);
        fNcells = nBins + 2;
    }

    private void write(RootOutput out) throws IOException {
        out.writeObject(tAttLine);
        out.writeObject(tAttFill);
        out.writeObject(tAttMarker);
        out.writeInt(fNcells);
        out.writeObject(fXaxis);
        out.writeObject(fYaxis);
        out.writeObject(fZaxis);
        out.writeShort(fBarOffset);
        out.writeShort(fBarWidth);
        out.writeDouble(fEntries);
        out.writeDouble(fTsumw);
        out.writeDouble(fTsumw2);
        out.writeDouble(fTsumwx);
        out.writeDouble(fTsumwx2);
        out.writeDouble(fMaximum);
        out.writeDouble(fMinimum);
        out.writeDouble(fNormFactor);
        out.writeObject(fContour);
        out.writeObject(fSumw2);
        out.writeObject(fOption);
        out.writeObject(fFunctions);
        out.writeInt(fBufferSize);
        for (int i = 0; i < fBufferSize; i++) {
            out.writeDouble(fBuffer[i]);
        }
        out.writeByte(fBinStatErrOpt.ordinal());
    }

    public void setEntries(double fEntries) {
        this.fEntries = fEntries;
    }

    public void setSumw(double fTsumw) {
        this.fTsumw = fTsumw;
    }

    public void setSumw2(double fTsumw2) {
        this.fTsumw2 = fTsumw2;
    }

    public void setSumx(double fTsumx) {
        this.fTsumwx = fTsumx;
    }

    public void setSumx2(double fTsumx2) {
        this.fTsumwx2 = fTsumx2;
    }
    
}
