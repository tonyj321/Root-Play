package play.annotations;

import java.lang.reflect.Field;
import play.TFile.TNamed;
import play.TFile.TString;

@RootClass(version = 4)
public class TStreamerElement extends TNamed {
    
    @StreamerInfo("element type")
    private int fType;
    @StreamerInfo("sizeof element")
    private int fSize;
    @StreamerInfo("cumulative size of all array dims")
    private int fArrayLength = 0;
    @StreamerInfo("number of array dimensions")
    private int fArrayDim = 0;
    @StreamerInfo("Maximum array index for array dimension")
    private int[] fMaxIndex = new int[5];
    @StreamerInfo("Data type name of data member")
    private TString fTypeName;
    

    TStreamerElement(Field f, StreamerInfo info) {
        super(new TString(f.getName()),new TString(info.value()));
    }
    
    
    TStreamerElement(Class c, RootClass rootClass) {
        super(new TString(TStreamerInfo.getClassName(rootClass, c)),new TString(rootClass.title()));
    }

    void setType(int fType) {
        this.fType = fType;
    }

    void setSize(int fSize) {
        this.fSize = fSize;
    }

    void setTypeName(TString fTypeName) {
        this.fTypeName = fTypeName;
    }

    @Override
    public String toString() {
        return "TStreamerElement{" + "fType=" + fType + ", fSize=" + fSize + ", fArrayLength=" + fArrayLength + ", fArrayDim=" + fArrayDim + ", fMaxIndex=" + fMaxIndex + ", fTypeName=" + fTypeName + '}';
    }
    
    
}
