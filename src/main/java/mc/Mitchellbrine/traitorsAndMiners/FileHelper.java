package mc.Mitchellbrine.traitorsAndMiners;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileHelper {

	public static void deleteDir(File dir) {
        if (!dir.exists()) return;
        if (dir.isDirectory()) {
            String[] contents = dir.list();
            for (String content : contents) {
                deleteDir(new File(dir, content));
            }
        }
        dir.delete();
    }
	
        public static void unzip(File zipfile, File directory) throws IOException {
            ZipFile zfile = new ZipFile(zipfile);
            Enumeration<? extends ZipEntry> entries = zfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File file = new File(directory, entry.getName());
                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    file.getParentFile().mkdirs();
                    InputStream in = zfile.getInputStream(entry);
                    try {
                        copy(in, file);
                    } finally {
                        in.close();
                    }
                }
            }
            zfile.close();
        }
        
        private static void copy(InputStream in, OutputStream out) throws IOException {
            byte[] buffer = new byte[1024];
            while (true) {
                int readCount = in.read(buffer);
                if (readCount < 0) {
                    break;
                }
                out.write(buffer, 0, readCount);
            }
        }

        private static void copy(InputStream in, File file) throws IOException {
            OutputStream out = new FileOutputStream(file);
            try {
                copy(in, out);
            } finally {
                out.close();
            }
        }
	
}
