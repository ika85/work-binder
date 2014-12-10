package work.binder.slave.ping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class Pinger {

    private static Log LOG = LogFactory.getLog(Pinger.class);
    private static final String PACKAGE = "package";
    private static final String DOT_ZIP = ".zip";
    private static final String SLOT_COUNT = "slotCount";
    private static final String EXECUTION = "execution";
    private static final String IN_PROGRESS = "In Progress";
    private static final String FINISHED = "Finished";
    private static final String CANCEL = "cancel";
    private static final String CANCELED = "canceled";

    private static final String TASKLIST = "tasklist";

    public static boolean copyStream(InputStream input, File packageFile)
	    throws IOException {

	OutputStream outputStream = null;

	boolean streamNotEmpty = false;

	byte[] buffer = new byte[1024];
	int bytesRead;
	while ((bytesRead = input.read(buffer)) != -1) {
	    streamNotEmpty = true;
	    if (outputStream == null) {
		outputStream = new FileOutputStream(packageFile);
	    }
	    outputStream.write(buffer, 0, bytesRead);
	}

	if (outputStream != null) {
	    outputStream.close();
	}

	return streamNotEmpty;
    }

    public static String isProcessRunning(String serviceName) {

	String serviceId = null;

	Process p;
	try {
	    p = Runtime.getRuntime().exec(TASKLIST);
	    BufferedReader reader = new BufferedReader(new InputStreamReader(
		    p.getInputStream()));
	    String line;
	    while ((line = reader.readLine()) != null) {

		if (line.contains(serviceName)) {

		    String linePart = line.substring(serviceName.length())
			    .trim();
		    serviceId = linePart.substring(0, linePart.indexOf(" "));
		    break;
		}
	    }

	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return serviceId;
    }

    public static void ping(String masterUrl) {

	HttpClient client = new DefaultHttpClient();
	HttpPost post = new HttpPost(masterUrl);
	try {
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

	    if (SlaveContext.isOccupied()) {

		if (SlaveContext.isFinished()) {
		    nameValuePairs.add(new BasicNameValuePair(EXECUTION,
			    FINISHED));
		    SlaveContext.setOccupied(false);
		    LocationProcessor.deletePackagesOnSlave();

		} else {

		    nameValuePairs.add(new BasicNameValuePair(EXECUTION,
			    IN_PROGRESS));
		}
	    } else {
		nameValuePairs.add(new BasicNameValuePair(SLOT_COUNT, ""
			+ LocationProcessor.getSlotCount()));
	    }

	    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	    try {
		HttpResponse response = client.execute(post);

		Header[] headers = response.getHeaders(CANCEL);

		boolean continueWithWork = true;

		if (headers != null && headers.length > 0) {
		    Header header = headers[0];
		    String headerName = header.getName();

		    if (CANCEL.equals(headerName)) {
			continueWithWork = false;
			if (SlaveContext.isOccupied()
				&& !SlaveContext.isFinished()) {
			    List<ProcessData> processes = SlaveContext
				    .getProcesses();

			    for (ProcessData processData : processes) {

				Process process = processData.getProcess();
				if (process != null) {
				    // TODO kill win/linux... process, not just
				    // java
				    process.destroy();

				    // String processName =
				    // java.lang.management.ManagementFactory
				    // .getRuntimeMXBean().getName();
				    // Long pid = Long.parseLong(processName
				    // .split("@")[0]);

				    String serviceId = isProcessRunning(processData
					    .getServiceName());

				    if (serviceId != null) {
					// windows solution, check linux & mac
					Runtime.getRuntime()
						.exec("taskkill /f /pid "
							+ serviceId);
				    }
				    // what if execution isn't cancelled for
				    // some
				    // reason

				    nameValuePairs.add(new BasicNameValuePair(
					    EXECUTION, CANCELED));
				    SlaveContext.setFinished(true);
				    SlaveContext.setOccupied(false);
				}
			    }

			}
		    }
		}

		if (continueWithWork) {
		    InputStream inputStream = response.getEntity().getContent();

		    boolean executionSuccessfullyFinished = executeIfThereIsSomething(inputStream);

		    if (executionSuccessfullyFinished) {
			SlaveContext.setFinished(true);

		    }

		}
	    } catch (ConnectException e) {
		LOG.error("There is a problem with a connection to the Master computer. Maybe application on the Master computer hasn't been started.");
	    }

	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    // suggestion:
    // master could set estimates for the job; if the job isn't done in the
    // expected period; master could abort the job, and send the job to some
    // other slave
    private static boolean executeIfThereIsSomething(InputStream inputStream)
	    throws IOException {

	boolean executionSuccessfullyFinished = false;

	File tempPackageFile = File.createTempFile(
		PACKAGE + System.currentTimeMillis(), DOT_ZIP);

	boolean packageReadyForUsing = copyStream(inputStream, tempPackageFile);

	if (packageReadyForUsing) {

	    for (int i = 0; i < LocationProcessor.getSlotCount(); i++) {

		File slotTempFolder = LocationProcessor.provideBinderFolder(i);
		if (slotTempFolder != null) {
		    File packageFile = new File(slotTempFolder, PACKAGE
			    + System.currentTimeMillis() + DOT_ZIP);
		    if (packageFile.exists()) {
			FileUtils.forceDelete(packageFile);
		    }

		    FileUtils.copyFile(tempPackageFile, packageFile);

		    SlaveContext.setOccupied(true);
		    SlaveContext.setFinished(false);

		    String exeFilePath = UnzipUtils.unzip(packageFile,
			    packageFile.getParent());

		    String fileName = new File(exeFilePath).getName();
		    // add switch for sh file
		    Process process = Runtime.getRuntime().exec(exeFilePath);

		    ProcessData processData = new ProcessData();
		    processData.setProcess(process);
		    processData.setServiceName(fileName);

		    SlaveContext.getProcesses().add(processData);
		    try {
			int processEnded = process.waitFor();

			executionSuccessfullyFinished = processEnded == 0;

			// what if there is some problem during execution of
			// Main
			// class from specified jar?

		    } catch (InterruptedException e) {
			e.printStackTrace();
		    }

		}
	    }
	}

	FileUtils.forceDelete(tempPackageFile);
	// TODO add what if slotTempFolder == null;

	return executionSuccessfullyFinished;
    }
}
