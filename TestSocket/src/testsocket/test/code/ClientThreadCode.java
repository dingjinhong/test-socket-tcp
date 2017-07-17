package testsocket.test.code;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ClientThreadCode extends Thread{

	  //客户端的socket

	  private Socket socket;    

	  //线程统计数，用来给线程编号

	  private static int cnt = 0;

	  private int clientId = cnt++;

	  private BufferedReader in;

	  private PrintWriter out;

	  //构造函数

	  public ClientThreadCode(InetAddress addr) 

	  {

	    try 

	    {
	    	
	      socket = new Socket(addr, 3333);

	    }

	    catch(IOException e) 

	    {

	          e.printStackTrace();

	    }

	    //实例化IO对象

	try 

	    {    

	      in = new BufferedReader(

	             new InputStreamReader(socket.getInputStream()));    

	       out = new PrintWriter(

	               new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

	        //开启线程

	        start();

	     } 

	     catch(IOException e) 

	     {

	        //出现异常，关闭socket 

	          try 

	          {

	            socket.close();

	        } 

	          catch(IOException e2) 

	          {

	              e2.printStackTrace();       

	          }

	     }

	  }  

	  //线程主体方法

	public void run() 

	  {

	    try 

	    {

	      out.println("Hello Server,My id is " + clientId );

	      String str = in.readLine();

	      System.out.println(str);

	      out.println("byebye");

	    } 

	    catch(IOException e) 

	    {

	       e.printStackTrace();  

	    }

	    finally 

	    {

	      try 

	      {

	        socket.close();

	      } 

	      catch(IOException e) 

	      {

	              e.printStackTrace();

	      }    

	    }

	  }
}
