package play.annotations;

import java.io.IOException;
import java.lang.reflect.Field;
import play.RootOutput;
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
    private static int version = 4;

    TStreamerElement(Field f, StreamerInfo info, int type, int size, TString typeName) {
        super(new TString(f.getName()), new TString(info.value()));
        this.fType = type;
        this.fSize = size;
        this.fTypeName = typeName;
    }

    TStreamerElement(Class c, RootClass rootClass, int type, int size, TString typeName) {
        super(new TString(TStreamerInfo.getClassName(rootClass, c)), new TString(rootClass.title()));
        this.fType = type;
        this.fSize = size;
        this.fTypeName = typeName;
    }

    @Override
    public void write(RootOutput out) throws IOException {
        out.writeInt(0x40000000 | myLength(out));
        out.writeShort(version);
        super.write(out);
        out.writeInt(fType);
        out.writeInt(fSize);
        out.writeInt(fArrayLength);
        out.writeInt(fArrayDim);
        for (int i : fMaxIndex) {
            out.writeInt(i);
        }
        out.writeObject(fTypeName);
    }

    @Override
    public int length(RootOutput out) throws IOException {
        return 4 + myLength(out);
    }

    private int myLength(RootOutput out) throws IOException {
        return 9*4+2 + super.length(out) + out.length(fTypeName);
    }

    @Override
    public String toString() {
        return "TStreamerElement{" + "fType=" + fType + ", fSize=" + fSize + ", fArrayLength=" + fArrayLength + ", fArrayDim=" + fArrayDim + ", fMaxIndex=" + fMaxIndex + ", fTypeName=" + fTypeName + '}';
    }
}
