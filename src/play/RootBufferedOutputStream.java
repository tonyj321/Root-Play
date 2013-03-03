package play;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author tonyj
 */
class RootBufferedOutputStream extends DataOutputStream implements RootOutputNonPublic {

    private static final int kByteCountMask = 0x40000000;
    private static final int kNewClassTag = 0xFFFFFFFF;
    private static final int kClassMask = 0x80000000;
    private static final int kMapOffset = 2;
    private final RootByteArrayOutputStream buffer;
    private Map<String, Long> classMap = new HashMap<>();
    private int offset;
    private final TFile tFile;

    RootBufferedOutputStream(TFile tFile, int offset) {
        this(tFile, new RootByteArrayOutputStream());
        this.offset = offset;
    }

    private RootBufferedOutputStream(TFile tFile, RootByteArrayOutputStream buffer) {
        super(buffer);
        this.buffer = buffer;
        this.tFile = tFile;
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
        return tFile.isLargeFile();
    }

    void writeTo(RootOutput out) throws IOException {
        buffer.writeTo(out);
    }

    @Override
    public void seek(long position) {
        buffer.seek(position - offset);
    }

    @Override
    public long getFilePointer() {
        return buffer.getFilePointer() + offset;
    }

    @Override
    public Map<String, Long> getClassMap() {
        return classMap;
    }

    @Override
    public Map<String, TStreamerInfo> getStreamerInfos() {
        return tFile.getStreamerInfos();
    }

    static void writeObject(RootOutputNonPublic out, RootObject o) throws IOException {
        if (o == null) {
            out.writeInt(0);
        } else {
            writeObject(out, o, o.getClass());
        }
    }

    static void writeObject(RootOutputNonPublic out, RootObject o, Class c) throws IOException {
        try {
            StreamerClassInfo classInfo = StreamerUtilities.getClassInfo(c);
            Map<String, TStreamerInfo> streamerInfos = out.getStreamerInfos();
            if (!classInfo.suppressStreamerInfo() && !streamerInfos.containsKey(classInfo.getName())) {
                System.out.println("adding " + classInfo.getName());
                streamerInfos.put(classInfo.getName(), StreamerUtilities.getStreamerInfo(c));
            }
            if (classInfo.hasStandardHeader()) {

                long objectPointer = out.getFilePointer();
                out.writeInt(0); // space for length
                out.writeShort(classInfo.getVersion());
                Class sc = c.getSuperclass();
                if (RootObject.class.isAssignableFrom(sc)) {
                    writeObject(out, o, sc);
                }
                Method m = c.getDeclaredMethod("write", RootOutput.class);
                m.setAccessible(true);
                m.invoke(o, out);
                long end = out.getFilePointer();
                out.seek(objectPointer);
                out.writeInt(kByteCountMask | (int) (end - objectPointer - 4));
                out.seek(end);
            } else {
                Class sc = c.getSuperclass();
                if (RootObject.class.isAssignableFrom(sc)) {
                    writeObject(out, o, sc);
                }
                Method m = c.getDeclaredMethod("write", RootOutput.class);
                m.setAccessible(true);
                m.invoke(o, out);
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | StreamerInfoException ex) {
            throw new IOException("Problem writing object of class " + c.getName(), ex);
        }
    }

    static void writeObjectRef(RootOutputNonPublic out, RootObject o) throws IOException {
        try {
            long objectPointer = out.getFilePointer();
            out.writeInt(0); // Space for length
            StreamerClassInfo classInfo = StreamerUtilities.getClassInfo(o.getClass());
            String className = classInfo.getName();
            Long address = out.getClassMap().get(className);
            if (address == null) {
                address = out.getFilePointer();
                out.writeInt(kNewClassTag);
                out.write(className.getBytes());
                out.writeByte(0); // Null terminated
                out.getClassMap().put(className, address);
                System.out.println(className + "=" + address);
            } else {
                out.writeInt(kClassMask | (address.intValue() + kMapOffset));
            }
            writeObject(out, o, o.getClass());
            long end = out.getFilePointer();
            out.seek(objectPointer);
            out.writeInt(0x40000000 | (int) (end - objectPointer - 4));
            out.seek(end);
        } catch (StreamerInfoException ex) {
            throw new IOException("Problem writing object", ex);
        }
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
