package testsocket.client;

import testsocket.nio.client.NIOClient;

public class ClientFactory {
	public static IClient getClient(boolean useNIO){
		if(useNIO){
			return new NIOClient();
		}else{
			return new Client();
		}
	} 
}
