package play;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 *
 * @author tonyj
 */
class StreamerUtilities {

    static StreamerClassInfo getClassInfo(Class c) {
        return new StreamerClassInfo(c);
    }

    static TStreamerInfo getStreamerInfo(Class c) throws StreamerInfoException {
        StreamerClassInfo classInfo = getClassInfo(c);

        Class s = c.getSuperclass();
        if (s != Object.class) {
            classInfo.setSuperClass(new StreamerClassInfo(s));
        }

        for (Field f : c.getDeclaredFields()) {
            try {
                if ((f.getModifiers() & (Modifier.TRANSIENT | Modifier.STATIC)) == 0) {
                    StreamerFieldInfo fieldInfo = new StreamerFieldInfo(classInfo, f);
                    classInfo.addField(fieldInfo);
                }
            } catch (StreamerInfoException x) {
                x.setField(c.getName(), f.getName());
                throw x;
            }
        }
        return new TStreamerInfo(classInfo);
    }
}