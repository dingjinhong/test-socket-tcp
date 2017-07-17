package testsocket.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;

import testsocket.model.TranObject;




public class ClientInputThread extends ClientReadThread {
	private Socket socket;  
    private TranObject msg;  
    private DataInputStream ois; 
    Gson gson =new Gson();
    public ClientInputThread(Socket socket) {  
        this.socket = socket;  
        try {  
            ois = new DataInputStream(socket.getInputStream());  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
  
   
  
    @Override  
    public void run() {  
        try {  
            while (isStart) {
            	String msgstr=ois.readUTF();
                msg =gson.fromJson(msgstr,TranObject.class);
                // 每收到一条消息，就调用接口的方法，并传入该消息对象，外部在实现接口的方法时，就可以及时处理传入的消息对象了  
                // 我不知道我有说明白没有？  
                messageListeners.removeAll(remove_messageListeners);
                remove_messageListeners.clear();
                for(Iterator<MessageListener> it=messageListeners.iterator();it.hasNext();){
                	
                		it.next().Message(msg);  
                	
                }
            }  
            ois.close();  
            if (socket != null)  
                socket.close();  
        }  catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
      
}
