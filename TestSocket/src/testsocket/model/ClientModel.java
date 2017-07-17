package testsocket.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;



public class ClientModel {
	private static ClientModel instance=new ClientModel();
	DataInputStream in =null;
	DataOutputStream out = null;
	private User user;
	Socket socket=null;
	private ClientModel(){
		
	}
	public boolean connectServer(){
		try {
			socket=new Socket();
			InetAddress address=InetAddress.getByName("127.0.0.1");
		    InetSocketAddress serverAddress=new InetSocketAddress(address,4331);
		    socket.connect(serverAddress);
		    in = new DataInputStream(socket.getInputStream());
		    out = new DataOutputStream(socket.getOutputStream());
		    return true;
		}catch(IOException e){
			return false;
		}
	}
	public synchronized boolean closeConnection(){
		try{
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
			if (socket != null) {
				socket.close();
			}
		
			//isConnected = false;
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		//	isConnected = true;
			return false;
		}
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public DataInputStream getIn() {
		return in;
	}
	public DataOutputStream getOut() {
		return out;
	}
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	public static ClientModel getInstance(){
		return instance;
	}
	
	
}
