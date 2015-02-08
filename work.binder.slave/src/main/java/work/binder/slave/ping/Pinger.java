package work.binder.slave.ping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

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
import org.apache.http.params.CoreConnectionPNames;

//TODO create service from Pinger; immediate=true, activate - ping();
public class Pinger {

    private static Log LOG = LogFactory.getLog(Pinger.class);
    private static final String PACKAGE = "package";
    private static final String DOT_ZIP = ".zip";
    private static final String PROCESSOR_COUNT = "processorCount";
    private static final String SLOT_COUNT = "slotCount";
    private static final String EXECUTION = "execution";
    private static final String IN_PROGRESS = "In Progress";
    private static final String FINISHED = "Finished";
    private static final String CANCEL = "cancel";
    private static final String CANCELED = "canceled";
    private static final String TASKLIST = "tasklist";
    private static final String CLEAR = "clear";
    private static final String PACKAGE_COMMAND = "packageCommand";

    public static File copyStream(InputStream input) throws IOException {

	OutputStream outputStream = null;
	File tempPackageFile = null;

	byte[] buffer = new byte[1024];
	int bytesRead;
	while ((bytesRead = input.read(buffer)) != -1) {
	    if (outputStream == null) {
		if (tempPackageFile == null) {

		    tempPackageFile = File.createTempFile(
			    PACKAGE + System.currentTimeMillis(), DOT_ZIP);
		}
		outputStream = new FileOutputStream(tempPackageFile);
	    }
	    outputStream.write(buffer, 0, bytesRead);
	}

	if (outputStream != null) {
	    outputStream.close();
	}

	return tempPackageFile;
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
	// final HttpParams httpParams = new BasicHttpParams();
	// HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
	HttpClient client = new DefaultHttpClient();
	client.getParams().setParameter(
		CoreConnectionPNames.CONNECTION_TIMEOUT, 130000);
	client.getParams()
		.setParameter(CoreConnectionPNames.SO_TIMEOUT, 160000);
	HttpPost post = new HttpPost(masterUrl);
	try {
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

	    if (SlaveContext.isOccupied()) {

		if (SlaveContext.isCleared()) {
		    SlaveContext.setCleared(false);
		    SlaveContext.setOccupied(false);
		    nameValuePairs.add(new BasicNameValuePair(EXECUTION,
			    FINISHED));
		} else if (SlaveContext.isCanceled()) {
		    nameValuePairs.add(new BasicNameValuePair(EXECUTION,
			    CANCELED));
		    SlaveContext.setCanceled(false);
		    SlaveContext.setOccupied(false);

		} else {

		    nameValuePairs.add(new BasicNameValuePair(EXECUTION,
			    IN_PROGRESS));
		}
	    } else {
		nameValuePairs.add(new BasicNameValuePair(PROCESSOR_COUNT, ""
			+ LocationProcessor.getProcessorCount()));
	    }

	    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	    try {
		HttpResponse response = client.execute(post);

		Header[] headers = response.getHeaders(CLEAR);
		if (headers != null && headers.length > 0) {
		    Header header = headers[0];
		    String headerName = header.getName();

		    if (CLEAR.equals(headerName)) {
			LocationProcessor.deletePackagesOnSlave();
			SlaveContext.setCleared(true);
		    }
		} else {

		    headers = response.getHeaders(CANCEL);

		    boolean continueWithWork = true;

		    if (headers != null && headers.length > 0) {
			Header header = headers[0];
			String headerName = header.getName();

			if (CANCEL.equals(headerName)) {
			    continueWithWork = false;
			    if (SlaveContext.isOccupied()) {
				List<ProcessData> processes = SlaveContext
					.getProcessesData();

				for (ProcessData processData : processes) {

				    Process process = processData.getProcess();
				    if (process != null) {
					// TODO kill win/linux... process, not
					// just
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
					    // windows solution, check linux &
					    // mac
					    Runtime.getRuntime().exec(
						    "taskkill /f /pid "
							    + serviceId);
					}
					// what if execution isn't cancelled for
					// some
					// reason

					SlaveContext.setCanceled(true);
				    }
				}

			    }
			}
		    }

		    if (continueWithWork) {

			InputStream inputStream = response.getEntity()
				.getContent();
			executeIfThereIsSomething(inputStream);

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
    private static void executeIfThereIsSomething(InputStream inputStream)
	    throws IOException {

	File tempPackageFile = copyStream(inputStream);

	if (tempPackageFile != null) {

	    Path mainPackagePath = Files.createTempDirectory("mainPackage"
		    + System.currentTimeMillis());
	    File mainPackage = mainPackagePath.toFile();

	    FileUtils.forceMkdir(mainPackage);
	    UnzipUtils.unzip(tempPackageFile, mainPackage.getAbsolutePath());

	    Properties properties = new Properties();
	    File[] propFiles = mainPackage.listFiles(new FilenameFilter() {

		@Override
		public boolean accept(File arg0, String arg1) {
		    return arg1.endsWith(".properties");
		}
	    });

	    if (propFiles.length > 0) {
		File additionalDataFile = propFiles[0];
		InputStream inStream = new FileInputStream(additionalDataFile);
		properties.load(inStream);
		inStream.close();

		int slotCount = Integer.valueOf(properties
			.getProperty(SLOT_COUNT));
		try {
		    // TODO slot instead of processor count
		    LocationProcessor.createDownloadTempDirs(slotCount);

		    for (int i = 0; i < slotCount; i++) {

			File slotTempFolder = LocationProcessor
				.provideBinderFolder(i);
			if (slotTempFolder != null) {

			    SlaveContext.setOccupied(true);

			    File folderWithPackages = new File(slotTempFolder,
				    PACKAGE + System.currentTimeMillis());

			    FileUtils.forceMkdir(folderWithPackages);
			    FileUtils.copyDirectory(mainPackage,
				    folderWithPackages);

			    for (File packages : folderWithPackages.listFiles()) {
				if (packages.getName().endsWith(".zip")) {
				    CommandFileData commandFileData = UnzipUtils
					    .unzip(packages,
						    packages.getParent());

				    String commandFilePath = commandFileData
					    .getCommandFilePath();

				    ProcessData processData = new ProcessData();

				    Process process = null;
				    if (commandFilePath == null) {
					LOG.error("Missing command file in the package");
				    } else {

					File commandFile = new File(
						commandFilePath);
					setExecutePermission(commandFile
						.getAbsolutePath());
					// check cancel after these changes
					String fileName = commandFile.getName();

					StringBuilder commandBuilder = new StringBuilder();
					if (commandFileData
						.isBatchFileIndicator()) {
					    commandBuilder.append("./");
					}
					commandBuilder.append(commandFilePath);
					commandBuilder.append(" ");
					commandBuilder.append(commandFile
						.getParentFile()
						.getAbsolutePath());
					commandBuilder.append(" ");
					commandBuilder.append(properties
						.getProperty(PACKAGE_COMMAND));

					process = Runtime.getRuntime().exec(
						commandBuilder.toString());
					processData.setServiceName(fileName);
				    }

				    processData.setProcess(process);

				    SlaveContext.getProcessesData().add(
					    processData);
				}
			    }

			}
		    }
		} finally {
		    FileUtils.forceDelete(tempPackageFile);
		    FileUtils.forceDelete(mainPackage);
		}
	    }
	}

	// TODO add what if slotTempFolder == null;

    }

    private static void setExecutePermission(String filePath)
	    throws IOException {

	Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
	// add owners permission
	perms.add(PosixFilePermission.OWNER_EXECUTE);
	// add group permissions
	perms.add(PosixFilePermission.GROUP_EXECUTE);
	// add others permissions
	perms.add(PosixFilePermission.OTHERS_EXECUTE);

	Files.setPosixFilePermissions(Paths.get(filePath), perms);
    }
}
