package chatserver;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.sql.rowset.spi.SyncResolver;

import logging.Log;
import parkingarea.Parkingarea;
import worker.GateWorker;


public class ClientHandler implements Runnable {
	private Boolean active = true;
	private Boolean echo = true;
	private Parkingarea myArea = null;
	private BufferedReader reader;
	private Socket sock;
	private String cmdData;
	private User user = new User();
	private HashMap<String, Runnable> cmdList = new HashMap<String, Runnable>();
	private HashMap<String, Runnable> cmdListAuth;
	private HashMap<String, User> users = new HashMap<String, User>();
	private static HashMap<String, User> authlist = new HashMap<String, User>();
	
	
	
	/* GETTERS UND SETTERS */
	/* ------------------------------------------------------------- */

	
	
	/* MainLoop */
	/* ------------------------------------------------------------- */
	
	@Override
	public void run() {
		String message;

		try {
			while(true){
				if ((message = reader.readLine()) != null){
					
					synchronized (GateWorker.lock) {
						// Wait for shutting down a Gate
					}
				
					if (echo && (message.charAt(0) != '@')) {	
						user.writer.println(message);
					}
					if (message.charAt(0) == '@') {
						message = message.substring(1);
					}
					
					this.ProcessStatement(message);
					if (this.active == false){
						this.sock.close();
					}
					// 
					//user.writer.println(":"+user.getName()+"#>");
					//
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	/* InitFunctions */
	/* ------------------------------------------------------------- */
	
	private void init_Functions() {
		
		// Befehle für die Nicht Authorisierten Nutzer
		this.cmdList.put("chat", () -> { this.sendMessage(); });
		this.cmdList.put("reply", () -> { this.reply(); });
		this.cmdList.put("help", () -> { this.ShowHelp(); });
		this.cmdList.put("login", () -> { this.Login(); });
		this.cmdList.put("Logging", () -> { this.Logging(); });
		this.cmdList.put("noLogging", () -> { this.noLogging(); });
		this.cmdList.put("exit", () -> { this.Exit(); });

	
		// Befehle für die Authorisierten Nutzer
		this.cmdListAuth = (HashMap<String, Runnable>) this.cmdList.clone();
		this.cmdList.put("logout", () -> { this.Logout(); });
		this.cmdList.put("who", () -> { this.who(); });
		this.cmdList.put("showUsers", () -> { this.showUsers(); });
		this.cmdList.put("shutdownGates", () -> { this.shutdownGates(); });
		this.cmdList.put("resumeGates", () -> { this.resumeGates(); });
		this.cmdList.put("driveInLock", () -> { this.driveInLock(); });
		this.cmdList.put("driveOutLock", () -> { this.driveOutLock(); });
		this.cmdList.put("removeLocks", () -> { this.removeLocks(); });
	}

	private void init_Users() {
		// List of Authenticated users for Login
		this.users.put("Hans", new User("Hans","1234",true));
		this.users.put("Peter", new User("Peter","1234",true));
		this.users.put("Ulf", new User("Ulf","1234",true));

	}
	
	
	/* KONSTRUKTOR */
	/* ------------------------------------------------------------- */
	
	public ClientHandler(Socket clientSocket, Parkingarea pArea){
		try {
			sock = clientSocket;
			InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
			// Set the Inputstream
			reader = new BufferedReader(isReader);
			// Set client writer
			user.writer = new PrintWriter(sock.getOutputStream());
			// Inits
			init_Functions();
			init_Users();
			myArea = pArea;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	/* FUNCTION FOR HANDLING REQUESTS */
	/* ------------------------------------------------------------- */
	
	/**
	 * Main Funciton for Processing Incoming data from Sockets
	 * Splits the Command into brackets and process it choosen by the first bracket(Command)
	 * 
	 * @param cmdStr (InputString to process from a SocketReader
	 * @throws IOException
	 */
	public void ProcessStatement(String cmdStr) throws IOException{
		
		StringTokenizer cmdTokenizer = new StringTokenizer(cmdStr," -");
		
		// Main Loop for processing Requests
		if (cmdTokenizer.hasMoreTokens()){
			
			// Split Request into Command and Parameters
			String cmd = cmdTokenizer.nextToken();
			if (cmdStr.length() > cmd.length()){
				cmdData = cmdStr.substring(cmd.length());
			} else {
				cmdData = "";
			}
			
			// Check which Commandset the user gets
			Runnable x = (user.getAuthorized()) ? cmdList.get(cmd) : cmdListAuth.get(cmd);
			/*
			if (user.getAuthorized()){
				x = cmdList.get(cmd);
			} else {
				x = cmdListAuth.get(cmd);
			}
			*/
			
			// If the Command exists execute it
			if (x != null) {
				x.run();
			} else {
				user.writer.println("Error unbekannter Befehl: " + cmd + ". Oder Ihnen fehlt die Berechtigung");
				user.writer.flush();
			}
		}
		
		
	}
	
	/* FUNCTIONS FOR REQUESTS */
	/* ------------------------------------------------------------- */
	
	/**
	 * Sends a Message to every active Connection
	 * Message is this.cmdData (String)
	 */
	private void sendMessage(){	
		// Send Message to everyone
		StringTokenizer token = new StringTokenizer(this.cmdData, " ");
		// If Parameter -to isSet
		if (token.hasMoreTokens()) {
			String param = token.nextToken();
			// Make new tokenizer for receiver and message
			if (param.equals("-to")) {
				String receiver=null,message="";
				// Check for receiver
				if (token.hasMoreTokens()){
					receiver = token.nextToken();
				}
				// Check for Reassemble the Rest of the message
				while (token.hasMoreTokens()){				
					message += " " + token.nextToken();
				}
				// Check if receiver and message exists
				if (receiver != null) {
				sendToUser(receiver,message);
				}else {
					// Print out Error Message
					user.writer.println("Error: Send to User Faild because of missing Parameters");
					user.writer.flush();					
				}				
			} else {
				SimpleChatServer.tellEveryone(this.cmdData);
			}		
		}
	}
	
	/**
	 * Sends a Message to a specific connected user identified by String receiver
	 * Message to send is String message
	 * @param String receiver
	 * @param String message
	 */
	private void sendToUser(String receiver, String message) {
		User u = authlist.get(receiver);
		
		if (u != null) {
			u.writer.println("Message from " + user.getName() + ":\t" + message);
			u.writer.flush();
			u.setReplyto(user);
		} else {
			user.writer.println("Error: " + receiver + " not found in System or not logged in.");
			user.writer.flush();
		}
		
		/*Map<String,User> map = authlist;
		// Iterate through the HashMap authlist and tell the Client all active authorized Users
		for (Map.Entry<String, User> entry : map.entrySet()){
			if (entry.getValue().getName().equals(receiver)){
				entry.getValue().writer.println("Message from:" + user.getName() + ":\t" + message);
				entry.getValue().writer.flush();				
			}
		}
		*/
	}

	/**
	 * Sends a Helpmessage to the connected User depending if he is logged in or not
	 * he gets a different Help message.
	 * Called by ProcessStatement
	 */
	private void ShowHelp() {
			if (user.getAuthorized()){
				// Commandset for priviledged users
				user.writer.println("Sie sind angemeldet als: " + user.getName());
				user.writer.println("Folgendes Befehlsset steht zur Verfügung:");			
				user.writer.println("help \t\t\t Zeigt diese Hilfe an");	
				user.writer.println("Logging \t\t\t Aktiviert das Logging");	
				user.writer.println("noLogging \t\t\t Deaktiviert das Logging");					
				user.writer.println("chat <Nachricht>\t\t Nachricht an alle im System angemeldeten user");	
				user.writer.println("resumeGates   [-a] <Gatenumber>\t -a öffnet alle Gates - mit Gatenumber ein spezielles Gate aktivieren");	
				user.writer.println("shutdownGates [-a] <Gatenumber>\t -a stopt alle Gates - mit Gatenumber ein spezielles Gate deaktivieren");	
				user.writer.println("driveInLock \t\t\t Einfahrsperre Aktivieren");	
				user.writer.println("driveOutLock \t\t\t Ausfahrsperre Aktivieren");	
				user.writer.println("removeLocks \t\t\t Ein- Ausfahrsperre Deaktivieren");	
				user.writer.println("logout \t\t\t Benutzer abmelden");	
				user.writer.println("Exit \t\t\t Vom System trennen");	

			} else {
				// Commandset for unprivildged users
				user.writer.println("Sie sind nicht Angemeldet!");
				user.writer.println("Folgendes eingeschränktes Befehlsset steht zur Verfügung:");
				user.writer.println("help \t\t\t Zeigt diese Hilfe an");
				user.writer.println("Logging \t\t\t Aktiviert das Logging");	
				user.writer.println("noLogging \t\t\t Deaktiviert das Logging");
				user.writer.println("chat [--to <username>] <Nachricht>\t Nachricht an alle im System angemeldeten user");
				user.writer.println("login <username> <password> \t Vom System abmelden");	
				user.writer.println("Exit \t\t\t Vom System trennen");	

			}
			user.writer.flush();
	}
	
	/**
	 * Loginfuction called by ProcessStatement
	 * 
	 */
	private void Login() {
		// Splits Command into Severl tokens.
		StringTokenizer token = new StringTokenizer(cmdData," ");
		String username="",password="";
		User test = null;
		
		// Check the Parameter if username isSet
		if (token.hasMoreTokens()){
			username = token.nextToken();	
			test = users.get(username);
		} else {
			user.writer.println("Fehlender Parameter: username");
		}
		
		// Check the Parameter if password isSet
		if (token.hasMoreTokens()){
			password = token.nextToken();
		} else {
			user.writer.println("Fehlender Parameter: password");
		}
		
		
		// Check if username and password is Correct
		if ((test != null) && (test.password.equals(password))){
			// Grant priviledges and set Values
			user.setAuthorized(true);
			user.setName(username);
			user.setPassword(password);
			
			// Add user to the authenticated list
			ClientHandler.authlist.put(username, this.user);
			user.writer.println("You are now logged in as " + username);
		} else {
			user.writer.println(", Error: Faschler Benutzername oder Passwort!" + username);
		}
		
		user.writer.flush();
		
		
	}
	
	/**
	 * Function for Logout of connected User 
	 * called by ProcessStatement
	 */
	private void Logout(){	
		if (user.getAuthorized()){
			user.writer.println("You have been logged out," + user.getName() );
			ClientHandler.authlist.remove(user.getName());
			this.user.setAuthorized(false);
			this.user.setName("Unknown");
		} else {
			user.writer.println("Error: Sie sind nicht angemeldet!");
		}
		
		user.writer.flush();
	}
	
	/**
	 * Terminates the connection of User
	 * called by ProcessStatement
	 */
	private void Exit(){	
		this.active = false;
		user.writer.println("Verbindung erfolgreich getrennt");
		user.writer.flush();
		noLogging();
		this.user.writer.close();
		
	}
	
	/**
	 * Activates Logging to the Liverticker of the Client.
	 * Sends the Logmessages to the Client
	 * puts the Users writer into the Log Class 
	 * called by ProcessStatement
	 */
	private void Logging(){	
		// Add user to Logging
		Log.addListener(this.user);
	}
	
	/**
	 * Deactivates the Logging
	 * removes the Users writer from Log Class
	 * called by ProcessStatement
	 */
	private void noLogging(){	
		// Remove user from Logging
		Log.removeListener(this.user);
	}
	
	/**
	 * Tells the connected User who he is
	 * called by ProcessStatement
	 */
	private void who(){	
		// Tell Client the username
		user.writer.println("Angemeldet als:" + user.getName());
		user.writer.flush();
	}
	
	/**
	 * Tells the connected User who is online
	 */
	private void showUsers(){	
		Map<String,User> map = authlist;
		user.writer.println("Folgende User sind am System angemeldet:");
		// Iterate through the HashMap authlist and tell the Client all active authorized Users
		for (Map.Entry<String, User> entry : map.entrySet()){
			user.writer.println(entry.getValue().getName());
		}		
		user.writer.flush();
	}
	
	/**
	 * Used for chat communication
	 * Sends Message to the User from who is the last Message
	 */
	private void reply(){	
		// Reply from last Message received
		sendToUser(user.getReplyto().getName(), this.cmdData);
	}
	
	/**
	 * Turns off Single Gates or all Gates depending on parameter -a for all or number
	 * interrupts the running Thread
	 * called by ProcessStatement
	 */
	private void shutdownGates() {	
		// Send Message to everyone
		StringTokenizer token = new StringTokenizer(this.cmdData, " ");
		// If Parameter -a isSet
		if (token.hasMoreTokens()) {
			String param = token.nextToken();
			if (param.equals("-a")) {
				// Shutdown all Gates	
				try {
						if (myArea.stopAllGates() == 1) {
							// All Gates Stopped
							user.writer.println("Success: Shutting down all Gates");
						} else {
							// Error while stopping the gates
							user.writer.println("Error: Shutting down all Gates");
						}
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} else {			
				int gateNr = Integer.parseInt(param);	
					// Shut down Gate
				try {
						if (myArea.stopGate(gateNr) == 1){
							// Success shutting down
							user.writer.println("Success: Gate Nr: " + gateNr + " succesfully shutted down.");
						} else {
							// Error shutting down
							user.writer.println("Error: while tried to shut down gate nr: " + gateNr + ".");
						}
					
				} catch (InterruptedException e) {	
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}		
		}
		user.writer.flush();
	}

	/**
	 * Turns stopped Gates online again
	 * resumes the interrupted Thread
	 * called by ProcessStatement
	 */
	private void resumeGates() {	
		// Send Message to everyone
		StringTokenizer token = new StringTokenizer(this.cmdData, " ");
		// If Parameter -a isSet
		if (token.hasMoreTokens()) {
			String param = token.nextToken();
			if (param.equals("-a")) {
				// Open all Gates	
				try {
						if (myArea.resumeAllGates() == 1) {
							// All Gates Resumed
							user.writer.println("Success: Opening all Gates");
						} else {
							// Error while resuming the gates
							user.writer.println("Error: Opening all Gates");
						}
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} else {
				int gateNr = Integer.parseInt(param);
				// Open Gate
				try {
						if (myArea.resumeGate(gateNr) == 1){
							// Success opening Gate
							user.writer.println("Success: Gate Nr: " + gateNr + " succesfully opened again");
						} else {
							// Error opening Gate
							user.writer.println("Error: while tried to open gate nr: " + gateNr + ".");
						}
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}		
		}
		user.writer.flush();
	}
	/**
	 * Stops all GateWorker from opening the Gate for Drive in
	 * called by ProcessStatement
	 */
	private void driveInLock(){
		GateWorker.setEinfahrsperre(true);
	}
	
	/**
	 * Stops all GateWorker from opening the Gate for Drive out
	 * called by ProcessStatement
	 */
	private void driveOutLock(){
		GateWorker.setAusfahrsperre(true);
	}
	
	/**
	 * Undo all active Drive in or Drive out Locks
	 * called by ProcessStatement
	 */
	private void removeLocks(){
		GateWorker.setAusfahrsperre(false);
		GateWorker.setEinfahrsperre(false);
	}
}
