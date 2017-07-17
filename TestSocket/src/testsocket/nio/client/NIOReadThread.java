package testsocket.nio.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import com.google.gson.Gson;

import testsocket.client.ClientReadThread;
import testsocket.model.TranObject;

public class NIOReadThread extends ClientReadThread {
	private SocketChannel socketChannel; 
	private SelectionKey key;
    private TranObject msg; 
    /*接受数据缓冲区*/  
    private static ByteBuffer rBuffer = ByteBuffer.allocate(1024); 
    private boolean isStart = true;   
    Gson gson =new Gson();
    Selector selector;
    public NIOReadThread(SelectionKey key) {
    	
    	this.key=key;
        this.socketChannel = (SocketChannel)key.channel();
        this.selector = key.selector();
    }   
  
    @Override  
    public void run() {  
        try {  
            while (isStart) {
            	selector.select();
				Set<SelectionKey> keySet=selector.selectedKeys();
				for(SelectionKey key:keySet){
					if(key.isValid()){
			            if (key.isReadable()) {  
			            	rBuffer.clear();
			            	int count=socketChannel.read(rBuffer);
			            	if(count >0){
				            	String msgstr=new String(rBuffer.array(), 0, count);
				            	rBuffer.flip();
				            	System.out.println(msgstr+"----------");
				                msg =gson.fromJson(msgstr,TranObject.class);
				                // 每收到一条消息，就调用接口的方法，并传入该消息对象，外部在实现接口的方法时，就可以及时处理传入的消息对象了  
				                // 我不知道我有说明白没有？  
				                messageListeners.removeAll(remove_messageListeners);
				                remove_messageListeners.clear();
				                for(Iterator<MessageListener> it=messageListeners.iterator();it.hasNext();){
				                	
				                		it.next().Message(msg);  
				                	
				                }
				                socketChannel.register(selector, SelectionKey.OP_READ);
				                
			            	}
		            	}
					}
				}
				keySet.clear();
            }   
            if (socketChannel != null)  
            	socketChannel.close();  
        }  catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
}
