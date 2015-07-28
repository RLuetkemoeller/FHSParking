package chatserver;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import parkingarea.Parkingarea;
import worker.GateWorker;


public class SimpleChatServer implements Runnable {
	private static Parkingarea myArea = null;
	private static ArrayList<PrintWriter> clientOutputStreams;
	private static HashMap<String, String> autlist = new HashMap<String, String>();
	
	/* GETTERS UND SETTERS */
	/* ------------------------------------------------------------- */
	
	public static Parkingarea getMyArea() {
		return myArea;
	}

	public static void setMyArea(Parkingarea myArea) {
		SimpleChatServer.myArea = myArea;
	}
	
	/* Main */
	/* ------------------------------------------------------------- */
	public static void main(String[] args) {
		System.out.println("Starte ChatServer");
		
		// Starte den ChatServer (main loop)
		new SimpleChatServer().go();
	}

	/**
	 * Main Function called when Thread is started.
	 */
	private void go() {
		// List for all connected Clients
		clientOutputStreams = new ArrayList<PrintWriter>();
		
		try {
			ServerSocket serverSock = new ServerSocket(5000);
			while(true) {
				synchronized (GateWorker.lock) {
					// Wait for shutting down a Gate
				}
				Socket clientSocket = serverSock.accept();
				PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
				clientOutputStreams.add(writer);
				Thread t = new Thread(new ClientHandler(clientSocket, myArea));
				t.start();
				System.out.println("got a connection");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
		}
		
	}
	
	@Override
	public void run() {
		SimpleChatServer.main(null);	
	}

	
	/* ClientFunctions to ALL */
	/* ------------------------------------------------------------- */

	/**
	 * Function that Sends a Message to every connected User
	 * @param message
	 */
	public static void tellEveryone(String message) {
		Iterator<PrintWriter> it = clientOutputStreams.iterator();
		
		// Iterate through List of connected Users
		while(it.hasNext()){
			PrintWriter writer = it.next();
			writer.println(message);
			writer.flush();
		}	
	}



}
