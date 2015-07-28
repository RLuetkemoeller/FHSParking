package logging;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.text.html.HTMLDocument.Iterator;

import chatserver.User;
import static java.nio.file.StandardOpenOption.*;

/**
 * 
 * @author rene
 * Main Class for Logging Systeminformations
 * Sends Loginformations to different OutputStreams
 */
public class Log {
	private static ArrayList<User> Listener = new ArrayList<User>();
	private static String path = "./logfile.txt";
	private static Path p = Paths.get(path);
	private static PrintWriter writer = null;
	
	/**
	 * Main functions called from outside for Sending Log messages
	 * Sends Logmessages to all active known writers
	 * @param data
	 */
	public static void println(String data){
		writer.println(data);
		System.out.println(data);
		writerToListener(data);
	}
	
	/**
	 * Open Logfile for writing LogMessages to File
	 */
	public static void init() {
		try {
			OutputStream out = new BufferedOutputStream(
			Files.newOutputStream(p, CREATE, APPEND));
			writer = new PrintWriter(out);
		} catch (IOException x) {
			System.err.println(x);
		}
	}
	
	public Log() {
		
	}

	/**
	 * Sends Messages to all known Listeners
	 * @param data
	 */
	public static void writerToListener(String data){
		for (User user : Listener) {
			user.writer.println("ticker: "+data);
			user.writer.flush();
		}
	}

	/**
	 * Remove Listener from Known List
	 * @param user
	 */
	public static void removeListener(User user) {
		Listener.remove(user);
		
	}

	/**
	 * Adds Listener from Known List
	 * @param user
	 */
	public static void addListener(User user) {
		Listener.add(user);
	}
	
}
