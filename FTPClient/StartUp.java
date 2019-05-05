package FTPClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class StartUp {

	private static Scanner in = new Scanner(System.in);
	private static Client c;
	
	public static void main(String[] args) throws IOException 
	{
		System.out.println("Starting...");
		
		String choice;
		String ip = null;
		int port = 0;
		
		try 
		{
			ip = InetAddress.getLocalHost().getHostAddress();
			
			System.out.println("Port to connect to: ");
			
			port = in.nextInt();
			
		} 
		catch (UnknownHostException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (InputMismatchException e)
		{
			e.printStackTrace();
		}	
		
		c = new Client(ip, port);
		
		while(true)
		{
			menu();
			choice = in.next();
			checkInput(choice);
		}
		
	}
	
	//Menu for the users choices
	static void menu()
	{
		System.out.println("Welcome...");
		System.out.println("[Menu]");
		System.out.println("1 = [SEND] | 2 = [GET] | 3 = [DELETE] | 4 = [EXIT]");
	}
	
	//Compare input to execute action
	static void checkInput(String input) throws IOException
	{
		switch(input)
		{
			//Send file over to server
			case("1"):
			{
				c.sendFile();
				break;
			}
			//Receive file from server
			case("2"):
			{
				try {
					c.getFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			//Delete file on server
			case("3"):
			{
				try {
					c.deleteFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			//Exit program, no need to protect from fall through on switch since this will quit.
			case("4"):
			{
				System.out.println("Exiting...");
				in.close();
				System.exit(1);
			}
			default:
			{
				System.out.println("Please choose an appropriate command.");
			}
		}
	}
}
