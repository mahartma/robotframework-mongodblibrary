package de.codecentric.robot.mongodblibrary.server;

import static de.codecentric.robot.mongodblibrary.keywords.MongodbLibrary.VERSION;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.robotframework.remoteserver.RemoteServer;

import de.codecentric.robot.mongodblibrary.keywords.MongodbLibrary;

public class MongodbLibraryRemoteServer {

	public static final int DEFAULT_PORT = 8270; 

	public static final String MESSAGE = "MongoDB Library v" + VERSION + " remote server started";
	
	private static org.robotframework.remoteserver.RemoteServer server;

	/**
	 * Remote Server main/startup method. Takes input from command line for Java
	 * class library (name) to load and invoke with reflection and the port to
	 * bind the remote server to. Defaults to port 8270 if not supplied.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// Setting port and 
		int port = DEFAULT_PORT;

		// Parse command line arguments
		for (int i = 0; i < args.length; i++) {
			if (args[i].equalsIgnoreCase("--port") || args[i].equalsIgnoreCase("-p")) {
				port = Integer.parseInt(args[i + 1]);
			}
			if (args[i].equalsIgnoreCase("--help") || args[i].equalsIgnoreCase("-h")) {
				displayUsage();
				System.exit(0);
			}
		}

		// The actual XML-RPC stuff
		try {
	        RemoteServer.configureLogging();
	        server = new RemoteServer();
	        server.addLibrary(MongodbLibrary.class, port);
	        server.start();
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm");
			System.out.println(MESSAGE + " on port " + port + " at " + dateFormat.format(new Date()));
		} catch (Exception e) {
			System.out.println("An exception occured when starting the server.\n\n"
					+ "Stacktrace:\n\n");
			e.printStackTrace();
		}
	}

	private static void displayUsage() {
		System.out.println("\n" + MESSAGE + "\n");
		System.out.println("Usage Info:\n");
		System.out.println("Ensure that the robotframework-mongodblibrary-" + VERSION + "-with-dependencies.jar JAR is in the classpath, e.g.:\n");
		System.out.println("set CLASSPATH=%CLASSPATH%;./robotframework-mongodblibrary-" + VERSION + "-with-dependencies.jar");
		System.out.println("The start the server as follows:\n");
		System.out.println("java -jar robotframework-mongodblibrary-" + VERSION + "-with-dependencies.jar --port <port>");
		System.out.println("");
	}
}