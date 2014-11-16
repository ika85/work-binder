package work.binder.slave.ping;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class Pinger {

    private static Log LOG = LogFactory.getLog(Pinger.class);
    private static final String JOB = "job";
    private static final String DOT_JAR = ".jar";
    private static final String SLOT_COUNT = "slotCount";

    public static boolean copyStream(InputStream input, File jarFile)
	    throws IOException {

	OutputStream outputStream = null;

	boolean streamNotEmpty = false;

	byte[] buffer = new byte[1024];
	int bytesRead;
	while ((bytesRead = input.read(buffer)) != -1) {
	    streamNotEmpty = true;
	    if (outputStream == null) {
		outputStream = new FileOutputStream(jarFile);
	    }
	    outputStream.write(buffer, 0, bytesRead);
	}

	if (outputStream != null) {
	    outputStream.close();
	}

	return streamNotEmpty;
    }

    public static void ping(String masterUrl) {

	HttpClient client = new DefaultHttpClient();
	HttpPost post = new HttpPost(masterUrl);
	try {
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

	    if (SlaveContext.isOccupied()) {

		boolean finishedJob = addFinishedJobs(post);

		if (finishedJob) {
		    SlaveContext.setFinished(true);
		    SlaveContext.setJarPath(null);
		    SlaveContext.setOccupied(false);
		    nameValuePairs
			    .add(new BasicNameValuePair("job", "Finished"));
		} else {
		    nameValuePairs.add(new BasicNameValuePair("job",
			    "In Progress"));
		}
	    } else {
		nameValuePairs.add(new BasicNameValuePair(SLOT_COUNT, ""
			+ LocationProcessor.getSlotCount()));
	    }

	    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	    try {
		HttpResponse response = client.execute(post);
		InputStream inputStream = response.getEntity().getContent();

		executeAssignedJob(inputStream);

	    } catch (ConnectException e) {
		LOG.error("There is a problem in the connect to the Master computer. Maybe application on the Master computer hasn't been started.");
		LOG.error(e, e);
	    }

	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    // suggestion:
    // master could set estimates for the job; if the job isn't done in the
    // expected period; master could abort the job, and send the job to some
    // other slave
    private static boolean addFinishedJobs(HttpPost post) {

	boolean finished = false;

	return finished;
    }

    private static void executeAssignedJob(InputStream inputStream)
	    throws IOException {

	File slotTempFolder = LocationProcessor.provideBinderFolder(1);
	if (slotTempFolder != null) {
	    File jarFile = new File(slotTempFolder, JOB
		    + System.currentTimeMillis() + DOT_JAR);
	    if (jarFile.exists()) {
		FileUtils.forceDelete(jarFile);
	    }

	    boolean fileReadyForExecution = copyStream(inputStream, jarFile);

	    inputStream.close();

	    if (fileReadyForExecution) {
		SlaveContext.setOccupied(true);
		SlaveContext.setFinished(false);
		SlaveContext.setJarPath(jarFile.getAbsolutePath());
		Process process = Runtime.getRuntime()
			.exec(String.format("java -jar %s",
				jarFile.getAbsolutePath()));
		// try {
		// int processEnded = process.waitFor();
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }

	    }
	}
	// TODO add what if slotTempFolder == null;
    }
}
