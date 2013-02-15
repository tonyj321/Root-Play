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

    private static final Map<Object,Factory> factories = new HashMap<>();
    static {
        factories.put(Integer.TYPE, new PrimitiveFactory(3,4,new TString("Int_t")));
        factories.put(Short.TYPE, new PrimitiveFactory(2,2,new TString("short")));
        factories.put(Float.TYPE, new PrimitiveFactory(5,4,new TString("Float_t")));
        factories.put(Double.TYPE, new PrimitiveFactory(8,8,new TString("Double_t")));
        factories.put(Boolean.TYPE, new PrimitiveFactory(8,8,new TString("Bool_t")));
        factories.put(TString.class, new StringFactory());
        factories.put("UShort_t", new PrimitiveFactory(12,2));
        factories.put("BASE", new BaseFactory());
    }
    
    public static String getClassName(Class c) {
        String result = "";
        RootClass rootClass = (RootClass) c.getAnnotation(RootClass.class);
        if (rootClass != null) {
            result = rootClass.className();
        }
        return result.length()==0 ? c.getSimpleName() : result;
    }

    public static TStreamerInfo getStreamerInfo(Class c) throws StreamerInfoException {
        RootClass rootClass = (RootClass) c.getAnnotation(RootClass.class);
        if (rootClass == null) {
            throw new StreamerInfoException("Cannot get streamer info for unannotated class");
        }
        TStreamerInfo info = new TStreamerInfo(c,rootClass);

        Class s = c.getSuperclass();
        while (s != null) {
            rootClass = (RootClass) s.getAnnotation(RootClass.class);
            if (rootClass != null) {
                info.add((new TStreamerBase(s,rootClass)));
            }
            s = s.getSuperclass();
        }
        
        for (Field f : c.getDeclaredFields()) {
            StreamerInfo streamerInfo = f.getAnnotation(StreamerInfo.class);
            if (streamerInfo != null) {
                String explicitType = streamerInfo.type();
                Class fClass = f.getType();
                Factory factory;
                if (explicitType.length()==0) {
                    factory = factories.get(fClass); }
                else {
                    factory = factories.get(explicitType);
                }
                if (factory==null) {
                    throw new StreamerInfoException("Cannot create element for "+f);
                }
                info.add(factory.createStreamerElement(f, streamerInfo));
            }
        }
        return info;
    }
    
    private static class Factory {
        TStreamerElement createStreamerElement(Field f, StreamerInfo i) {
            return new TStreamerElement(f,i);
        }
    }
    
    private static class StringFactory extends Factory {

        @Override
        TStreamerElement createStreamerElement(Field f, StreamerInfo i) {
            return new TStreamerString(f,i);
        }        
    }
    private static class PrimitiveFactory extends Factory {
        private final int type;
        private final TString name;
        private final int size;

        PrimitiveFactory(int type, int size) {
            this(type,size,null);
        }
        PrimitiveFactory(int type, int size, TString name) {
            this.type=type;
            this.size = size;
            this.name = name;
        }
        @Override
        TStreamerElement createStreamerElement(Field f, StreamerInfo i) {
            return new TStreamerBasicType(f,i,type,size,name);
        }
        
    }
    private static class BaseFactory extends Factory {

        @Override
        TStreamerElement createStreamerElement(Field f, StreamerInfo i) {
            return new TStreamerBase(f,i);
        }
    }
    
    public static void main(String[] args) throws StreamerInfoException {
        TStreamerInfo streamerInfo = Utilities.getStreamerInfo(TAxis.class);
        System.out.println(streamerInfo);
    }
}
