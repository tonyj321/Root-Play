package play;

import java.lang.reflect.Field;
import play.annotations.Counter;
import play.annotations.FieldType;
import play.annotations.Super;
import play.annotations.Title;

/**
 * Summarizes the information known about a field. This information comes from
 * reflection and annotations.
 *
 * @author tonyj
 */
class StreamerFieldInfo {
    
    private final StreamerClassInfo parentClassInfo;
    private final Field field;
    private final String title;
    private final boolean isBase;
    private final boolean isArray;
    private final String counter;
    private final Class fClass;
    private final StreamerClassInfo fieldClassInfo;
    private Type type;

    StreamerFieldInfo(StreamerClassInfo c, Field f) throws StreamerInfoException {
        this.parentClassInfo = c;
        this.field = f;
        Class tClass = f.getType();
        this.isArray = tClass.isArray();
        this.fClass = isArray ? tClass.getComponentType() : tClass;
        this.fieldClassInfo = new StreamerClassInfo(fClass);
        Title titleAnnotation = f.getAnnotation(Title.class);
        title = titleAnnotation == null ? "" : titleAnnotation.value();
        isBase = f.isAnnotationPresent(Super.class);
        Counter counterAnnotation = f.getAnnotation(Counter.class);
        counter = counterAnnotation == null ? null : counterAnnotation.value();
        if (counterAnnotation != null) {
            if (!isArray) {
                throw new StreamerInfoException("Field with counter is not an array");
            }
            StreamerFieldInfo cInfo = c.findField(counter);
            if (cInfo == null || cInfo.getType() != Type.kInt) {
                throw new StreamerInfoException("Reference to non-existent or non-integer element " + counter);
            }
            cInfo.type = Type.kCounter;
        }        
        FieldType typeAnnotation = f.getAnnotation(FieldType.class);
        Type explicitType = typeAnnotation == null ? null : typeAnnotation.value();
        if (explicitType == null) {
            type = Type.forClass(fClass);
            if (isArray) {
                type = type.arrayType();
            }
        } else {
            type = explicitType;
        }
    }

    boolean isBasicType() {
        return type.getValue() < 20;
    }

    boolean isArray() {
        return isArray;
    }

    public boolean isSuper() {
        return isBase;
    }

    String getName() {
        return field.getName();
    }

    String getTypeName() {
        String name = type.getName() == null ? fieldClassInfo.getName() : type.getName();
        if (isArray() || type==Type.kObjectp || type==Type.kObjectP) {
            name += "*";
        }
        return name;
    }

    int getArrayDim() {
        return 0;
    }

    int getMaxIndex(int i) {
        return 0;
    }

    String getTitle() {
        return title;
    }

    Type getType() {
        return type;
    }

    StreamerClassInfo getAsSuperClass() {
        return fieldClassInfo;
    }

    String getCountName() {
        return counter;
    }

    String getCountClass() {
        return parentClassInfo.getName();
    }

    int getCountVersion() {
        return parentClassInfo.getVersion();
    }

    boolean isBasicPointer() {
        return counter != null;
    }

    int getSize() {
        return type.getSize();
    }
}