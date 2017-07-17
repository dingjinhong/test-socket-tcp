package testsocket.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerThread extends Thread {
	Socket socket;
	 boolean HAVE=false;
	 String clientName;
	 DataInputStream in=null;
	 DataOutputStream out=null;
	 String s=null;
	 ClientList clientList;
	 ClientNode pC;
	 ServerThread(Socket soc,ClientList list){
		 socket=soc;
		 //String addr=
		 setName(String.valueOf(socket.getInetAddress()));
		 clientList=list;
		 clientName=String.valueOf(socket.getInetAddress());
		 try{
	        in=new DataInputStream(socket.getInputStream());
	        out=new DataOutputStream(socket.getOutputStream());
	     }catch(IOException e){}
	 }
	 public void run(){
		 try{
			 s=in.readUTF();
	      	}catch(IOException e){}
		 pC=clientList.getNodeById(s);
		 if(pC!=null){
			 try {
				 if(pC.thread==Thread.currentThread()){
					 out.writeUTF("e");
					 return;
				 }
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }else{
			 
			 clientList.addNode(this, s);
			 pC=clientList.getNodeById(s);
			 try {
				out.writeUTF("s");
				 return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			 
		 }
	    //out.writeUTF("connected");
		
		 if(pC==null){
			 try{
				 out.writeUTF("与好友连接失败！");
				 return;
			 }
			 catch(IOException e){}
		 }else{
			 while(true){
				 try{
					 s=in.readUTF();
					 pC.thread.out.writeUTF(getName()+"说：\n"+s+"\n\n");
				 }catch(IOException e){
					 return;
				 }
			 }
		 }
	 }
}
