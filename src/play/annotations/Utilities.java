package play.annotations;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import play.TFile.TArrayD;
import play.TFile.TAxis;
import play.TFile.TNamed;
import play.TFile.TObject;
import play.TFile.TString;
import static play.annotations.StreamerInfo.Type.kObjectP;
import static play.annotations.StreamerInfo.Type.kObjectp;
import static play.annotations.StreamerInfo.Type.kTString;

/**
 *
 * @author tonyj
 */
public class Utilities {

    private static final Map<Class, TypeDescription> primitives = new HashMap<>();

    static {
        primitives.put(Integer.TYPE, new TypeDescription(StreamerInfo.Type.kInt, 4, new TString("Int_t")));
        primitives.put(Short.TYPE, new TypeDescription(StreamerInfo.Type.kShort, 2, new TString("short")));
        primitives.put(Float.TYPE, new TypeDescription(StreamerInfo.Type.kFloat, 4, new TString("Float_t")));
        primitives.put(Double.TYPE, new TypeDescription(StreamerInfo.Type.kDouble, 8, new TString("Double_t")));
        primitives.put(Boolean.TYPE, new TypeDescription(StreamerInfo.Type.kBool, 1, new TString("Bool_t")));
        primitives.put(TString.class, new TypeDescription(StreamerInfo.Type.kTString, 8, new TString("TString")));
        primitives.put(TNamed.class, new TypeDescription(StreamerInfo.Type.kTNamed, 0, new TString("TNamed")));
        primitives.put(TObject.class, new TypeDescription(StreamerInfo.Type.kTObject, 0, new TString("TString")));
        primitives.put(TArrayD.class, new TypeDescription(StreamerInfo.Type.kAny, 0, new TString("TArrayD")));
    }
    private static final Map<StreamerInfo.Type, TypeDescription> explicits = new HashMap<>();

    static {
        explicits.put(StreamerInfo.Type.kUShort, new TypeDescription(StreamerInfo.Type.kUShort, 2, new TString("UShort_t")));
    }

    public static RootClassInfo getClassInfo(Class c) {
        return new RootClassInfo(c);
    }

    public static TStreamerInfo getStreamerInfo(Class c) throws StreamerInfoException {
        RootClass rootClass = (RootClass) c.getAnnotation(RootClass.class);
        if (rootClass == null) {
            throw new StreamerInfoException("Cannot get streamer info for unannotated class: " + c.getName());
        }
        TStreamerInfo info = new TStreamerInfo(c, rootClass);

        Class s = c.getSuperclass();
        if (s != null) {
            rootClass = (RootClass) s.getAnnotation(RootClass.class);
            if (rootClass != null) {
                TypeDescription desc = getTypeDescriptionForClass(s, rootClass);
                info.add(new TStreamerBase(s, rootClass, desc.type == StreamerInfo.Type.kObject ? StreamerInfo.Type.kBase : desc.type , 0));
            }
        }

        for (Field f : c.getDeclaredFields()) {
            StreamerInfo streamerInfo = f.getAnnotation(StreamerInfo.class);
            if (streamerInfo != null) {
                try {
                    info.add(createElementForField(info, f, streamerInfo));
                } catch (StreamerInfoException x) {
                    String msg = String.format("Error in field %s with type %s of class %s", f.getName(), f.getType().getName(), c.getName());
                    throw new StreamerInfoException(msg, x);
                }
            }
        }
        return info;
    }

    private static TStreamerElement createElementForField(TStreamerInfo info, Field f, StreamerInfo streamerInfo) {

        TypeDescription desc = getTypeDescriptionForField(f, streamerInfo);
        String counter = streamerInfo.counter();
        if (counter.length() > 0) {
            if (!desc.isArray()) {
                throw new StreamerInfoException("Element with counter is not an array");
            }
            TStreamerElement index = info.findElementByName(counter);
            if (index == null || index.getType() != StreamerInfo.Type.kInt) {
                throw new StreamerInfoException("Reference to non-existent or non-integer element " + counter);
            }
            index.setType(StreamerInfo.Type.kCounter);
            return new TStreamerBasicPointer(f, streamerInfo, desc.type, desc.size, desc.typeName,
                    new TString(counter), info.getName(), info.getClassVersion());
        }
        return createElementFromType(desc, f, streamerInfo);

    }

