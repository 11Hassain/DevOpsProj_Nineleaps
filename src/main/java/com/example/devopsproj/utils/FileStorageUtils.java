package com.example.devopsproj.utils;

import java.io.ByteArrayOutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class FileStorageUtils {

    private FileStorageUtils() {
        // This constructor is intentionally left empty because the class
        // only contains static methods for mapping and doesn't need to be instantiated.
        throw new UnsupportedOperationException("This class should not be instantiated.");
    }

    public static byte[] compressFile(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4*1024];
        while (!deflater.finished()) {
            int size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }

        return outputStream.toByteArray();
    }

    public static byte[] decompressFile(byte[] data) throws DataFormatException {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4*1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);
            }
        } catch (DataFormatException e){
            throw new DataFormatException();
        }

        return outputStream.toByteArray();
    }
}
