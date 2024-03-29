package hse.hw02.serializable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Serializable {
    void serialize(OutputStream out) throws IOException;
    void deserialize(InputStream in) throws IOException;
}