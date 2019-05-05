package FTPServer;

import java.io.IOException;
import java.util.Scanner;

public class StartServer {

	public static void main(String[] args) {
	
		while(true)
		{
			System.out.print("Which port do you want to start the server on?:");
			Scanner in = new Scanner(System.in);
			int port = in.nextInt();
			Server s = new Server(port);
	
			try 
			{
				s.run();
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		
			
	}

}
