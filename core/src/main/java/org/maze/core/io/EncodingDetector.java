package org.maze.core.io;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class EncodingDetector {
    public static String detectEncoding(File file) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            byte[] bom = new byte[4];
            int read = bis.read(bom, 0, 4);
            if (read < 4) {
                return StandardCharsets.UTF_8.name(); 
            }

            if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) && (bom[2] == (byte) 0xBF)) {
                return StandardCharsets.UTF_8.name();
            } else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {
                return StandardCharsets.UTF_16BE.name();
            } else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {
                return StandardCharsets.UTF_16LE.name();
            } else if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00) && (bom[2] == (byte) 0xFE) && (bom[3] == (byte) 0xFF)) {
                return "UTF-32BE";
            } else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE) && (bom[2] == (byte) 0x00) && (bom[3] == (byte) 0x00)) {
                return "UTF-32LE";
            }

            return StandardCharsets.UTF_8.name(); 
        }
    }
}