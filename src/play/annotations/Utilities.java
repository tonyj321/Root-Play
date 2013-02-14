package play.annotations;

import play.streamerinfo.TStreamerInfo;

/**
 *
 * @author tonyj
 */
public class Utilities {

    public static String getClassName(Class c) {
        String result = "";
        RootClass rootClass = (RootClass) c.getAnnotation(RootClass.class);
        if (rootClass != null) {
            result = rootClass.className();
        }
        return result.length()==0 ? c.getSimpleName() : result;
    }

    public TStreamerInfo getStreamerInfo(Class c) {
        return null;
        
    }
}
