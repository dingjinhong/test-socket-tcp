package testsocket.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.google.gson.Gson;

import testsocket.model.TranObject;

public class OutputThread extends Thread {
	 private OutputThreadMap map;  
	    private DataOutputStream oos;  
	    private TranObject object;  
	    private boolean isStart = true;// 循环标志位  
	    private Socket socket;  
	    private Gson gson=new Gson();
	  
	    public OutputThread(Socket socket, OutputThreadMap map) {  
	        try {  
	            this.socket = socket;  
	            this.map = map;  
	            oos = new DataOutputStream(socket.getOutputStream());// 在构造器里面实例化对象输出流  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    }  
	  
	    public void setStart(boolean isStart) {//用于外部关闭写线程  
	        this.isStart = isStart;  
	    }  
	  
	    // 调用写消息线程，设置了消息之后，唤醒run方法，可以节约资源  
	    public void setMessage(TranObject object) {  
	        this.object = object;  
	        synchronized (this) {  
	            notify();  
	        }  
	    }  
	  
	    @Override  
	    public void run() {  
	        try {  
	            while (isStart) {  
	                // 没有消息写出的时候，线程等待  
	                synchronized (this) {  
	                    wait();  
	                }  
	                if (object != null) {
	                	String objstr=gson.toJson(object);
	                    oos.writeUTF(objstr); 
	                    oos.flush();  
	                }  
	            }  
	            if (oos != null)// 循环结束后，关闭流，释放资源  
	                oos.close();  
	            if (socket != null)  
	                socket.close();  
	        } catch (InterruptedException e) {  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    }  
}
