package work.binder.ui.jar;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import work.binder.ui.LayoutReloadComponent;
import work.binder.ui.Locations;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;

@SuppressWarnings("serial")
public class UploadFileProcessor extends LayoutReloadComponent {

    public static class JarStorager implements Receiver {

	private String fileName;
	private String mtype;
	private int counter;

	public String getFileName() {
	    return fileName;
	}

	public int getLineBreakCount() {
	    return counter;
	}

	public String getMimeType() {
	    return mtype;
	}

	public OutputStream receiveUpload(String filename, String MIMEType) {
	    counter = 0;
	    fileName = filename;
	    mtype = MIMEType;

	    OutputStream outputStream = null;
	    try {
		outputStream = new FileOutputStream(
			Locations.UPLOAD_PACKAGE_LOCATION + "/" + filename);
	    } catch (FileNotFoundException e) {
		e.printStackTrace();
	    }

	    return outputStream;
	}

    }
    private Label state = new Label();
    private Label result = new Label();
    private Label fileName = new Label();

    private Label textualProgress = new Label();

    private ProgressIndicator pi = new ProgressIndicator();

    private JarStorager counter = new JarStorager();

    private Upload upload = new Upload(null, counter);

    // TODO what if there is already file with the same name?!
    public UploadFileProcessor() {
	setSpacing(true);

	addComponent(new Label(
		"Upload a jar which will be processed in the next job"));

	// make analyzing start immediatedly when file is selected
	// TODO maybe the following line should be removed
	upload.setImmediate(true);
	upload.setButtonCaption("Upload Jar");
	addComponent(upload);

	final Button cancelProcessing = new Button("Cancel");
	cancelProcessing.addListener(new Button.ClickListener() {
	    public void buttonClick(ClickEvent event) {
		upload.interruptUpload();
	    }
	});
	cancelProcessing.setVisible(false);
	cancelProcessing.setStyleName("small");

	Panel p = new Panel("Status");
	p.setSizeUndefined();
	FormLayout l = new FormLayout();
	l.setMargin(true);
	p.setContent(l);
	HorizontalLayout stateLayout = new HorizontalLayout();
	stateLayout.setSpacing(true);
	stateLayout.addComponent(state);
	stateLayout.addComponent(cancelProcessing);
	stateLayout.setCaption("Current state");
	state.setValue("");
	l.addComponent(stateLayout);
	fileName.setCaption("File name");
	l.addComponent(fileName);
	pi.setCaption("Progress");
	pi.setVisible(false);
	l.addComponent(pi);
	textualProgress.setVisible(false);
	l.addComponent(textualProgress);

	addComponent(p);

	upload.addListener(new Upload.StartedListener() {
	    public void uploadStarted(StartedEvent event) {
		pi.setValue(0f);
		pi.setVisible(true);
		pi.setPollingInterval(500);
		textualProgress.setVisible(true);
		state.setValue("Uploading");
		fileName.setValue(event.getFilename());

		cancelProcessing.setVisible(true);
	    }
	});

	upload.addListener(new Upload.ProgressListener() {
	    public void updateProgress(long readBytes, long contentLength) {
		pi.setValue(new Float(readBytes / (float) contentLength));
		textualProgress.setValue("Processed " + readBytes
			+ " bytes of " + contentLength);
	    }

	});

	upload.addListener(new Upload.SucceededListener() {
	    public void uploadSucceeded(SucceededEvent event) {
		result.setValue("Upload - succeeded");
	    }
	});

	upload.addListener(new Upload.FailedListener() {
	    public void uploadFailed(FailedEvent event) {
		result.setValue("Upload - failed");
	    }
	});

	upload.addListener(new Upload.FinishedListener() {
	    public void uploadFinished(FinishedEvent event) {
		state.setValue("Upload - finished");
		pi.setVisible(false);
		textualProgress.setVisible(false);
		cancelProcessing.setVisible(false);

	    }
	});

    }

    @Override
    public void reload() {
	// TODO Auto-generated method stub

    }

}