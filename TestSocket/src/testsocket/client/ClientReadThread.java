package testsocket.client;

import java.util.ArrayList;
import java.util.List;

import testsocket.model.TranObject;

public class ClientReadThread extends Thread {
		protected boolean isStart = true;  
	  protected List<MessageListener> remove_messageListeners=new ArrayList<MessageListener>();
	  protected List<MessageListener> messageListeners=new  ArrayList<MessageListener>();// 消息监听接口对象
	  /** 
	     * 提供给外部的消息监听方法 
	     *  
	     * @param messageListener 
	     *            消息监听接口对象 
	     */  
	 public void addMessageListener(MessageListener messageListener) {  
	        this.messageListeners.add(messageListener);
	  }  
	 public void removeMessageListener(MessageListener messageListener) {  
	      this.remove_messageListeners.add(messageListener);
	 }
	 public void setStart(boolean isStart) {  
	        this.isStart = isStart;  
	    }
	    /** 
	     * 消息监听接口 
	     *  
	     * @author way 
	     *  
	     */  
	    public interface MessageListener {  
	        public void Message(TranObject msg);  
	    }  
	
}
