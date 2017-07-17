package testsocket.nio.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import com.google.gson.Gson;

import testsocket.client.ClientWriteThread;
import testsocket.model.ClientRequest;
import testsocket.model.TranObject;
import testsocket.model.TranObjectType;

public class NIOWriteThread extends ClientWriteThread {
	 /*发送数据缓冲区*/  
	private ByteBuffer sBuffer = ByteBuffer.allocate(1024);
	 private Charset cs = Charset.forName("utf-8");  
	 private SocketChannel channel; 
	 private SelectionKey key;
	    private TranObject msg;  
	    private Gson gson=new Gson();
	    public NIOWriteThread(SelectionKey key) {
	    	this.key=key;
	        this.channel =(SocketChannel)key.channel();  
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
	                	System.out.println("write:  "+msgstr);
	                	System.out.println(msg);
	            		 sBuffer.clear();  
	                     sBuffer.put(msgstr.getBytes(cs));  
	                     sBuffer.flip();
	                     channel.write(sBuffer); 
	                   // oos.writeUTF(msgstr);  
	                   // oos.flush();  
	                    if (msg.getType() == TranObjectType.CLIENT_TO_SEVER&&
	                    		msg.getRequest()==ClientRequest.LOGOUT) {// 如果是发送下线的消息，就直接跳出循环  
	                        break;  
	                    }  
	                   
	                }
	                synchronized (this) {  
                     wait();// 发送完消息后，线程进入等待状态  
                 }  
	            }  
	            if (channel != null)  
	            	channel.close();  
	        } catch (InterruptedException e) {  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    }  
}
