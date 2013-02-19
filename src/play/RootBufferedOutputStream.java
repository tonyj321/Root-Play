package play;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import play.annotations.Utilities;
import play.annotations.Utilities.RootClassInfo;

/**
 *
 * @author tonyj
 */
class RootBufferedOutputStream extends DataOutputStream implements RootOutput {

    private static final int kByteCountMask = 0x40000000;
    private static final int kNewClassTag = 0xFFFFFFFF;
    private static final int kClassMask = 0x80000000;
    private static final int kMapOffset = 2;
    private final RootByteArrayOutputStream buffer;
    private Map<String, Long> classMap = new HashMap<>();

    RootBufferedOutputStream() {
        this(new RootByteArrayOutputStream());
    }

    private RootBufferedOutputStream(RootByteArrayOutputStream buffer) {
        super(buffer);
        this.buffer = buffer;
    }

    @Override
    public void writeObject(RootObject o) throws IOException {
        writeObject(this, o);
    }

    @Override
    public void writeObjectRef(RootObject o) throws IOException {
        writeObjectRef(this, o);
    }

    @Override
    public boolean isLargeFile() {
        return false;
    }

    void writeTo(RootOutput out) throws IOException {
        buffer.writeTo(out);
    }

    @Override
    public void seek(long position) {
        buffer.seek(position);
    }

    @Override
    public long getFilePointer() {
        return buffer.getFilePointer();
    }

    @Override
    public Map<String, Long> getClassMap() {
        return classMap;
    }
    

    static void writeObject(RootOutput out, RootObject o) throws IOException {
        if (o == null) {
            out.writeInt(0);
        } else {
            RootClassInfo classInfo = Utilities.getClassInfo(o.getClass());
            if (classInfo.hasStandardHeader()) {
                long objectPointer = out.getFilePointer();
                out.writeInt(0); // space for length
                out.writeShort(classInfo.getVersion());
                o.write(out);
                long end = out.getFilePointer();
                out.seek(objectPointer);
                out.writeInt(kByteCountMask | (int) (end - objectPointer - 4));
                out.seek(end);
            } else {
                o.write(out);
            }
        }
    }

    static void writeObjectRef(RootOutput out, RootObject o) throws IOException {
        long objectPointer = out.getFilePointer();
        out.writeInt(0); // Space for length
        RootClassInfo classInfo = Utilities.getClassInfo(o.getClass());
        String className = classInfo.getName();
        Long address = out.getClassMap().get(className);
        if (address == null) {
            address = out.getFilePointer();
            out.writeInt(kNewClassTag);
            out.write(className.getBytes());
            out.writeByte(0); // Null terminated
            out.getClassMap().put(className, address);
        } else {
            out.writeInt(kClassMask | (address.intValue() + kMapOffset));
        }
        out.writeShort(classInfo.getVersion());
        o.write(out);
        long end = out.getFilePointer();
        out.seek(objectPointer);
        out.writeInt(0x40000000 | (int) (end - objectPointer - 4));
        out.seek(end);
    }

    private static class RootByteArrayOutputStream extends ByteArrayOutputStream {

        private void writeTo(RootOutput out) throws IOException {
            out.write(buf, 0, count);
        }

        private long getFilePointer() {
            return count;
        }

        private void seek(long position) {
            count = (int) position;
        }
    }
}
