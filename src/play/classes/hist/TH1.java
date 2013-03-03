package play.classes.hist;

import java.io.IOException;
import play.RootOutput;
import play.annotations.ClassDef;
import play.Type;
import play.annotations.Counter;
import play.annotations.FieldType;
import play.annotations.Super;
import play.annotations.Title;
import play.classes.TArrayD;
import play.classes.TList;
import play.classes.TNamed;
import play.classes.TString;

/**
 * The base histogram class.
 *
 * @see <a href="http://root.cern.ch/root/htmldoc/TH1.html">TH1</a>
 * @author tonyj
 */
@ClassDef(version = 6, checkSum = -381522971)
@Title("1-Dim histogram base class")
public class TH1 extends TNamed {

    @Super
    private TAttLine tAttLine = new TAttLine();
    @Super
    private TAttFill tAttFill = new TAttFill();
    @Super
    private TAttMarker tAttMarker = new TAttMarker();
    @Title("number of bins(1D), cells (2D) +U/Overflows")
    private int fNcells;
    @Title("X axis descriptor")
    private TAxis fXaxis;
    @Title("Y axis descriptor")
    private TAxis fYaxis;
    @Title("Z axis descriptor")
    private TAxis fZaxis;
    @Title("(1000*offset) for bar charts or legos")
    private short fBarOffset = 0;
    @Title("(1000*width) for bar charts or legos")
    private short fBarWidth = 1000;
    @Title("Number of entries")
    double fEntries = 0;
    @Title("Total Sum of weights")
    double fTsumw = 0;
    @Title("Total Sum of squares of weights")
    double fTsumw2 = 0;
    @Title("Total Sum of weight*X")
    double fTsumwx = 0;
    @Title("Total Sum of weight*X*X")
    double fTsumwx2 = 0;
    @Title("Maximum value for plotting")
    private double fMaximum = -1111;
    @Title("Minimum value for plotting")
    private double fMinimum = -1111;
    @Title("Normalization factor")
    private double fNormFactor = 0;
    @Title("Array to display contour levels")
    private TArrayD fContour;
    @Title("Array of sum of squares of weights")
    private TArrayD fSumw2;
    @Title("histogram options")
    private TString fOption = TString.empty();
    @Title("Pointer to list of functions (fits and user)")
    @FieldType(value = Type.kObjectp)
    private TList fFunctions = new TList();
    @Title("fBuffer size")
    private int fBufferSize = 0;
    @Title("entry buffer")
    @Counter("fBufferSize")
    private double[] fBuffer = null;
    private transient EBinErrorOpt fBinStatErrOpt = EBinErrorOpt.kNormal;

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
