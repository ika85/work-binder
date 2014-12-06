package work.binder.slave.ping;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UnzipUtils {

    private static final int BUFFER = 2048;
    private static final String DOT_BAT = ".bat";

    public static String unzip(File zipFile, String outputFolder) {

	String exeFilePath = null;
	try {

	    ZipFile zip = new ZipFile(zipFile);
	    String newPath = outputFolder;

	    new File(newPath).mkdir();

	    @SuppressWarnings("rawtypes")
	    Enumeration zipFileEntries = zip.entries();

	    while (zipFileEntries.hasMoreElements()) {

		ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
		String currentEntry = entry.getName();

		File destFile = new File(newPath, currentEntry);

		File destinationParent = destFile.getParentFile();

		destinationParent.mkdirs();

		if (!entry.isDirectory()) {
		    BufferedInputStream is = new BufferedInputStream(
			    zip.getInputStream(entry));
		    int currentByte;

		    byte data[] = new byte[BUFFER];

		    FileOutputStream fos = new FileOutputStream(destFile);
		    BufferedOutputStream dest = new BufferedOutputStream(fos,
			    BUFFER);

		    while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
			dest.write(data, 0, currentByte);
		    }
		    dest.flush();
		    dest.close();
		    is.close();

		    // should we handle situation when there is more then one
		    // batch file
		    if (currentEntry.endsWith(DOT_BAT)) {
			exeFilePath = destFile.getAbsolutePath();
		    }
		}

	    }

	    zip.close();
	} catch (Exception e) {
	    e.printStackTrace();
	    // TODO add exception handle
	}

	return exeFilePath;

    }

}
