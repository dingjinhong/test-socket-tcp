package testsocket.test.code;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ThreadClient {

	public static void main(String[] args) 

		      throws IOException, InterruptedException 

		  {

		    int threadNo = 0;

		       InetAddress addr = 

		       InetAddress.getByName("localhost");

		    for(threadNo = 0;threadNo<3;threadNo++)

		    {

		       new ClientThreadCode(addr);

		    }
		    System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhh");

		  }
}