    private static TStreamerElement createElementFromType(TypeDescription type, Field f, StreamerInfo info) {
        if (type.isBase) {
            Class fClass = f.getType();
            RootClass rootClass = (RootClass) fClass.getAnnotation(RootClass.class);
            return new TStreamerBase(fClass, rootClass, type.type, type.size);
        } else if (type.isBasicType()) {
            return new TStreamerBasicType(f, info, type.type, type.size, type.typeName);
        }
        switch (type.type) {
            case kTString:
                return new TStreamerString(f, info, type.type, type.size, type.typeName);
            case kObjectP:
            case kObjectp:
                return new TStreamerObjectPointer(f, info, type.type, type.size, type.typeName);
            case kAny:
                return new TStreamerObjectAny(f, info, type.type, type.size, type.typeName);
            default:
                return new TStreamerObject(f, info, type.type, type.size, type.typeName);

        }
    }

    // A variable to be streamed must either be a RootObject (or annotated as a RootClass), or a java
    // type for which we have a mapping to a root type, or an array of one of the above, or have an explicit
    // type mapping specified in the StreamerInfo
    //
    private static TypeDescription getTypeDescriptionForField(Field f, StreamerInfo streamerInfo) {
        StreamerInfo.Type explicitType = streamerInfo.type();
        Class fClass = f.getType();
        RootClass rootClass = (RootClass) fClass.getAnnotation(RootClass.class);

        switch (explicitType) {
            case kNone:
                if (fClass.isArray()) {
                    Class aClass = fClass.getComponentType();
                    TypeDescription desc = getTypeDescriptionForClass(aClass, rootClass);
                    desc.setIsArray(true);
                    return desc;
                }
                return getTypeDescriptionForClass(fClass, rootClass);
            case kObjectp:
            case kObjectP:
                if (rootClass == null) {
                    throw new StreamerInfoException("Field of type POINTER is not a RootClass");
                }
                return new TypeDescription(explicitType, 4, new TString(TStreamerInfo.getClassName(rootClass, fClass) + "*"));
            case kBase:
                if (rootClass == null) {
                    throw new StreamerInfoException("Field of type BASE is not a RootClass");
                }
                TypeDescription desc = new TypeDescription(explicitType, 0, new TString(TStreamerInfo.getClassName(rootClass, fClass)));
                desc.setIsBase(true);
                return desc;
            default: // Some other explicit type
                desc = explicits.get(explicitType);
                if (desc == null) {
                    throw new StreamerInfoException(String.format("Field of type %s has no explicit mapping", explicitType));
                }
                return desc;
        }
    }

    private static TypeDescription getTypeDescriptionForClass(Class fClass, RootClass rootClass) {
        TypeDescription desc = primitives.get(fClass);
        if (desc != null) {
            return desc;
        }
        if (rootClass == null) {
            throw new StreamerInfoException("Field is neither a primitive nor annotated as a RootClass");
        }
        return new TypeDescription(StreamerInfo.Type.kObject, 12, new TString(TStreamerInfo.getClassName(rootClass, fClass)));
    }

    public static class RootClassInfo {

        private final Class javaClass;
        private final RootClass rootClass;

        RootClassInfo(Class c) throws StreamerInfoException {
            this.javaClass = c;
            this.rootClass = (RootClass) c.getAnnotation(RootClass.class);
            if (rootClass == null) {
                throw new StreamerInfoException("Cannot get class info for unannotated class: " + c.getName());
            }
        }

        public String getName() {
            return TStreamerInfo.getClassName(rootClass, javaClass);
        }

        public int getVersion() {
            return rootClass.version();
        }

        public boolean hasStandardHeader() {
            return rootClass.hasStandardHeader();
        }

        public String getTitle() {
            return rootClass.title();
        }
    }

    static class TypeDescription {

        final StreamerInfo.Type type;
        final int size;
        TString typeName;
        private boolean isArray = false;
        private boolean isBase = false;

        TypeDescription(StreamerInfo.Type type, int size, TString typeName) {
            this.type = type;
            this.size = size;
            this.typeName = typeName;
        }

        private boolean isBasicType() {
            return type.getValue() < 20;
        }

        private void setIsArray(boolean b) {
            typeName = new TString(typeName.getString() + "*");
            type.setIsArray(true);
            isArray = b;
        }

        private boolean isArray() {
            return isArray;
        }

        public boolean isBase() {
            return isBase;
        }

        public void setIsBase(boolean isBase) {
            this.isBase = isBase;
        }
    }

    public static void main(String[] args) throws StreamerInfoException {
        TStreamerInfo streamerInfo = Utilities.getStreamerInfo(TAxis.class);
        System.out.println(streamerInfo);
    }
}
