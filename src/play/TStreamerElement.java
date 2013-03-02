package play;

import java.io.IOException;
import java.lang.reflect.Field;
import play.annotations.ClassDef;
import play.annotations.StreamerInfo;
import play.classes.TNamed;
import play.classes.TString;

@ClassDef(version = 4)
abstract class TStreamerElement extends TNamed {

    @StreamerInfo("element type")
     int fType;
    @StreamerInfo("sizeof element")
     int fSize;
    @StreamerInfo("cumulative size of all array dims")
    private int fArrayLength = 0;
    @StreamerInfo("number of array dimensions")
    private int fArrayDim = 0;
    @StreamerInfo("Maximum array index for array dimension")
    private int[] fMaxIndex = new int[5];
    @StreamerInfo("Data type name of data member")
     TString fTypeName;
    Type type;

    TStreamerElement(Field f, StreamerInfo info, Type type, int size, TString typeName) {
        super(new TString(f.getName()), new TString(info.value()));
        this.type = type;
        this.fType = type.getValue();
        this.fSize = size;
        this.fTypeName = typeName;
    }

    TStreamerElement(Class c, ClassDef rootClass, Type type, int size, TString typeName) {
        super(new TString(TStreamerInfo.getClassName(rootClass, c)), new TString(rootClass.title()));
        this.type = type;
        this.fType = type.getValue();
        this.fSize = size;
        this.fTypeName = typeName;
    }

    private void write(RootOutput out) throws IOException {
        out.writeInt(fType);
        out.writeInt(fSize);
        out.writeInt(fArrayLength);
        out.writeInt(fArrayDim);
        for (int i : fMaxIndex) {
            out.writeInt(i);
        }
        out.writeObject(fTypeName);
    }

    public TString getTypeName() {
        return fTypeName;
    }

    public int getArrayDim() {
        return fArrayDim;
    }

    public int getMaxIndex(int index) {
        return fMaxIndex[index];
    }
    
    public Type getType() {
        return type;
    }
   

    @Override
    public String toString() {
        return "TStreamerElement{" + "fType=" + fType + ", fSize=" + fSize + ", fArrayLength=" + fArrayLength + ", fArrayDim=" + fArrayDim + ", fMaxIndex=" + fMaxIndex + ", fTypeName=" + fTypeName + '}';
    }

    void setType(Type type) {
        this.type = type;
        this.fType = type.getValue();
    }
}
