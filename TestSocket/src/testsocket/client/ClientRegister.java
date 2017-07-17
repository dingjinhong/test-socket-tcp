package testsocket.client;

public class ClientRegister {
	private static ClientRegister instance=new ClientRegister();
	IClient currentClient;
	public IClient getCurrentClient() {
		return currentClient;
	}
	public void setCurrentClient(IClient currentClient) {
		this.currentClient = currentClient;
	}
	public static ClientRegister getInstance(){
		return instance;
	}
	private ClientRegister(){
		
	}
}
