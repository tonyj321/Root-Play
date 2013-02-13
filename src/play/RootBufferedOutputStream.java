package play;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author tonyj
 */
class RootBufferedOutputStream extends DataOutputStream implements RootOutput {

    private final RootByteArrayOutputStream buffer;

    RootBufferedOutputStream() {
        this(new RootByteArrayOutputStream());
    }

    private RootBufferedOutputStream(RootByteArrayOutputStream buffer) {
        super(buffer);
        this.buffer = buffer;
    }

    @Override
    public void writeObject(RootObject o) throws IOException {
        if (o == null) {
            writeInt(0);
        } else {
            o.write(this);
        }
    }

    @Override
    public int length(RootObject o) throws IOException {
        return o == null ? 4 : o.length(this);
    }

    @Override
    public boolean isLargeFile() {
        return false;
    }

    void writeTo(RootOutput out) throws IOException {
        buffer.writeTo(out);


    }

    private static class RootByteArrayOutputStream extends ByteArrayOutputStream {

        private void writeTo(RootOutput out) throws IOException {
            out.write(buf, 0, count);
        }
    }
}
