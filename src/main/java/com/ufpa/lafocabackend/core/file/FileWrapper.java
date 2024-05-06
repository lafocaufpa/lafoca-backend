package com.ufpa.lafocabackend.core.file;

import java.io.IOException;
import java.io.InputStream;

public interface FileWrapper {
    String getName();
    String getOriginalFilename();
    String getContentType();
    boolean isEmpty();
    long getSize();
    byte[] getBytes() throws IOException;
    InputStream getInputStream() throws IOException;
}
