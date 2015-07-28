package chatserver;

import java.io.PrintWriter;


/**
 * 
 * @author rene
 * Class for connected Users to Store Userinformations
 */
public class User {
	private User replyto = null;
	private String name = "Unknown";
	private Boolean authorized = false;
	public PrintWriter writer = null;
	public String password = null;


	public User (){
	}
	
	public User (String name, String password, Boolean auth){
		this.password = password;
		this.name = name;
		this.authorized = auth;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getAuthorized() {
		return authorized;
	}
	public void setAuthorized(Boolean authorized) {
		this.authorized = authorized;
	}
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public User getReplyto() {
		return replyto;
	}

	public void setReplyto(User replyto) {
		this.replyto = replyto;
	}
}
