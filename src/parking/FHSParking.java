package parking;

import logging.Log;
import chatserver.SimpleChatServer;
import parkingarea.Gate;
import parkingarea.Parkingarea;
import worker.GateWorker;

/**
 * Main Class that Starts the Programm
 * @author rene
 * 
 */
public class FHSParking {
	static private SimpleChatServer ChatServer = new SimpleChatServer();
	public static void main(String[] args) throws InterruptedException {
		
		Parkingarea myArea = new Parkingarea();
		// Sets the runtime to 10 Minutes
		long patience = 1000 * 60 * 1;
		// Save the Startingtime of the Programm
		long startTime = System.currentTimeMillis();
		boolean active = true;
		SimpleChatServer.setMyArea(myArea);
		Thread Th_ChatServer = new Thread(ChatServer,"ChatServer");
		Log.init();
		Th_ChatServer.start();
		myArea.run();
		
		// Sets the Mainthread sleeping till patience is over
		Thread.sleep(patience);
		
		myArea.stop();
		
	}
	

}
