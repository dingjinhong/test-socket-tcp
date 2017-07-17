package testsocket.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.google.gson.Gson;

import testsocket.model.ClientRequest;
import testsocket.model.TranObject;
import testsocket.model.TranObjectType;




public class ClientOutputThread extends ClientWriteThread {
	 private Socket socket;  
	    private DataOutputStream oos;  
	    private TranObject msg;  
	    private Gson gson=new Gson();
	    public ClientOutputThread(Socket socket) {  
	        this.socket = socket;  
	        try {  
	            oos = new DataOutputStream(socket.getOutputStream());  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    }  
	  
	  
	    // 这里处理跟服务器是一样的  
	    public void setMsg(TranObject msg) {  
	        this.msg = msg;  
	        synchronized (this) {  
	            notify();  
	            System.out.println("setMsg");
	        }  
	    }  
	  
	    @Override  
	    public void run() {  
	        try {  
	            while (isStart) {  
	                if (msg != null) {
	                	String msgstr=gson.toJson(msg);
	                	System.out.println(msg);
	                    oos.writeUTF(msgstr);  
	                    oos.flush();  
	                    if (msg.getType() == TranObjectType.CLIENT_TO_SEVER&&
	                    		msg.getRequest()==ClientRequest.LOGOUT) {// 如果是发送下线的消息，就直接跳出循环  
	                        break;  
	                    }  
	                   
	                }
	                synchronized (this) {  
                        wait();// 发送完消息后，线程进入等待状态  
                    }  
	            }  
	            oos.close();// 循环结束后，关闭输出流和socket  
	            if (socket != null)  
	                socket.close();  
	        } catch (InterruptedException e) {  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    }  
}
