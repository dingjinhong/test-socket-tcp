package testsocket.server;

public class ClientNode {
	String id;
	ServerThread thread;
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ServerThread getThread() {
		return thread;
	}

	public void setThread(ServerThread thread) {
		this.thread = thread;
	}
	
}
