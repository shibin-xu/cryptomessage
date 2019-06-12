package org.cloudguard.ipc;


import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;



public class CoreSaveLoadUtil {

    public static String LoadFromFile(String fileName) throws IOException
    {
        String serialized = null;
        byte[] bytes = Files.readAllBytes(Paths.get(fileName));
        serialized = new String(bytes);
        return serialized;
    }

    public static void SaveToFile(String filename, String serialized) throws IOException
    {
        byte[] bytes = serialized.getBytes();
        Files.write(Paths.get(filename), bytes);
    }
}
