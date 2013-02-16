package play.annotations;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import play.TFile.TAxis;
import play.TFile.TString;

/**
 *
 * @author tonyj
 */
public class Utilities {

    private static final Map<Object, Factory> factories = new HashMap<>();

    static {
        factories.put(Integer.TYPE, new PrimitiveFactory(3, 4, new TString("Int_t")));
        factories.put(Short.TYPE, new PrimitiveFactory(2, 2, new TString("short")));
        factories.put(Float.TYPE, new PrimitiveFactory(5, 4, new TString("Float_t")));
        factories.put(Double.TYPE, new PrimitiveFactory(8, 8, new TString("Double_t")));
        factories.put(Boolean.TYPE, new PrimitiveFactory(8, 8, new TString("Bool_t")));
        factories.put(TString.class, new StringFactory());
        factories.put("UShort_t", new PrimitiveFactory(12, 2));
        factories.put("BASE", new BaseFactory());
    }

    public static String getClassName(Class c) {
        String result = "";
        RootClass rootClass = (RootClass) c.getAnnotation(RootClass.class);
        if (rootClass != null) {
            result = rootClass.className();
        }
        return result.length() == 0 ? c.getSimpleName() : result;
    }

    public static TStreamerInfo getStreamerInfo(Class c) throws StreamerInfoException {
        RootClass rootClass = (RootClass) c.getAnnotation(RootClass.class);
        if (rootClass == null) {
            throw new StreamerInfoException("Cannot get streamer info for unannotated class: "+c.getName());
        }
        TStreamerInfo info = new TStreamerInfo(c, rootClass);

        Class s = c.getSuperclass();
        while (s != null) {
            rootClass = (RootClass) s.getAnnotation(RootClass.class);
            if (rootClass != null) {
                info.add((new TStreamerBase(s, rootClass)));
            }
            s = s.getSuperclass();
        }

        for (Field f : c.getDeclaredFields()) {
            StreamerInfo streamerInfo = f.getAnnotation(StreamerInfo.class);
            if (streamerInfo != null) {
                String explicitType = streamerInfo.type();
                Class fClass = f.getType();
                Factory factory;
                if (explicitType.length() == 0) {
                    factory = factories.get(fClass);
                } else {
                    factory = factories.get(explicitType);
                }
                if (factory == null) {
                    factory = new ElementFactory(62, 12, new TString(getClassName(fClass)));
                }
                info.add(factory.createStreamerElement(f, streamerInfo));
            }
        }
        return info;
    }

    private interface Factory {
        TStreamerElement createStreamerElement(Field f, StreamerInfo i);
    }
    
    private static class ElementFactory implements Factory {

        final int type;
        final int size;
        final TString typeName;

        ElementFactory(int type, int size, TString typeName) {
            this.type = type;
            this.size = size;
            this.typeName = typeName;
        }

        @Override
        public TStreamerElement createStreamerElement(Field f, StreamerInfo i) {
            return new TStreamerElement(f, i, type, size, typeName);
        }
    }

    private static class StringFactory implements Factory {

        @Override
        public TStreamerElement createStreamerElement(Field f, StreamerInfo i) {
            return new TStreamerString(f, i);
        }
    }

    private static class PrimitiveFactory extends ElementFactory {

        PrimitiveFactory(int type, int size) {
            this(type, size, null);
        }

        PrimitiveFactory(int type, int size, TString name) {
            super(type,size,name);
        }

        @Override
        public TStreamerElement createStreamerElement(Field f, StreamerInfo i) {
            return new TStreamerBasicType(f, i, type, size, typeName);
        }
    }

    private static class BaseFactory implements Factory {

        @Override
        public TStreamerElement createStreamerElement(Field f, StreamerInfo i) {
            Class s = f.getType();
            RootClass rootClass = (RootClass) s.getAnnotation(RootClass.class);
            return new TStreamerBase(s, rootClass);
        }
    }

    public static void main(String[] args) throws StreamerInfoException {
        TStreamerInfo streamerInfo = Utilities.getStreamerInfo(TAxis.class);
        System.out.println(streamerInfo);
    }
}
