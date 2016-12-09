package at.jku.dke.miner.common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This dummy LogUtils will only "log" on console.
 * 
 * @author Philipp
 *
 */
public class LogUtilities {
	private static final LogUtilities logger = new LogUtilities();
	private static final String datePattern = "HH:mm:ss.SSS";
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
	
	private LogUtilities() {
	}
	
	public static LogUtilities log() {
		return logger;
	}
	
	public void debug(String message) {
		System.out.println("[DEBUG]<" + getFormattedDate() + ">: " + message);
	}
	
	public void info(String message) {
		System.out.println("[INFO]<" + getFormattedDate() + ">: " + message);
	}
	
	public void warn(String message) {
		System.out.println("[WARN]<" + getFormattedDate() + ">: " + message);
	}
	
	public void error(String message) {
		System.err.println("[ERROR]<" + getFormattedDate() + ">: " + message);
	}
	
	private String getFormattedDate() {
		return dateFormat.format(new Date());
	}
}