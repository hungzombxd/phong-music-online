package huu.phong.musiconline.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;


public class FileUtils {
    public static void copyURLToFile(URL url, File file, FileUtils.Streaming streaming){
        try {
            URLConnection connection = url.openConnection();
            int length = connection.getContentLength();
            byte[] buffer = new byte[4096];
            int reading = -1;
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
            BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
            int offset = 0;
            while (offset < length){
				try {
					reading = in.read(buffer);
				} catch (Exception e) {
                    if (in != null) in.close();
                    connection = url.openConnection();
                    connection.setRequestProperty("Accept-Ranges", "bytes");
                    connection.setRequestProperty("Range", "bytes=" + offset + "-");
                    connection.connect();
                    in = new BufferedInputStream(connection.getInputStream());
                }
                if (reading == -1) break;
                out.write(buffer, 0, reading);
                offset += reading;
                if (streaming != null) streaming.progressing(length, offset);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public interface Streaming{
        void progressing(int length, int offset);
    }
}
