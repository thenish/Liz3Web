package de.liz3.liz3web.util;

import java.io.*;

/**
 * Created by yannh on 25.02.2017.
 */
public class IOUtils {

    public static String convertStreamToString(InputStream is) throws IOException {
        return new String(convertStreamToByteArray(is));
    }

    public static byte[] convertStreamToByteArray(InputStream is) throws IOException {
        if(is == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int len; (len = is.read(buffer)) != -1; )
            baos.write(buffer, 0, len);
        baos.flush();
        is.close();
        return baos.toByteArray();
    }

    public static void writeByteArrayToFile(File f, byte[] bytes) throws IOException {
        if(f == null || bytes == null) return;
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bytes);
        fos.close();
    }

    public static byte[] readByteArrayFromFile(File f) throws IOException {
        if (!f.exists()) {
            f.createNewFile();
            return new byte[0];
        }
        FileInputStream fos = new FileInputStream(f);
        return convertStreamToByteArray(fos);
    }
}
