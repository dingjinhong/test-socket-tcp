package testsocket.nio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;

import com.google.gson.Gson;

import testsocket.client.ClientReadThread;
import testsocket.client.ClientWriteThread;
import testsocket.client.IClient;
import testsocket.model.ClientRequest;
import testsocket.model.Constants;
import testsocket.model.TranObject;
import testsocket.model.TranObjectType;
import testsocket.model.User;

public class NIOClient implements IClient{
    private NIOClientThread clientThread;
    SocketChannel socketChannel;
    Selector selector;
    private User user;
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public boolean start(){
		try {
			socketChannel=SocketChannel.open();
			socketChannel.configureBlocking(false);
			selector=Selector.open();
			socketChannel.register(selector, SelectionKey.OP_CONNECT);
			socketChannel.connect(new InetSocketAddress("localhost", Constants.SERVER_PORT));
			boolean isConnect=false;
			while(!isConnect){
				selector.select();
				Set<SelectionKey> keySet=selector.selectedKeys();
				for(SelectionKey key:keySet){
					if (key.isConnectable()) {  
			            /*  
			             * 连接建立事件，已成功连接至服务器  
			             */  
			        	SocketChannel client = (SocketChannel)key.channel();  
			            if (client.isConnectionPending()) {  
			                client.finishConnect();  
			                clientThread = new NIOClientThread(key);  
			                clientThread.start();  
			            }
			            client.register(selector, SelectionKey.OP_READ); 
			            isConnect=true;
			            
			        }  
				}
				keySet.clear();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
    private class NIOClientThread extends Thread {  
    	  
        private NIOReadThread in;  
        private NIOWriteThread out;  
  
        public NIOClientThread(SelectionKey key) {  
            in = new NIOReadThread(key);  
            out = new NIOWriteThread(key);  
        }  
  
        public void run() {  
            in.setStart(true);  
            out.setStart(true);  
            in.start();  
            out.start();  
        }  
  
        // 得到读消息线程  
        public ClientReadThread getIn() {  
            return in;  
        }  
  
        // 得到写消息线程  
        public ClientWriteThread getOut() {  
            return out;  
        }  
    }
	@Override
	public void close() {
		// TODO Auto-generated method stub
		TranObject<User> msg=new TranObject<User>(TranObjectType.CLIENT_TO_SEVER);
		msg.setRequest(ClientRequest.LOGOUT);
		msg.setObj(user);
		Gson gson=new Gson();
		msg.setObjStr(gson.toJson(user));
		clientThread.getOut().setMsg(msg);
	}
	@Override
	public ClientReadThread getReadThread() {
		// TODO Auto-generated method stub
		return clientThread.getIn();
	}
	@Override
	public ClientWriteThread getWriteThread() {
		// TODO Auto-generated method stub
		return clientThread.getOut();
	}  
}
