package com.ufpa.lafocabackend.core.utils;

import com.ufpa.lafocabackend.domain.exception.InvalidFileException;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class StandardCustomMultipartFile implements CustomMultipartFile, Serializable {

    private final Part part;
    private final String filename;

    public StandardCustomMultipartFile(Part part, String filename) {
        this.part = part;
        this.filename = filename;
        validate(this);
    }

    @Override
    public String getName() {
        return this.part.getName();
    }

    @Override
    public String getOriginalFilename() {
        return this.filename;
    }

    @Override
    public String getContentType() {
        return this.part.getContentType();
    }

    @Override
    public boolean isEmpty() {
        return (this.part.getSize() == 0);
    }

    @Override
    public long getSize() {
        return this.part.getSize();
    }

    @Override
    public byte[] getBytes() throws IOException {

        return part.getInputStream().readAllBytes();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.part.getInputStream();
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        this.part.write(dest.getPath());
        if (dest.isAbsolute() && !dest.exists()) {
            // Servlet Part.write is not guaranteed to support absolute file paths:
            // may translate the given path to a relative location within a temp dir
            // (e.g. on Jetty whereas Tomcat and Undertow detect absolute paths).
            // At least we offloaded the file from memory storage; it'll get deleted
            // from the temp dir eventually in any case. And for our user's purposes,
            // we can manually copy it to the requested location as a fallback.
            Files.copy(this.part.getInputStream(), dest.toPath());
        }
    }

    @Override
    public void transferTo(Path dest) throws IOException, IllegalStateException {
        Files.copy(this.part.getInputStream(), dest);
    }

    public void validate (StandardCustomMultipartFile file){
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("O arquivo não pode ser nulo ou vazio.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new InvalidFileException("O arquivo deve ter um nome e uma extensão.");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!Arrays.asList("jpg", "png", "svg", "jpeg").contains(extension)) {
            throw new InvalidFileException("Extensão de arquivo não permitida. Apenas .jpg, .png, .svg, .jpeg são aceitas.");
        }

        long size = file.getSize();
        if (size < 20 * 1024) {
            throw new InvalidFileException("O arquivo deve ter pelo menos 20KB.");
        } else if (size > 5 * 1024 * 1024) {
            throw new InvalidFileException("O arquivo deve ter no máximo 5MB.");
        }
    }

}