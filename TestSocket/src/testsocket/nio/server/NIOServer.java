package testsocket.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import testsocket.model.Constants;

public class NIOServer {

    
	private ExecutorService pool;
    //解码buffer    
    private Charset cs = Charset.forName("utf-8"); 
    
    private static Selector selector;
    
    public NIOServer(){
    	 try {  
             init();  
         }  
         catch (Exception e) {  
             e.printStackTrace();  
         }  
    }
    
    private void init() throws IOException{
    	pool = Executors.newFixedThreadPool(5);
    	ServerSocketChannel serverSocketChannel= ServerSocketChannel.open();
    	serverSocketChannel.configureBlocking(false);
    	ServerSocket serverSocket=serverSocketChannel.socket();
    	serverSocket.bind(new InetSocketAddress(Constants.SERVER_PORT));
    	selector=Selector.open();
    	serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    	System.out.println("Server start on port:"+Constants.SERVER_PORT);
    }
    private void listen(){
    	while(true){
    		try{
    			selector.select();
    			Set<SelectionKey> selectionKeys=selector.selectedKeys();
    			System.out.println(selectionKeys.size());
    			for(SelectionKey key:selectionKeys){
    				handle(key);
    			}
    			selectionKeys.clear();
    		}catch(Exception e){
    			 e.printStackTrace();  
                 break;
    		}
    	}
    }
    /**  
     * 处理不同的事件  
    */  
    private void handle(SelectionKey selectionKey) throws IOException {  
        ServerSocketChannel server = null;  
        SocketChannel client = null;
        if(selectionKey.isValid()){
	        if (selectionKey.isAcceptable()) {  
	            /*  
	             * 客户端请求连接事件  
	             * serversocket为该客户端建立socket连接，将此socket注册READ事件，监听客户端输入  
	             * READ事件：当客户端发来数据，并已被服务器控制线程正确读取时，触发该事件  
	             */  
	            server = (ServerSocketChannel)selectionKey.channel();  
	            client = server.accept();  
	            client.configureBlocking(false);  
	            client.register(selector, SelectionKey.OP_READ);  
	        }  
	        else if (selectionKey.isReadable()) {  
	            /*  
	             * READ事件，收到客户端发送数据，读取数据后继续注册监听客户端  
	             */  
	        	System.out.println(selectionKey);
	        	selectionKey.interestOps(selectionKey.interestOps()&(~SelectionKey.OP_READ));
	           pool.execute(new ReadThread(selectionKey));
	        }
        }
    }  
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		NIOServer server=new NIOServer();
		server.listen();
		
	}

}
