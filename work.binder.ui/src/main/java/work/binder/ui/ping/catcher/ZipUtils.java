package work.binder.ui.ping.catcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    public static boolean zip(List<String> filelist, String zipLocation)
	    throws IOException {

	boolean done = false;

	byte[] buffer = new byte[1024];

	try {

	    FileOutputStream fos = new FileOutputStream(zipLocation);
	    ZipOutputStream zos = new ZipOutputStream(fos);

	    for (String file : filelist) {
		ZipEntry ze = new ZipEntry(new File(file).getName());
		zos.putNextEntry(ze);
		FileInputStream in = new FileInputStream(file);

		int len;
		while ((len = in.read(buffer)) > 0) {
		    zos.write(buffer, 0, len);
		}

		in.close();
		zos.closeEntry();
	    }
	    zos.close();
	    done = true;
	} catch (IOException ex) {
	    ex.printStackTrace();
	}

	return done;
    }

}
