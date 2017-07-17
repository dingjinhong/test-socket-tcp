package testsocket.client;

import testsocket.model.User;

public interface IClient {
	public boolean start();
	public void close();
	public ClientReadThread getReadThread();
	public ClientWriteThread getWriteThread();
	public void setUser(User user);
	public User getUser();
	
}
