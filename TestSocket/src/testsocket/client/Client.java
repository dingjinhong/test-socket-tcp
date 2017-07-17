package testsocket.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.google.gson.Gson;

import testsocket.model.ClientRequest;
import testsocket.model.Constants;
import testsocket.model.TranObject;
import testsocket.model.TranObjectType;
import testsocket.model.User;

public class Client implements IClient{
	private Socket client;  
    private ClientThread clientThread;  
   // private String ip;  
 //   private int port;  
    //private static Client instance=new Client("127.0.0.1",Constants.SERVER_PORT);
    private User user;
    private Gson gson=new Gson();
    
    public Gson getGson() {
		return gson;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public User getUser() {
		return user;
	}
	public Client() {  
       // this.ip = ip;  
       // this.port = port;  
    }  
//    public static Client getInstance(){
//    	return instance;
//    }
    public boolean start() {  
        try {  
            client = new Socket();  
            // client.connect(new InetSocketAddress(Constants.SERVER_IP,  
            // Constants.SERVER_PORT), 3000); 
            InetAddress address=InetAddress.getByName("localhost");
            client.connect(new InetSocketAddress(address, Constants.SERVER_PORT));  
            if (client.isConnected()) {  
                // System.out.println("Connected..");  
                clientThread = new ClientThread(client);  
                clientThread.start();  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
            return false;  
        }  
        return true;  
    }  
  
    // 直接通过client得到读线程  
    public ClientReadThread getReadThread() {  
        return clientThread.getIn();  
    }  
  
    // 直接通过client得到写线程  
    public  ClientWriteThread getWriteThread() {  
        return clientThread.getOut();  
    }  
  
    // 直接通过client停止读写消息  
    public void close() {
    	TranObject<User> msg=new TranObject<User>(TranObjectType.CLIENT_TO_SEVER);
		msg.setRequest(ClientRequest.LOGOUT);
		msg.setObj(user);
		Gson gson=new Gson();
		msg.setObjStr(gson.toJson(user));
		clientThread.getOut().setMsg(msg);
    }  
      
    private class ClientThread extends Thread {  
  
        private ClientInputThread in;  
        private ClientOutputThread out;  
  
        public ClientThread(Socket socket) {  
            in = new ClientInputThread(socket);  
            out = new ClientOutputThread(socket);  
        }  
  
        public void run() {  
            in.setStart(true);  
            out.setStart(true);  
            in.start();  
            out.start();  
        }  
  
        // 得到读消息线程  
        public ClientInputThread getIn() {  
            return in;  
        }  
  
        // 得到写消息线程  
        public ClientOutputThread getOut() {  
            return out;  
        }  
    }
 
}
