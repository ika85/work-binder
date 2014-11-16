package work.binder.slave.ping;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class LocationProcessor {

    private static int _slotCount;

    static {
	_slots = new HashMap<Integer, File>();
	_slotCount = Runtime.getRuntime().availableProcessors();
	createDownloadTempDirs();

    }

    private static final String BINDER_SLAVE = "binder";

    private static Map<Integer, File> _slots;

    protected static int getSlotCount() {
	return _slotCount;
    }

    private static void createDownloadTempDirs() {

	int coreCount = getSlotCount();
	for (int i = 0; i < coreCount; i++) {

	    try {
		Path tempPath = Files.createTempDirectory(BINDER_SLAVE + i);
		File file = tempPath.toFile();

		if (file.exists()) {
		    _slots.put(i, file);
		}
	    } catch (IOException e1) {
		e1.printStackTrace();
	    }
	}
    }

    /**
     * could be null
     * */
    // TODO what if the job is done on the one slot but it isn't on the other.
    // Should that available slot be available for the new job?
    // TODO Should be delete files the job on the slave is done?
    protected static File provideBinderFolder(int i) {
	return _slots.get(i);
    }

}
