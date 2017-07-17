package testsocket.test.code;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThreadCode extends Thread {

    private Socket clientSocket;

    //IO句柄

    private BufferedReader sin;

    private PrintWriter sout;    

    //默认的构造函数

    public ServerThreadCode()

    {}  

    public ServerThreadCode(Socket s) throws IOException 

    {

           clientSocket = s;            

           //初始化sin和sout的句柄

           sin = new BufferedReader(new InputStreamReader(clientSocket

                         .getInputStream()));

     sout = new PrintWriter(new BufferedWriter(new OutputStreamWriter(

                         clientSocket.getOutputStream())), true);             

           //开启线程

           start(); 

    }

    //线程执行的主体函数

    public void run() 

    {

           try 

           {

                  //用循环来监听通讯内容

                  for(;;) 

                  {

             String str = sin.readLine();

                         //如果接收到的是byebye，退出本次通讯

                         if (str.equals("byebye"))

                         {     

                                break;

                         }     

                         System.out.println("In Server reveived the info: " + str);

                         sout.println(str);

                  }

                  System.out.println("closing the server socket!");

           } 

     catch (IOException e) 

           {

                  e.printStackTrace();

           } 

           finally 

           {

                  System.out.println("close the Server socket and the io.");

                  try 

         {

                         clientSocket.close();

                  } 

                  catch (IOException e) 

                  {

                         e.printStackTrace();

                  }

           }

    }
}
