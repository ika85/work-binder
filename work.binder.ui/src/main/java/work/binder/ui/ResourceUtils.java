package work.binder.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ResourceUtils {

    public static Properties loadIPAdresses(String filePath) {

	Properties properties = new Properties();
	// TODO this should not be done every time!
	InputStream inputStream;
	try {
	    inputStream = new FileInputStream(filePath);
	    try {
		properties.load(inputStream);
	    } catch (IOException e) {
		// TODO throw own exception
	    }
	    inputStream.close();
	} catch (FileNotFoundException e) {
	    // TODO throw own exception
	} catch (IOException e) {
	    // TODO throw own exception
	}
	return properties;
    }

    public static List<String> providePrepararedPackages(String filelocation,
	    final String dotExt) {

	File file = new File(filelocation);
	String[] jarsArray = file.list(new FilenameFilter() {

	    public boolean accept(File arg0, String arg1) {

		return arg1.endsWith(dotExt);
	    }
	});

	List<String> jarsList = null;

	if (jarsArray == null) {
	    jarsList = new ArrayList<String>();
	} else {
	    jarsList = Arrays.asList(jarsArray);
	}

	return jarsList;
    }
}
