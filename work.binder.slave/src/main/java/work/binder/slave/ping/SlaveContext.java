package work.binder.slave.ping;

public class SlaveContext {

    private static boolean _occupied;
    private static String _jarPath;
    private static boolean _finished;

    public static String getJarPath() {
	return _jarPath;
    }

    public static boolean isFinished() {
	return _finished;
    }

    public static synchronized boolean isOccupied() {
	return _occupied;
    }

    public static void setFinished(boolean finished) {
	_finished = finished;
    }

    public static void setJarPath(String jarPath) {
	_jarPath = jarPath;
    }

    public static synchronized void setOccupied(boolean occupied) {
	_occupied = occupied;
    }

}
