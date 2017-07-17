package testsocket.test.code;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadServer {
	 //端口号

    static final int portNo = 3333;

    public static void main(String[] args) throws IOException 

    {

           //服务器端的socket

           ServerSocket s = new ServerSocket(portNo);

           System.out.println("The Server is start: " + s);      

           try 

           {

                  for(;;)                          

                  {

               //阻塞,直到有客户端连接

                         Socket socket = s.accept();

                         //通过构造函数，启动线程

                     new ServerThreadCode(socket);

                  }

           }

        finally 

           {

                  s.close();

           }

    }
  
}
